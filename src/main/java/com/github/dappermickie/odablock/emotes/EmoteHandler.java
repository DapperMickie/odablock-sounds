package com.github.dappermickie.odablock.emotes;

import com.github.dappermickie.odablock.OdablockConfig;
import static com.github.dappermickie.odablock.OdablockPlugin.ODABLOCK;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.util.Text;

@Slf4j
@Singleton
public class EmoteHandler
{
	private static final Pattern WHITESPACE_REGEXP = Pattern.compile("[\\s\\u00A0]");

	@Inject
	private ChatIconManager chatIconManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	private int[] iconIds;

	public void loadEmotes()
	{
		if (iconIds != null)
		{
			return;
		}

		Emote[] emotes = Emote.values();
		iconIds = new int[emotes.length];

		for (int i = 0; i < emotes.length; i++)
		{
			final Emote emoji = emotes[i];
			final BufferedImage image = emoji.loadImage();
			iconIds[i] = chatIconManager.registerChatIcon(image);
		}
	}

	public void onChatMessage(ChatMessage chatMessage)
	{
		if (!config.emotes())
		{
			return;
		}

		if (iconIds == null)
		{
			return;
		}

		switch (chatMessage.getType())
		{
			case PUBLICCHAT:
			case MODCHAT:
			case FRIENDSCHAT:
			case CLAN_CHAT:
			case CLAN_GUEST_CHAT:
			case CLAN_GIM_CHAT:
			case PRIVATECHAT:
			case PRIVATECHATOUT:
			case MODPRIVATECHAT:
				break;
			default:
				return;
		}

		final MessageNode messageNode = chatMessage.getMessageNode();
		final String message = messageNode.getValue();
		final String updatedMessage = updateMessage(message);

		if (updatedMessage == null)
		{
			return;
		}

		messageNode.setValue(updatedMessage);
	}

	public void onOverheadTextChanged(final OverheadTextChanged event)
	{
		if (!config.emotes())
		{
			return;
		}

		if (!(event.getActor() instanceof Player))
		{
			return;
		}

		final String message = event.getOverheadText();
		final String updatedMessage = updateMessage(message);

		if (updatedMessage == null)
		{
			return;
		}

		event.getActor().setOverheadText(updatedMessage);
	}

	public void onCommandExecuted(CommandExecuted event)
	{
		if (event.getCommand().startsWith("odaemote") || event.getCommand().startsWith("odablockemote"))
		{
			clientThread.invoke(this::sendOdaEmotes);
		}
	}

	private void sendOdaEmotes()
	{
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, ODABLOCK, "Odablock emote list:", null);
		for (Emote emote : Emote.values())
		{
			final int emoteId = iconIds[emote.ordinal()];
			final String emoteImage = "<img=" + chatIconManager.chatIconIndex(emoteId) + ">";
			StringBuilder chatMessageSb = new StringBuilder();
			chatMessageSb.append(emote.getTrigger());
			chatMessageSb.append(" : ");
			chatMessageSb.append(emoteImage);
			List<String> altTriggerOriginal = Arrays.asList(emote.getAltTriggers());
			List<String> altTriggers = new ArrayList<>(altTriggerOriginal);
			if (!emote.getTrigger().startsWith("oda") && !emote.getTrigger().startsWith(":"))
			{
				altTriggers.add("oda" + emote.getTrigger());
			}
			for (int i = 0; i < altTriggers.size(); i++)
			{
				if (i == 0)
				{
					chatMessageSb.append(" (alt: ");
				}
				else
				{
					chatMessageSb.append(", ");
				}
				String altTrigger = altTriggers.get(i);
				chatMessageSb.append(altTrigger);
				if (!altTrigger.startsWith("oda") && !altTrigger.startsWith(":"))
				{
					chatMessageSb.append(", oda");
					chatMessageSb.append(altTrigger);
				}
				if (i == (altTriggers.size() - 1))
				{
					chatMessageSb.append(" )");
				}
			}
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, ODABLOCK, chatMessageSb.toString(), null);
		}
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, ODABLOCK, "Some emotes have an alternative trigger that will get translated into an emote.", null);
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, ODABLOCK, "For example: both \"cap\" and \"odacap\" work.", null);
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, ODABLOCK, "To disable an emote, you can add it to the \"Emote Ignore List\" in the plugin settings.", null);
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, ODABLOCK, "Add the emote and the alternative triggers to completely disable an emote.", null);
	}

	@Nullable
	String updateMessage(final String message)
	{
		final String[] messageWords = WHITESPACE_REGEXP.split(message);
		final String[] ignoredEmotes = config.emoteIgnoreList().split(",");

		boolean editedMessage = false;
		for (int i = 0; i < messageWords.length; i++)
		{
			// Remove tags except for <lt> and <gt>
			final String originalTrigger = Text.removeFormattingTags(messageWords[i]);
			final String trigger = originalTrigger.toLowerCase();
			final Emote emote = Emote.getEmoji(trigger);

			if (emote == null)
			{
				continue;
			}

			boolean shouldSkip = false;
			for (String ignored : ignoredEmotes)
			{
				if (ignored.strip().equalsIgnoreCase(trigger))
				{
					shouldSkip = true;
					break;
				}
			}

			if (shouldSkip)
			{
				break;
			}

			final int emoteId = iconIds[emote.ordinal()];
			messageWords[i] = messageWords[i].replace(originalTrigger, "<img=" + chatIconManager.chatIconIndex(emoteId) + ">");
			editedMessage = true;
		}

		// If we haven't edited the message any, don't update it.
		if (!editedMessage)
		{
			return null;
		}

		return Strings.join(messageWords, " ");
	}
}
