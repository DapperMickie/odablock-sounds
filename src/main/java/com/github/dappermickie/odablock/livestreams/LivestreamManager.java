package com.github.dappermickie.odablock.livestreams;

import com.github.dappermickie.odablock.ChatRightClickManager;
import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.RightClickAction;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Singleton
public class LivestreamManager
{
	private Livestream livestream = null;
	private int lastChecked = -1;
	private int lastSentMessage = -1;

	@Inject
	private Client client;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private Gson gson;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private OdablockConfig config;

	@Inject
	private ChatRightClickManager chatRightClickManager;

	@Inject
	private ScheduledExecutorService executor;

	public void onGameTick(GameTick gameTick)
	{
		if (!config.livestream())
		{
			return;
		}

		sendLivestreamMessage(false);

		int currentTick = client.getTickCount();
		if (lastChecked == -1 || currentTick - lastChecked > 100)
		{
			executor.submit(() -> {
				sendRequest(currentTick);
			});

			lastChecked = currentTick;
		}
	}

	private void sendRequest(final int currentTick)
	{
		Request request = new Request.Builder()
			.url("https://raw.githubusercontent.com/DapperMickie/odablock-sounds-notifier/main/livestream.json")
			.build();
		try (Response response = okHttpClient.newCall(request).execute())
		{
			if (!response.isSuccessful() || response.body() == null)
			{
				return;
			}

			String jsonResponse = response.body().string();
			Livestream newLivestream = gson.fromJson(jsonResponse, Livestream.class);

			if (livestream != null &&
				newLivestream.getKick().isLive() == livestream.getKick().isLive() &&
				newLivestream.getTwitch().isLive() == livestream.getTwitch().isLive())
			{
				lastChecked = currentTick;
				return;
			}

			livestream = newLivestream;
			clientThread.invokeLater(() -> {
				sendLivestreamMessage(true);
			});
		}
		catch (IOException ignored)
		{
		}
	}

	private void sendLivestreamMessage(boolean force)
	{
		final int currentTick = client.getTickCount();

		// Only send once every x minutes, unless we force send (in case he goes live)
		if (!force && lastSentMessage != -1 && currentTick - lastSentMessage < config.livestreamInterval() * 100)
		{
			return;
		}

		// Only send if oda is live
		if (livestream == null || (!livestream.getTwitch().isLive() && !livestream.getKick().isLive()))
		{
			return;
		}

		lastSentMessage = currentTick;

		ChatMessageBuilder chatMessage = new ChatMessageBuilder();
		String hex = Integer.toHexString(config.livestreamColor().getRGB()).substring(2);
		String message;
		if (livestream.getKick().isLive())
		{
			final String title = livestream.getKick().getTitle().split("\\|")[0].trim();
			chatMessage
				.append(ChatColorType.NORMAL)
				.append("Odablock is live on ")
				.append(ChatColorType.HIGHLIGHT)
				.append("KICK")
				.append(ChatColorType.NORMAL)
				.append("! ")
				.append(ChatColorType.HIGHLIGHT)
				.append(title);
			message = chatMessage.build().replaceAll("colHIGHLIGHT", "col=" + hex);
			RightClickAction rightClickAction = new RightClickAction("Open Kick Stream", "https://kick.com/odablock");
			chatRightClickManager.putInMap(message, rightClickAction);
		}
		else if (livestream.getTwitch().isLive())
		{
			final String title = livestream.getTwitch().getTitle().split("\\|")[0].trim();
			chatMessage.append(ChatColorType.NORMAL)
				.append("Odablock is live on ")
				.append(ChatColorType.HIGHLIGHT)
				.append("TWITCH")
				.append(ChatColorType.NORMAL)
				.append("! ")
				.append(ChatColorType.HIGHLIGHT)
				.append(title);
			message = chatMessage.build().replaceAll("colHIGHLIGHT", "col=" + hex);
			RightClickAction rightClickAction = new RightClickAction("Open Twitch Stream", "https://twitch.tv/odablock");
			chatRightClickManager.putInMap(message, rightClickAction);
		}
		else
		{
			// return if not live on either kick or twitch
			return;
		}

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.GAMEMESSAGE)
			.runeLiteFormattedMessage(message)
			.build());
	}
}
