package com.github.dappermickie.odablock.overrides;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SoundLabelNormalizer
{
	private static final Pattern TRAILING_REVISION = Pattern.compile("^r(\\d+)$", Pattern.CASE_INSENSITIVE);
	private static final Set<String> ACRONYMS = new HashSet<>(Arrays.asList(
		"ACB", "AGS", "DDS", "DH", "TOA", "TOB", "COX", "PK", "HCIM", "PVP", "PVM", "NPC", "OSRS"
	));

	private SoundLabelNormalizer()
	{
	}

	public static String normalize(final String fileName)
	{
		String withoutExtension = fileName;
		int extensionIndex = fileName.lastIndexOf('.');
		if (extensionIndex > 0)
		{
			withoutExtension = fileName.substring(0, extensionIndex);
		}

		String withSpaces = withoutExtension
			.replace('_', ' ')
			.replace('-', ' ')
			.replaceAll("([a-z])([A-Z])", "$1 $2")
			.replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
			.replaceAll("\\s+", " ")
			.trim();

		if (withSpaces.isEmpty())
		{
			return fileName;
		}

		String[] tokens = withSpaces.split(" ");
		StringBuilder label = new StringBuilder();

		for (String token : tokens)
		{
			if (token.isEmpty())
			{
				continue;
			}

			Matcher revisionMatcher = TRAILING_REVISION.matcher(token);
			String normalizedToken;
			if (revisionMatcher.matches())
			{
				normalizedToken = revisionMatcher.group(1);
			}
			else
			{
				String uppercase = token.toUpperCase(Locale.ENGLISH);
				if (ACRONYMS.contains(uppercase))
				{
					normalizedToken = uppercase;
				}
				else if (token.matches("\\d+"))
				{
					normalizedToken = token;
				}
				else if (token.length() == 1)
				{
					normalizedToken = token.toUpperCase(Locale.ENGLISH);
				}
				else
				{
					normalizedToken = token.substring(0, 1).toUpperCase(Locale.ENGLISH)
						+ token.substring(1).toLowerCase(Locale.ENGLISH);
				}
			}

			if (label.length() > 0)
			{
				label.append(' ');
			}
			label.append(normalizedToken);
		}

		return label.toString();
	}
}
