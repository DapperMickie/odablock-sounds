package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.callback.ClientThread;

import java.util.concurrent.ScheduledExecutorService;

@Singleton
@Slf4j
public class CryptSound
{
	private static final String CRYPT_MESSAGE = "You've broken into a crypt!";

	private static final int BARROWS_REGION_ID = 14131;
	private static final int CRYPT_REGION_ID = 14231;

	/** Center of Dharok's mound (matches RuneLite BarrowsBrothers plugin). */
	private static final WorldPoint DHAROK_MOUND = new WorldPoint(3575, 3298, 0);
	private static final int MOUND_MAX_DISTANCE = 6;
	private static final int CRYPT_OBJECT_SCAN_RADIUS = 20;

	private static final WorldPoint[] OTHER_BROTHER_MOUNDS = {
		new WorldPoint(3566, 3289, 0),
		new WorldPoint(3577, 3283, 0),
		new WorldPoint(3566, 3275, 0),
		new WorldPoint(3553, 3283, 0),
		new WorldPoint(3557, 3298, 0),
	};

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private OdablockConfig config;

	public boolean onChatMessage(ChatMessage chatMessage)
	{
		if (!config.cryptSound())
		{
			return false;
		}

		if (!chatMessage.getMessage().equals(CRYPT_MESSAGE))
		{
			return false;
		}

		clientThread.invokeLater(() ->
		{
			if (isEnteringDharokCrypt())
			{
				soundEngine.playClip(Sound.CRYPT, executor);
			}
		});

		return true;
	}

	private boolean isEnteringDharokCrypt()
	{
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return false;
		}

		WorldPoint location = localPlayer.getWorldLocation();
		int regionId = location.getRegionID();

		if (regionId == CRYPT_REGION_ID)
		{
			return isDharokCryptRoom();
		}

		if (regionId == BARROWS_REGION_ID)
		{
			return isAtDharokMound(location);
		}

		return false;
	}

	private boolean isAtDharokMound(WorldPoint location)
	{
		if (location.getPlane() != 0)
		{
			return false;
		}

		int dharokDistance = location.distanceTo(DHAROK_MOUND);
		if (dharokDistance > MOUND_MAX_DISTANCE)
		{
			return false;
		}

		for (WorldPoint otherMound : OTHER_BROTHER_MOUNDS)
		{
			if (location.distanceTo(otherMound) < dharokDistance)
			{
				return false;
			}
		}

		return true;
	}

	private boolean isDharokCryptRoom()
	{
		return hasNearbyObject(ObjectID.BARROWS_STAIRS_DHAROK)
			|| hasNearbyObject(ObjectID.BARROW_DHAROK_SARCOPHAGUS);
	}

	private boolean hasNearbyObject(int objectId)
	{
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return false;
		}

		LocalPoint localPoint = localPlayer.getLocalLocation();
		if (localPoint == null)
		{
			return false;
		}

		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		int plane = client.getPlane();
		int baseX = localPoint.getSceneX();
		int baseY = localPoint.getSceneY();

		for (int dx = -CRYPT_OBJECT_SCAN_RADIUS; dx <= CRYPT_OBJECT_SCAN_RADIUS; dx++)
		{
			for (int dy = -CRYPT_OBJECT_SCAN_RADIUS; dy <= CRYPT_OBJECT_SCAN_RADIUS; dy++)
			{
				int sceneX = baseX + dx;
				int sceneY = baseY + dy;

				if (sceneX < 0 || sceneY < 0 || sceneX >= Constants.SCENE_SIZE || sceneY >= Constants.SCENE_SIZE)
				{
					continue;
				}

				Tile tile = tiles[plane][sceneX][sceneY];
				if (tile == null)
				{
					continue;
				}

				for (GameObject gameObject : tile.getGameObjects())
				{
					if (gameObject != null && gameObject.getId() == objectId)
					{
						return true;
					}
				}
			}
		}

		return false;
	}
}
