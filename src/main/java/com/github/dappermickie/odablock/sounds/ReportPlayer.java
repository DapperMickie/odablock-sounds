package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
@Slf4j
public class ReportPlayer
{

	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	private static final String REPORT_FOR = "Report for";
	private static final String REPORT_CONFIRMATION = "Thank you for submitting your abuse report";
	private static final int DUPLICATE_WINDOW_TICKS = 2;

	private int lastPlaybackTick = Integer.MIN_VALUE;

	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (config.sendReport() && isReportOption(menuOptionClicked.getMenuOption()))
		{
			playSoundOnce();
		}
	}

	public void onChatMessage(ChatMessage chatMessage)
	{
		if (config.sendReport()
			&& chatMessage.getType() == ChatMessageType.GAMEMESSAGE
			&& isReportConfirmation(chatMessage.getMessage()))
		{
			playSoundOnce();
		}
	}

	static boolean isReportOption(String option)
	{
		return option != null && REPORT_FOR.equalsIgnoreCase(Text.removeTags(option).trim());
	}

	static boolean isReportConfirmation(String message)
	{
		return message != null && Text.removeTags(message).contains(REPORT_CONFIRMATION);
	}

	private void playSoundOnce()
	{
		int currentTick = client.getTickCount();
		if (lastPlaybackTick != Integer.MIN_VALUE
			&& currentTick >= lastPlaybackTick
			&& currentTick - lastPlaybackTick <= DUPLICATE_WINDOW_TICKS)
		{
			return;
		}

		lastPlaybackTick = currentTick;
		soundEngine.playClip(Sound.REPORT_PLAYER_1, executor);
	}
}
