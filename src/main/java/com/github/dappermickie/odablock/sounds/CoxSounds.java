package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;

@Singleton
@Slf4j
public class CoxSounds
{

	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	private Random random = new Random();

	private static final int COX_LIGHT_OBJECT_ID = 28848;
	private static final int COX_VARBIT_LIGHT_TYPE = 5456;

	private boolean chestsHandled = false;
	private boolean lightObjectDetected = false;

	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (!config.coxWhiteChest() && !config.coxPurpleChest())
		{
			return;
		}

		int objectId = event.getGameObject().getId();
		if (objectId == COX_LIGHT_OBJECT_ID)
		{
			log.debug("CoX light object detected: {}", objectId);
			lightObjectDetected = true;
			handleLight();
		}
	}

	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		int objectId = event.getGameObject().getId();
		if (objectId == COX_LIGHT_OBJECT_ID)
		{
			log.debug("CoX light object despawned: {}", objectId);
			lightObjectDetected = false;
			chestsHandled = false;
		}
	}

	public void onGameTick(GameTick event)
	{
		if (!lightObjectDetected || chestsHandled)
		{
			return;
		}

		int lightType = client.getVarbitValue(COX_VARBIT_LIGHT_TYPE);
		if (lightType > 0)
		{
			log.debug("*** COX LIGHT BECAME ACTIVE *** lightType={}", lightType);
			handleLight();
		}
	}

	private void handleLight()
	{
		if (chestsHandled)
		{
			return;
		}

		int lightType = client.getVarbitValue(COX_VARBIT_LIGHT_TYPE);
		log.debug("*** COX LIGHT DETECTED *** lightType={}", lightType);

		if (lightType == 0)
		{
			log.debug("*** COX LIGHT *** Not active yet (lightType=0), waiting for active status");
			return;
		}

		chestsHandled = true;

		boolean isPurple = (lightType == 2);
		if (isPurple)
		{
			if (config.coxPurpleChest())
			{
				log.debug("*** COX UNIQUE DROP *** playing purple sound");
				soundEngine.playClip(Sound.GETTING_PURPLE_1, executor);
			}
		}
		else
		{
			if (config.coxWhiteChest())
			{
				log.debug("*** COX STANDARD DROP *** playing white light sound");
				soundEngine.playClip(Sound.WHITE_LIGHT_AFTER_RAID, executor);
			}
		}
	}
}
