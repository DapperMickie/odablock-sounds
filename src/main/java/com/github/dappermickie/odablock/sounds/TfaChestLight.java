package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;

@Singleton
public class TfaChestLight
{
	// Loot room template region, also used by the TFA Plugin Hub plugin's RaidRoom.
	private static final int LOOT_REGION = 16474;
	private static final int OWN_CHEST_VARBIT = VarbitID.TFA_VAULT_OWN_CHEST;

	private final Client client;
	private final OdablockConfig config;
	private final SoundEngine soundEngine;
	private final ScheduledExecutorService executor;
	private final ClientThread clientThread;

	private boolean chestsHandled;
	private boolean uniqueChestOpeningHandled;
	private boolean purpleCheckPending;
	private int roomGeneration;

	@Inject
	public TfaChestLight(Client client, OdablockConfig config, SoundEngine soundEngine,
		ScheduledExecutorService executor, ClientThread clientThread)
	{
		this.client = client;
		this.config = config;
		this.soundEngine = soundEngine;
		this.executor = executor;
		this.clientThread = clientThread;
	}

	public void onGameTick(GameTick event)
	{
		updateChestState(true);
	}

	public void onVarbitChanged(VarbitChanged event)
	{
		int id = event.getVarbitId();
		if (id == OWN_CHEST_VARBIT || (id >= VarbitID.TFA_VAULT_CHEST_0_STATE
			&& id <= VarbitID.TFA_VAULT_CHEST_7_STATE))
		{
			updateChestState(false);
		}
	}

	public void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();
		if (npc != null && (isUniqueChest(npc) || isStandardChest(npc)))
		{
			updateChestState(false);
		}
	}

	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof NPC) || !isInLootRoom())
		{
			return;
		}
		NPC npc = (NPC) event.getActor();
		if (isUniqueChest(npc) && isOpeningAnimation(npc.getAnimation()))
		{
			playOpeningSound();
		}
	}

	private boolean isInLootRoom()
	{
		Player player = client.getLocalPlayer();
		return client.getGameState() == GameState.LOGGED_IN && player != null
			&& player.getLocalLocation() != null
			&& WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID() == LOOT_REGION;
	}

	private void updateChestState(boolean allowPurple)
	{
		if (!isInLootRoom())
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				reset();
			}
			return;
		}

		boolean ownChest = client.getVarbitValue(OWN_CHEST_VARBIT) > 0;
		boolean uniqueRewardAvailable = false;
		for (NPC npc : client.getNpcs())
		{
			// The eight standard chests transform according to ownership and opened state.
			NPCComposition composition = npc.getTransformedComposition();
			if (composition == null)
			{
				continue;
			}
			int id = composition.getId();
			if (id >= NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_NOTMINE
				&& id <= NpcID.TFA_REWARDS_CHEST_VAULT_OPEN_MINE)
			{
				ownChest |= npc.hasSpotAnim(SpotanimID.VFX_TFA_SMALL_CHEST_SPARKLES);
			}
			switch (id)
			{
				case NpcID.TFA_UNIQUE_CHEST_VAULT_CLOSED:
					uniqueRewardAvailable = true;
					if (isOpeningAnimation(npc.getAnimation()))
					{
						playOpeningSound();
					}
					break;
				case NpcID.TFA_UNIQUE_CHEST_VAULT_OPEN:
					uniqueRewardAvailable = true;
					break;
				case NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_MINE:
				case NpcID.TFA_REWARDS_CHEST_VAULT_OPEN_MINE:
					ownChest = true;
					break;
			}
		}

		if (chestsHandled)
		{
			return;
		}

		// Eight ordinary chests with no sparkles is also the initial room state.
		// Only an allocated unique chest makes the absence of our own chest a purple.
		if (!ownChest)
		{
			if (!uniqueRewardAvailable)
			{
				return;
			}
			if (!allowPurple)
			{
				checkPurpleAtTickEnd();
				return;
			}
		}

		chestsHandled = true;
		if (!ownChest)
		{
			if (config.tfaPurpleChest())
			{
				soundEngine.playClip(Sound.GETTING_PURPLE_1, executor);
			}
		}
		else if (config.tfaWhiteChest())
		{
			soundEngine.playClip(Sound.WHITE_LIGHT_AFTER_RAID, executor);
		}
	}

	private void checkPurpleAtTickEnd()
	{
		if (purpleCheckPending)
		{
			return;
		}
		purpleCheckPending = true;
		final int generation = roomGeneration;
		clientThread.invokeAtTickEnd(() -> {
			if (generation != roomGeneration)
			{
				return;
			}
			purpleCheckPending = false;
			// Ownership and NPC updates may arrive in either order within one tick.
			updateChestState(true);
		});
	}

	private boolean isUniqueChest(NPC npc)
	{
		NPCComposition composition = npc.getTransformedComposition();
		int id = composition == null ? npc.getId() : composition.getId();
		return id == NpcID.TFA_UNIQUE_CHEST_VAULT_CLOSED || id == NpcID.TFA_UNIQUE_CHEST_VAULT_OPEN;
	}

	private boolean isStandardChest(NPC npc)
	{
		NPCComposition composition = npc.getTransformedComposition();
		int id = composition == null ? npc.getId() : composition.getId();
		return id >= NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_NOTMINE
			&& id <= NpcID.TFA_REWARDS_CHEST_VAULT_OPEN_MINE;
	}

	private static boolean isOpeningAnimation(int animation)
	{
		return animation == AnimationID.CHEST_GUTHIXIAN01_OPEN
			|| animation == AnimationID.CHEST_GUTHIXIAN02_OPEN01;
	}

	private void playOpeningSound()
	{
		if (!uniqueChestOpeningHandled)
		{
			uniqueChestOpeningHandled = true;
			if (config.tfaPurpleChestOpens())
			{
				soundEngine.playClip(Sound.TOA_CHEST_OPENS, executor);
			}
		}
	}

	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGIN_SCREEN:
			case LOGGING_IN:
			case HOPPING:
				reset();
				break;
		}
	}

	public void reset()
	{
		roomGeneration++;
		purpleCheckPending = false;
		chestsHandled = false;
		uniqueChestOpeningHandled = false;
	}
}
