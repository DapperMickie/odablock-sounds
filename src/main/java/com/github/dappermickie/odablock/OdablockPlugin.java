package com.github.dappermickie.odablock;

import com.github.dappermickie.odablock.emotes.EmoteHandler;
import com.github.dappermickie.odablock.livestreams.LivestreamManager;
import com.github.dappermickie.odablock.notifications.NotificationManager;
import com.github.dappermickie.odablock.sounds.AcbSpec;
import com.github.dappermickie.odablock.sounds.AcceptTrade;
import com.github.dappermickie.odablock.sounds.AchievementDiaries;
import com.github.dappermickie.odablock.sounds.AgsSpec;
import com.github.dappermickie.odablock.sounds.CollectionLog;
import com.github.dappermickie.odablock.sounds.CombatAchievements;
import com.github.dappermickie.odablock.sounds.CoxSounds;
import com.github.dappermickie.odablock.sounds.DdsSpec;
import com.github.dappermickie.odablock.sounds.Death;
import com.github.dappermickie.odablock.sounds.DeclineTrade;
import com.github.dappermickie.odablock.sounds.DhAxe;
import com.github.dappermickie.odablock.sounds.DismissRandomEvent;
import com.github.dappermickie.odablock.sounds.EnteringBankPin;
import com.github.dappermickie.odablock.sounds.GiveBone;
import com.github.dappermickie.odablock.sounds.HairDresser;
import com.github.dappermickie.odablock.sounds.KillingPlayer;
import com.github.dappermickie.odablock.sounds.KillingRat;
import com.github.dappermickie.odablock.sounds.LevelUp;
import com.github.dappermickie.odablock.sounds.OdablockWarriors;
import com.github.dappermickie.odablock.sounds.Pet;
import com.github.dappermickie.odablock.sounds.PetDog;
import com.github.dappermickie.odablock.sounds.PkChest;
import com.github.dappermickie.odablock.sounds.PrayerDown;
import com.github.dappermickie.odablock.sounds.QuestCompleted;
import com.github.dappermickie.odablock.sounds.RedemptionProc;
import com.github.dappermickie.odablock.sounds.ReportPlayer;
import com.github.dappermickie.odablock.sounds.RubyBoltProc;
import com.github.dappermickie.odablock.sounds.SnowBalled;
import com.github.dappermickie.odablock.sounds.ToaChestLight;
import com.github.dappermickie.odablock.sounds.ToaChestOpens;
import com.github.dappermickie.odablock.sounds.TobChestLight;
import com.github.dappermickie.odablock.sounds.TurnOnRun;
import com.github.dappermickie.odablock.sounds.Vengeance;
import com.github.dappermickie.odablock.sounds.ZebakRoar;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.OkHttpClient;

@Slf4j
@PluginDescriptor(
	name = "Odablock Plugin",
	description = "Replace and add in-game sounds by Odablock",
	tags = {"odablock", "stats", "levels", "quests", "diary", "announce"}
)

public class OdablockPlugin extends Plugin
{
	@Inject
	private Client client;

	@Getter(AccessLevel.PACKAGE)
	@Inject
	private ClientThread clientThread;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private OdablockConfig config;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private OkHttpClient okHttpClient;


	// Start of sound injections
	@Inject
	private RedemptionProc redemptionProc;

	@Inject
	private LevelUp levelUp;

	@Inject
	private DdsSpec ddsSpec;

	@Inject
	private AgsSpec agsSpec;

	@Inject
	private Death death;

	@Inject
	private AcceptTrade acceptTrade;

	@Inject
	private PetDog petDog;

	@Inject
	private DebugScripts debugScripts;

	@Inject
	private PrayerDown prayerDown;

	@Inject
	private TurnOnRun turnOnRun;

	@Inject
	private ReportPlayer reportPlayer;

	@Inject
	private DeclineTrade declineTrade;

	@Inject
	private AcbSpec acbSpec;

	@Inject
	private DismissRandomEvent dismissRandomEvent;

	@Inject
	private RubyBoltProc rubyBoltProc;

	@Inject
	private EnteringBankPin enteringBankPin;

	@Inject
	private Pet pet;

	@Inject
	private DhAxe dhAxe;

	@Inject
	private KillingRat killingRat;

	@Inject
	private ToaChestLight toaChestLight;

	@Inject
	private ToaChestOpens toaChestOpens;

	@Inject
	private TobChestLight tobChestLight;

	@Inject
	private CollectionLog collectionLog;

	@Inject
	private QuestCompleted questCompleted;

	@Inject
	private CombatAchievements combatAchievements;

	@Inject
	private AchievementDiaries achievementDiaries;

	@Inject
	private GiveBone giveBone;

	@Inject
	private SnowBalled snowBalled;

	@Inject
	private HairDresser hairDresser;

	@Inject
	private PkChest pkChest;

	@Inject
	private Vengeance vengeance;

	@Inject
	private CoxSounds coxSounds;

	@Inject
	private ZebakRoar zebakRoar;

	@Inject
	private KillingPlayer killingPlayer;

	@Inject
	private OdablockWarriors odablockWarriors;
	// End of sound injections

	@Inject
	private LivestreamManager livestreamManager;

	@Inject
	private NotificationManager notificationManager;

	@Inject
	private ChatRightClickManager chatRightClickManager;

	@Inject
	private EmoteHandler emoteHandler;

	@Inject
	@Named("developerMode")
	private boolean developerMode;

	public static final String ODABLOCK = "Odablock";

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(this::setupOldMaps);
		achievementDiaries.setLastLoginTick(-1);
		prayerDown.setLastLoginTick(-1);
		emoteHandler.loadEmotes();
		executor.submit(() -> {
			PlayerKillLineManager.Setup(okHttpClient);
			SoundFileManager.ensureDownloadDirectoryExists();
			SoundFileManager.downloadAllMissingSounds(okHttpClient);
			SnowballUserManager.ensureDownloadDirectoryExists();
			SnowballUserManager.downloadSnowballUsers(okHttpClient);
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		levelUp.clear();
		achievementDiaries.clearOldAchievementDiaries();
		soundEngine.close();
	}

	private void setupOldMaps()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			levelUp.clear();
			achievementDiaries.clearOldAchievementDiaries();
		}
		else
		{
			levelUp.setOldExperience();
			achievementDiaries.setOldAchievementDiaries();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		collectionLog.onGameStateChanged(event);
		switch (event.getGameState())
		{
			case LOGIN_SCREEN:
			case HOPPING:
			case LOGGING_IN:
			case LOGIN_SCREEN_AUTHENTICATOR:
				levelUp.clear();
				achievementDiaries.clearOldAchievementDiaries();
			case CONNECTION_LOST:
				// set to -1 here in-case of race condition with varbits changing before this handler is called
				// when game state becomes LOGGED_IN
				//soundEngine.playClip(Sound.CLIENT_DISCONNECTS, executor);

				achievementDiaries.setLastLoginTick(-1);
				prayerDown.setLastLoginTick(-1);
				collectionLog.setlastColLogSettingWarning();
				break;
			case LOGGED_IN:
				final int currentTick = client.getTickCount();
				achievementDiaries.setLastLoginTick(currentTick);
				prayerDown.setLastLoginTick(currentTick);
				break;
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		levelUp.onStatChanged(statChanged);
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath)
	{
		death.onActorDeath(actorDeath);
	}


	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		emoteHandler.onChatMessage(chatMessage);

		if (acceptTrade.onChatMessage(chatMessage))
		{
			return;
		}
		else if (petDog.onChatMessage(chatMessage))
		{
			return;
		}
		else if (pet.onChatMessage(chatMessage))
		{
			return;
		}
		else if (killingRat.onChatMessage(chatMessage))
		{
			return;
		}
		else if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE && chatMessage.getType() != ChatMessageType.SPAM)
		{
			return;
		}
		else if (collectionLog.onChatMessage(chatMessage))
		{
			return;

		}
		else if (questCompleted.onChatMessage(chatMessage))
		{
			return;
		}
		else if (combatAchievements.onChatMessage(chatMessage))
		{
			return;
		}
		else if (giveBone.onChatMessage(chatMessage))
		{
			return;
		}
		else if (killingPlayer.onChatMessage(chatMessage))
		{
			return;
		}
		else if (coxSounds.onChatMessage(chatMessage))
		{
			return;
		}
	}

	@Subscribe
	private void onOverheadTextChanged(OverheadTextChanged event)
	{
		emoteHandler.onOverheadTextChanged(event);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (developerMode)
		{
			debugScripts.onVarbitChanged(event);
		}

		vengeance.onVarbitChanged(event);
		turnOnRun.onVarbitChanged(event);
		dhAxe.onVarbitChanged(event);
		tobChestLight.onVarbitChanged(event);
		collectionLog.onVarbitChanged(event);
		achievementDiaries.onVarbitChanged(event);
		killingPlayer.onVarbitChanged(event);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (developerMode)
		{
			debugScripts.onMenuOptionClicked(menuOptionClicked);
		}

		petDog.onMenuOptionClicked(menuOptionClicked);
		turnOnRun.onMenuOptionClicked(menuOptionClicked);
		reportPlayer.onMenuOptionClicked(menuOptionClicked);
		declineTrade.onMenuOptionClicked(menuOptionClicked);
		dismissRandomEvent.onMenuOptionClicked(menuOptionClicked);
		odablockWarriors.onMenuOptionClicked(menuOptionClicked);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (developerMode)
		{
			debugScripts.onWidgetLoaded(event);
		}

		hairDresser.onWidgetLoaded(event);
		pkChest.onWidgetLoaded(event);
		odablockWarriors.onWidgetLoaded(event);

		if (event.getGroupId() == 621)
		{
			setWidget();
		}
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned playerSpawned)
	{
		snowBalled.onPlayerSpawned(playerSpawned);
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned playerDespawned)
	{
		snowBalled.onPlayerDespawned(playerDespawned);
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		killingRat.onNpcDespawned(npcDespawned);
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved projectileMoved)
	{
		snowBalled.onProjectileMoved(projectileMoved);
	}

	@Provides
	OdablockConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OdablockConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (OdablockConfig.CONFIG_GROUP.equals(event.getGroup()))
		{
			collectionLog.onConfigChanged(event);
		}
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event)
	{
		killingRat.onInteractingChanged(event);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		final Player local = client.getLocalPlayer();
		int currentTick = client.getTickCount();

		redemptionProc.onTick(currentTick, local);
		ddsSpec.onTick(currentTick, local);
		agsSpec.onTick(currentTick, local);
		prayerDown.onGameTick(event);
		tobChestLight.onGameTick(event);
		coxSounds.onGameTick(event);
		livestreamManager.onGameTick(event);
		notificationManager.onGameTick(event);
		chatRightClickManager.onGameTick(event);

		// Should always happen after all tick events
		cleanupTicks(currentTick);
	}

	private void cleanupTicks(final int currentTick)
	{
		agsSpec.cleanupTicks(currentTick);
		ddsSpec.cleanupTicks(currentTick);
		petDog.cleanupTicks(currentTick);
		redemptionProc.cleanupTicks(currentTick);
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event)
	{
		acbSpec.onAreaSoundEffectPlayed(event);
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		rubyBoltProc.onSoundEffectPlayed(event);
		enteringBankPin.onSoundEffectPlayed(event);
		redemptionProc.onSoundPlayed(event);
		agsSpec.onSoundEffectPlayed(event);
		ddsSpec.onSoundEffectPlayed(event);
		prayerDown.onSoundEffectPlayed(event);
		zebakRoar.onSoundEffectPlayed(event);
		acbSpec.onSoundEffectPlayed(event);
	}

	@Subscribe
	public void onWallObjectSpawned(final WallObjectSpawned event)
	{
		toaChestLight.onWallObjectSpawned(event);
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		toaChestOpens.onGameObjectSpawned(event);
		tobChestLight.onGameObjectSpawned(event);
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		tobChestLight.onGameObjectDespawned(event);
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
	{
		if (developerMode)
		{
			debugScripts.onScriptCallbackEvent(scriptCallbackEvent);
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted event)
	{
		emoteHandler.onCommandExecuted(event);
		//if(event.getCommand().equals("loadwarrior")) {
		//	odablockWarriors.onWidgetLoaded(null);
		//}
	}

	private void setWidget()
	{
		clientThread.invokeLater(this::lateSet);

	}

	private boolean lateSet()
	{
		// Left sidebar
		Widget titleContainer = client.getWidget(40697868);
		Widget[] titles = titleContainer.getDynamicChildren();
		if (titles.length == 0)
		{
			return false;
		}
		Widget title = titles[0];
		title.setText("Final Strand");
		titleContainer.revalidate();

		// Top of the inner container (right side)
		Widget titleClogContainer = client.getWidget(40697875);
		Widget[] titleClogContainerWidgets = titleClogContainer.getDynamicChildren();
		titleClogContainerWidgets[0].setText("Final Strand");
		titleClogContainerWidgets[1].setText("Obtained: <col=00ff00>9/11</col");
		titleClogContainerWidgets[2].setText("Final Strand Kills: <col=00ff00>911</col>");
		titleClogContainer.revalidate();

		// Items
		Widget itemsContainer = client.getWidget(40697892);

		itemsContainer.deleteAllChildren();

		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.ECLIPSE_ATLATL, 0);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.ATLATL_DART, 1, 715);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.REGEN_BRACELET, 2);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.HUEYCOATL_HIDE_CHAPS, 3);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.BERSERKER_NECKLACE, 4);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.TZHAARKETOM, 5);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.ROBIN_HOOD_HAT, 6);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.STRENGTH_AMULET_T, 7);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.BERSERKER_RING, 8);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.VARROCK_ARMOUR_1, 9);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.BLIGHTED_MANTA_RAY, 10, 10);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.SARADOMIN_BREW4, 11, 2);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.BLIGHTED_SUPER_RESTORE4, 12, 1);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.BLIGHTED_SUPER_RESTORE3, 13, 1);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.SUPER_STRENGTH3, 14, 1);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.SUPER_ATTACK3, 15, 1);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.FIRE_CAPE, 16, 1);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.BOUNTY_CRATE_TIER_8, 17, 1);
		createNewItem(itemsContainer, "Eclipse Atlatl", ItemID.HAIR, 18, 0);

		itemsContainer.revalidate();

		return true;
	}

	private void createNewItem(Widget container, String name, int id, int index)
	{
		createNewItem(container, name, id, index, 1);
	}

	private void createNewItem(Widget container, String name, int id, int index, int quantity)
	{
		int[] xValues = {0, 42, 84, 126, 168, 210};
		int x = xValues[index % 6];
		int y = (index / 6) * 36;

		Widget eclipseAtlatl = container.createChild(5);
		eclipseAtlatl.setItemId(id);
		eclipseAtlatl.setName("<col=ff9040>" + name + "</col>");
		eclipseAtlatl.setBorderType(1);
		eclipseAtlatl.setModelZoom(746);
		eclipseAtlatl.setItemQuantity(quantity);
		eclipseAtlatl.setItemQuantityMode(2);
		eclipseAtlatl.setOpacity(quantity > 0 ? 0 : 150);
		eclipseAtlatl.setOriginalHeight(36);
		eclipseAtlatl.setOriginalWidth(36);
		eclipseAtlatl.setOriginalX(x);
		eclipseAtlatl.setOriginalY(y);
		eclipseAtlatl.revalidate();
	}

	public static int TO_GROUP(int id)
	{
		return id >>> 16;
	}
}
