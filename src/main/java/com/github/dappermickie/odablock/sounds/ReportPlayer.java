package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.OdablockPlugin;
import com.github.dappermickie.odablock.RandomSoundUtility;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.events.MenuOptionClicked;

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

	private static final int REPORT_SCREEN_GROUP_ID = 875;
	private static final String REPORT_FOR = "Report for";

	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		// Thank you RuneWatch for groupId
		int groupId = OdablockPlugin.TO_GROUP(menuOptionClicked.getParam1());
		String option = menuOptionClicked.getMenuOption();
		MenuAction action = menuOptionClicked.getMenuAction();

		if (config.sendReport() && REPORT_SCREEN_GROUP_ID == groupId && option.equals(REPORT_FOR))
		{
			soundEngine.playClip(Sound.REPORT_PLAYER_1, executor);
		}
	}
}
