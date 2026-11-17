package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static com.github.dappermickie.odablock.OdablockPlugin.ODABLOCK;

@Singleton
@Slf4j
public class LevelUp
{
	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	private static final int MAX_TOTAL_LEVEL = 2376;

	private static final String message = "Level up: completed.";
	private static final String level99Message = "Level 99: completed.";
	private static final String maxTotalLevelMessage = "Max total level: completed.";
	private static final int LOGIN_STAT_SYNC_TICK_GRACE = 1;
	private static final int PVP_ARENA_WORLD_1 = 558;
	private static final int PVP_ARENA_WORLD_2 = 570;
	private static final int PVP_ARENA_WORLD_3 = 578;

	private final Map<Skill, Integer> oldExperience = new EnumMap<>(Skill.class);
	private int oldTotalLevel = -1;
	private int lastLoginTick = -1;

	public void onStatChanged(StatChanged statChanged)
	{
		final Skill skill = statChanged.getSkill();

		// Modified from Nightfirecat's virtual level ups plugin as this info isn't (yet?) built in to statChanged event
		final int xpAfter = client.getSkillExperience(skill);
		final int levelAfter = Experience.getLevelForXp(xpAfter);
		final int xpBefore = oldExperience.getOrDefault(skill, -1);
		final int levelBefore = xpBefore == -1 ? -1 : Experience.getLevelForXp(xpBefore);
		final int totalLevelBefore = oldTotalLevel;
		final int totalLevelAfter = client.getTotalLevel();

		oldExperience.put(skill, xpAfter);
		oldTotalLevel = totalLevelAfter;

		if (isPvpArenaWorld())
		{
			return;
		}

		// Ignore the login stat sync window. During this period, the client can report
		// transient stat values that would otherwise look like massive level jumps.
		if (lastLoginTick >= 0 && client.getTickCount() <= lastLoginTick + LOGIN_STAT_SYNC_TICK_GRACE)
		{
			return;
		}

		// Do not proceed if any of the following are true:
		//  * xpBefore == -1              (don't fire when first setting new known value)
		//  * xpAfter <= xpBefore         (do not allow 200m -> 200m exp drops)
		//  * levelBefore >= levelAfter   (stop if if we're not actually reaching a new level)
		//  * levelAfter > MAX_REAL_LEVEL && config says don't include virtual (level is virtual and config ignores virtual)
		if (xpBefore == -1 || xpAfter <= xpBefore || levelBefore >= levelAfter ||
			(levelAfter > Experience.MAX_REAL_LEVEL && !config.announceLevelUpIncludesVirtual()))
		{
			return;
		}

		// Hitting level 99 in a skill is a milestone and gets its own sound/announcement,
		// taking precedence over the generic level-up sound for that single event.
		final boolean justReached99 = levelBefore < Experience.MAX_REAL_LEVEL
			&& levelAfter >= Experience.MAX_REAL_LEVEL;

		final boolean justMaxed = totalLevelBefore >= 0
			&& totalLevelBefore < MAX_TOTAL_LEVEL && totalLevelAfter >= MAX_TOTAL_LEVEL;
		final boolean announceMaxTotalLevel = justReached99 && justMaxed && config.announceMaxTotalLevel();
		if (justReached99 && (config.announceLevel99() || announceMaxTotalLevel))
		{

			if (config.showChatMessages())
			{
				client.addChatMessage(
					ChatMessageType.PUBLICCHAT,
					ODABLOCK,
					announceMaxTotalLevel ? maxTotalLevelMessage : level99Message,
					null
				);
			}

			if (announceMaxTotalLevel)
			{
				soundEngine.playClip(Sound.BIG_GAMON, executor);
			}
			else
			{
				soundEngine.playClip(Sound.REGULAR_GAMON, executor);
			}
			return;
		}

		if (config.announceLevelUp())
		{
			if (config.showChatMessages())
			{
				client.addChatMessage(ChatMessageType.PUBLICCHAT, ODABLOCK, message, null);
			}
			soundEngine.playClip(Sound.LEVEL_UP, executor);
		}
	}

	public void clear()
	{
		oldExperience.clear();
		oldTotalLevel = -1;
		lastLoginTick = -1;
	}

	public void setOldExperience()
	{
		for (final Skill skill : Skill.values())
		{
			oldExperience.put(skill, client.getSkillExperience(skill));
		}
		oldTotalLevel = client.getTotalLevel();
	}

	public void setLastLoginTick(int tick)
	{
		lastLoginTick = tick;
	}

	private boolean isPvpArenaWorld()
	{
		final int world = client.getWorld();
		return world == PVP_ARENA_WORLD_1 || world == PVP_ARENA_WORLD_2 || world == PVP_ARENA_WORLD_3;
	}
}
