package com.github.dappermickie.odablock;

public enum Sound
{
	REDEMPTION_PROC("redemption", "RedemptionProc_r1.wav"),//#1
	REDEMPTION_PROC_2("redemption", "RedemptionProc_r2.wav"),//#1
	REDEMPTION_PROC_3("redemption", "RedemptionProc_r3.wav"),//#1
	REDEMPTION_PROC_4("redemption", "RedemptionProc_r4.wav"),//#1

	DEATH("death", "DyingHCIMCompleted_r1.wav"),//#2
	COLLECTION_LOG_SLOT("collectionlog", "ColLogSlotCompleted_r1.wav"),//#3
	SMITED_NO_PRAYER("smited", "SmitedNoPrayer.wav"),//#4
	NEW_PET("newpet", "NewPet.wav"),//#5
	PETTING_DOG("pettingdog", "PettingDog.wav"),//#6
	DECLINE_TRADE("declinetrade", "DeclineTrade.wav"),//#7
	VENGEANCE("vengeance", "Vengeance.wav"),//#8

	KILLING_SOMEONE_1("playerkilling", "KillingSomeone_r1.wav"),//#9
	KILLING_SOMEONE_2("playerkilling", "KillingSomeone_r2.wav"),//#9
	KILLING_SOMEONE_3("playerkilling", "KillingSomeone_r3.wav"),//#9
	KILLING_SOMEONE_4("playerkilling", "KillingSomeone_r4.wav"),//#9

	REPORT_PLAYER_1("reportplayer", "ReportPlayer_r1.wav"),//#10
	REPORT_PLAYER_2("reportplayer", "ReportPlayer_r2.wav"),//#10
	REPORT_PLAYER_3("reportplayer", "ReportPlayer_r3.wav"),//#10

	ZEBAK_ROAR("zebakroar", "ZebakRoar.wav"),//#12
	RUBY_PROC("rubyproc", "RubyProc.wav"),//#13
	//HIT_BY_KEPHRI("holykephri", "HitByKephri.wav"),//#14

	COMBAT_TASK("combattask", "CombatTaskCompleted_r1.wav"),//#15
	COMBAT_TASK_2("combattask", "CombatTaskCompleted_r2.wav"),//#15
	COMBAT_TASK_3("combattask", "CombatTaskCompleted_r3.wav"),//#15
	COMBAT_TASK_4("combattask", "CombatTaskCompleted_r4.wav"),//#15

	DDS_SPEC("ddsspec", "DdsSpec_r1.wav"),//#16
	DDS_SPEC_2("ddsspec", "DdsSpec_r2.wav"),//#16
	DDS_SPEC_3("ddsspec", "DdsSpec_r3.wav"),//#16

	ACCEPTED_TRADE("accepttrade", "AcceptTrade.wav"),//#17

	GETTING_PURPLE_1("gettingpurple", "GettingPurple_r1.wav"),//#18
	GETTING_PURPLE_2("gettingpurple", "GettingPurple_r2.wav"),//#18


	KILLING_RAT_OR_SCURRIUS_1("killingrat", "KillingRatOrScurrius_r1.wav"),//#18
	KILLING_RAT_OR_SCURRIUS_2("killingrat", "KillingRatOrScurrius_r2.wav"),//#18

	DISMISSING_RANDOM_EVENT("dismissrandomevent", "DismissingRandomEvent.wav"),//#20
	TYPING_IN_BANKPIN("typingbankpin", "TypingInBankpin.wav"),//#21
	CLIENT_DISCONNECTS("clientdisconnects", "ClientDisconnects.wav"),//#22
	TOA_CHEST_OPENS("toachestopens", "ToaChestOpens.wav"),//#23
	WHITE_LIGHT_AFTER_RAID("whitelight", "WhiteLightAfterRaid.wav"),//#24
	TURNING_ON_RUN("turningonrun", "TurningOnRun.wav"),//#25
	CLICKING_PK_LOOT_CHEST("pkchest", "ClickingPkLootChest.wav"),//#26

	ACB_SPEC("acbspec", "AcbSpec_r1.wav"),//#27
	ACB_SPEC_2("acbspec", "AcbSpec_r2.wav"),//#27
	ACB_SPEC_3("acbspec", "AcbSpec_r3.wav"),

	AGS_SPEC("agsspec", "AgsSpec_r1.wav"),//#28
	AGS_SPEC_2("agsspec", "AgsSpec_r2.wav"),//#28
	AGS_SPEC_3("agsspec", "AgsSpec_r3.wav"),//#28

	DH_AXE_CHOP("dhchop", "DhAxe_chop_r1.wav"),//#29
	DH_AXE_CHOP_2("dhchop", "DhAxe_chop_r2.wav"),

	DH_AXE_HACK("dhhack", "DhAxe_hack_r1.wav"),//#29
	DH_AXE_HACK_2("dhhack", "DhAxe_hack_r2.wav"),//#29

	DH_AXE_BLOCK("dhblock", "DhAxe_block_r1.wav"),//#29
	DH_AXE_BLOCK_2("dhblock", "DhAxe_block_r2.wav"),//#29
	DH_AXE_BLOCK_3("dhblock", "DhAxe_block_r3.wav"),

	DH_AXE_SMASH("dhsmash", "DhAxe_smash_r1.wav"),//#29
	DH_AXE_SMASH_2("dhsmash", "DhAxe_smash_r2.wav"),//#29
	DH_AXE_SMASH_3("dhsmash", "DhAxe_smash_r3.wav"),//#29

	LEVEL_UP("levelup", "LevelUpCompleted_r1.wav"),
	QUEST("quest", "QuestCompleted_r1.wav"),
	ACHIEVEMENT_DIARY("achievementdiary", "AchievementDiary_r1.wav"),
	ACHIEVEMENT_DIARY_2("achievementdiary", "AchievementDiary_r2.wav"),
	EASTER_EGG_STRAYDOG_BONE("givebone", "GiveBone.wav"),

	HAIRDRESSER_SOUND_1("hairdresser", "Hairdresser_r1.wav"),
	HAIRDRESSER_SOUND_2("hairdresser", "Hairdresser_r2.wav"),
	HAIRDRESSER_SOUND_3("hairdresser", "Hairdresser_r3.wav"),

	SNOWBALL_1("snowball", "Snowball_r1.wav"),
	SNOWBALL_2("snowball", "Snowball_r2.wav"),

	FREEZE("freeze", "Freeze.wav"),

	CRYPT("crypt", "Crypt.wav"),
	WARRIOR("warriors", "odablock_warriors.wav");

	private final String resourceName;
	private final String directory;

	Sound(String directory, String resourceName)
	{
		this.directory = directory;
		this.resourceName = resourceName;
	}


	String getResourceName()
	{
		return resourceName;
	}

	String getDirectory()
	{
		return directory;
	}
}
