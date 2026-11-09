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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
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

	public synchronized Optional<File> getRandomOverrideFile(final String poolDirectory)
	{
		final Set<String> selectedKeys = getOverrideFileNames(poolDirectory);
		if (selectedKeys.isEmpty())
		{
			return Optional.empty();
		}

		List<File> validOverrideFiles = new ArrayList<>();
		for (String storageKey : selectedKeys)
		{
			File resolved = resolveStorageKey(storageKey, poolDirectory);
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

	public synchronized Set<String> getOverrideFileNames(final String poolDirectory)
	{
		Map<String, LinkedHashSet<String>> overridePools = loadOverridePools();
		LinkedHashSet<String> rawValues = overridePools.getOrDefault(poolDirectory, new LinkedHashSet<>());
		LinkedHashSet<String> sanitized = sanitizeStorageKeys(
			poolDirectory,
			rawValues,
			false,
			new HashMap<>()
		);

		boolean changed = !sanitized.equals(rawValues);
		if (sanitized.isEmpty())
		{
			changed = overridePools.remove(poolDirectory) != null || changed;
		}
		else
		{
			overridePools.put(poolDirectory, sanitized);
		}

		if (changed)
		{
			writeOverridePools(overridePools);
		}

		return Collections.unmodifiableSet(sanitized);
	}

	public synchronized void setOverrideFileNames(final String poolDirectory, final Collection<String> storageKeys)
	{
		Map<String, LinkedHashSet<String>> overridePools = loadOverridePools();
		LinkedHashSet<String> sanitized = sanitizeStorageKeys(
			poolDirectory,
			storageKeys,
			false,
			new HashMap<>()
		);

		if (sanitized.isEmpty() || sanitized.equals(getDefaultStorageKeys(poolDirectory, false)))
		{
			overridePools.remove(poolDirectory);
		}
		else
		{
			overridePools.put(poolDirectory, sanitized);
		}
		writeOverridePools(overridePools);
	}

	public synchronized void sanitizePersistedOverrides(final boolean refreshCache)
	{
		Map<String, LinkedHashSet<String>> overridePools = loadOverridePools();
		Map<String, LinkedHashSet<String>> sanitizedPools = new LinkedHashMap<>();
		Map<String, Boolean> directoryHasFilesCache = new HashMap<>();
		Set<String> knownPools = new LinkedHashSet<>(SoundPools.allDirectories());
		boolean changed = false;

		for (Map.Entry<String, LinkedHashSet<String>> entry : overridePools.entrySet())
		{
			String poolDirectory = SoundPools.normalizePoolKey(entry.getKey());
			if (!knownPools.contains(poolDirectory))
			{
				changed = true;
				continue;
			}

			LinkedHashSet<String> sanitized = sanitizeStorageKeys(
				poolDirectory,
				entry.getValue(),
				refreshCache,
				directoryHasFilesCache
			);

			if (sanitized.isEmpty())
			{
				changed = true;
				continue;
			}

			sanitizedPools.put(poolDirectory, sanitized);
			if (!poolDirectory.equals(entry.getKey()) || !sanitized.equals(entry.getValue()))
			{
				changed = true;
			}
		}

		if (changed)
		{
			writeOverridePools(sanitizedPools);
		}
	}

	public synchronized void clearOverrideFileNames(final String poolDirectory)
	{
		Map<String, LinkedHashSet<String>> overridePools = loadOverridePools();
		overridePools.remove(poolDirectory);
		writeOverridePools(overridePools);
	}

	public synchronized void clearAllOverrides()
	{
		writeOverridePools(new LinkedHashMap<>());
	}

	public synchronized String exportOverridesAsJson()
	{
		Map<String, LinkedHashSet<String>> pools = loadOverridePools();
		Map<String, List<String>> export = new LinkedHashMap<>();
		for (Map.Entry<String, LinkedHashSet<String>> entry : pools.entrySet())
		{
			if (entry.getValue() == null || entry.getValue().isEmpty())
			{
				continue;
			}
			export.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		return gson.toJson(export);
	}

	public synchronized ImportResult importOverridesFromJson(final String json) throws JsonSyntaxException
	{
		Map<String, List<String>> incoming = parseOverrideJson(json);
		return applyImportedOverrides(incoming, false);
	}

	public synchronized ImportResult replaceAllOverridesFromJson(final String json) throws JsonSyntaxException
	{
		Map<String, List<String>> incoming = parseOverrideJson(json);
		return applyImportedOverrides(incoming, true);
	}

	private ImportResult applyImportedOverrides(final Map<String, List<String>> incoming, final boolean replaceAll)
	{
		Map<String, LinkedHashSet<String>> currentPools = replaceAll ? new LinkedHashMap<>() : loadOverridePools();
		int importedActions = 0;
		int skippedEntries = 0;
		int skippedActions = 0;

		for (Map.Entry<String, List<String>> entry : incoming.entrySet())
		{
			String poolDirectory = SoundPools.normalizePoolKey(entry.getKey());
			if (!SoundPools.allDirectories().contains(poolDirectory))
			{
				skippedActions++;
				continue;
			}

			List<String> rawValues = entry.getValue();
			if (rawValues != null)
			{
				for (String value : rawValues)
				{
					if (value == null || value.trim().isEmpty())
					{
						skippedEntries++;
					}
				}
			}
			LinkedHashSet<String> sanitized = sanitizeStorageKeys(
				poolDirectory,
				rawValues,
				true,
				new HashMap<>()
			);

			if (sanitized.isEmpty())
			{
				currentPools.remove(poolDirectory);
			}
			else
			{
				currentPools.put(poolDirectory, sanitized);
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

	public synchronized Set<String> getDefaultStorageKeys(final String poolDirectory, final boolean refreshCache)
	{
		List<File> defaultFiles = SoundFileManager.listFilesInDirectory(poolDirectory, refreshCache);
		LinkedHashSet<String> keys = new LinkedHashSet<>();
		for (File file : defaultFiles)
		{
			keys.add(poolDirectory + "/" + file.getName());
		}
		return keys;
	}

	public synchronized List<SoundOverrideOption> getAllSoundOptions(final String poolDirectory, final boolean refreshCache)
	{
		Set<String> uniqueDirectories = new TreeSet<>();
		for (Sound sound : Sound.values())
		{
			uniqueDirectories.add(sound.getDirectory());
		}
		uniqueDirectories.add(SoundFileManager.CUSTOM_DIRECTORY);

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

			boolean isDefaultDir = directory.equals(poolDirectory);

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
			if (left.isDefaultForAction() != right.isDefaultForAction())
			{
				return left.isDefaultForAction() ? -1 : 1;
			}
			return SoundPools.compareDisplayLabels(
				left.getDisplayLabel(),
				right.getDisplayLabel()
			);
		});
		return options;
	}

	private File resolveStorageKey(String storageKey, String poolDirectory)
	{
		StorageKeyParts keyParts = parseStorageKey(storageKey, poolDirectory);
		if (keyParts == null)
		{
			return null;
		}
		return SoundFileManager.lookupFile(keyParts.directory, keyParts.fileName);
	}

	private String promoteLegacyKey(String storageKey, String poolDirectory)
	{
		if (storageKey == null || storageKey.isEmpty() || storageKey.contains("/"))
		{
			return storageKey;
		}
		return poolDirectory + "/" + storageKey;
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
			Map<String, Boolean> directoryHasFilesCache = new HashMap<>();
			Set<String> knownPools = new LinkedHashSet<>(SoundPools.allDirectories());
			for (Map.Entry<String, List<String>> entry : rawPools.entrySet())
			{
				String poolDirectory = SoundPools.normalizePoolKey(entry.getKey());
				if (!knownPools.contains(poolDirectory))
				{
					continue;
				}
				LinkedHashSet<String> parsedValues = sanitizeStorageKeys(
					poolDirectory,
					entry.getValue(),
					false,
					directoryHasFilesCache
				);
				if (parsedValues.isEmpty())
				{
					continue;
				}
				parsedPools.merge(poolDirectory, parsedValues, (existing, incoming) -> {
					existing.addAll(incoming);
					return existing;
				});
			}
			return parsedPools;
		}
		catch (JsonSyntaxException exception)
		{
			log.warn("Failed to parse sound override pools. Resetting overrides cache.", exception);
			return new LinkedHashMap<>();
		}
	}

	private LinkedHashSet<String> sanitizeStorageKeys(
		final String poolDirectory,
		final Collection<String> storageKeys,
		final boolean refreshCache,
		final Map<String, Boolean> directoryHasFilesCache)
	{
		LinkedHashSet<String> sanitized = new LinkedHashSet<>();
		if (storageKeys == null)
		{
			return sanitized;
		}

		for (String value : storageKeys)
		{
			if (value == null || value.trim().isEmpty())
			{
				continue;
			}

			String normalizedKey = promoteLegacyKey(value.trim(), poolDirectory);
			StorageKeyParts keyParts = parseStorageKey(normalizedKey, poolDirectory);
			if (keyParts == null)
			{
				continue;
			}

			if (canValidateDirectoryContents(keyParts.directory, refreshCache, directoryHasFilesCache)
				&& SoundFileManager.lookupFile(keyParts.directory, keyParts.fileName) == null)
			{
				continue;
			}

			sanitized.add(keyParts.directory + "/" + keyParts.fileName);
		}
		return sanitized;
	}

	private boolean canValidateDirectoryContents(
		final String directory,
		final boolean refreshCache,
		final Map<String, Boolean> directoryHasFilesCache)
	{
		if (SoundFileManager.CUSTOM_DIRECTORY.equals(directory))
		{
			return true;
		}

		return directoryHasFilesCache.computeIfAbsent(
			directory,
			key -> !SoundFileManager.listFilesInDirectory(key, refreshCache).isEmpty()
		);
	}

	private StorageKeyParts parseStorageKey(final String storageKey, final String poolDirectory)
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
			directory = poolDirectory;
			fileName = storageKey;
		}
		else
		{
			directory = storageKey.substring(0, separatorIndex).trim();
			fileName = storageKey.substring(separatorIndex + 1).trim();
		}

		if ((!SoundFileManager.CUSTOM_DIRECTORY.equals(directory) && !SoundPools.allDirectories().contains(directory))
			|| fileName.isEmpty() || fileName.contains("/") || fileName.contains("\\")
			|| !fileName.toLowerCase(Locale.ROOT).endsWith(".wav"))
		{
			return null;
		}

		return new StorageKeyParts(directory, fileName);
	}

	private void writeOverridePools(final Map<String, LinkedHashSet<String>> pools)
	{
		Map<String, List<String>> serialized = new LinkedHashMap<>();
		for (Map.Entry<String, LinkedHashSet<String>> entry : pools.entrySet())
		{
			if (entry.getValue() == null || entry.getValue().isEmpty())
			{
				continue;
			}
			serialized.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}

		configManager.setConfiguration(
			OdablockConfig.CONFIG_GROUP,
			OdablockConfig.SOUND_OVERRIDE_POOLS_KEY,
			gson.toJson(serialized)
		);
	}

	private static final class StorageKeyParts
	{
		private final String directory;
		private final String fileName;

		private StorageKeyParts(String directory, String fileName)
		{
			this.directory = directory;
			this.fileName = fileName;
		}
	}
}
