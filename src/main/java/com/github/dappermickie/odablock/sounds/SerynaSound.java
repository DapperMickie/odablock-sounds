package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import  net.runelite.api.gameval.NpcID;
import net.runelite.api.Player;
import net.runelite.api.events.InteractingChanged;

@Singleton
@Slf4j
public class SerynaSound
{
	@Inject
	private Client client;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private OdablockConfig config;

	private static final int RANDOM_EVENT_TIMEOUT = 150;
	private int lastNotificationTick = -RANDOM_EVENT_TIMEOUT; // to avoid double notifications

	public void onInteractingChanged(InteractingChanged event)
	{
		if (!config.serynaSound()) {
			return;
		}

		Actor source = event.getSource();
		Actor target = event.getTarget();
		Player player = client.getLocalPlayer();

		// Check that the npc is interacting with the player and the player isn't interacting with the npc, so
		// that the notification doesn't fire from talking to other user's randoms
		if (player == null
			|| target != player
			|| player.getInteracting() == source
			|| !(source instanceof NPC)
			|| ((NPC) source).getId() != NpcID.MACRO_SANDWICH_LADY_NPC)
		{
			return;
		}

		if (client.getTickCount() - lastNotificationTick > RANDOM_EVENT_TIMEOUT)
		{
			lastNotificationTick = client.getTickCount();

			soundEngine.playClip(Sound.SERYNA_SOUND, executor);
		}
	}
}
