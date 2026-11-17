package com.github.dappermickie.odablock;

import com.github.dappermickie.odablock.sounds.AcbSpec;
import com.github.dappermickie.odablock.sounds.AgsSpec;
import com.github.dappermickie.odablock.sounds.DdsSpec;
import com.github.dappermickie.odablock.sounds.EnteringBankPin;
import com.github.dappermickie.odablock.sounds.PrayerDown;
import com.github.dappermickie.odablock.sounds.RedemptionProc;
import com.github.dappermickie.odablock.sounds.RubyBoltProc;
import com.github.dappermickie.odablock.sounds.ZebakRoar;
import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.SoundEffectPlayed;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RubyBoltProcTest
{
	private final OdablockPlugin plugin = new OdablockPlugin();
	private final RubyBoltProc ruby = new RubyBoltProc();
	private final Client client = mock(Client.class);
	private final Player local = mock(Player.class);
	private final OdablockConfig config = mock(OdablockConfig.class);
	private final SoundEngine engine = mock(SoundEngine.class);
	private final ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
	private final AgsSpec ags = mock(AgsSpec.class);

	@Before
	public void setUp() throws Exception
	{
		when(client.getLocalPlayer()).thenReturn(local);
		when(config.ownPlayerOnly()).thenReturn(true);
		when(config.rubyBoltProc()).thenReturn(true);
		inject(ruby, "client", client);
		inject(ruby, "config", config);
		inject(ruby, "soundEngine", engine);
		inject(ruby, "executor", executor);
		inject(plugin, "client", client);
		inject(plugin, "config", config);
		inject(plugin, "rubyBoltProc", ruby);
		inject(plugin, "enteringBankPin", mock(EnteringBankPin.class));
		inject(plugin, "zebakRoar", mock(ZebakRoar.class));
		inject(plugin, "redemptionProc", mock(RedemptionProc.class));
		inject(plugin, "agsSpec", ags);
		inject(plugin, "ddsSpec", mock(DdsSpec.class));
		inject(plugin, "prayerDown", mock(PrayerDown.class));
		inject(plugin, "acbSpec", mock(AcbSpec.class));
	}

	@Test
	public void recordedNullSourceProcPlaysWithOwnPlayerOnlyEnabled()
	{
		SoundEffectPlayed event = soundEvent(null, 2911, 2);
		plugin.onSoundEffectPlayed(event);
		assertTrue(event.isConsumed());
		verify(engine).playClip(Sound.RUBY_PROC, executor);
		verifyNoInteractions(ags);
	}

	@Test
	public void explicitLocalPlayerProcPlays()
	{
		SoundEffectPlayed event = soundEvent(local, 2911, 0);
		plugin.onSoundEffectPlayed(event);
		assertTrue(event.isConsumed());
		verify(engine).playClip(Sound.RUBY_PROC, executor);
	}

	@Test
	public void explicitOtherPlayerProcIsFiltered()
	{
		SoundEffectPlayed event = soundEvent(mock(Player.class), 2911, 0);
		plugin.onSoundEffectPlayed(event);
		assertFalse(event.isConsumed());
		verifyNoInteractions(engine);
	}

	@Test
	public void disabledRubyReplacementLeavesOriginalSound()
	{
		when(config.rubyBoltProc()).thenReturn(false);
		SoundEffectPlayed event = soundEvent(null, 2911, 2);
		plugin.onSoundEffectPlayed(event);
		assertFalse(event.isConsumed());
		verifyNoInteractions(engine);
	}

	@Test
	public void nullSourceWithoutLoggedInPlayerIsIgnored()
	{
		when(client.getLocalPlayer()).thenReturn(null);
		SoundEffectPlayed event = soundEvent(null, 2911, 2);
		plugin.onSoundEffectPlayed(event);
		assertFalse(event.isConsumed());
		verifyNoInteractions(engine);
	}

	@Test
	public void unrelatedNullSourceSoundRemainsFiltered()
	{
		SoundEffectPlayed event = soundEvent(null, SoundIds.AGS_SPEC.Id, 0);
		plugin.onSoundEffectPlayed(event);
		assertFalse(event.isConsumed());
		verifyNoInteractions(engine, ags);
	}

	@Test
	public void otherPlayerAllowedWhenOwnPlayerOnlyDisabled()
	{
		when(config.ownPlayerOnly()).thenReturn(false);
		SoundEffectPlayed event = soundEvent(mock(Player.class), 2911, 0);
		plugin.onSoundEffectPlayed(event);
		assertTrue(event.isConsumed());
		verify(engine).playClip(Sound.RUBY_PROC, executor);
	}

	private static SoundEffectPlayed soundEvent(net.runelite.api.Actor source, int id, int delay)
	{
		SoundEffectPlayed event = new SoundEffectPlayed(source);
		event.setSoundId(id);
		event.setDelay(delay);
		return event;
	}

	private static void inject(Object target, String name, Object value) throws Exception
	{
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}
}
