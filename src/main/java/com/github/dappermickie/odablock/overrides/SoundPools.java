package com.github.dappermickie.odablock.overrides;

import com.github.dappermickie.odablock.Sound;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

/**
 * Sound pools are keyed by {@link Sound#getDirectory()}. Every downloaded sound folder
 * can be overridden from the panel; playback resolves overrides from the sound's directory.
 */
public final class SoundPools
{
	private static final Map<String, String> LEGACY_OVERRIDE_KEYS = buildLegacyKeys();
	private static final Map<String, String> DISPLAY_NAMES = buildDisplayNames();
	private static final Collator DISPLAY_NAME_COLLATOR = Collator.getInstance(Locale.ENGLISH);

	static
	{
		DISPLAY_NAME_COLLATOR.setStrength(Collator.PRIMARY);
	}

	private static final List<String> ALL_DIRECTORIES = buildAllDirectories();

	private SoundPools()
	{
	}

	public static List<String> allDirectories()
	{
		return ALL_DIRECTORIES;
	}

	private static List<String> buildAllDirectories()
	{
		TreeSet<String> directories = new TreeSet<>();
		for (Sound sound : Sound.values())
		{
			directories.add(sound.getDirectory());
		}
		List<String> sorted = new ArrayList<>(directories);
		sorted.sort(SoundPools::compareByDisplayName);
		return Collections.unmodifiableList(sorted);
	}

	public static List<String> allDirectoriesSortedByDisplayName()
	{
		return allDirectories();
	}

	public static int compareByDisplayName(final String leftDirectory, final String rightDirectory)
	{
		return DISPLAY_NAME_COLLATOR.compare(getDisplayName(leftDirectory), getDisplayName(rightDirectory));
	}

	public static int compareDisplayLabels(final String leftLabel, final String rightLabel)
	{
		return DISPLAY_NAME_COLLATOR.compare(leftLabel, rightLabel);
	}

	public static String getDisplayName(final String directory)
	{
		return DISPLAY_NAMES.getOrDefault(directory, SoundLabelNormalizer.normalize(directory));
	}

	/**
	 * Maps persisted override keys from older plugin versions to sound directories.
	 */
	public static String normalizePoolKey(final String key)
	{
		if (key == null || key.isEmpty())
		{
			return key;
		}
		return LEGACY_OVERRIDE_KEYS.getOrDefault(key, key);
	}

	private static Map<String, String> buildLegacyKeys()
	{
		Map<String, String> legacy = new LinkedHashMap<>();
		legacy.put("death", "death");
		legacy.put("levelUp", "levelup");
		legacy.put("collectionLog", "collectionlog");
		legacy.put("questCompleted", "quest");
		legacy.put("combatAchievement", "combattask");
		legacy.put("achievementDiary", "achievementdiary");
		legacy.put("newPet", "newpet");
		legacy.put("petDog", "pettingdog");
		legacy.put("acceptTrade", "accepttrade");
		legacy.put("declineTrade", "declinetrade");
		legacy.put("dismissRandomEvent", "dismissrandomevent");
		legacy.put("vengeance", "vengeance");
		legacy.put("killingPlayer", "playerkilling");
		legacy.put("reportPlayer", "reportplayer");
		legacy.put("zebakRoar", "zebakroar");
		legacy.put("rubyBoltProc", "rubyproc");
		legacy.put("bankPin", "typingbankpin");
		legacy.put("prayerDown", "smited");
		legacy.put("turnOnRun", "turningonrun");
		legacy.put("redemptionProc", "redemption");
		legacy.put("ddsSpec", "ddsspec");
		legacy.put("agsSpec", "agsspec");
		legacy.put("acbSpec", "acbspec");
		legacy.put("dhAxeChop", "dhchop");
		legacy.put("dhAxeHack", "dhhack");
		legacy.put("dhAxeSmash", "dhsmash");
		legacy.put("dhAxeBlock", "dhblock");
		legacy.put("toaPurpleChest", "gettingpurple");
		legacy.put("toaWhiteChest", "whitelight");
		legacy.put("toaChestOpens", "toachestopens");
		legacy.put("tobPurpleChest", "gettingpurple");
		legacy.put("tobWhiteChest", "whitelight");
		legacy.put("coxPurpleChest", "gettingpurple");
		legacy.put("coxWhiteChest", "whitelight");
		legacy.put("pkChest", "pkchest");
		legacy.put("giveBone", "givebone");
		legacy.put("hairdresser", "hairdresser");
		legacy.put("snowballed", "snowball");
		legacy.put("killingRat", "killingrat");
		return Collections.unmodifiableMap(legacy);
	}

	private static Map<String, String> buildDisplayNames()
	{
		Map<String, String> names = new LinkedHashMap<>();
		names.put("death", "Death");
		names.put("levelup", "Level Up");
		names.put("regulargamon", "Level 99");
		names.put("biggamon", "Max Total Level");
		names.put("collectionlog", "Collection Log");
		names.put("quest", "Quest Completed");
		names.put("combattask", "Combat Achievement");
		names.put("achievementdiary", "Achievement Diary");
		names.put("newpet", "New Pet");
		names.put("thecrane", "The Crane");
		names.put("pettingdog", "Pet Dog");
		names.put("accepttrade", "Accept Trade");
		names.put("declinetrade", "Decline Trade");
		names.put("dismissrandomevent", "Dismiss Random Event");
		names.put("gettingragged", "Random Event Spawned");
		names.put("vengeance", "Vengeance");
		names.put("playerkilling", "Killing Player");
		names.put("reportplayer", "Report Player");
		names.put("zebakroar", "Zebak Roar");
		names.put("rubyproc", "Ruby Bolt Proc");
		names.put("typingbankpin", "Bank Pin");
		names.put("smited", "Prayer Down");
		names.put("turningonrun", "Turn On Run");
		names.put("itemdrop", "Item Drop");
		names.put("redemption", "Redemption Proc");
		names.put("ddsspec", "DDS Spec");
		names.put("agsspec", "AGS Spec");
		names.put("acbspec", "ACB Spec");
		names.put("dhchop", "DH Axe Chop");
		names.put("dhhack", "DH Axe Hack");
		names.put("dhsmash", "DH Axe Smash");
		names.put("dhblock", "DH Axe Block");
		names.put("gettingpurple", "Purple Chest");
		names.put("whitelight", "White Chest Light");
		names.put("toachestopens", "Opening the Chest (TOA / TFA)");
		names.put("pkchest", "PK Chest");
		names.put("givebone", "Give Bone");
		names.put("hairdresser", "Hairdresser");
		names.put("snowball", "Snowballed");
		names.put("killingrat", "Killing Rat / Scurrius");
		names.put("odasalert", "Notification Alert");
		names.put("livestream", "Livestream Go Live");
		names.put("phoenixnecklace", "Phoenix Necklace");
		names.put("fahhhhh", "Empty Barrows / Moons Chest");
		names.put("welcomescreen", "Welcome Screen");
		names.put("crypt", "Crypt (Dharok Barrows)");
		names.put("freeze", "Freeze");
		names.put("warriors", "Odablock Warriors");
		names.put("terry", "Terry - Odablock X @ACRAZEOFFICIAL");
		names.put("clientdisconnects", "Client Disconnects");
		return Collections.unmodifiableMap(names);
	}
}
