package com.github.dappermickie.odablock;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.util.Text;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class SoundFileManager
{

	private static final File DOWNLOAD_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "odablock-sounds");
	private static final String DELETE_WARNING_FILENAME = "EXTRA_FILES_WILL_BE_DELETED_BUT_FOLDERS_WILL_REMAIN";
	private static final String SOUNDVERSION_FILENAME = "SOUNDVERSION";
	private static final File DELETE_WARNING_FILE = new File(DOWNLOAD_DIR, DELETE_WARNING_FILENAME);
	private static final HttpUrl RAW_GITHUB = HttpUrl.parse("https://raw.githubusercontent.com/dappermickie/odablock-sounds/sounds-v2");

	private static boolean isUpdating = false;

	private static Map<String, String[]> soundDirectoryMap = new HashMap<>();

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void ensureDownloadDirectoryExists()
	{
		if (!DOWNLOAD_DIR.exists())
		{
			DOWNLOAD_DIR.mkdirs();
		}
		try
		{
			DELETE_WARNING_FILE.createNewFile();
		}
		catch (IOException ignored)
		{
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void ensureSoundDirectoryExists(File soundDirectory)
	{
		if (!soundDirectory.exists())
		{
			soundDirectory.mkdirs();
		}
		File customDirectory = new File(soundDirectory, "custom");
		if (!customDirectory.exists())
		{
			customDirectory.mkdirs();
		}
		try
		{
			soundDirectory.createNewFile();
		}
		catch (IOException ignored)
		{
		}
	}

	public static void downloadAllMissingSounds(final OkHttpClient okHttpClient)
	{
		// Get set of existing files in our dir - existing sounds will be skipped, unexpected files (not dirs, some sounds depending on config) will be deleted


		HttpUrl versionUrl = RAW_GITHUB.newBuilder().addPathSegment(SOUNDVERSION_FILENAME).build();
		int latestVersion = -1;
		try (Response res = okHttpClient.newCall(new Request.Builder().url(versionUrl).build()).execute())
		{
			if (res.body() != null)
			{
				latestVersion = Integer.parseInt(Text.standardize(res.body().string()));
			}
		}
		catch (IOException e)
		{
			log.error("Odablock Plugin could not download sound version", e);
			return;
		}

		int currentVersion = -1;
		try
		{
			currentVersion = getSoundVersion();
		}
		catch (IOException e)
		{
			// No current version available
			var soundVersionFile = new File(DOWNLOAD_DIR, SOUNDVERSION_FILENAME);
			try
			{
				soundVersionFile.createNewFile();
			}
			catch (IOException e2)
			{
				log.error("Couldn't create soundversion file");
			}
		}

		if (latestVersion == currentVersion)
		{
			return;
		}
		isUpdating = true;
		writeLatestVersion(latestVersion);

		for (String filename : getFilesPresent(DOWNLOAD_DIR))
		{
			File toDelete = new File(DOWNLOAD_DIR, filename);
			//noinspection ResultOfMethodCallIgnored
			toDelete.delete();
			log.warn("Odablock plugin deleted " + filename);
		}

		List<String> cleanedDirectories = new ArrayList<>();

		// Download any sounds that are not yet present but desired
		for (Sound sound : getDesiredSoundList())
		{
			String soundDirectory = sound.getDirectory();
			String fileNameToDownload = sound.getResourceName();
			File soundDirectoryFile = new File(DOWNLOAD_DIR, soundDirectory);

			ensureSoundDirectoryExists(soundDirectoryFile);

			if (!cleanedDirectories.contains(soundDirectory))
			{
				Set<String> filesPresent = getFilesPresent(soundDirectoryFile);

				for (String filename : filesPresent)
				{
					File toDelete = new File(soundDirectoryFile, filename);
					//noinspection ResultOfMethodCallIgnored
					toDelete.delete();
					log.warn("Odablock plugin deleted " + filename);
				}

				cleanedDirectories.add(soundDirectory);
			}

			if (RAW_GITHUB == null)
			{
				// Hush intellij, it's okay, the potential NPE can't hurt you now
				log.error("Odablock Plugin could not download sounds due to an unexpected null RAW_GITHUB value");
				isUpdating = false;
				return;
			}
			HttpUrl soundUrl = RAW_GITHUB.newBuilder().addPathSegment(soundDirectory).addPathSegment(fileNameToDownload).build();
			Path outputPath = Paths.get(soundDirectoryFile.getPath(), fileNameToDownload);
			try (Response res = okHttpClient.newCall(new Request.Builder().url(soundUrl).build()).execute())
			{
				if (res.body() != null)
				{
					Files.copy(new BufferedInputStream(res.body().byteStream()), outputPath, StandardCopyOption.REPLACE_EXISTING);
					log.warn("Odablock plugin downloaded " + fileNameToDownload);
				}
			}
			catch (IOException e)
			{
				log.error("Odablock Plugin could not download sounds", e);
				isUpdating = false;
				return;
			}
		}

		isUpdating = false;
	}

	private static Set<String> getFilesPresent(File directory)
	{
		File[] downloadDirFiles = directory.listFiles();
		if (downloadDirFiles == null || downloadDirFiles.length == 0)
		{
			return new HashSet<>();
		}

		return Arrays.stream(downloadDirFiles)
			.filter(file -> !file.isDirectory())
			.map(File::getName)
			.filter(filename -> !DELETE_WARNING_FILENAME.equals(filename))
			.filter(filename -> !SOUNDVERSION_FILENAME.equals(filename))
			.collect(Collectors.toSet());
	}

	private static Set<Sound> getDesiredSoundList()
	{
		return Arrays.stream(Sound.values())
			.collect(Collectors.toSet());
	}

	public static InputStream getSoundStream(Sound sound) throws FileNotFoundException
	{
		if (!soundDirectoryMap.containsKey(sound.getDirectory()))
		{
			File soundDirectoryPath = Paths.get(DOWNLOAD_DIR.getPath(), sound.getDirectory()).toFile();
			File customSoundDirectoryPath = Paths.get(soundDirectoryPath.getPath(), "custom").toFile();

			File[] files = customSoundDirectoryPath.listFiles();
			if (files == null || files.length == 0) {
				files = soundDirectoryPath.listFiles();
			}

			if (files == null || files.length == 0) {
				return null;
			}

			var soundFileArray = Arrays.stream(files)
				.filter(file -> !file.isDirectory())
				.map(File::getAbsolutePath)
				.collect(Collectors.toSet())
				.toArray(new String[0]);

			soundDirectoryMap.put(sound.getDirectory(), soundFileArray);
		}
		String soundFile = RandomSoundUtility.getRandomSound(soundDirectoryMap.get(sound.getDirectory()));
		return new FileInputStream(soundFile);
	}

	public static int getSoundVersion() throws IOException
	{
		File soundVersionFile = new File(DOWNLOAD_DIR, SOUNDVERSION_FILENAME);
		String soundVersionContent = Files.readString(soundVersionFile.toPath());
		return Integer.parseInt(soundVersionContent);
	}

	public static boolean getIsUpdating()
	{
		return isUpdating;
	}

	private static void writeLatestVersion(int version)
	{
		var soundVersionFile = new File(DOWNLOAD_DIR, SOUNDVERSION_FILENAME);
		try
		{
			FileWriter myWriter = new FileWriter(soundVersionFile);
			myWriter.write(String.valueOf(version));
			myWriter.close();
		}
		catch (IOException e)
		{
			log.error("Couldn't write latest soundversion");
		}
	}
}