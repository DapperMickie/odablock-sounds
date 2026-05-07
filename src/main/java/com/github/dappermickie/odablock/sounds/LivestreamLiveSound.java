package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LivestreamLiveSound
{
	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	public void playSound()
	{
		soundEngine.playClip(Sound.GAMON_GO_LIVE, executor);
	}
}
