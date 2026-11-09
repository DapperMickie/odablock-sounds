package com.github.dappermickie.odablock.livestreams;

import com.github.dappermickie.odablock.ChatRightClickManager;
import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.RightClickAction;
import com.github.dappermickie.odablock.sounds.LivestreamLiveSound;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.runelite.api.GameState;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
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
	private static final Duration LIVE_SOUND_MAX_AGE = Duration.ofMinutes(3);

	private boolean active = true;
	private int generation;
	private Livestream livestream = null;
	private int lastChecked = -1;
	private int lastSentMessage = -1;
	private boolean offlineAnnouncementSent = false;
	private boolean suppressLiveTransitionSound = false;

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
		if (!active || !config.livestream())
		{
			return;
		}

		handleTickReset();
		sendLiveLivestreamMessage(false);

		int currentTick = client.getTickCount();
		if (lastChecked == -1
			|| currentTick < lastChecked
			|| currentTick - lastChecked > 100)
		{
			final int requestGeneration = generation;
			executor.submit(() -> sendRequest(requestGeneration));

			lastChecked = currentTick;
		}
	}

	public void resetStateForWorldHopOrLogin()
	{
		generation++;
		// Reset poll/message timing so we immediately refresh after state transitions.
		// Keep the last livestream snapshot so hop/login doesn't count as a live transition.
		lastChecked = -1;
		lastSentMessage = -1;
		suppressLiveTransitionSound = true;
	}

	private void sendRequest(final int requestGeneration)
	{
		Request request = new Request.Builder()
			.url("https://raw.githubusercontent.com/LogicalSoIutions/odablock-sounds-live/refs/heads/main/livestream.json")
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

			clientThread.invokeLater(() -> applySnapshot(newLivestream, requestGeneration));
		}
		catch (IOException | JsonParseException ignored)
		{
		}
	}

	private void applySnapshot(Livestream newLivestream, int requestGeneration)
	{
		if (!active || generation != requestGeneration || !config.livestream()
			|| client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		boolean wasSuppressingLiveSound = suppressLiveTransitionSound;
		suppressLiveTransitionSound = false;
		if (livestream != null && newLivestream.isLive() == livestream.isLive()
			&& Objects.equals(newLivestream.getTitle(), livestream.getTitle())
			&& Objects.equals(newLivestream.getWentLiveAt(), livestream.getWentLiveAt()))
		{
			return;
		}

		Livestream previousLivestream = livestream;
		boolean becameOffline = previousLivestream != null && previousLivestream.isLive() && !newLivestream.isLive();
		livestream = newLivestream;
		if (newLivestream.isLive())
		{
			offlineAnnouncementSent = false;
			sendLiveLivestreamMessage(true);
		}
		if (shouldPlayLiveSound(previousLivestream, newLivestream, wasSuppressingLiveSound))
		{
			livestreamLiveSound.playSound();
		}
		else if (becameOffline && !offlineAnnouncementSent)
		{
			sendOfflineLivestreamMessage();
			offlineAnnouncementSent = true;
		}
	}

	public void startUp()
	{
		active = true;
		resetStateForWorldHopOrLogin();
	}

	public void shutDown()
	{
		active = false;
		generation++;
	}

	private void sendLiveLivestreamMessage(boolean force)
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

		queueLivestreamMessage(chatMessage.build(), hex);
	}

	private void sendOfflineLivestreamMessage()
	{
		ChatMessageBuilder chatMessage = new ChatMessageBuilder();
		chatMessage
			.append(ChatColorType.HIGHLIGHT)
			.append("Odablock went offline.");

		String hex = Integer.toHexString(config.livestreamColor().getRGB()).substring(2);
		queueLivestreamMessage(chatMessage.build(), hex);
	}

	private void queueLivestreamMessage(String chatMessage, String hex)
	{
		String message = chatMessage.replaceAll("colHIGHLIGHT", "col=" + hex);
		RightClickAction rightClickAction = new RightClickAction("Open Livestream", "https://kick.com/odablock");
		chatRightClickManager.putInMap(message, rightClickAction);
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.GAMEMESSAGE)
			.runeLiteFormattedMessage(message)
			.build());
	}

	private boolean shouldPlayLiveSound(
		Livestream previousLivestream,
		Livestream newLivestream,
		boolean wasSuppressingLiveSound)
	{
		if (!config.livestreamPlaySound() || wasSuppressingLiveSound || !newLivestream.isLive())
		{
			return false;
		}

		final Instant newWentLiveAt = parseWentLiveAt(newLivestream.getWentLiveAt());
		if (newWentLiveAt == null)
		{
			return false;
		}

		final Instant previousWentLiveAt = parseWentLiveAt(
			previousLivestream == null ? null : previousLivestream.getWentLiveAt());
		if (Objects.equals(newWentLiveAt, previousWentLiveAt))
		{
			return false;
		}

		final Duration age = Duration.between(newWentLiveAt, Instant.now());
		return !age.isNegative() && age.compareTo(LIVE_SOUND_MAX_AGE) <= 0;
	}

	private static Instant parseWentLiveAt(String wentLiveAt)
	{
		if (wentLiveAt == null || wentLiveAt.isEmpty())
		{
			return null;
		}

		try
		{
			return Instant.parse(wentLiveAt);
		}
		catch (DateTimeParseException e)
		{
			return null;
		}
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