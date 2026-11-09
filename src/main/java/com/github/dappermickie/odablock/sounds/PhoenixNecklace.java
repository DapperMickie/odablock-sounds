package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
@Slf4j
public class PhoenixNecklace {
	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	public boolean onChatMessage(ChatMessage chatMessage) {
		if (config.phoenixNecklace() &&
				chatMessage.getMessage()
						.contains("Your phoenix necklace heals you, but is destroyed in the process.")) {
			soundEngine.playClip(Sound.PHOENIX_NECKLACE, executor);
			return true;
		}
		return false;
	}
}
