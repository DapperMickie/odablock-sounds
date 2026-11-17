package com.github.dappermickie.odablock;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.util.Text;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public abstract class SnowballUserManager
{

	private static final File DOWNLOAD_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "odablock-users");
	private static final HttpUrl RAW_GITHUB = HttpUrl.parse("https://raw.githubusercontent.com/dappermickie/odablock-sounds/snowball");
	private static final String USERVERSION_FILENAME = "USERVERSION";
	private static final String USERS_FILENAME = "users.txt";

	private static volatile String[] users;

	public static void ensureDownloadDirectoryExists()
	{
		if (!DOWNLOAD_DIR.exists())
		{
			DOWNLOAD_DIR.mkdirs();
		}
	}

	public static synchronized void downloadSnowballUsers(final OkHttpClient okHttpClient)
	{
		ensureDownloadDirectoryExists();
		HttpUrl versionUrl = RAW_GITHUB.newBuilder().addPathSegment(USERVERSION_FILENAME).build();
		Path temporary = null;
		try
		{
			int latestVersion;
			try (Response res = okHttpClient.newCall(new Request.Builder().url(versionUrl).build()).execute())
			{
				if (!res.isSuccessful() || res.body() == null)
				{
					return;
				}
				latestVersion = Integer.parseInt(res.body().string().trim());
			}
			int currentVersion = -1;
			try
			{
				currentVersion = getUserVersion();
			}
			catch (IOException | NumberFormatException ignored)
			{
			}
			Path destination = DOWNLOAD_DIR.toPath().resolve(USERS_FILENAME);
			if (latestVersion == currentVersion && Files.isRegularFile(destination))
			{
				return;
			}
			HttpUrl usersUrl = RAW_GITHUB.newBuilder().addPathSegment(USERS_FILENAME).build();
			try (Response res = okHttpClient.newCall(new Request.Builder().url(usersUrl).build()).execute())
			{
				if (!res.isSuccessful() || res.body() == null)
				{
					return;
				}
				temporary = Files.createTempFile(DOWNLOAD_DIR.toPath(), "users-", ".tmp");
				Files.copy(res.body().byteStream(), temporary, StandardCopyOption.REPLACE_EXISTING);
				Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
				users = null;
				writeLatestVersion(latestVersion);
			}
		}
		catch (IOException | NumberFormatException e)
		{
			log.warn("Odablock Plugin could not update snowball users", e);
		}
		finally
		{
			if (temporary != null)
			{
				try { Files.deleteIfExists(temporary); }
				catch (IOException e) { log.debug("Could not remove temporary user download", e); }
			}
		}
	}

	public static String[] getUsers()
	{
		if (SnowballUserManager.users != null)
		{
			return SnowballUserManager.users;
		}
		File userVersionFile = new File(DOWNLOAD_DIR, USERS_FILENAME);
		try
		{
			String userVersionFileContent = Files.readString(userVersionFile.toPath());
			String[] originalUsers = userVersionFileContent.split(",");
			String[] users = new String[originalUsers.length];

			for (int i = 0; i < originalUsers.length; i++)
			{
				users[i] = Text.standardize(originalUsers[i]);
			}

			SnowballUserManager.users = users;

			return users;
		}
		catch (IOException e)
		{
			return new String[0];
		}
	}

	private static int getUserVersion() throws IOException
	{
		File userVersionFile = new File(DOWNLOAD_DIR, USERVERSION_FILENAME);
		String userVersionFileContent = Files.readString(userVersionFile.toPath());
		return Integer.parseInt(userVersionFileContent.trim());
	}

	private static void writeLatestVersion(int version)
	{
		var soundVersionFile = new File(DOWNLOAD_DIR, USERVERSION_FILENAME);
		try
		{
			FileWriter myWriter = new FileWriter(soundVersionFile);
			myWriter.write(String.valueOf(version));
			myWriter.close();
		}
		catch (IOException e)
		{
			log.error("Couldn't write latest userversion");
		}
	}
}
