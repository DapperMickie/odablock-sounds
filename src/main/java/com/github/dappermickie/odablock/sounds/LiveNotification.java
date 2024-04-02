package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.KickResponse;
import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Singleton
@Slf4j
public class LiveNotification
{
	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private OkHttpClient webClient;

	@Inject
	private Gson gson;

	private static final String kickRequestUrl = "https://kick.com/api/v2/channels/odablock";
	private static final String twitchRequestUrl = "https://twitch.tv/odablock";

	private static final int fifteenHoursInTicks = 90_000;

	private int lastTwitchNotificationTick = -1;
	private int lastTwitchNotificationCheckTick = -1;

	private int lastKickNotificationTick = -1;
	private int lastKickNotificationCheckTick = -1;

	public void onGameTick(GameTick event)
	{
		final int currentTick = client.getTickCount();
		// Check every 5 minutes if we haven't already notified the user
		// If the user has been notified, make sure to not notify again unless
		// The last notification was > 15h
		if (config.kickNotification())
		{
			if (currentTick - lastKickNotificationTick > fifteenHoursInTicks || lastKickNotificationTick == -1)
			{
				if (lastKickNotificationCheckTick == -1 || currentTick - lastKickNotificationCheckTick > 500)
				{
					executor.schedule(()->handleKickRequest(currentTick), 200, TimeUnit.MILLISECONDS);
				}
			}
		}

		if (config.twitchNotification())
		{
			if (currentTick - lastTwitchNotificationTick > fifteenHoursInTicks || lastTwitchNotificationTick == -1)
			{
				if (lastTwitchNotificationCheckTick == -1 || currentTick - lastTwitchNotificationCheckTick > 500)
				{
					executor.schedule(()->handleTwitchRequest(currentTick), 200, TimeUnit.MILLISECONDS);
				}
			}
		}
	}

	private void handleKickRequest(final int currentTick)
	{
		try
		{
			Response response = webClient.newCall(buildRequest(kickRequestUrl)).execute();
			ResponseBody body = response.body();
			if (body == null)
			{
				return;
			}
			String bodyString = body.string();
			KickResponse kickResponse = gson.fromJson(bodyString, KickResponse.class);

			if (kickResponse != null && kickResponse.livestream != null && kickResponse.livestream.is_live)
			{
				// Play sound
				soundEngine.playClip(Sound.KICK_LIVE, executor);
				lastKickNotificationTick = currentTick;
			}
			lastKickNotificationCheckTick = currentTick;
		}
		catch (IOException | JsonSyntaxException ignored)
		{
			lastKickNotificationCheckTick = currentTick;
		}
	}

	private void handleTwitchRequest(final int currentTick)
	{
		try
		{
			Response response = webClient.newCall(buildRequest(twitchRequestUrl)).execute();
			ResponseBody body = response.body();
			if (body == null)
			{
				return;
			}
			String bodyString = body.string();
			if (bodyString.contains("isLiveBroadcast"))
			{
				// Play sound
				soundEngine.playClip(Sound.TWITCH_LIVE, executor);
				lastTwitchNotificationTick = currentTick;
			}
			lastTwitchNotificationCheckTick = currentTick;
		}
		catch (IOException ignored)
		{
			lastTwitchNotificationCheckTick = currentTick;
		}
	}

	private static Request buildRequest(String requestUrl)
	{
		HttpUrl url = HttpUrl.get(requestUrl);

		log.debug("Built URL {}", url);

		return new Request.Builder()
			.url(url)
			.build();
	}
}
