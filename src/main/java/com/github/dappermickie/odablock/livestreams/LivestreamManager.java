package com.github.dappermickie.odablock.livestreams;

import com.github.dappermickie.odablock.OdablockConfig;
import com.google.gson.Gson;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ScriptEvent;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.Text;
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

	public void onGameTick(GameTick gameTick)
	{
		if (!config.livestream())
		{
			return;
		}

		addMenuOptionToOpenBrowser();
		sendLivestreamMessage(false);

		int currentTick = client.getTickCount();
		if (lastChecked == -1 || currentTick - lastChecked > 100)
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
				sendLivestreamMessage(true);
			}
			catch (IOException ignored)
			{
			}

			lastChecked = currentTick;
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
		if (livestream.getKick().isLive())
		{
			chatMessage
				.append(ChatColorType.HIGHLIGHT)
				.append("Odablock")
				.append(ChatColorType.NORMAL)
				.append(" is live on ")
				.append(ChatColorType.HIGHLIGHT)
				.append("KICK")
				.append(ChatColorType.NORMAL)
				.append("! ")
				.append(ChatColorType.HIGHLIGHT)
				.append(livestream.getKick().getTitle());
		}
		else if (livestream.getTwitch().isLive())
		{
			chatMessage.append(ChatColorType.NORMAL)
				.append("Odablock is live on ")
				.append(ChatColorType.HIGHLIGHT)
				.append("TWITCH")
				.append(ChatColorType.NORMAL)
				.append("! ")
				.append(ChatColorType.HIGHLIGHT)
				.append(livestream.getKick().getTitle());
		}
		else
		{
			// return if not live on either kick or twitch
			return;
		}

		sendLivestreamChatMessage(chatMessage);
	}

	protected void addMenuOptionToOpenBrowser()
	{
		Widget chatWidget = client.getWidget(ComponentID.CHATBOX_MESSAGE_LINES);
		if (chatWidget != null)
		{
			for (Widget w : chatWidget.getDynamicChildren())
			{
				String untaggedText = Text.removeTags(w.getText());
				if (untaggedText.contains("Odablock is live on ") && untaggedText.toLowerCase().contains("twitch"))
				{
					clientThread.invokeLater(() -> {
						w.setAction(1, "Open Twitch Livestream");
						w.setOnOpListener((JavaScriptCallback) this::openTwitch);
						w.setHasListener(true);
						w.setNoClickThrough(true);
						w.revalidate();
					});
				}
				else if (untaggedText.contains("Odablock is live on ") && untaggedText.toLowerCase().contains("kick"))
				{
					clientThread.invokeLater(() -> {
						w.setAction(1, "Open Kick Livestream");
						w.setOnOpListener((JavaScriptCallback) this::openKick);
						w.setHasListener(true);
						w.setNoClickThrough(true);
						w.revalidate();
					});
				}
				else
				{
					clientThread.invokeLater(() -> {
						w.setHasListener(false);
						w.setNoClickThrough(false);
						w.revalidate();
					});
				}
			}
		}
	}

	protected void sendLivestreamChatMessage(ChatMessageBuilder response)
	{
		String hex = Integer.toHexString(config.livestreamColor().getRGB()).substring(2);
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.GAMEMESSAGE)
			.runeLiteFormattedMessage(response.build().replaceAll("colHIGHLIGHT", "col=" + hex))
			.build());
	}

	protected void openKick(ScriptEvent ev)
	{
		LinkBrowser.browse("https://kick.com/odablock");
		log.info("Opened a link to Odablocks kick livestream!");
	}

	protected void openTwitch(ScriptEvent ev)
	{
		LinkBrowser.browse("https://twitch.tv/odablock");
		log.info("Opened a link to Odablocks twitch livestream!");
	}
}
