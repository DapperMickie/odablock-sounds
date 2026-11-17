package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import com.github.dappermickie.odablock.SoundIds;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.SoundEffectPlayed;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
@Slf4j
public class RubyBoltProc
{

	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		if (!config.rubyBoltProc() || event.getSoundId() != SoundIds.RUBY_BOLT_PROC.Id)
		{
			return;
		}

		final Player local = client.getLocalPlayer();
		if (local == null || (config.ownPlayerOnly()
			&& event.getSource() != null && event.getSource() != local))
		{
			return;
		}

		event.consume();
		soundEngine.playClip(Sound.RUBY_PROC, executor);
	}
}
