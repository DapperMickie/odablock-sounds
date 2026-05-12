package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.SoundEngine;
import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OdablockWarriorsTest
{
	@Test
	public void doesNotConsumeMenuClickWhenWarriorsDisabled() throws Exception
	{
		OdablockWarriors odablockWarriors = new OdablockWarriors();
		OdablockConfig config = mock(OdablockConfig.class);
		when(config.warriors()).thenReturn(false);

		Widget warriorWidget = mock(Widget.class);
		MenuOptionClicked event = mock(MenuOptionClicked.class);
		when(event.getWidget()).thenReturn(warriorWidget);

		setField(odablockWarriors, "config", config);
		setField(odablockWarriors, "warriorWidget", warriorWidget);
		setField(odablockWarriors, "soundEngine", mock(SoundEngine.class));
		setField(odablockWarriors, "executor", mock(ScheduledExecutorService.class));

		odablockWarriors.onMenuOptionClicked(event);

		verify(event, never()).consume();
	}

	private static void setField(Object target, String fieldName, Object value) throws Exception
	{
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}
