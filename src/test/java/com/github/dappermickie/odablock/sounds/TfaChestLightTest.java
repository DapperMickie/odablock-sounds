package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class TfaChestLightTest
{
	private final Client client = mock(Client.class);
	private final OdablockConfig config = mock(OdablockConfig.class);
	private final SoundEngine engine = mock(SoundEngine.class);
	private final ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
	private final TfaChestLight handler = new TfaChestLight(client, config, engine, executor);
	private final LocalPoint location = new LocalPoint(64, 64);
	private MockedStatic<WorldPoint> worldPoints;

	@Before
	public void setUp()
	{
		Player player = mock(Player.class);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getLocalLocation()).thenReturn(location);
		when(config.tfaWhiteChest()).thenReturn(true);
		when(config.tfaPurpleChest()).thenReturn(true);
		worldPoints = mockStatic(WorldPoint.class);
		setRegion(16474);
	}

	@After
	public void tearDown()
	{
		worldPoints.close();
	}

	@Test
	public void whitePlaysOnceEvenWhenChestOpensOrNpcsTemporarilyDisappear()
	{
		doReturn(Collections.singletonList(chest(NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_MINE))).when(client).getNpcs();
		tick();
		verifyNoInteractions(engine);
		tick();
		doReturn(Collections.emptyList()).when(client).getNpcs();
		tick();
		doReturn(Collections.singletonList(chest(NpcID.TFA_REWARDS_CHEST_VAULT_OPEN_MINE))).when(client).getNpcs();
		tick();
		verify(engine).playClip(Sound.WHITE_LIGHT_AFTER_RAID, executor);
		verifyNoMoreInteractions(engine);
	}

	@Test
	public void waitsForUniqueRevealBeforePlayingPurpleOnce()
	{
		List<NPC> chests = standardChests();
		doReturn(chests).when(client).getNpcs();
		tick();
		verifyNoInteractions(engine);
		tick();
		verifyNoInteractions(engine);
		chests.add(chest(NpcID.TFA_UNIQUE_CHEST_VAULT_CLOSED));
		tick();
		verifyNoInteractions(engine);
		tick();
		verify(engine).playClip(Sound.GETTING_PURPLE_1, executor);
		verifyNoMoreInteractions(engine);
	}

	@Test
	public void disabledPurpleDoesNotFallBackToWhite()
	{
		when(config.tfaPurpleChest()).thenReturn(false);
		doReturn(Collections.singletonList(chest(NpcID.TFA_UNIQUE_CHEST_VAULT_CLOSED))).when(client).getNpcs();
		tick();
		tick();
		verifyNoInteractions(engine);
	}

	@Test
	public void whiteToggleIsIndependentOfPurpleToggle()
	{
		when(config.tfaWhiteChest()).thenReturn(false);
		doReturn(Collections.singletonList(chest(NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_MINE))).when(client).getNpcs();
		tick();
		tick();
		verifyNoInteractions(engine);
		handler.reset();
		doReturn(Collections.singletonList(chest(NpcID.TFA_UNIQUE_CHEST_VAULT_CLOSED))).when(client).getNpcs();
		tick();
		tick();
		verify(engine).playClip(Sound.GETTING_PURPLE_1, executor);
	}

	@Test
	public void leavingLootRoomAllowsNextRunToPlay()
	{
		doReturn(Collections.singletonList(chest(NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_MINE))).when(client).getNpcs();
		tick();
		tick();
		setRegion(16470);
		tick();
		setRegion(16474);
		tick();
		tick();
		verify(engine, times(2)).playClip(Sound.WHITE_LIGHT_AFTER_RAID, executor);
	}

	@Test
	public void sceneReloadDoesNotReplayButWorldHopResets()
	{
		doReturn(Collections.singletonList(chest(NpcID.TFA_UNIQUE_CHEST_VAULT_CLOSED))).when(client).getNpcs();
		tick();
		tick();
		GameStateChanged state = new GameStateChanged();
		state.setGameState(GameState.LOADING);
		handler.onGameStateChanged(state);
		tick();
		verify(engine).playClip(Sound.GETTING_PURPLE_1, executor);
		state.setGameState(GameState.HOPPING);
		handler.onGameStateChanged(state);
		tick();
		tick();
		verify(engine, times(2)).playClip(Sound.GETTING_PURPLE_1, executor);
	}

	@Test
	public void partiallyLoadedChestsDoNotCountAsMissingSparkle()
	{
		List<NPC> chests = standardChests();
		chests.remove(0);
		doReturn(chests).when(client).getNpcs();
		tick();
		tick();
		verifyNoInteractions(engine);
		chests.add(chest(NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_MINE));
		tick();
		tick();
		verify(engine).playClip(Sound.WHITE_LIGHT_AFTER_RAID, executor);
		verifyNoMoreInteractions(engine);
	}

	@Test
	public void sparkleArrivingOnFollowingTickPlaysWhiteEvenIfTeammateHasPurple()
	{
		List<NPC> chests = standardChests();
		doReturn(chests).when(client).getNpcs();
		tick();
		when(chests.get(0).hasSpotAnim(SpotanimID.VFX_TFA_SMALL_CHEST_SPARKLES)).thenReturn(true);
		chests.add(chest(NpcID.TFA_UNIQUE_CHEST_VAULT_CLOSED));
		tick();
		tick();
		verify(engine).playClip(Sound.WHITE_LIGHT_AFTER_RAID, executor);
		verifyNoMoreInteractions(engine);
	}

	@Test
	public void unrelatedOrHiddenChestsDoNotTriggerSound()
	{
		doReturn(Arrays.asList(mock(NPC.class),
			chest(NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_NOTMINE), chest(1))).when(client).getNpcs();
		tick();
		tick();
		verifyNoInteractions(engine);
	}

	private void setRegion(int region)
	{
		WorldPoint point = mock(WorldPoint.class);
		when(point.getRegionID()).thenReturn(region);
		worldPoints.when(() -> WorldPoint.fromLocalInstance(client, location)).thenReturn(point);
	}

	private NPC chest(int transformedId)
	{
		NPC npc = mock(NPC.class);
		NPCComposition composition = mock(NPCComposition.class);
		when(npc.getTransformedComposition()).thenReturn(composition);
		when(composition.getId()).thenReturn(transformedId);
		return npc;
	}

	private List<NPC> standardChests()
	{
		List<NPC> chests = new ArrayList<>();
		for (int i = 0; i < 8; i++)
		{
			chests.add(chest(NpcID.TFA_REWARDS_CHEST_VAULT_CLOSED_NOTMINE));
		}
		return chests;
	}

	private void tick()
	{
		handler.onGameTick(new GameTick());
	}
}
