package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.SoundEngine;
import com.github.dappermickie.odablock.SoundIds;
import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.api.Client;
import net.runelite.api.events.SoundEffectPlayed;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrayerDownTest
{
	@Test
	public void doesNotConsumePrayerDownSoundWhenConfigDisabled() throws Exception
	{
		PrayerDown prayerDown = new PrayerDown();
		OdablockConfig config = mock(OdablockConfig.class);
		when(config.prayerMessage()).thenReturn(false);

		Client client = mock(Client.class);
		when(client.getTickCount()).thenReturn(10);

		RedemptionProc redemptionProc = mock(RedemptionProc.class);
		when(redemptionProc.HasRedemptionProcced()).thenReturn(false);

		SoundEffectPlayed event = mock(SoundEffectPlayed.class);
		when(event.getSoundId()).thenReturn(SoundIds.PRAYER_DOWN.Id);

		setField(prayerDown, "config", config);
		setField(prayerDown, "client", client);
		setField(prayerDown, "redemptionProc", redemptionProc);
		setField(prayerDown, "soundEngine", mock(SoundEngine.class));
		setField(prayerDown, "executor", mock(ScheduledExecutorService.class));
		prayerDown.setLastLoginTick(1);

		prayerDown.onSoundEffectPlayed(event);

		verify(event, never()).consume();
	}

	@Test
	public void doesNotConsumePrayerDownSoundDuringLoginGraceTicks() throws Exception
	{
		PrayerDown prayerDown = new PrayerDown();
		OdablockConfig config = mock(OdablockConfig.class);
		when(config.prayerMessage()).thenReturn(true);

		Client client = mock(Client.class);
		when(client.getTickCount()).thenReturn(2);

		RedemptionProc redemptionProc = mock(RedemptionProc.class);
		when(redemptionProc.HasRedemptionProcced()).thenReturn(false);

		SoundEffectPlayed event = mock(SoundEffectPlayed.class);
		when(event.getSoundId()).thenReturn(SoundIds.PRAYER_DOWN.Id);

		setField(prayerDown, "config", config);
		setField(prayerDown, "client", client);
		setField(prayerDown, "redemptionProc", redemptionProc);
		setField(prayerDown, "soundEngine", mock(SoundEngine.class));
		setField(prayerDown, "executor", mock(ScheduledExecutorService.class));
		prayerDown.setLastLoginTick(1);

		prayerDown.onSoundEffectPlayed(event);

		verify(event, never()).consume();
	}

	@Test
	public void consumesPrayerDownSoundWhenReplacementEnabledAndEligible() throws Exception
	{
		PrayerDown prayerDown = new PrayerDown();
		OdablockConfig config = mock(OdablockConfig.class);
		when(config.prayerMessage()).thenReturn(true);

		Client client = mock(Client.class);
		when(client.getTickCount()).thenReturn(10);

		RedemptionProc redemptionProc = mock(RedemptionProc.class);
		when(redemptionProc.HasRedemptionProcced()).thenReturn(false);

		SoundEffectPlayed event = mock(SoundEffectPlayed.class);
		when(event.getSoundId()).thenReturn(SoundIds.PRAYER_DOWN.Id);

		setField(prayerDown, "config", config);
		setField(prayerDown, "client", client);
		setField(prayerDown, "redemptionProc", redemptionProc);
		setField(prayerDown, "soundEngine", mock(SoundEngine.class));
		setField(prayerDown, "executor", mock(ScheduledExecutorService.class));
		prayerDown.setLastLoginTick(1);

		prayerDown.onSoundEffectPlayed(event);

		verify(event).consume();
	}

	private static void setField(Object target, String fieldName, Object value) throws Exception
	{
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}
