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
		keyName = "welcomeScreen",
		name = "Welcome Screen",
		description = "Do you want to play the '' sound whenever you login and the welcome screen pops up?",
		section = GENERAL_SECTION,
		position = 14
	)
	default boolean welcomeScreen()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSidebar",
		name = "Show Override Sidebar",
		description = "Show the sound override sidebar panel on the right side of the screen.",
		section = GENERAL_SECTION,
		position = 15
	)
	default boolean showSidebar()
	{
		return true;
	}

	@ConfigItem(
		keyName = "clientDisconnect",
		name = "Client Disconnect",
		description = "Play a sound when the game loses its connection to the server. Does not trigger for ordinary logout or world hopping.",
		section = GENERAL_SECTION,
		position = 16
	)
	default boolean clientDisconnect()
	{
		return true;
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
	// PvP / PvM
	// =========================================================================

	@ConfigSection(
		name = "PvP / PvM",
		description = "PvP and PvM-related sounds.",
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

	@ConfigItem(
		keyName = "freezeSound",
		name = "Freeze Sound",
		description = "Do you want to play the 'Stop right there mr. squidward' sound when you get frozen?",
		section = PVP_SECTION,
		position = 35
	)
	default boolean freezeSound()
	{
		return true;
	}

	@ConfigItem(
		keyName = "cryptSound",
		name = "Crypt Sound",
		description = "Do you want to play the 'I did well..' sound when you enter Dharok's Barrows crypt?",
		section = PVP_SECTION,
		position = 36
	)
	default boolean cryptSound()
	{
		return true;
	}

	@ConfigItem(
		keyName = "emptyChestSound",
		name = "Empty Barrows / Moons Chest",
		description = "Do you want to play 'Fahhhhh' when you open a Barrows or Moons chest with no unique loot?",
		section = PVP_SECTION,
		position = 37
	)
	default boolean emptyChestSound()
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
		description = "Should Odablock play a sound when you reach level 99 in a skill? This replaces the standard level-up sound for that milestone.",
		section = ACHIEVEMENTS_SECTION,
		position = 43
	)
	default boolean announceLevel99()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceMaxTotalLevel",
		name = "Max Total Level",
		description = "Should Odablock play a special sound when a level 99 completes max total level (2376)? When disabled, maxing uses the regular level 99 sound instead.",
		section = ACHIEVEMENTS_SECTION,
		position = 44
	)
	default boolean announceMaxTotalLevel()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceQuestCompletion",
		name = "Quest Completions",
		description = "Should Odablock announce when you complete a quest?",
		section = ACHIEVEMENTS_SECTION,
		position = 45
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
		position = 46
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
		position = 47
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
		position = 48
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
		position = 49
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
		position = 50
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
		position = 51
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
		keyName = "randomEventSpawned",
		name = "Random Event Spawned",
		description = "Should Odablock play the 'Random Event Spawned' sound when a random event NPC spawns for you?",
		section = INTERACTIONS_SECTION,
		position = 54
	)
	default boolean randomEventSpawned()
	{
		return true;
	}

	@ConfigItem(
		keyName = "phoenixNecklace",
		name = "Phoenix Necklace",
		description = "Should Odablock say something when your phoenix necklace breaks?",
		section = INTERACTIONS_SECTION,
		position = 55
	)
	default boolean phoenixNecklace()
	{
		return true;
	}

	@ConfigItem(
		keyName = "petDog",
		name = "Pet the Dog",
		description = "Should Stella say 'Who's a good little zoggy!' when you pet the dog?",
		section = INTERACTIONS_SECTION,
		position = 56
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
		position = 57
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
		position = 58
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
		position = 59
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
		position = 60
	)
	default boolean bankPin()
	{
		return true;
	}


	@ConfigItem(
		keyName = "itemDrop",
		name = "Item Drop",
		description = "Should Odablock play a sound when you receive a valuable item drop from a kill?",
		section = INTERACTIONS_SECTION,
		position = 62
	)
	default boolean itemDrop()
	{
		return true;
	}

	@Range(min = 0, max = 2147483647)
	@ConfigItem(
		keyName = "itemDropMinGeValue",
		name = "Item Drop Minimum GE Value",
		description = "Minimum Grand Exchange value for loot from your kills to trigger the item drop sound.",
		section = INTERACTIONS_SECTION,
		position = 63
	)
	default int itemDropMinGeValue()
	{
		return 10_000_000;
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

	@ConfigSection(
		name = "The Fractured Archive",
		description = "All configurations regarding The Fractured Archive.",
		position = 350
	)
	String TFA_SECTION = "tfaSection";

	@ConfigItem(
		keyName = "tfaWhiteChest",
		name = "TFA White Chest",
		description = "Should Odablock say something as soon as your regular chest is identified at The Fractured Archive?",
		section = TFA_SECTION,
		position = 351
	)
	default boolean tfaWhiteChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "tfaPurpleChest",
		name = "TFA Purple Chest",
		description = "Should Odablock say something as soon as your purple chest is identified at The Fractured Archive?",
		section = TFA_SECTION,
		position = 352
	)
	default boolean tfaPurpleChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "tfaPurpleChestOpens",
		name = "Opening the Chest",
		description = "When enabled, Odablock will say 'Please GAGECK' when someone in your party starts opening the purple chest at The Fractured Archive.",
		section = TFA_SECTION,
		position = 353
	)
	default boolean tfaPurpleChestOpens()
	{
		return true;
	}

	// =========================================================================
	// Music & Emotes
	// =========================================================================

	@ConfigSection(
		name = "Music & Emotes",
		description = "Additional music tracks and Odablock emote handling.",
		position = 400
	)
	String MUSIC_EMOTES_SECTION = "musicEmotesSection";

	@ConfigItem(
		keyName = "warriors",
		name = "Odablock Warriors",
		description = "Add Odablock Warriors as its own entry at the bottom of the in-game music player.",
		section = MUSIC_EMOTES_SECTION,
		position = 401
	)
	default boolean warriors()
	{
		return true;
	}

	@ConfigItem(
		keyName = "terry",
		name = "Terry",
		description = "Add Terry - Odablock X @ACRAZEOFFICIAL as its own entry at the bottom of the in-game music player.",
		section = MUSIC_EMOTES_SECTION,
		position = 401
	)
	default boolean terry()
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

	@ConfigItem(
		keyName = "notificationPlaySound",
		name = "Play Sound",
		description = "Should Odablock play a sound when a notification is sent?",
		section = NOTIFICATION_SECTION,
		position = 603
	)
	default boolean notificationPlaySound()
	{
		return true;
	}

	// =========================================================================
	// Debug
	// =========================================================================

	@ConfigSection(
		name = "Debug",
		description = "Troubleshooting and logging options.",
		position = 900,
		closedByDefault = true
	)
	String DEBUG_SECTION = "debugSection";

	@ConfigItem(
		keyName = "developerLogging",
		name = "Debug Logging",
		description = "Log game state changes for troubleshooting.",
		section = DEBUG_SECTION,
		position = 901
	)
	default boolean developerLogging()
	{
		return false;
	}

	// =========================================================================
	// Custom Barrows Faces Settings
	// =========================================================================

	@ConfigSection(
		name = "Custom Barrows Faces",
		description = "Configure custom faces for Barrows Brothers.",
		position = 600
	)
	String CUSTOM_BARROWS_SECTION = "customBarrowsSection";

	@ConfigItem(
		keyName = "customBarrowsAhrim",
		name = "Ahrim",
		description = "Enable custom barrows face overlay for Ahrim.",
		section = CUSTOM_BARROWS_SECTION,
		position = 601
	)
	default boolean customBarrowsAhrim()
	{
		return true;
	}

	@ConfigItem(
		keyName = "customBarrowsDharok",
		name = "Dharok",
		description = "Enable custom barrows face overlay for Dharok.",
		section = CUSTOM_BARROWS_SECTION,
		position = 602
	)
	default boolean customBarrowsDharok()
	{
		return true;
	}

	@ConfigItem(
		keyName = "customBarrowsGuthan",
		name = "Guthan",
		description = "Enable custom barrows face overlay for Guthan.",
		section = CUSTOM_BARROWS_SECTION,
		position = 603
	)
	default boolean customBarrowsGuthan()
	{
		return true;
	}

	@ConfigItem(
		keyName = "customBarrowsKaril",
		name = "Karil",
		description = "Enable custom barrows face overlay for Karil.",
		section = CUSTOM_BARROWS_SECTION,
		position = 604
	)
	default boolean customBarrowsKaril()
	{
		return true;
	}

	@ConfigItem(
		keyName = "customBarrowsTorag",
		name = "Torag",
		description = "Enable custom barrows face overlay for Torag.",
		section = CUSTOM_BARROWS_SECTION,
		position = 605
	)
	default boolean customBarrowsTorag()
	{
		return true;
	}

	@ConfigItem(
		keyName = "customBarrowsVerac",
		name = "Verac",
		description = "Enable custom barrows face overlay for Verac.",
		section = CUSTOM_BARROWS_SECTION,
		position = 606
	)
	default boolean customBarrowsVerac()
	{
		return true;
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
