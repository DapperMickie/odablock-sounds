package com.github.dappermickie.odablock.overrides;

import com.github.dappermickie.odablock.Sound;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SoundOverrideAction
{
	DEATH("death", "Death", Sound.DEATH),
	LEVEL_UP("levelUp", "Level Up", Sound.LEVEL_UP),
	LEVEL_99("level99", "Level 99", Sound.GAMON_GO_LIVE),
	COLLECTION_LOG("collectionLog", "Collection Log", Sound.COLLECTION_LOG_SLOT),
	QUEST_COMPLETED("questCompleted", "Quest Completed", Sound.QUEST),
	COMBAT_ACHIEVEMENT("combatAchievement", "Combat Achievement", Sound.COMBAT_TASK),
	ACHIEVEMENT_DIARY("achievementDiary", "Achievement Diary", Sound.ACHIEVEMENT_DIARY),
	NEW_PET("newPet", "New Pet", Sound.NEW_PET),
	PET_DOG("petDog", "Pet Dog", Sound.PETTING_DOG),
	ACCEPT_TRADE("acceptTrade", "Accept Trade", Sound.ACCEPTED_TRADE),
	DECLINE_TRADE("declineTrade", "Decline Trade", Sound.DECLINE_TRADE),
	DISMISS_RANDOM_EVENT("dismissRandomEvent", "Dismiss Random Event", Sound.DISMISSING_RANDOM_EVENT),
	VENGEANCE("vengeance", "Vengeance", Sound.VENGEANCE),
	KILLING_PLAYER("killingPlayer", "Killing Player", Sound.KILLING_SOMEONE_1),
	REPORT_PLAYER("reportPlayer", "Report Player", Sound.REPORT_PLAYER_1),
	ZEBAK_ROAR("zebakRoar", "Zebak Roar", Sound.ZEBAK_ROAR),
	RUBY_BOLT_PROC("rubyBoltProc", "Ruby Bolt Proc", Sound.RUBY_PROC),
	BANK_PIN("bankPin", "Bank Pin", Sound.TYPING_IN_BANKPIN),
	PRAYER_DOWN("prayerDown", "Prayer Down", Sound.SMITED_NO_PRAYER),
	TURN_ON_RUN("turnOnRun", "Turn On Run", Sound.TURNING_ON_RUN),
	REDEMPTION_PROC("redemptionProc", "Redemption Proc", Sound.REDEMPTION_PROC),
	DDS_SPEC("ddsSpec", "DDS Spec", Sound.DDS_SPEC),
	AGS_SPEC("agsSpec", "AGS Spec", Sound.AGS_SPEC),
	ACB_SPEC("acbSpec", "ACB Spec", Sound.ACB_SPEC),
	DH_AXE_CHOP("dhAxeChop", "DH Axe Chop", Sound.DH_AXE_CHOP),
	DH_AXE_HACK("dhAxeHack", "DH Axe Hack", Sound.DH_AXE_HACK),
	DH_AXE_SMASH("dhAxeSmash", "DH Axe Smash", Sound.DH_AXE_SMASH),
	DH_AXE_BLOCK("dhAxeBlock", "DH Axe Block", Sound.DH_AXE_BLOCK),
	TOA_PURPLE_CHEST("toaPurpleChest", "TOA Purple Chest", Sound.GETTING_PURPLE_1),
	TOA_WHITE_CHEST("toaWhiteChest", "TOA White Chest", Sound.WHITE_LIGHT_AFTER_RAID),
	TOA_CHEST_OPENS("toaChestOpens", "TOA Chest Opens", Sound.TOA_CHEST_OPENS),
	TOB_PURPLE_CHEST("tobPurpleChest", "TOB Purple Chest", Sound.GETTING_PURPLE_1),
	TOB_WHITE_CHEST("tobWhiteChest", "TOB White Chest", Sound.WHITE_LIGHT_AFTER_RAID),
	COX_PURPLE_CHEST("coxPurpleChest", "COX Purple Chest", Sound.GETTING_PURPLE_1),
	COX_WHITE_CHEST("coxWhiteChest", "COX White Chest", Sound.WHITE_LIGHT_AFTER_RAID),
	PK_CHEST("pkChest", "PK Chest", Sound.CLICKING_PK_LOOT_CHEST),
	GIVE_BONE("giveBone", "Give Bone", Sound.EASTER_EGG_STRAYDOG_BONE),
	HAIRDRESSER("hairdresser", "Hairdresser", Sound.HAIRDRESSER_SOUND_1),
	SNOWBALLED("snowballed", "Snowballed", Sound.SNOWBALL_1),
	KILLING_RAT("killingRat", "Killing Rat/Scurrius", Sound.KILLING_RAT_OR_SCURRIUS_1),
	LIVESTREAM_GO_LIVE("livestreamGoLive", "Livestream Go Live", Sound.GAMON_GO_LIVE);

	private final String key;
	private final String displayName;
	private final Sound defaultSound;
}
