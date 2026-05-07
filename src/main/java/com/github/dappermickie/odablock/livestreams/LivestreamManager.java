package com.github.dappermickie.odablock.livestreams;

import com.github.dappermickie.odablock.ChatRightClickManager;
import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.RightClickAction;
import com.github.dappermickie.odablock.sounds.LivestreamLiveSound;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
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

	@Inject
	private LivestreamLiveSound livestreamLiveSound;

	public void onGameTick(GameTick gameTick)
	{
		if (!config.livestream())
		{
			return;
		}

		handleTickReset();
		sendLivestreamMessage(false);

		int currentTick = client.getTickCount();
		if (lastChecked == -1 || currentTick < lastChecked || currentTick - lastChecked > 100)
		{
			executor.submit(() -> {
				sendRequest(currentTick);
			});

			lastChecked = currentTick;
		}
	}

	public void resetStateForWorldHopOrLogin()
	{
		// Reset poll/message timing so we immediately refresh after state transitions.
		// Keep the last livestream snapshot so hop/login doesn't count as a live transition.
		lastChecked = -1;
		lastSentMessage = -1;
	}

	private void sendRequest(final int currentTick)
	{
		Request request = new Request.Builder()
			.url("https://live.odablock.cc/")
			.build();
		try (Response response = okHttpClient.newCall(request).execute())
		{
			if (!response.isSuccessful() || response.body() == null)
			{
				return;
			}

			String jsonResponse = response.body().string();
			Livestream newLivestream = gson.fromJson(jsonResponse, Livestream.class);

			if (newLivestream == null)
			{
				return;
			}

			if (livestream != null &&
				newLivestream.isLive() == livestream.isLive() &&
				Objects.equals(newLivestream.getTitle(), livestream.getTitle()))
			{
				lastChecked = currentTick;
				return;
			}

			final boolean wasLive = livestream != null && livestream.isLive();
			final boolean isLive = newLivestream.isLive();
			final boolean becameLive = livestream != null && !wasLive && isLive;

			livestream = newLivestream;
			clientThread.invokeLater(() -> {
				sendLivestreamMessage(true);
				if (becameLive)
				{
					livestreamLiveSound.playSound();
				}
			});
		}
		catch (IOException e)
		{
		}
		catch (Exception e)
		{
		}
	}

	private void sendLivestreamMessage(boolean force)
	{
		final int currentTick = client.getTickCount();

		// Only send once every x minutes, unless we force send (in case he goes live)
		if (!force &&
			lastSentMessage != -1 &&
			currentTick >= lastSentMessage &&
			currentTick - lastSentMessage < config.livestreamInterval() * 100)
		{
			return;
		}

		// Only send if oda is live
		if (livestream == null || !livestream.isLive())
		{
			return;
		}

		lastSentMessage = currentTick;

		ChatMessageBuilder chatMessage = new ChatMessageBuilder();
		String hex = Integer.toHexString(config.livestreamColor().getRGB()).substring(2);
		final String title = livestream.getTitle() == null ? "" : livestream.getTitle().trim();
		chatMessage
			.append(ChatColorType.NORMAL)
			.append("Odablock is live! ");

		if (!title.isEmpty())
		{
			chatMessage
				.append(ChatColorType.HIGHLIGHT)
				.append(title);
		}

		String message = chatMessage.build().replaceAll("colHIGHLIGHT", "col=" + hex);
		RightClickAction rightClickAction = new RightClickAction("Open Livestream", "https://kick.com/odablock");
		chatRightClickManager.putInMap(message, rightClickAction);

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.GAMEMESSAGE)
			.runeLiteFormattedMessage(message)
			.build());
	}

	private void handleTickReset()
	{
		final int currentTick = client.getTickCount();
		if (lastChecked != -1 && currentTick < lastChecked)
		{
			lastChecked = -1;
		}
		if (lastSentMessage != -1 && currentTick < lastSentMessage)
		{
			lastSentMessage = -1;
		}
	}
}
