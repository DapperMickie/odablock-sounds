package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.WidgetSizeMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OdablockWarriorsTest
{
	@Mock private Client client;
	@Mock private OdablockConfig config;
	@Mock private SoundEngine engine;
	@Mock private ScheduledExecutorService executor;
	@InjectMocks private OdablockWarriors music;

	private Widget list;
	private Widget scroll;
	private final AtomicReference<Widget[]> children = new AtomicReference<>();
	private Widget original;

	@Before
	public void setUp()
	{
		list = mock(Widget.class, RETURNS_SELF);
		scroll = mock(Widget.class, RETURNS_SELF);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(config.warriors()).thenReturn(true);
		when(config.terry()).thenReturn(true);
		when(client.getWidget(InterfaceID.Music.JUKEBOX)).thenReturn(list);
		when(client.getWidget(InterfaceID.Music.SCROLLABLE)).thenReturn(scroll);
		when(list.getOriginalHeight()).thenReturn(16);
		when(scroll.getScrollHeight()).thenReturn(16);
		original = row(0, "7th Realm");
		children.set(new Widget[]{original});
		when(list.getChildren()).thenAnswer(inv -> children.get());
		when(list.getDynamicChildren()).thenAnswer(inv -> children.get());
		doAnswer(inv -> { children.set(inv.getArgument(0)); return null; })
			.when(list).setChildren(any(Widget[].class));
		when(list.createChild(eq(-1), eq(WidgetType.TEXT))).thenAnswer(inv -> {
			int index = children.get().length;
			Widget child = row(index, "");
			Widget[] next = Arrays.copyOf(children.get(), index + 1);
			next[index] = child;
			children.set(next);
			return child;
		});
	}

	@Test
	public void addsSeparateRowsWithoutRenamingOrDuplicatingOriginals()
	{
		music.onGameTick(new GameTick());
		music.onGameTick(new GameTick());
		assertEquals(3, children.get().length);
		assertSame(original, children.get()[0]);
		assertEquals("Odablock Warriors", children.get()[1].getText());
		assertEquals("Terry", children.get()[2].getText());
		verify(original, never()).setText(anyString());
		verify(original, never()).setName(anyString());
		verify(scroll).setScrollHeight(48);
	}

	@Test
	public void relativeNativeSizingDoesNotCollapseListOrCustomRowWidths()
	{
		// Native widgets can have raw width/height zero, meaning fill the parent.
		when(list.getOriginalHeight()).thenReturn(0);
		when(list.getHeightMode()).thenReturn(WidgetSizeMode.MINUS);
		when(list.getHeight()).thenReturn(300);
		when(original.getOriginalWidth()).thenReturn(0);
		when(original.getWidth()).thenReturn(170);
		music.onGameTick(new GameTick());
		verify(list).setHeightMode(WidgetSizeMode.ABSOLUTE);
		verify(list).setOriginalHeight(300);
		verify(children.get()[1]).setOriginalWidth(170);
		verify(children.get()[2]).setOriginalWidth(170);
		music.shutDown();
		verify(list).setHeightMode(WidgetSizeMode.MINUS);
		verify(list).setOriginalHeight(0);
	}

	@Test
	public void routesEachDynamicChildToItsOwnSongAndStopAction()
	{
		music.onGameTick(new GameTick());
		MenuOptionClicked warriors = click(1, "Play");
		dispatchClick(warriors, 1, 1);
		assertFalse(warriors.isConsumed());
		verify(engine).playManagedClip(Sound.WARRIOR, executor);
		MenuOptionClicked terry = click(2, "Play");
		dispatchClick(terry, 2, 1);
		assertFalse(terry.isConsumed());
		verify(engine).playManagedClip(Sound.TERRY, executor);
		dispatchClick(click(2, "Stop"), 2, 2);
		verify(engine).stopManagedClip();
	}

	@Test
	public void sharedParentIdDoesNotHijackOriginalTrack()
	{
		music.onGameTick(new GameTick());
		MenuOptionClicked event = click(0, "Play");
		music.onMenuOptionClicked(event);
		assertFalse(event.isConsumed());
		verify(engine).stopManagedClip();
		verify(engine, never()).playManagedClip(any(), any());
	}

	@Test
	public void disablingOneTrackStopsItAndPreservesTheOther()
	{
		music.onGameTick(new GameTick());
		dispatchClick(click(2, "Play"), 2, 1);
		when(config.terry()).thenReturn(false);
		music.onConfigChanged("terry");
		assertEquals(2, children.get().length);
		assertSame(original, children.get()[0]);
		assertEquals("Odablock Warriors", children.get()[1].getText());
		verify(engine).stopManagedClip();
		when(config.terry()).thenReturn(true);
		music.onConfigChanged("terry");
		assertEquals(3, children.get().length);
	}

	@Test
	public void recreatesRowsAfterGameRebuildsList()
	{
		music.onGameTick(new GameTick());
		children.set(new Widget[]{original});
		music.onGameTick(new GameTick());
		assertEquals(3, children.get().length);
		assertSame(original, children.get()[0]);
	}

	@Test
	public void shutdownRemovesOnlyPluginRowsAndRestoresScrollHeight()
	{
		music.onGameTick(new GameTick());
		music.shutDown();
		assertArrayEquals(new Widget[]{original}, children.get());
		verify(list).setOriginalHeight(16);
		verify(scroll).setScrollHeight(16);
	}

	private Widget row(int index, String label)
	{
		Widget row = mock(Widget.class, RETURNS_SELF);
		AtomicReference<String> text = new AtomicReference<>(label);
		AtomicInteger y = new AtomicInteger();
		AtomicReference<Object[]> listener = new AtomicReference<>();
		doAnswer(inv -> {
			listener.set(inv.getArguments());
			return null;
		}).when(row).setOnOpListener(any(JavaScriptCallback.class));
		when(row.getOnOpListener()).thenAnswer(inv -> listener.get());
		when(row.getId()).thenReturn(InterfaceID.Music.JUKEBOX);
		when(row.getIndex()).thenReturn(index);
		when(row.getType()).thenReturn(WidgetType.TEXT);
		when(row.getText()).thenAnswer(inv -> text.get());
		when(row.setText(anyString())).thenAnswer(inv -> { text.set(inv.getArgument(0)); return row; });
		when(row.getOriginalY()).thenAnswer(inv -> y.get());
		when(row.setOriginalY(anyInt())).thenAnswer(inv -> { y.set(inv.getArgument(0)); return row; });
		when(row.getOriginalHeight()).thenReturn(16);
		when(row.getHeight()).thenReturn(16);
		when(row.getRelativeY()).thenAnswer(inv -> y.get());
		when(row.getWidth()).thenReturn(170);
		when(row.getOriginalWidth()).thenReturn(170);
		return row;
	}

	private void dispatchClick(MenuOptionClicked event, int index, int operation)
	{
		music.onMenuOptionClicked(event);
		assertFalse("Widget operations must reach their callback", event.isConsumed());
		Object[] listener = children.get()[index].getOnOpListener();
		assertNotNull("A listener is required for the client to offer widget actions", listener);
		ScriptEvent scriptEvent = mock(ScriptEvent.class);
		when(scriptEvent.getOp()).thenReturn(operation);
		((JavaScriptCallback) listener[0]).run(scriptEvent);
	}

	private MenuOptionClicked click(int index, String option)
	{
		MenuEntry entry = mock(MenuEntry.class);
		when(entry.getParam1()).thenReturn(InterfaceID.Music.JUKEBOX);
		when(entry.getParam0()).thenReturn(index);
		when(entry.getOption()).thenReturn(option);
		return new MenuOptionClicked(entry);
	}
}
