package com.github.dappermickie.odablock;

import com.github.dappermickie.odablock.overrides.SoundOverrideAction;
import com.github.dappermickie.odablock.overrides.SoundOverrideService;
import java.io.File;
import java.io.FileNotFoundException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
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

	@Inject
	private SoundOverrideService soundOverrideService;

	public void playClip(Sound sound, Executor executor)
	{
		executor.execute(() -> playClipInternal(sound, null));
	}

	public void playClip(SoundOverrideAction action, Executor executor)
	{
		executor.execute(() -> playClipInternal(action.getDefaultSound(), action));
	}

	public void playClip(Sound sound, SoundOverrideAction action, Executor executor)
	{
		executor.execute(() -> playClipInternal(sound, action));
	}

	public void playClip(Sound sound, ScheduledExecutorService executor, Duration initialDelay)
	{
		executor.schedule(() -> playClipInternal(sound, null), initialDelay.toMillis(), TimeUnit.MILLISECONDS);
	}

	public void playFile(File file, Executor executor)
	{
		if (file == null)
		{
			return;
		}
		executor.execute(() -> playFileInternal(file));
	}

	private void playFileInternal(File file)
	{
		if (SoundFileManager.getIsUpdating())
		{
			return;
		}

		float gain = 20f * (float) Math.log10(config.announcementVolume() / 100f);
		try
		{
			audioPlayer.play(file, gain);
		}
		catch (IOException | UnsupportedAudioFileException | LineUnavailableException exception)
		{
			log.warn("Failed to preview sound {}", file.getName(), exception);
		}
	}

	private void playClipInternal(Sound sound, SoundOverrideAction action)
	{
		if (SoundFileManager.getIsUpdating())
		{
			return;
		}

		float gain = 20f * (float) Math.log10(config.announcementVolume() / 100f);
		try
		{
			File soundFile = action == null
				? SoundFileManager.getSoundStream(sound)
				: soundOverrideService.getRandomOverrideFile(action).orElseGet(() -> {
					try
					{
						return SoundFileManager.getSoundStream(sound);
					}
					catch (FileNotFoundException fileNotFoundException)
					{
						return null;
					}
				});
			if (soundFile == null)
			{
				log.warn("No audio file available for {}", action != null ? action.getDisplayName() : sound.name());
				return;
			}

			audioPlayer.play(soundFile, gain);
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