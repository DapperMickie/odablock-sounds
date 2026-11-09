package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.WallObject;
import net.runelite.api.events.WallObjectSpawned;

@Singleton
public class ToaChestLight
{
	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	// Tomb template region, also identified by the ToA Plugin Hub plugin's RaidRoom.
	private static final int LOOT_REGION = 14672;
	private static final int VARBIT_ID_SARCOPHAGUS = 14373;
	private static final int WALL_OBJECT_ID_SARCOPHAGUS = 46221;

	private boolean rewardHandled;

	public void onWallObjectSpawned(final WallObjectSpawned event)
	{
		final WallObject wallObject = event.getWallObject();
		if (wallObject.getId() != WALL_OBJECT_ID_SARCOPHAGUS || rewardHandled)
		{
			return;
		}

		// Spawn events can repeat for the same chest or during scene rebuilds.
		// Mark before queuing audio so only one random clip is requested per reward.
		rewardHandled = true;
		boolean purple = client.getVarbitValue(VARBIT_ID_SARCOPHAGUS) % 2 != 0;
		if (purple)
		{
			if (config.toaPurpleChest())
			{
				soundEngine.playClip(Sound.GETTING_PURPLE_1, executor);
			}
		}
		else if (config.toaWhiteChest())
		{
			soundEngine.playClip(Sound.WHITE_LIGHT_AFTER_RAID, executor);
		}
	}

	public void onGameTick(GameTick event)
	{
		Player player = client.getLocalPlayer();
		if (client.getGameState() == GameState.LOGGED_IN && player != null && player.getLocalLocation() != null
			&& WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID() != LOOT_REGION)
		{
			reset();
		}
	}

	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			reset();
		}
		// Preserve the guard across LOADING and connection recovery in the same room.
	}

	public void reset()
	{
		rewardHandled = false;
	}
}
