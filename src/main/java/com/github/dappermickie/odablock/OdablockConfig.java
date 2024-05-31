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

	@ConfigItem(
		keyName = "rubyBoltProc",
		name = "Ruby Bolt SLAAA",
		description = "Should the ruby bolt proc be replaced with oda's SLAAA?",
		position = 0
	)
	default boolean rubyBoltProc()
	{
		return true;
	}

	@ConfigItem(
		keyName = "zebakRoar",
		name = "Zebak Roar",
		description = "Should the zebak roar be replaced with oda's SLAAA?",
		position = 1
	)
	default boolean zebakRoar()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vengeance",
		name = "Vengeance",
		description = "Should oda's 'Invisivengene' play whenever you cast vengeance?",
		position = 2
	)
	default boolean vengeance()
	{
		return true;
	}

	@ConfigItem(
		keyName = "playerKilling",
		name = "Player Killing",
		description = "Should Odablock tell you something when you kill a player? (only works if you're still close to the player when he dies)",
		position = 3
	)
	default boolean playerKilling()
	{
		return true;
	}

	@ConfigItem(
		keyName = "onlyForOwnPlayer",
		name = "Only Own Player",
		description = "Should Odablock sounds play for your player only? (ags, dds spec ...)",
		position = 4
	)
	default boolean ownPlayerOnly()
	{
		return true;
	}

	@ConfigItem(
		keyName = "ddsSpec",
		name = "DDS Spec",
		description = "Should Odablock sounds play for your dds spec?",
		position = 5
	)
	default boolean ddsSpec()
	{
		return true;
	}

	@ConfigItem(
		keyName = "agsSpec",
		name = "AGS Spec",
		description = "Should Odablock sounds play for ags spec?",
		position = 6
	)
	default boolean agsSpec()
	{
		return true;
	}

	@ConfigItem(
		keyName = "acbSpec",
		name = "ACB Spec",
		description = "Should Odablock sounds play for acb spec? (only works with soudns on)",
		position = 7
	)
	default boolean acbSpec()
	{
		return true;
	}

	@ConfigItem(
		keyName = "bankPin",
		name = "Bank Pin",
		description = "Should Odablock make the 'ai ai ai ai' sound when you type in your bank pin?",
		position = 8
	)
	default boolean bankPin()
	{
		return true;
	}

	@ConfigItem(
		keyName = "turnOnRun",
		name = "Turn on run",
		description = "Should Odablock say 'FAST! I said FAST!' sound when you turn your run on?",
		position = 9
	)
	default boolean turnOnRun()
	{
		return true;
	}

	@ConfigItem(
		keyName = "petDog",
		name = "Pet the Dog",
		description = "Should Stella say 'Who's a good little zoggy!' when you pet the dog?",
		position = 10
	)
	default boolean petDog()
	{
		return true;
	}

	@ConfigItem(
		keyName = "dismissRandomEvent",
		name = "Dismiss random event",
		description = "Should Odablock say 'No sanks!' when you dismiss a random event?",
		position = 11
	)
	default boolean dismissRandomEvent()
	{
		return true;
	}

	@ConfigItem(
		keyName = "declineTrade",
		name = "Decline Trade",
		description = "Should Odablock say 'No Sanks!' when you decline a trade?",
		position = 12
	)
	default boolean declineTrade()
	{
		return true;
	}


	@ConfigItem(
		keyName = "acceptTrade",
		name = "Accept Trade",
		description = "Should Odablock say 'Oda the generous strikes again!' when you accept a trade?",
		position = 13
	)
	default boolean acceptTrade()
	{
		return true;
	}

	@ConfigItem(
		keyName = "sendReport",
		name = "Send Report",
		description = "Should Odablock say 'Reported for salutations!' when you report someone?",
		position = 14
	)
	default boolean sendReport()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceLevelUp",
		name = "Level ups",
		description = "Should Odablock announce when you gain a level in a skill?",
		position = 15
	)
	default boolean announceLevelUp()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceLevelUpIncludesVirtual",
		name = "Include virtual level ups",
		description = "Should Odablock announce when you gain a virtual (>99) level in a skill?",
		position = 16
	)
	default boolean announceLevelUpIncludesVirtual()
	{
		return false;
	}

	@ConfigItem(
		keyName = "announceQuestCompletion",
		name = "Quest completions",
		description = "Should Odablock announce when you complete a quest?",
		position = 17
	)
	default boolean announceQuestCompletion()
	{
		return true;
	}

	@ConfigItem(
		keyName = "prayerMessage",
		name = "Prayer Message",
		description = "Should Odablock let you know when you run out of prayer?",
		position = 18
	)
	default boolean prayerMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "redemptionMessage",
		name = "Redemption Message",
		description = "Should Odablock let you know when you proc a redemption?",
		position = 19
	)
	default boolean redemptionMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceCollectionLog",
		name = "New collection log entry",
		description = "Should Odablock announce when you fill in a new slot in your collection log? This one relies on you having chat messages (included with the popup option) enabled in game settings!",
		position = 20
	)
	default boolean announceCollectionLog()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceAchievementDiary",
		name = "Completed achievement diaries",
		description = "Should Odablock announce when you complete a new achievement diary?",
		position = 21
	)
	default boolean announceAchievementDiary()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceCombatAchievement",
		name = "Completed combat achievement tasks",
		description = "Should Odablock announce when you complete a new combat achievement task?",
		position = 22
	)
	default boolean announceCombatAchievement()
	{
		return true;
	}

	@ConfigItem(
		keyName = "announceDeath",
		name = "When you die",
		description = "Should Odablock say something when you die?",
		position = 23
	)
	default boolean announceDeath()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showChatMessages",
		name = "Show fake public chat message (only you will see it)",
		description = "Should Odablock announce your achievements in game chat as well as audibly?",
		position = 24
	)
	default boolean showChatMessages()
	{
		return true;
	}

	@Range(
		min = 0,
		max = 200
	)
	@ConfigItem(
		keyName = "announcementVolume",
		name = "Announcement volume",
		description = "Adjust how loud the audio announcements are played!",
		position = 25
	)
	default int announcementVolume()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "dhAxe",
		name = "DH/Soulreaper Axe sounds",
		description = "Should Odablock say something whenever you switch styles on your DH or soulreaper axe?",
		position = 26
	)
	default boolean dhAxe()
	{
		return true;
	}

	@ConfigItem(
		keyName = "giveBone",
		name = "Give Bone",
		description = "Should stella say something whenever you give a bone to a dog?",
		position = 27
	)
	default boolean giveBone()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hairDresser",
		name = "Hairdresser",
		description = "Should Odablock say something whenever you open up the hairdresser interface in falador?",
		position = 28
	)
	default boolean hairDresser()
	{
		return true;
	}

	@ConfigItem(
		keyName = "killingRat",
		name = "Killing rat (or scurrius)",
		description = "Should Odablock say something whenever you kill a rat or Scurrius?",
		position = 29
	)
	default boolean killingRat()
	{
		return true;
	}

	@ConfigItem(
		keyName = "receivedPet",
		name = "Received pet",
		description = "Should Odablock say something whenever you receive a pet?",
		position = 29
	)
	default boolean receivedPet()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pkChest",
		name = "PK Chest",
		description = "Should Odablock say something whenever you open the PK chest?",
		position = 30
	)
	default boolean pkChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "snowballed",
		name = "Snowballed",
		description = "Should Odablock say something whenever you get snowballed by Odablock or one of his mods?",
		position = 31
	)
	default boolean snowballed()
	{
		return true;
	}

	@ConfigItem(
		keyName = "emotes",
		name = "Emotes",
		description = "Configures whether or not some of the text in game gets replaced with Odablock's emotes.<br />type '::odaemotes' in chat to see a list of all available emotes.",
		position = 32
	)
	default boolean emotes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "emoteIgnoreList",
		name = "Emote Ignore List",
		description = "A comma separated list of emotes to ignore for example: \":p, :)\".<br />type '::odaemotes' in chat to see a list of all available emotes.",
		position = 33
	)
	default String emoteIgnoreList()
	{
		return "";
	}

	@ConfigSection(
		name = "Tombs of Amascut",
		description = "All the configurations regarding Tombs of Amascut.",
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
		description = "When enabled, Odablock will say something if you receive a purple.",
		section = TOA_SECTION,
		position = 102
	)
	default boolean toaPurpleChest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableToaPurpleChestOpens",
		name = "Opening The Chest",
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
		description = "All the configurations regarding Theatre of Blood.",
		position = 200
	)
	String TOB_SECTION = "tobSection";

	@ConfigItem(
		keyName = "tobWhiteChest",
		name = "TOB White chest",
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
		name = "TOB Purple chest",
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
		description = "All the configurations regarding Chambers of Xeric.",
		position = 300
	)
	String COX_SECTION = "coxSection";

	@ConfigItem(
		keyName = "coxWhiteChest",
		name = "COX White chest",
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
		name = "COX Purple chest",
		description = "Should Odablock say something whenever you get a purple light at COX?",
		section = COX_SECTION,
		position = 302
	)
	default boolean coxPurpleChest()
	{
		return true;
	}

	@ConfigSection(
		name = "Livestream",
		description = "All livestream configurations.",
		position = 400
	)
	String LIVESTREAM_SECTION = "livestreamSection";

	@ConfigItem(
		keyName = "livestream",
		name = "Livestream Notification",
		description = "Should Odablock send a message whenever he is live?",
		section = LIVESTREAM_SECTION,
		position = 401
	)
	default boolean livestream()
	{
		return true;
	}

	@ConfigItem(
		keyName = "livestreamInterval",
		name = "Notification Interval",
		description = "Set the interval of the notification message for livestreams in minutes.",
		section = LIVESTREAM_SECTION,
		position = 402
	)
	default int livestreamInterval()
	{
		return 30;
	}

	@ConfigItem(
		keyName = "livestreamColor",
		name = "Notification Color",
		description = "Set the color of the notification message for livestreams.",
		section = LIVESTREAM_SECTION,
		position = 402
	)
	default Color livestreamColor()
	{
		return Color.RED;
	}

	@ConfigSection(
		name = "Notifications",
		description = "All notification configurations.",
		position = 500
	)
	String NOTIFICATION_SECTION = "notificationSection";

	@ConfigItem(
		keyName = "notification",
		name = "Odablock Notifications",
		description = "Should Odablock send out notifications?",
		section = NOTIFICATION_SECTION,
		position = 501
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
		position = 502
	)
	default Color notificationColor()
	{
		return Color.RED;
	}
}
