package com.github.dappermickie.odablock;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(OdablockConfig.CONFIG_GROUP)
public interface OdablockConfig extends Config
{
	String CONFIG_GROUP = "odablockplugin";
	String SOUND_OVERRIDE_POOLS_KEY = "soundOverridePools";

	// =========================================================================
	// General Settings
	// =========================================================================

	@ConfigSection(
		name = "General",
		description = "Global plugin settings.",
		position = 10
	)
	String GENERAL_SECTION = "generalSection";

	@Range(min = 0, max = 200)
	@ConfigItem(
		keyName = "announcementVolume",
		name = "Announcement Volume",
		description = "Adjust how loud the audio announcements are played.",
		section = GENERAL_SECTION,
		position = 11
	)
	default int announcementVolume()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "showChatMessages",
		name = "Show Fake Public Chat Message",
		description = "Should Odablock announce your achievements in game chat as well as audibly? Only you will see these messages.",
		section = GENERAL_SECTION,
		position = 12
	)
	default boolean showChatMessages()
	{
		return true;
	}

	@ConfigItem(
		keyName = "onlyForOwnPlayer",
		name = "Only Own Player",
		description = "Should Odablock sounds play for your player only? For example: AGS spec, DDS spec, etc.",
		section = GENERAL_SECTION,
		position = 13
	)
	default boolean ownPlayerOnly()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSidebar",
		name = "Show Override Sidebar",
		description = "Show the sound override sidebar panel on the right side of the screen.",
		section = GENERAL_SECTION,
		position = 14
	)
	default boolean showSidebar()
	{
		return false;
	}

	// =========================================================================
	// Specs & Combat
	// =========================================================================

	@ConfigSection(
		name = "Specs & Combat",
		description = "Special attack and combat-related sound replacements.",
		position = 20
	)
	String SPECS_SECTION = "specsSection";

	@ConfigItem(
		keyName = "rubyBoltProc",
		name = "Ruby Bolt SLAAA",
		description = "Should the ruby bolt proc be replaced with Oda's SLAAA?",
		section = SPECS_SECTION,
		position = 21
	)
	default boolean rubyBoltProc()
	{
		return true;
	}

	@ConfigItem(
		keyName = "zebakRoar",
		name = "Zebak Roar",
		description = "Should Zebak's roar be replaced with Oda's SLAAA?",
		section = SPECS_SECTION,
		position = 22
	)
	default boolean zebakRoar()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vengeance",
		name = "Vengeance",
		description = "Should Oda's 'Invisivengene' play whenever you cast Vengeance?",
		section = SPECS_SECTION,
		position = 23
	)
	default boolean vengeance()
	{
		return true;
	}

	@ConfigItem(
		keyName = "ddsSpec",
		name = "DDS Spec",
		description = "Should Odablock sounds play for your DDS spec?",
		section = SPECS_SECTION,
		position = 24
	)
	default boolean ddsSpec()
	{
		return true;
	}

	@ConfigItem(
		keyName = "agsSpec",
		name = "AGS Spec",
		description = "Should Odablock sounds play for your AGS spec?",
		section = SPECS_SECTION,
		position = 25
	)
	default boolean agsSpec()
	{
		return true;
	}

	@ConfigItem(
		keyName = "acbSpec",
		name = "ACB Spec",
		description = "Should Odablock sounds play for your ACB spec?",
		section = SPECS_SECTION,
		position = 26
	)
	default boolean acbSpec()
	{
		return true;
	}

	@ConfigItem(
		keyName = "dhAxe",
		name = "DH/Soulreaper Axe Sounds",
		description = "Should Odablock say something whenever you switch styles on your DH or Soulreaper Axe?",
		section = SPECS_SECTION,
		position = 27
	)
	default boolean dhAxe()
	{
		return true;
	}

	@ConfigItem(
		keyName = "prayerMessage",
		name = "Prayer Message",
		description = "Should Odablock let you know when you run out of prayer?",
		section = SPECS_SECTION,
		position = 28
	)
	default boolean prayerMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "redemptionMessage",
		name = "Redemption Message",
		description = "Should Odablock let you know when you proc Redemption?",
		section = SPECS_SECTION,
		position = 29
	)
	default boolean redemptionMessage()
	{
		return true;
	}

	// =========================================================================
	// PvP
	// =========================================================================

	@ConfigSection(
		name = "PvP",
		description = "PvP-related sounds.",
		position = 30
	)
	String PVP_SECTION = "pvpSection";

	@ConfigItem(
		keyName = "playerKilling",
		name = "Player Killing",
		description = "Should Odablock tell you something when you kill a player? This only works if you're still close to the player when they die.",
		section = PVP_SECTION,
		position = 31
	)
	default boolean playerKilling()
	{
		return true;
	}

	@ConfigItem(
		keyName = "sendReport",
		name = "Send Report",
		description = "Should Odablock say 'Reported for salutations!' when you report someone?",
		section = PVP_SECTION,
		position = 32
	)
	default boolean sendReport()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pkChest",
		name = "PK Chest",
		description = "Should Odablock say something whenever you open the PK chest?",
		section = PVP_SECTION,
		position = 33
	)
	default boolean pkChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "snowballed",
		name = "Snowballed",
		description = "Should Odablock say something whenever you get snowballed by Odablock or one of his mods?",
		section = PVP_SECTION,
		position = 34
	)
	default boolean snowballed()
	{
		return true;
	}

	// =========================================================================
	// Achievements & Milestones
	// =========================================================================

	@ConfigSection(
		name = "Achievements & Milestones",
		description = "Sounds for level ups, quests, achievements, drops, and death.",
		position = 40
	)
	String ACHIEVEMENTS_SECTION = "achievementsSection";

	@ConfigItem(
		keyName = "announceLevelUp",
		name = "Level Ups",
		description = "Should Odablock announce when you gain a level in a skill?",
		section = ACHIEVEMENTS_SECTION,
		position = 41
	)
	default boolean announceLevelUp()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceLevelUpIncludesVirtual",
		name = "Include Virtual Level Ups",
		description = "Should Odablock announce when you gain a virtual level above 99 in a skill?",
		section = ACHIEVEMENTS_SECTION,
		position = 42
	)
	default boolean announceLevelUpIncludesVirtual()
	{
		return false;
	}

	@ConfigItem(
		keyName = "announceLevel99",
		name = "Level 99",
		description = "Should Odablock play the gamon sound when you reach level 99 in a skill? This replaces the standard level-up sound for that milestone.",
		section = ACHIEVEMENTS_SECTION,
		position = 43
	)
	default boolean announceLevel99()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceQuestCompletion",
		name = "Quest Completions",
		description = "Should Odablock announce when you complete a quest?",
		section = ACHIEVEMENTS_SECTION,
		position = 44
	)
	default boolean announceQuestCompletion()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceCollectionLog",
		name = "New Collection Log Entry",
		description = "Should Odablock announce when you fill in a new slot in your collection log? This relies on you having chat messages enabled in the game settings, including the popup option.",
		section = ACHIEVEMENTS_SECTION,
		position = 45
	)
	default boolean announceCollectionLog()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceAchievementDiary",
		name = "Completed Achievement Diaries",
		description = "Should Odablock announce when you complete a new achievement diary?",
		section = ACHIEVEMENTS_SECTION,
		position = 46
	)
	default boolean announceAchievementDiary()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceCombatAchievement",
		name = "Completed Combat Achievement Tasks",
		description = "Should Odablock announce when you complete a new combat achievement task?",
		section = ACHIEVEMENTS_SECTION,
		position = 47
	)
	default boolean announceCombatAchievement()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceDeath",
		name = "When You Die",
		description = "Should Odablock say something when you die?",
		section = ACHIEVEMENTS_SECTION,
		position = 48
	)
	default boolean announceDeath()
	{
		return true;
	}

	@ConfigItem(
		keyName = "receivedPet",
		name = "Received Pet",
		description = "Should Odablock say something whenever you receive a pet?",
		section = ACHIEVEMENTS_SECTION,
		position = 49
	)
	default boolean receivedPet()
	{
		return true;
	}

	@ConfigItem(
		keyName = "killingRat",
		name = "Killing Rat or Scurrius",
		description = "Should Odablock say something whenever you kill a rat or Scurrius?",
		section = ACHIEVEMENTS_SECTION,
		position = 50
	)
	default boolean killingRat()
	{
		return true;
	}

	// =========================================================================
	// Trades & Interactions
	// =========================================================================

	@ConfigSection(
		name = "Trades & Interactions",
		description = "Sounds for trades, interfaces, and other interactions.",
		position = 50
	)
	String INTERACTIONS_SECTION = "interactionsSection";

	@ConfigItem(
		keyName = "acceptTrade",
		name = "Accept Trade",
		description = "Should Odablock say 'Oda the generous strikes again!' when you accept a trade?",
		section = INTERACTIONS_SECTION,
		position = 51
	)
	default boolean acceptTrade()
	{
		return true;
	}

	@ConfigItem(
		keyName = "declineTrade",
		name = "Decline Trade",
		description = "Should Odablock say 'No Sanks!' when you decline a trade?",
		section = INTERACTIONS_SECTION,
		position = 52
	)
	default boolean declineTrade()
	{
		return true;
	}

	@ConfigItem(
		keyName = "dismissRandomEvent",
		name = "Dismiss Random Event",
		description = "Should Odablock say 'No sanks!' when you dismiss a random event?",
		section = INTERACTIONS_SECTION,
		position = 53
	)
	default boolean dismissRandomEvent()
	{
		return true;
	}

	@ConfigItem(
		keyName = "petDog",
		name = "Pet the Dog",
		description = "Should Stella say 'Who's a good little zoggy!' when you pet the dog?",
		section = INTERACTIONS_SECTION,
		position = 54
	)
	default boolean petDog()
	{
		return true;
	}

	@ConfigItem(
		keyName = "giveBone",
		name = "Give Bone",
		description = "Should Stella say something whenever you give a bone to a dog?",
		section = INTERACTIONS_SECTION,
		position = 55
	)
	default boolean giveBone()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hairDresser",
		name = "Hairdresser",
		description = "Should Odablock say something whenever you open the hairdresser interface in Falador?",
		section = INTERACTIONS_SECTION,
		position = 56
	)
	default boolean hairDresser()
	{
		return true;
	}

	@ConfigItem(
		keyName = "turnOnRun",
		name = "Turn on Run",
		description = "Should Odablock say 'FAST! I said FAST!' when you turn your run on?",
		section = INTERACTIONS_SECTION,
		position = 57
	)
	default boolean turnOnRun()
	{
		return true;
	}

	@ConfigItem(
		keyName = "bankPin",
		name = "Bank PIN",
		description = "Should Odablock make the 'ai ai ai ai' sound when you type in your bank PIN?",
		section = INTERACTIONS_SECTION,
		position = 58
	)
	default boolean bankPin()
	{
		return true;
	}

	// =========================================================================
	// Raids
	// =========================================================================

	@ConfigSection(
		name = "Tombs of Amascut",
		description = "All configurations regarding Tombs of Amascut.",
		position = 100
	)
	String TOA_SECTION = "toaSection";

	@ConfigItem(
		keyName = "toaWhiteChest",
		name = "TOA White Chest",
		description = "When enabled, Odablock will say something if you receive a white light.",
		section = TOA_SECTION,
		position = 101
	)
	default boolean toaWhiteChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "toaPurpleChest",
		name = "TOA Purple Chest",
		description = "When enabled, Odablock will say something if you receive a purple light.",
		section = TOA_SECTION,
		position = 102
	)
	default boolean toaPurpleChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableToaPurpleChestOpens",
		name = "Opening the Chest",
		description = "When enabled, Odablock will say 'Please GAGECK' whenever someone in your party opens the purple chest at TOA.",
		section = TOA_SECTION,
		position = 103
	)
	default boolean toaPurpleChestOpens()
	{
		return true;
	}

	@ConfigSection(
		name = "Theatre of Blood",
		description = "All configurations regarding Theatre of Blood.",
		position = 200
	)
	String TOB_SECTION = "tobSection";

	@ConfigItem(
		keyName = "tobWhiteChest",
		name = "TOB White Chest",
		description = "Should Odablock say something whenever you receive a white chest at TOB?",
		section = TOB_SECTION,
		position = 201
	)
	default boolean tobWhiteChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "tobPurpleChest",
		name = "TOB Purple Chest",
		description = "Should Odablock say something whenever you receive a purple chest at TOB?",
		section = TOB_SECTION,
		position = 202
	)
	default boolean tobPurpleChest()
	{
		return true;
	}

	@ConfigSection(
		name = "Chambers of Xeric",
		description = "All configurations regarding Chambers of Xeric.",
		position = 300
	)
	String COX_SECTION = "coxSection";

	@ConfigItem(
		keyName = "coxWhiteChest",
		name = "COX White Chest",
		description = "Should Odablock say something whenever you get a white light at COX?",
		section = COX_SECTION,
		position = 301
	)
	default boolean coxWhiteChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "coxPurpleChest",
		name = "COX Purple Chest",
		description = "Should Odablock say something whenever you get a purple light at COX?",
		section = COX_SECTION,
		position = 302
	)
	default boolean coxPurpleChest()
	{
		return true;
	}

	// =========================================================================
	// Music & Emotes
	// =========================================================================

	@ConfigSection(
		name = "Music & Emotes",
		description = "Music replacements and Odablock emote handling.",
		position = 400
	)
	String MUSIC_EMOTES_SECTION = "musicEmotesSection";

	@ConfigItem(
		keyName = "warriors",
		name = "Odablock Warriors",
		description = "Should the '7th Realm' in-game sound be replaced with the Odablock Warriors song?",
		section = MUSIC_EMOTES_SECTION,
		position = 401,
		warning = "If you turn this off, you'll have to reload the client to be able to manually play '7th Realm' again."
	)
	default boolean warriors()
	{
		return true;
	}

	@ConfigItem(
		keyName = "emotes",
		name = "Emotes",
		description = "Configures whether some of the text in game gets replaced with Odablock's emotes.<br />Type '::odaemotes' in chat to see a list of all available emotes.",
		section = MUSIC_EMOTES_SECTION,
		position = 402
	)
	default boolean emotes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "emoteIgnoreList",
		name = "Emote Ignore List",
		description = "A comma-separated list of emotes to ignore. For example: ':p, :)'.<br />Type '::odaemotes' in chat to see a list of all available emotes.",
		section = MUSIC_EMOTES_SECTION,
		position = 403
	)
	default String emoteIgnoreList()
	{
		return "";
	}

	// =========================================================================
	// Livestream
	// =========================================================================

	@ConfigSection(
		name = "Livestream",
		description = "All livestream configurations.",
		position = 500
	)
	String LIVESTREAM_SECTION = "livestreamSection";

	@ConfigItem(
		keyName = "livestream",
		name = "Livestream Notification",
		description = "Should Odablock send a message whenever he is live?",
		section = LIVESTREAM_SECTION,
		position = 501
	)
	default boolean livestream()
	{
		return true;
	}

	@ConfigItem(
		keyName = "livestreamPlaySound",
		name = "Play Sound",
		description = "Should Odablock play a sound when he goes live?",
		section = LIVESTREAM_SECTION,
		position = 502
	)
	default boolean livestreamPlaySound()
	{
		return true;
	}

	@ConfigItem(
		keyName = "livestreamInterval",
		name = "Notification Interval",
		description = "Set the interval of the livestream notification message in minutes.",
		section = LIVESTREAM_SECTION,
		position = 503
	)
	default int livestreamInterval()
	{
		return 30;
	}

	@ConfigItem(
		keyName = "livestreamColor",
		name = "Notification Color",
		description = "Set the color of the livestream notification message.",
		section = LIVESTREAM_SECTION,
		position = 504
	)
	default Color livestreamColor()
	{
		return Color.RED;
	}

	// =========================================================================
	// Notifications
	// =========================================================================

	@ConfigSection(
		name = "Notifications",
		description = "All notification configurations.",
		position = 600
	)
	String NOTIFICATION_SECTION = "notificationSection";

	@ConfigItem(
		keyName = "notification",
		name = "Odablock Notifications",
		description = "Should Odablock send notifications?",
		section = NOTIFICATION_SECTION,
		position = 601
	)
	default boolean notifications()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notificationColor",
		name = "Notification Color",
		description = "Set the color of the notification messages.",
		section = NOTIFICATION_SECTION,
		position = 602
	)
	default Color notificationColor()
	{
		return Color.RED;
	}

	// =========================================================================
	// Developer (always last)
	// =========================================================================

	@ConfigSection(
		name = "Developer",
		description = "Developer mode configurations.",
		position = 900,
		closedByDefault = true
	)
	String DEVELOPER_SECTION = "developerSection";

	@ConfigItem(
		keyName = "developerLogging",
		name = "Developer Logging",
		description = "Enable developer logging when developer mode is active.",
		section = DEVELOPER_SECTION,
		position = 901
	)
	default boolean developerLogging()
	{
		return false;
	}

	// =========================================================================
	// Internal (hidden)
	// =========================================================================

	@ConfigItem(
		keyName = SOUND_OVERRIDE_POOLS_KEY,
		name = "Sound Override Pools",
		description = "Internal storage for sound override selections.",
		position = 10000,
		hidden = true
	)
	default String soundOverridePools()
	{
		return "";
	}
}
