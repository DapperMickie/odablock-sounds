package com.github.dappermickie.odablock;

import java.io.FileNotFoundException;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
@Slf4j
public class SoundEngine
{
	@Inject
	private OdablockConfig config;

	@Inject
	private AudioPlayer audioPlayer;

	public void playClip(Sound sound, Executor executor)
	{
		executor.execute(() -> playClip(sound));
	}

	public void playClip(Sound sound, ScheduledExecutorService executor, Duration initialDelay)
	{
		executor.schedule(() -> playClip(sound), initialDelay.toMillis(), TimeUnit.MILLISECONDS);
	}

	private void playClip(Sound sound)
	{
		if (SoundFileManager.getIsUpdating())
		{
			return;
		}

		float gain = 20f * (float) Math.log10(config.announcementVolume() / 100f);
		try
		{
			audioPlayer.play(SoundFileManager.getSoundStream(sound), gain);
		}
		catch (FileNotFoundException e)
		{
			log.warn("Sound file not found for " + sound, e);
		}
		catch (IOException e)
		{
			log.warn("Failed to play Odablock sound " + sound, e);
		}
		catch (UnsupportedAudioFileException e)
		{
			log.warn("Failed to play Odablock sound " + sound, e);
		}
		catch (LineUnavailableException e)
		{
			log.warn("Failed to play Odablock sound " + sound, e);
		}
	}

	public void close()
	{
		// No cleanup needed for AudioPlayer
	}
}