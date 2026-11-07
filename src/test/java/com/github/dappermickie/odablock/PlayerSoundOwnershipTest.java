package com.github.dappermickie.odablock;

import com.github.dappermickie.odablock.sounds.*;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.SoundEffectPlayed;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerSoundOwnershipTest
{
	@Mock private Client client;
	@Mock private OdablockConfig config;
	@Mock private AcbSpec acbSpec;
	@Mock private RubyBoltProc rubyBoltProc;
	@Mock private AgsSpec agsSpec;
	@Mock private DdsSpec ddsSpec;
	@Mock private RedemptionProc redemptionProc;
	@Mock private PrayerDown prayerDown;
	@Mock private EnteringBankPin enteringBankPin;
	@Mock private ZebakRoar zebakRoar;
	@InjectMocks private OdablockPlugin plugin;

	@Test
	public void ownOnlyRejectsOtherPlayersNpcsAndUnknownSources()
	{
		when(config.ownPlayerOnly()).thenReturn(true);
		when(client.getLocalPlayer()).thenReturn(mock(Player.class));
		for (Actor source : new Actor[]{mock(Player.class), mock(NPC.class), null})
		{
			for (SoundIds sound : new SoundIds[]{SoundIds.ACB_SPEC, SoundIds.RUBY_BOLT_PROC,
				SoundIds.AGS_SPEC, SoundIds.DDS_SPEC, SoundIds.REDEMPTION_PROC, SoundIds.PRAYER_DOWN})
			{
				SoundEffectPlayed event = soundEvent(source, sound.Id);
				plugin.onSoundEffectPlayed(event);
				assertFalse(event.isConsumed());
			}
			AreaSoundEffectPlayed area = areaEvent(source);
			plugin.onAreaSoundEffectPlayed(area);
			assertFalse(area.isConsumed());
		}
		verifyNoInteractions(acbSpec, rubyBoltProc, agsSpec, ddsSpec, redemptionProc, prayerDown);
	}

	@Test
	public void ownOnlyAllowsLocalPlayerThroughBothRoutes()
	{
		Player local = mock(Player.class);
		when(config.ownPlayerOnly()).thenReturn(true);
		when(client.getLocalPlayer()).thenReturn(local);
		assertPlayerSoundsRouted(local);
	}

	@Test
	public void disabledOwnOnlyPreservesOtherAndUnknownSourceRouting()
	{
		assertPlayerSoundsRouted(mock(Player.class));
		assertPlayerSoundsRouted(mock(NPC.class));
		assertPlayerSoundsRouted(null);
	}

	@Test
	public void noLocalPlayerDoesNotMakeUnknownSourceLocal()
	{
		when(config.ownPlayerOnly()).thenReturn(true);
		plugin.onSoundEffectPlayed(soundEvent(null, SoundIds.ACB_SPEC.Id));
		plugin.onAreaSoundEffectPlayed(areaEvent(null));
		verifyNoInteractions(acbSpec, rubyBoltProc, agsSpec, ddsSpec, redemptionProc, prayerDown);
	}

	@Test
	public void ownOnlyPreservesBankPinAndBossSoundRouting()
	{
		when(config.ownPlayerOnly()).thenReturn(true);
		SoundEffectPlayed pin = soundEvent(null, SoundIds.BANK_PIN.Id);
		SoundEffectPlayed roar = soundEvent(mock(NPC.class), 5829);
		plugin.onSoundEffectPlayed(pin);
		plugin.onSoundEffectPlayed(roar);
		verify(enteringBankPin).onSoundEffectPlayed(pin);
		verify(zebakRoar).onSoundEffectPlayed(roar);
		verifyNoInteractions(acbSpec, rubyBoltProc, agsSpec, ddsSpec, redemptionProc, prayerDown);
	}

	private void assertPlayerSoundsRouted(Actor source)
	{
		SoundEffectPlayed event = soundEvent(source, SoundIds.ACB_SPEC.Id);
		plugin.onSoundEffectPlayed(event);
		verify(acbSpec).onSoundEffectPlayed(event);
		verify(rubyBoltProc).onSoundEffectPlayed(event);
		verify(agsSpec).onSoundEffectPlayed(event);
		verify(ddsSpec).onSoundEffectPlayed(event);
		verify(redemptionProc).onSoundPlayed(event);
		verify(prayerDown).onSoundEffectPlayed(event);
		AreaSoundEffectPlayed area = areaEvent(source);
		plugin.onAreaSoundEffectPlayed(area);
		verify(acbSpec).onAreaSoundEffectPlayed(area);
	}

	private static SoundEffectPlayed soundEvent(Actor source, int soundId)
	{
		SoundEffectPlayed event = new SoundEffectPlayed(source);
		event.setSoundId(soundId);
		return event;
	}

	private static AreaSoundEffectPlayed areaEvent(Actor source)
	{
		AreaSoundEffectPlayed event = new AreaSoundEffectPlayed(source);
		event.setSoundId(SoundIds.ACB_SPEC.Id);
		return event;
	}
}
