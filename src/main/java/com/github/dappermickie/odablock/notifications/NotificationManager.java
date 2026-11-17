package com.github.dappermickie.odablock.notifications;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.runelite.api.GameState;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
public class NotificationManager {

	private static final String NOTIFICATIONS_URL =
		"https://raw.githubusercontent.com/LogicalSoIutions/odablock-sounds-live/refs/heads/main/custom_notifications.json";

	private final Set<String> sentMessages = new HashSet<>();
	private int lastChecked = -1;
	private int generation;
	private boolean active = true;
	private boolean initialCheckComplete = false;
	private String baselineMessage = null;

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
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	public void resetStateForWorldHopOrLogin() {
		generation++;
		lastChecked = -1;
		initialCheckComplete = false;
		baselineMessage = null;
	}

	public void onGameTick(GameTick gameTick) {
		if (!active || !config.notifications()) {
			return;
		}
		handleTickReset();
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

	private void handleTickReset() {
		final int currentTick = client.getTickCount();
		if (lastChecked != -1 && currentTick < lastChecked) {
			lastChecked = -1;
		}
	}

	private void sendRequest(int requestGeneration) {
		// Cache-bust so GitHub raw CDN serves fresh JSON soon after updates.
		final String url = NOTIFICATIONS_URL + "?t=" + System.currentTimeMillis();
		Request request = new Request.Builder()
				.url(url)
				.header("Cache-Control", "no-cache")
				.build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			if (!response.isSuccessful() || response.body() == null) {
				return;
			}

			String jsonResponse = response.body().string();
			Notification notification = gson.fromJson(jsonResponse, Notification.class);
			clientThread.invokeLater(() -> applyNotification(notification, requestGeneration));
		}
		catch (IOException | JsonParseException ignored)
		{
		}
	}

	private void applyNotification(Notification notification, int requestGeneration)
	{
		if (!active || generation != requestGeneration || !config.notifications()
			|| client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		String message = notification == null ? null : notification.getMessage();
		if (!initialCheckComplete)
		{
			baselineMessage = message;
			initialCheckComplete = true;
			return;
		}
		if (message == null || message.trim().isEmpty()
			|| Objects.equals(message, baselineMessage) || sentMessages.contains(message))
		{
			return;
		}
		sentMessages.add(message);
		sendMessage(notification);
		if (config.notificationPlaySound())
		{
			soundEngine.playClip(Sound.ODAS_ALERT, executor);
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

	private void sendMessage(Notification notification) {
		ChatMessageBuilder chatMessage = new ChatMessageBuilder();
		chatMessage
				.append(ChatColorType.HIGHLIGHT)
				.append("Odablock's Announcement ~ ")
				.append(notification.getMessage());
		String hex = Integer.toHexString(config.notificationColor().getRGB()).substring(2);
		String message = chatMessage.build().replaceAll("colHIGHLIGHT", "col=" + hex);
		chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.GAMEMESSAGE)
				.runeLiteFormattedMessage(message)
				.build());
	}
}
