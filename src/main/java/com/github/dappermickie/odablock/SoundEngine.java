package com.github.dappermickie.odablock;

import com.github.dappermickie.odablock.overrides.SoundOverrideService;
import com.github.dappermickie.odablock.overrides.SoundPools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;

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

	private final Object managedClipLock = new Object();
	private Clip managedClip;
	private long managedRequest;
	private volatile boolean active = true;
	private volatile long lifecycle;

	public void playClip(Sound sound, Executor executor)
	{
		long session = lifecycle;
		executor.execute(() -> { if (active && lifecycle == session) playClipInternal(sound); });
	}

	public void playClip(Sound sound, ScheduledExecutorService executor, Duration initialDelay)
	{
		long session = lifecycle;
		executor.schedule(() -> { if (active && lifecycle == session) playClipInternal(sound); }, initialDelay.toMillis(), TimeUnit.MILLISECONDS);
	}

	public void playFile(File file, Executor executor)
	{
		if (file == null)
		{
			return;
		}
		long session = lifecycle;
		executor.execute(() -> { if (active && lifecycle == session) playFileInternal(file); });
	}

	private void playFileInternal(File file)
	{
		if (!active || config.announcementVolume() <= 0 || SoundFileManager.getIsUpdating())
		{
			return;
		}

		float gain = 20f * (float) Math.log10(config.announcementVolume() / 100f);
		try
		{
			audioPlayer.play(file, gain);
		}
		catch (IOException | UnsupportedAudioFileException | LineUnavailableException | IllegalArgumentException exception)
		{
			log.warn("Failed to preview sound {}", file.getName(), exception);
		}
	}

	private void playClipInternal(Sound sound)
	{
		if (!active || config.announcementVolume() <= 0 || SoundFileManager.getIsUpdating())
		{
			return;
		}

		float gain = 20f * (float) Math.log10(config.announcementVolume() / 100f);
		try
		{
			File soundFile = resolveSoundFile(sound).orElse(null);
			if (soundFile == null)
			{
				log.warn("No audio file available for {}", SoundPools.getDisplayName(sound.getDirectory()));
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
		catch (LineUnavailableException | IllegalArgumentException e)
		{
			log.warn("Failed to play Odablock sound " + sound, e);
		}
	}

	public void playManagedClip(Sound sound, Executor executor)
	{
		final long request;
		synchronized (managedClipLock)
		{
			request = ++managedRequest;
		}
		executor.execute(() -> playManagedClipInternal(sound, request));
	}

	public void stopManagedClip()
	{
		Clip previous;
		synchronized (managedClipLock)
		{
			++managedRequest;
			previous = managedClip;
			managedClip = null;
		}
		closeClip(previous);
	}

	private static void closeClip(Clip clip)
	{
		if (clip != null)
		{
			clip.stop();
			clip.close();
		}
	}

	private void playManagedClipInternal(Sound sound, long request)
	{
		synchronized (managedClipLock)
		{
			if (request != managedRequest || !active || config.announcementVolume() <= 0
				|| SoundFileManager.getIsUpdating())
			{
				return;
			}
		}

		Clip clip = null;
		boolean published = false;
		try
		{
			File soundFile = resolveSoundFile(sound).orElse(null);
			if (soundFile == null)
			{
				log.warn("No audio file available for {}", SoundPools.getDisplayName(sound.getDirectory()));
				return;
			}

			try (AudioInputStream stream = AudioSystem.getAudioInputStream(soundFile))
			{
				clip = AudioSystem.getClip();
				clip.open(stream);
			}
			try
			{
				var control = (javax.sound.sampled.FloatControl) clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
				float gain = 20f * (float) Math.log10(config.announcementVolume() / 100f);
				control.setValue(Math.max(control.getMinimum(), Math.min(control.getMaximum(), gain)));
			}
			catch (IllegalArgumentException e)
			{
				log.debug("Managed clip gain is not supported", e);
			}

			final Clip openedClip = clip;
			clip.addLineListener(event -> {
				if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP)
				{
					synchronized (managedClipLock)
					{
						if (managedClip == openedClip)
						{
							managedClip = null;
						}
					}
					openedClip.close();
				}
			});

			Clip previous;
			synchronized (managedClipLock)
			{
				// A stop, newer selection, or shutdown may arrive while audio is decoding.
				if (!active || request != managedRequest || config.announcementVolume() <= 0)
				{
					return;
				}
				previous = managedClip;
				managedClip = clip;
				clip.start();
				published = true;
			}
			closeClip(previous);
		}
		catch (IOException | UnsupportedAudioFileException | LineUnavailableException | IllegalArgumentException e)
		{
			log.warn("Failed to play managed Odablock sound {}", sound, e);
		}
		finally
		{
			if (!published && clip != null)
			{
				synchronized (managedClipLock)
				{
					if (managedClip == clip) managedClip = null;
				}
				clip.close();
			}
		}
	}

	private Optional<File> resolveSoundFile(Sound sound) throws FileNotFoundException
	{
		String poolDirectory = sound.getDirectory();
		File soundFile = soundOverrideService.getRandomOverrideFile(poolDirectory).orElseGet(() -> {
			try
			{
				return SoundFileManager.getSoundStream(sound);
			}
			catch (FileNotFoundException fileNotFoundException)
			{
				return null;
			}
		});

		return Optional.ofNullable(soundFile);
	}

	public void startUp()
	{
		active = true;
	}

	public void close()
	{
		active = false;
		lifecycle++;
		stopManagedClip();
	}
}
