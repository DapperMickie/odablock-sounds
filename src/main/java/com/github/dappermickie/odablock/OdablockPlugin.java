package com.github.dappermickie.odablock;

import com.github.dappermickie.odablock.emotes.EmoteHandler;
import com.github.dappermickie.odablock.livestreams.LivestreamManager;
import com.github.dappermickie.odablock.notifications.NotificationManager;
import com.github.dappermickie.odablock.ui.SoundOverridesPanel;
import com.github.dappermickie.odablock.sounds.AcbSpec;
import com.github.dappermickie.odablock.sounds.AcceptTrade;
import com.github.dappermickie.odablock.sounds.AchievementDiaries;
import com.github.dappermickie.odablock.sounds.AgsSpec;
import com.github.dappermickie.odablock.sounds.CollectionLog;
import com.github.dappermickie.odablock.sounds.CombatAchievements;
import com.github.dappermickie.odablock.sounds.CoxSounds;
import com.github.dappermickie.odablock.sounds.CryptSound;
import com.github.dappermickie.odablock.sounds.CustomBarrowsFaces;
import com.github.dappermickie.odablock.sounds.CustomBarrowsFacesOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import com.github.dappermickie.odablock.sounds.DdsSpec;
import com.github.dappermickie.odablock.sounds.Death;
import com.github.dappermickie.odablock.sounds.DeclineTrade;
import com.github.dappermickie.odablock.sounds.DhAxe;
import com.github.dappermickie.odablock.sounds.DismissRandomEvent;
import com.github.dappermickie.odablock.sounds.EmptyChestSound;
import com.github.dappermickie.odablock.sounds.EnteringBankPin;
import com.github.dappermickie.odablock.sounds.FreezeSound;
import com.github.dappermickie.odablock.sounds.GiveBone;
import com.github.dappermickie.odablock.sounds.HairDresser;
import com.github.dappermickie.odablock.sounds.ItemDrop;
import com.github.dappermickie.odablock.sounds.KillingPlayer;
import com.github.dappermickie.odablock.sounds.KillingRat;
import com.github.dappermickie.odablock.sounds.LevelUp;
import com.github.dappermickie.odablock.sounds.OdablockWarriors;
import com.github.dappermickie.odablock.sounds.Pet;
import com.github.dappermickie.odablock.sounds.PetDog;
import com.github.dappermickie.odablock.sounds.PkChest;
import com.github.dappermickie.odablock.sounds.PrayerDown;
import com.github.dappermickie.odablock.sounds.RandomEventSpawned;
import com.github.dappermickie.odablock.sounds.QuestCompleted;
import com.github.dappermickie.odablock.sounds.RedemptionProc;
import com.github.dappermickie.odablock.sounds.ReportPlayer;
import com.github.dappermickie.odablock.sounds.RubyBoltProc;
import com.github.dappermickie.odablock.sounds.SpellbookSwap;
import com.github.dappermickie.odablock.sounds.SnowBalled;
import com.github.dappermickie.odablock.sounds.ToaChestLight;
import com.github.dappermickie.odablock.sounds.TfaChestLight;
import com.github.dappermickie.odablock.sounds.ToaChestOpens;
import com.github.dappermickie.odablock.sounds.TobChestLight;
import com.github.dappermickie.odablock.sounds.TurnOnRun;
import com.github.dappermickie.odablock.sounds.Vengeance;
import com.github.dappermickie.odablock.sounds.WelcomeScreenSound;
import com.github.dappermickie.odablock.sounds.ZebakRoar;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
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
import net.runelite.api.events.NpcSpawned;
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
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
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
	private TfaChestLight tfaChestLight;

	@Inject
	private ZebakRoar zebakRoar;

	@Inject
	private KillingPlayer killingPlayer;

	@Inject
	private OdablockWarriors odablockWarriors;

	@Inject
	private SpellbookSwap spellbookSwap;

	@Inject
	private FreezeSound freezeSound;

	@Inject
	private CryptSound cryptSound;

	@Inject
	private WelcomeScreenSound welcomeScreen;

	@Inject
	private com.github.dappermickie.odablock.sounds.PhoenixNecklace phoenixNecklace;

	@Inject
	private EmptyChestSound emptyChestSound;

	@Inject
	private RandomEventSpawned randomEventSpawned;

	@Inject
	private ItemDrop itemDrop;
	// End of sound injections

	@Inject
	private CustomBarrowsFaces customBarrowsFaces;

	@Inject
	private CustomBarrowsFacesOverlay customBarrowsFacesOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private LivestreamManager livestreamManager;

	@Inject
	private NotificationManager notificationManager;

	@Inject
	private ChatRightClickManager chatRightClickManager;

	@Inject
	private EmoteHandler emoteHandler;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private SoundOverridesPanel soundOverridesPanel;



	private NavigationButton soundOverridesNavigationButton;

	public static final String ODABLOCK = "Odablock";

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(this::setupOldMaps);
		achievementDiaries.setLastLoginTick(-1);
		prayerDown.setLastLoginTick(-1);
		spellbookSwap.setLastLoginTick(-1);
		emoteHandler.loadEmotes();
		SwingUtilities.invokeLater(this::setUpOverridesNavigation);
		overlayManager.add(customBarrowsFacesOverlay);
		executor.submit(() -> {
			PlayerKillLineManager.Setup(okHttpClient);
			SoundFileManager.ensureDownloadDirectoryExists();
			SoundFileManager.downloadAllMissingSounds(okHttpClient);
			SnowballUserManager.ensureDownloadDirectoryExists();
			SnowballUserManager.downloadSnowballUsers(okHttpClient);
			customBarrowsFaces.reloadImages();
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		tfaChestLight.reset();
		levelUp.clear();
		achievementDiaries.clearOldAchievementDiaries();
		soundEngine.close();
		customBarrowsFaces.shutDown();
		overlayManager.remove(customBarrowsFacesOverlay);
		SwingUtilities.invokeLater(this::removeOverridesNavigation);
	}

	private void setUpOverridesNavigation()
	{
		removeOverridesNavigation();
		if (!config.showSidebar())
		{
			return;
		}
		soundOverridesNavigationButton = NavigationButton.builder()
			.tooltip("Odablock Sound Overrides")
			.icon(createOverridesIcon())
			.priority(8)
			.panel(soundOverridesPanel)
			.build();
		clientToolbar.addNavigation(soundOverridesNavigationButton);
	}

	private void removeOverridesNavigation()
	{
		if (soundOverridesNavigationButton != null)
		{
			clientToolbar.removeNavigation(soundOverridesNavigationButton);
			soundOverridesNavigationButton = null;
		}
	}

	private static BufferedImage createOverridesIcon()
	{
		final int size = 16;
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

			Color speakerColor = new Color(235, 235, 235);
			graphics.setColor(speakerColor);

			int[] speakerX = {3, 6, 9, 9, 6, 3};
			int[] speakerY = {6, 6, 3, 13, 10, 10};
			graphics.fillPolygon(speakerX, speakerY, speakerX.length);

			graphics.setStroke(new java.awt.BasicStroke(1.4f));
			Color waveColor = new Color(255, 152, 41);
			graphics.setColor(waveColor);
			graphics.drawArc(7, 4, 4, 8, -55, 110);
			graphics.drawArc(9, 2, 5, 12, -55, 110);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
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
		tfaChestLight.onGameStateChanged(event);
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
				livestreamManager.resetStateForWorldHopOrLogin();
				notificationManager.resetStateForWorldHopOrLogin();

				achievementDiaries.setLastLoginTick(-1);
				prayerDown.setLastLoginTick(-1);
				spellbookSwap.setLastLoginTick(-1);
				collectionLog.setlastColLogSettingWarning();
				break;
			case LOGGED_IN:
				final int currentTick = client.getTickCount();
				levelUp.setOldExperience();
				levelUp.setLastLoginTick(currentTick);
				achievementDiaries.setLastLoginTick(currentTick);
				prayerDown.setLastLoginTick(currentTick);
				spellbookSwap.setLastLoginTick(currentTick);
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
		reportPlayer.onChatMessage(chatMessage);

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
		else if (freezeSound.onChatMessage(chatMessage))
		{
			return;
		}
		else if (cryptSound.onChatMessage(chatMessage))
		{
			return;
		}
		else if (phoenixNecklace.onChatMessage(chatMessage))
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
		if (config.developerLogging())
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
		spellbookSwap.onVarbitChanged(event);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (config.developerLogging())
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
		if (config.developerLogging())
		{
			debugScripts.onWidgetLoaded(event);
		}

		hairDresser.onWidgetLoaded(event);
		pkChest.onWidgetLoaded(event);
		emptyChestSound.onWidgetLoaded(event);
		odablockWarriors.onWidgetLoaded(event);
		welcomeScreen.onWidgetLoaded(event);
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
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		randomEventSpawned.onNpcSpawned(npcSpawned);
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived event)
	{
		itemDrop.onNpcLootReceived(event);
	}

	@Subscribe
	public void onPlayerLootReceived(PlayerLootReceived event)
	{
		itemDrop.onPlayerLootReceived(event);
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
			odablockWarriors.onConfigChanged(event.getKey());
			if (event.getKey().equals("showSidebar"))
			{
				SwingUtilities.invokeLater(this::setUpOverridesNavigation);
			}
			if (event.getKey().startsWith("customBarrows"))
			{
				customBarrowsFaces.reloadImages();
			}
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
		tfaChestLight.onGameTick(event);
		randomEventSpawned.onGameTick(event);
		livestreamManager.onGameTick(event);
		notificationManager.onGameTick(event);
		chatRightClickManager.onGameTick(event);
		collectionLog.onGameTick(event);
		pet.onGameTick(event);
		customBarrowsFaces.onGameTick();
		odablockWarriors.onGameTick(event);

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
		if (!shouldHandlePlayerSound(event.getSource()))
		{
			return;
		}
		acbSpec.onAreaSoundEffectPlayed(event);
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		// UI sounds have no actor; boss sounds intentionally originate from an NPC.
		enteringBankPin.onSoundEffectPlayed(event);
		zebakRoar.onSoundEffectPlayed(event);

		if (!shouldHandlePlayerSound(event.getSource()))
		{
			return;
		}
		rubyBoltProc.onSoundEffectPlayed(event);
		redemptionProc.onSoundPlayed(event);
		agsSpec.onSoundEffectPlayed(event);
		ddsSpec.onSoundEffectPlayed(event);
		prayerDown.onSoundEffectPlayed(event);
		acbSpec.onSoundEffectPlayed(event);
	}

	private boolean shouldHandlePlayerSound(net.runelite.api.Actor source)
	{
		// Unknown sources cannot be attributed to our player, including during login.
		return !config.ownPlayerOnly()
			|| (source != null && source == client.getLocalPlayer());
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
		coxSounds.onGameObjectSpawned(event);
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		tobChestLight.onGameObjectDespawned(event);
		coxSounds.onGameObjectDespawned(event);
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
	{
		if (config.developerLogging())
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

	public static int TO_GROUP(int id)
	{
		return id >>> 16;
	}
}
