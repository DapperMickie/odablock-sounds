package com.github.dappermickie.odablock;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
public abstract class SoundFileManager
{

	private static final File DOWNLOAD_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "odablock-sounds");
	public static final String CUSTOM_DIRECTORY = "custom";
	private static final File CUSTOM_DIR = new File(DOWNLOAD_DIR, CUSTOM_DIRECTORY);
	private static final String DELETE_WARNING_FILENAME = "EXTRA_FILES_WILL_BE_DELETED_BUT_FOLDERS_WILL_REMAIN";
	private static final String SOUNDVERSION_FILENAME = "SOUNDVERSION";
	private static final File DELETE_WARNING_FILE = new File(DOWNLOAD_DIR, DELETE_WARNING_FILENAME);
	private static final HttpUrl RAW_GITHUB = HttpUrl.parse("https://raw.githubusercontent.com/dappermickie/odablock-sounds/sounds-v3");

	static final List<String> BARROWS_FACE_FILES = List.of(
		"oda_face_1.png", "oda_face_2.png", "oda_face_3.png", "oda_face_5.png",
		"oda_face_6.png", "oda_face_7.png", "torvesta_face.png");

	private static volatile boolean isUpdating = false;

	private static final Map<String, String[]> soundDirectoryMap = new ConcurrentHashMap<>();

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void ensureDownloadDirectoryExists()
	{
		if (!DOWNLOAD_DIR.exists())
		{
			DOWNLOAD_DIR.mkdirs();
		}
		if (!CUSTOM_DIR.exists())
		{
			CUSTOM_DIR.mkdirs();
		}
		try
		{
			DELETE_WARNING_FILE.createNewFile();
		}
		catch (IOException ignored)
		{
		}
	}

	public static synchronized void downloadAllMissingSounds(final OkHttpClient okHttpClient)
	{
		ensureDownloadDirectoryExists();
		downloadAllMissingSounds(okHttpClient, DOWNLOAD_DIR.toPath());
	}

	static void downloadAllMissingSounds(OkHttpClient okHttpClient, Path directory)
	{
		int latestVersion = getLatestVersion(okHttpClient, RAW_GITHUB.newBuilder()
			.addPathSegment(SOUNDVERSION_FILENAME).build());
		if (latestVersion < 0)
		{
			return;
		}

		boolean refresh = latestVersion != getCurrentVersion(directory);
		isUpdating = true;
		try
		{
			boolean complete = true;
			for (Sound sound : Sound.values())
			{
				Path destination = directory.resolve(sound.getDirectory()).resolve(sound.getResourceName());
				if (refresh || !isValidWav(destination))
				{
					// One unavailable addition must not prevent the rest of the catalog downloading.
					complete &= downloadSound(sound, destination, okHttpClient);
				}
			}
			if (complete)
			{
				writeLatestVersion(directory, latestVersion);
			}
		}
		finally
		{
			clearAllSoundDirectoryCaches();
			isUpdating = false;
		}
	}

	private static int getCurrentVersion(Path directory)
	{
		try
		{
			return Integer.parseInt(Files.readString(directory.resolve(SOUNDVERSION_FILENAME)).trim());
		}
		catch (IOException | NumberFormatException e)
		{
			return -1;
		}
	}

	private static int getLatestVersion(OkHttpClient okHttpClient, HttpUrl versionUrl)
	{
		try (Response res = okHttpClient.newCall(new Request.Builder().url(versionUrl).build()).execute())
		{
			if (res.isSuccessful() && res.body() != null)
			{
				return Integer.parseInt(res.body().string().trim());
			}
		}
		catch (IOException | NumberFormatException e)
		{
			log.warn("Odablock Plugin could not download sound version", e);
		}
		return -1;
	}

	private static boolean downloadSound(Sound sound, Path destination, OkHttpClient okHttpClient)
	{
		return downloadAsset(sound.getDirectory(), sound.getResourceName(), destination, okHttpClient, false);
	}

	public static void downloadMissingBarrowsFaces(OkHttpClient okHttpClient)
	{
		downloadMissingBarrowsFaces(okHttpClient, DOWNLOAD_DIR.toPath());
	}

	static void downloadMissingBarrowsFaces(OkHttpClient okHttpClient, Path directory)
	{
		for (String name : BARROWS_FACE_FILES)
		{
			Path destination = directory.resolve("odafaces").resolve(name);
			if (!isValidImage(destination))
			{
				downloadAsset("odafaces", name, destination, okHttpClient, true);
			}
		}
	}

	private static boolean isValidImage(Path path)
	{
		try
		{
			return javax.imageio.ImageIO.read(path.toFile()) != null;
		}
		catch (IOException e)
		{
			return false;
		}
	}

	private static boolean downloadAsset(String directory, String name, Path destination,
		OkHttpClient okHttpClient, boolean image)
	{
		HttpUrl soundUrl = RAW_GITHUB.newBuilder().addPathSegment(directory).addPathSegment(name).build();
		Path temporary = null;
		try (Response res = okHttpClient.newCall(new Request.Builder().url(soundUrl).build()).execute())
		{
			if (!res.isSuccessful() || res.body() == null)
			{
				log.warn("Could not download {} (HTTP {})", name, res.code());
				return false;
			}
			Files.createDirectories(destination.getParent());
			temporary = Files.createTempFile(destination.getParent(), "download-", ".tmp");
			Files.copy(res.body().byteStream(), temporary, StandardCopyOption.REPLACE_EXISTING);
			if (image ? !isValidImage(temporary) : !isValidWav(temporary))
			{
				log.warn("Downloaded file is not a valid {}: {}", image ? "image" : "WAV", name);
				return false;
			}
			// Keep working audio intact until its replacement has downloaded successfully.
			Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
			return true;
		}
		catch (IOException e)
		{
			log.warn("Odablock Plugin could not download {}", name, e);
			return false;
		}
		finally
		{
			if (temporary != null)
			{
				try
				{
					Files.deleteIfExists(temporary);
				}
				catch (IOException e)
				{
					log.debug("Could not remove temporary sound download", e);
				}
			}
		}
	}

	private static boolean isValidWav(Path path)
	{
		try
		{
			return javax.sound.sampled.AudioFileFormat.Type.WAVE.equals(
				javax.sound.sampled.AudioSystem.getAudioFileFormat(path.toFile()).getType());
		}
		catch (IOException | javax.sound.sampled.UnsupportedAudioFileException e)
		{
			return false;
		}
	}

	public static File getSoundStream(Sound sound) throws FileNotFoundException
	{
		String[] soundFileArray = getOrLoadSoundFiles(sound.getDirectory(), false);
		if (soundFileArray.length == 0)
		{
			return null;
		}
		return Paths.get(RandomSoundUtility.getRandomSound(soundFileArray)).toFile();
	}

	public static List<File> listCandidateSoundFiles(Sound sound)
	{
		return listCandidateSoundFiles(sound, false);
	}

	public static List<File> listCandidateSoundFiles(Sound sound, boolean refreshCache)
	{
		String[] soundFileArray = getOrLoadSoundFiles(sound.getDirectory(), refreshCache);
		if (soundFileArray.length == 0)
		{
			return Collections.emptyList();
		}

		return Arrays.stream(soundFileArray)
			.map(Paths::get)
			.map(Path::toFile)
			.collect(Collectors.toList());
	}

	public static void clearSoundDirectoryCache(Sound sound)
	{
		soundDirectoryMap.remove(sound.getDirectory());
	}

	public static void clearAllSoundDirectoryCaches()
	{
		soundDirectoryMap.clear();
	}

	public static List<File> listFilesInDirectory(String directory, boolean refreshCache)
	{
		String[] soundFileArray = getOrLoadSoundFiles(directory, refreshCache);
		if (soundFileArray.length == 0)
		{
			return Collections.emptyList();
		}
		return Arrays.stream(soundFileArray)
			.map(Paths::get)
			.map(Path::toFile)
			.collect(Collectors.toList());
	}

	public static File lookupFile(String directory, String fileName)
	{
		if (directory == null || fileName == null)
		{
			return null;
		}
		for (File file : listFilesInDirectory(directory, false))
		{
			if (fileName.equals(file.getName()))
			{
				return file;
			}
		}
		return null;
	}

	private static String[] getOrLoadSoundFiles(String directory, boolean refreshCache)
	{
		if (directory == null)
		{
			return new String[0];
		}

		if (refreshCache)
		{
			soundDirectoryMap.remove(directory);
		}

		String[] cached = soundDirectoryMap.computeIfAbsent(directory, key -> {
			String[] files = loadSoundFilesFromDisk(key);
			return files.length == 0 ? null : files;
		});
		return cached == null ? new String[0] : cached;
	}

	private static String[] loadSoundFilesFromDisk(String directory)
	{
		File soundDirectoryPath = Paths.get(DOWNLOAD_DIR.getPath(), directory).toFile();
		File customSoundDirectoryPath = Paths.get(soundDirectoryPath.getPath(), "custom").toFile();
		File[] files = customSoundDirectoryPath.listFiles(SoundFileManager::isSoundFile);
		if (files == null || files.length == 0)
		{
			files = soundDirectoryPath.listFiles(SoundFileManager::isSoundFile);
		}

		String[] soundFileArray = files == null
			? new String[0]
			: Arrays.stream(files)
				.filter(file -> !file.isDirectory())
				.map(File::getAbsolutePath)
				.distinct()
				.toArray(String[]::new);

		return soundFileArray;
	}

	private static boolean isSoundFile(File file)
	{
		return file.isFile() && file.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".wav");
	}

	public static int getSoundVersion() throws IOException
	{
		File soundVersionFile = new File(DOWNLOAD_DIR, SOUNDVERSION_FILENAME);
		String soundVersionContent = Files.readString(soundVersionFile.toPath());
		return Integer.parseInt(soundVersionContent.trim());
	}

	public static boolean getIsUpdating()
	{
		return isUpdating;
	}

	private static void writeLatestVersion(Path directory, int version)
	{
		try
		{
			Files.writeString(directory.resolve(SOUNDVERSION_FILENAME), Integer.toString(version));
		}
		catch (IOException e)
		{
			log.warn("Couldn't write latest sound version", e);
		}
	}
}
