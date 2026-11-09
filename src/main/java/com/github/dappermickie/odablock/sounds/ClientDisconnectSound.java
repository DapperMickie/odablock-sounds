package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;

@Singleton
public class ClientDisconnectSound
{
	private final OdablockConfig config;
	private final SoundEngine soundEngine;
	private final ScheduledExecutorService executor;
	private boolean disconnectHandled;

	@Inject
	public ClientDisconnectSound(OdablockConfig config, SoundEngine soundEngine,
		ScheduledExecutorService executor)
	{
		this.config = config;
		this.soundEngine = soundEngine;
		this.executor = executor;
	}

	public void reset(GameState initialState)
	{
		// Enabling the plugin during an existing outage must not replay it.
		disconnectHandled = initialState == GameState.CONNECTION_LOST;
	}

	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			disconnectHandled = false;
		}
		else if (event.getGameState() == GameState.CONNECTION_LOST && !disconnectHandled)
		{
			// Reconnect attempts can repeat this state. Rearm only after a successful login.
			disconnectHandled = true;
			if (config.clientDisconnect())
			{
				soundEngine.playClip(Sound.CLIENT_DISCONNECTS, executor);
			}
		}
	}
}
