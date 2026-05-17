package com.github.dappermickie.odablock.overrides;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundFileManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

@Singleton
@Slf4j
public class SoundOverrideService
{
	private static final Type RAW_OVERRIDES_TYPE = new TypeToken<Map<String, List<String>>>()
	{
	}.getType();

	@Inject
	private ConfigManager configManager;

	@Inject
	private Gson gson;

	public Optional<File> getRandomOverrideFile(final SoundOverrideAction action)
	{
		final Set<String> selectedKeys = getOverrideFileNames(action);
		if (selectedKeys.isEmpty())
		{
			return Optional.empty();
		}

		List<File> validOverrideFiles = new ArrayList<>();
		for (String storageKey : selectedKeys)
		{
			File resolved = resolveStorageKey(storageKey, action);
			if (resolved != null)
			{
				validOverrideFiles.add(resolved);
			}
		}
		if (validOverrideFiles.isEmpty())
		{
			return Optional.empty();
		}

		int selectedIndex = ThreadLocalRandom.current().nextInt(validOverrideFiles.size());
		return Optional.of(validOverrideFiles.get(selectedIndex));
	}

	public Set<String> getOverrideFileNames(final SoundOverrideAction action)
	{
		Map<String, LinkedHashSet<String>> overridePools = loadOverridePools();
		LinkedHashSet<String> rawValues = overridePools.getOrDefault(action.getKey(), new LinkedHashSet<>());
		LinkedHashSet<String> normalized = new LinkedHashSet<>();
		for (String value : rawValues)
		{
			normalized.add(promoteLegacyKey(value, action));
		}
		return Collections.unmodifiableSet(normalized);
	}

	public void setOverrideFileNames(final SoundOverrideAction action, final Collection<String> storageKeys)
	{
		Map<String, LinkedHashSet<String>> overridePools = loadOverridePools();
		LinkedHashSet<String> sanitized = storageKeys.stream()
			.filter(name -> name != null && !name.trim().isEmpty())
			.map(String::trim)
			.collect(Collectors.toCollection(LinkedHashSet::new));

		// If the user's selection is empty or matches the action's default sounds exactly,
		// there is no real override to persist; treat both cases as "clear the override".
		// This avoids saving a no-op override when a user opens the picker, keeps the
		// pre-checked defaults, and backs out.
		if (sanitized.isEmpty() || sanitized.equals(getDefaultStorageKeys(action, false)))
		{
			overridePools.remove(action.getKey());
		}
		else
		{
			overridePools.put(action.getKey(), sanitized);
		}
		writeOverridePools(overridePools);
	}

	public void clearOverrideFileNames(final SoundOverrideAction action)
	{
		Map<String, LinkedHashSet<String>> overridePools = loadOverridePools();
		overridePools.remove(action.getKey());
		writeOverridePools(overridePools);
	}

	public void clearAllOverrides()
	{
		writeOverridePools(new LinkedHashMap<>());
	}

	public String exportOverridesAsJson()
	{
		Map<String, LinkedHashSet<String>> pools = loadOverridePools();
		Map<String, List<String>> export = new LinkedHashMap<>();
		for (SoundOverrideAction action : SoundOverrideAction.values())
		{
			Set<String> keys = pools.get(action.getKey());
			if (keys == null || keys.isEmpty())
			{
				continue;
			}
			export.put(action.getKey(), new ArrayList<>(keys));
		}
		return gson.toJson(export);
	}

	public ImportResult importOverridesFromJson(final String json) throws JsonSyntaxException
	{
		Map<String, List<String>> incoming = parseOverrideJson(json);
		return applyImportedOverrides(incoming, false);
	}

	public ImportResult replaceAllOverridesFromJson(final String json) throws JsonSyntaxException
	{
		Map<String, List<String>> incoming = parseOverrideJson(json);
		return applyImportedOverrides(incoming, true);
	}

	private ImportResult applyImportedOverrides(final Map<String, List<String>> incoming, final boolean replaceAll)
	{
		Map<String, SoundOverrideAction> knownActions = new LinkedHashMap<>();
		for (SoundOverrideAction action : SoundOverrideAction.values())
		{
			knownActions.put(action.getKey(), action);
		}

		Map<String, LinkedHashSet<String>> currentPools = replaceAll ? new LinkedHashMap<>() : loadOverridePools();
		int importedActions = 0;
		int skippedEntries = 0;
		int skippedActions = 0;

		for (Map.Entry<String, List<String>> entry : incoming.entrySet())
		{
			SoundOverrideAction action = knownActions.get(entry.getKey());
			if (action == null)
			{
				skippedActions++;
				continue;
			}

			List<String> rawValues = entry.getValue();
			LinkedHashSet<String> sanitized = new LinkedHashSet<>();
			if (rawValues != null)
			{
				for (String value : rawValues)
				{
					if (value == null || value.trim().isEmpty())
					{
						skippedEntries++;
						continue;
					}
					String trimmed = value.trim();
					String promoted = promoteLegacyKey(trimmed, action);
					sanitized.add(promoted);
				}
			}

			if (sanitized.isEmpty())
			{
				currentPools.remove(action.getKey());
			}
			else
			{
				currentPools.put(action.getKey(), sanitized);
			}
			importedActions++;
		}

		writeOverridePools(currentPools);
		return new ImportResult(importedActions, skippedActions, skippedEntries);
	}

	private Map<String, List<String>> parseOverrideJson(final String json) throws JsonSyntaxException
	{
		if (json == null || json.trim().isEmpty())
		{
			throw new JsonSyntaxException("Override JSON is empty.");
		}

		Map<String, List<String>> incoming = gson.fromJson(json, RAW_OVERRIDES_TYPE);
		if (incoming == null)
		{
			throw new JsonSyntaxException("Override JSON did not parse to an object.");
		}
		return incoming;
	}

	public static final class ImportResult
	{
		private final int importedActions;
		private final int skippedActions;
		private final int skippedEntries;

		public ImportResult(int importedActions, int skippedActions, int skippedEntries)
		{
			this.importedActions = importedActions;
			this.skippedActions = skippedActions;
			this.skippedEntries = skippedEntries;
		}

		public int getImportedActions()
		{
			return importedActions;
		}

		public int getSkippedActions()
		{
			return skippedActions;
		}

		public int getSkippedEntries()
		{
			return skippedEntries;
		}
	}

	public Set<String> getDefaultStorageKeys(final SoundOverrideAction action, final boolean refreshCache)
	{
		String actionDirectory = action.getDefaultSound().getDirectory();
		List<File> defaultFiles = SoundFileManager.listFilesInDirectory(actionDirectory, refreshCache);
		LinkedHashSet<String> keys = new LinkedHashSet<>();
		for (File file : defaultFiles)
		{
			keys.add(actionDirectory + "/" + file.getName());
		}
		return keys;
	}

	public List<SoundOverrideOption> getAllSoundOptions(final SoundOverrideAction action, final boolean refreshCache)
	{
		Set<String> uniqueDirectories = new TreeSet<>();
		for (Sound sound : Sound.values())
		{
			uniqueDirectories.add(sound.getDirectory());
		}

		String defaultDirectory = action.getDefaultSound().getDirectory();
		List<SoundOverrideOption> options = new ArrayList<>();

		for (String directory : uniqueDirectories)
		{
			List<File> filesInDir = SoundFileManager.listFilesInDirectory(directory, refreshCache);
			if (filesInDir.isEmpty())
			{
				continue;
			}

			Map<String, List<File>> filesByLabel = new LinkedHashMap<>();
			for (File file : filesInDir)
			{
				String normalized = SoundLabelNormalizer.normalize(file.getName());
				filesByLabel.computeIfAbsent(normalized, ignored -> new ArrayList<>()).add(file);
			}

			boolean isDefaultDir = directory.equals(defaultDirectory);

			for (Map.Entry<String, List<File>> entry : filesByLabel.entrySet())
			{
				String normalizedLabel = entry.getKey();
				List<File> files = entry.getValue();
				boolean hasDuplicates = files.size() > 1;
				files.sort((left, right) -> left.getName().toLowerCase(Locale.ENGLISH)
					.compareTo(right.getName().toLowerCase(Locale.ENGLISH)));

				for (File file : files)
				{
					String displayLabel = hasDuplicates
						? normalizedLabel + " (" + file.getName() + ")"
						: normalizedLabel;
					options.add(new SoundOverrideOption(
						directory,
						file.getName(),
						displayLabel,
						file,
						isDefaultDir
					));
				}
			}
		}

		options.sort((left, right) -> {
			// defaults first, then alphabetical
			if (left.isDefaultForAction() != right.isDefaultForAction())
			{
				return left.isDefaultForAction() ? -1 : 1;
			}
			return left.getDisplayLabel().toLowerCase(Locale.ENGLISH)
				.compareTo(right.getDisplayLabel().toLowerCase(Locale.ENGLISH));
		});
		return options;
	}

	private File resolveStorageKey(String storageKey, SoundOverrideAction action)
	{
		if (storageKey == null || storageKey.isEmpty())
		{
			return null;
		}
		int separatorIndex = storageKey.indexOf('/');
		String directory;
		String fileName;
		if (separatorIndex < 0)
		{
			// Legacy: filename only -> assume action's own directory
			directory = action.getDefaultSound().getDirectory();
			fileName = storageKey;
		}
		else
		{
			directory = storageKey.substring(0, separatorIndex);
			fileName = storageKey.substring(separatorIndex + 1);
		}
		return SoundFileManager.lookupFile(directory, fileName);
	}

	private String promoteLegacyKey(String storageKey, SoundOverrideAction action)
	{
		if (storageKey == null || storageKey.isEmpty() || storageKey.contains("/"))
		{
			return storageKey;
		}
		return action.getDefaultSound().getDirectory() + "/" + storageKey;
	}

	private Map<String, LinkedHashSet<String>> loadOverridePools()
	{
		String rawOverrideJson = configManager.getConfiguration(OdablockConfig.CONFIG_GROUP, OdablockConfig.SOUND_OVERRIDE_POOLS_KEY);
		if (rawOverrideJson == null || rawOverrideJson.trim().isEmpty())
		{
			return new LinkedHashMap<>();
		}

		try
		{
			Map<String, List<String>> rawPools = gson.fromJson(rawOverrideJson, RAW_OVERRIDES_TYPE);
			if (rawPools == null || rawPools.isEmpty())
			{
				return new LinkedHashMap<>();
			}

			Map<String, LinkedHashSet<String>> parsedPools = new LinkedHashMap<>();
			for (Map.Entry<String, List<String>> entry : rawPools.entrySet())
			{
				LinkedHashSet<String> parsedValues = entry.getValue() == null
					? new LinkedHashSet<>()
					: entry.getValue().stream()
						.filter(name -> name != null && !name.trim().isEmpty())
						.map(String::trim)
						.collect(Collectors.toCollection(LinkedHashSet::new));
				if (!parsedValues.isEmpty())
				{
					parsedPools.put(entry.getKey(), parsedValues);
				}
			}
			return parsedPools;
		}
		catch (JsonSyntaxException exception)
		{
			log.warn("Failed to parse sound override pools. Resetting overrides cache.", exception);
			return new LinkedHashMap<>();
		}
	}

	private void writeOverridePools(final Map<String, LinkedHashSet<String>> pools)
	{
		Map<String, List<String>> serialized = new LinkedHashMap<>();
		for (SoundOverrideAction action : SoundOverrideAction.values())
		{
			Set<String> keys = pools.get(action.getKey());
			if (keys == null || keys.isEmpty())
			{
				continue;
			}
			serialized.put(action.getKey(), new ArrayList<>(keys));
		}

		configManager.setConfiguration(
			OdablockConfig.CONFIG_GROUP,
			OdablockConfig.SOUND_OVERRIDE_POOLS_KEY,
			gson.toJson(serialized)
		);
	}
}
