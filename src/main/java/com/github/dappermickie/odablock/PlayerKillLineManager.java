package com.github.dappermickie.odablock;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayerKillLineManager
{
	private static final HttpUrl RAW_GITHUB = HttpUrl.parse("https://raw.githubusercontent.com/dappermickie/odablock-sounds/playerkillpatterns");
	private static final String USERS_FILENAME = "pklines.txt";
	private static volatile Pattern[] patterns = new Pattern[0];

	public static void setup(final OkHttpClient okHttpClient)
	{
		HttpUrl usersUrl = RAW_GITHUB.newBuilder().addPathSegment(USERS_FILENAME).build();
		try (Response res = okHttpClient.newCall(new Request.Builder().url(usersUrl).build()).execute())
		{
			if (res.isSuccessful() && res.body() != null)
			{
				List<Pattern> loaded = new ArrayList<>();
				for (String line : res.body().string().split("\n"))
				{
					if (!line.trim().isEmpty())
					{
						try
						{
							loaded.add(Pattern.compile(line.trim()));
						}
						catch (PatternSyntaxException ignored)
						{
							// Keep other valid patterns when the remote list contains a bad entry.
						}
					}
				}
				patterns = loaded.toArray(new Pattern[0]);
			}
		}
		catch (IOException e)
		{
			return;
		}
	}

	public static Pattern[] getPatterns()
	{
		return patterns;
	}
}
