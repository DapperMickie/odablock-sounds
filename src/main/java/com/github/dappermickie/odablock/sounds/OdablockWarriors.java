package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.WidgetSizeMode;

/** Adds plugin music without changing any of the game's track widgets. */
public class OdablockWarriors
{
	private static final int ROW_HEIGHT = 16;
	private final Map<Sound, Widget> entries = new LinkedHashMap<>();

	@Inject private Client client;
	@Inject private SoundEngine soundEngine;
	@Inject private ScheduledExecutorService executor;
	@Inject private OdablockConfig config;

	private Widget parent;
	private int originalListHeight;
	private int originalListHeightMode;
	private int originalRenderedListHeight;
	private int originalScrollHeight;
	private Sound playing;

	public void onWidgetLoaded(WidgetLoaded event)
	{
		// The game populates the list after WidgetLoaded; the next tick adds our rows.
		if (event.getGroupId() == InterfaceID.MUSIC)
		{
			entries.clear();
			parent = null;
		}
	}

	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getParam1() >>> 16 != InterfaceID.MUSIC)
		{
			return;
		}

		for (Map.Entry<Sound, Widget> entry : entries.entrySet())
		{
			Widget row = entry.getValue();
			// Dynamic children share a packed id: both parent id and child index matter.
			if (isAttached(row) && event.getParam1() == row.getId()
				&& event.getParam0() == row.getIndex() && enabled(entry.getKey()))
			{
				// Let the widget operation callback handle this custom entry.
				return;
			}
		}

		// Let ordinary music controls and original tracks work normally.
		stop();
	}

	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			stop();
			return;
		}
		updateEntries();
	}

	public void onConfigChanged(String key)
	{
		if (!"warriors".equals(key) && !"terry".equals(key))
		{
			return;
		}
		if (playing != null && !enabled(playing))
		{
			stop();
		}
		updateEntries();
	}

	public void shutDown()
	{
		stop();
		removeEntries();
		parent = null;
	}

	private boolean enabled(Sound sound)
	{
		return sound == Sound.WARRIOR ? config.warriors() : config.terry();
	}

	private boolean isAttached(Widget row)
	{
		Widget[] children = parent == null ? null : parent.getChildren();
		return children != null && row.getIndex() >= 0 && row.getIndex() < children.length
			&& children[row.getIndex()] == row;
	}

	private void updateEntries()
	{
		Widget list = client.getWidget(InterfaceID.Music.JUKEBOX);
		if (list == null)
		{
			return;
		}
		if (parent != list || entries.values().stream().anyMatch(row -> !isAttached(row)))
		{
			entries.clear();
			parent = list;
			originalListHeight = list.getOriginalHeight();
			originalListHeightMode = list.getHeightMode();
			originalRenderedListHeight = list.getHeight();
			Widget scroll = client.getWidget(InterfaceID.Music.SCROLLABLE);
			originalScrollHeight = scroll == null ? 0 : scroll.getScrollHeight();
		}

		if (entries.containsKey(Sound.WARRIOR) == config.warriors()
			&& entries.containsKey(Sound.TERRY) == config.terry())
		{
			return;
		}

		removeEntries();
		int y = 0;
		Widget template = null;
		for (Widget row : list.getDynamicChildren())
		{
			if (row != null && !row.isSelfHidden())
			{
				y = Math.max(y, row.getRelativeY() + row.getHeight());
				if (row.getType() == WidgetType.TEXT)
				{
					template = row;
				}
			}
		}
		for (Sound sound : new Sound[]{Sound.WARRIOR, Sound.TERRY})
		{
			if (!enabled(sound))
			{
				continue;
			}
			String label = sound == Sound.WARRIOR ? "Odablock Warriors" : "Terry";
			Widget row = list.createChild(-1, WidgetType.TEXT)
				.setText(label)
				.setName("<col=ff9040>" + label + "</col>")
				.setTextColor(0xff9040)
				.setFontId(template == null ? 494 : template.getFontId())
				.setTextShadowed(true)
				.setOriginalX(template == null ? 0 : template.getRelativeX())
				.setOriginalY(y)
				.setOriginalWidth(template == null ? Math.max(170, list.getWidth()) : template.getWidth())
				.setOriginalHeight(ROW_HEIGHT)
				.setHasListener(true);
			row.setAction(0, "Play");
			row.setAction(1, "Stop");
			// Without an operation listener, the client omits these actions from the menu.
			row.setOnOpListener((JavaScriptCallback) event -> {
				if (!isAttached(row) || !enabled(sound))
				{
					return;
				}
				if (event.getOp() == 1)
				{
					playing = sound;
					soundEngine.playManagedClip(sound, executor);
				}
				else if (event.getOp() == 2)
				{
					stop();
				}
			});
			row.revalidate();
			entries.put(sound, row);
			y += ROW_HEIGHT;
		}
		if (!entries.isEmpty())
		{
			// The native container uses relative sizing. A pixel height in MINUS mode
			// makes it negative and clips the entire music list.
			list.setHeightMode(WidgetSizeMode.ABSOLUTE);
			list.setOriginalHeight(Math.max(originalRenderedListHeight, y));
			list.revalidate();
			resizeScroll(Math.max(originalScrollHeight, list.getRelativeY() + y));
		}
	}

	private void removeEntries()
	{
		if (parent == null || entries.isEmpty())
		{
			return;
		}
		Widget[] children = parent.getChildren();
		if (children != null)
		{
			children = children.clone();
			for (Widget row : entries.values())
			{
				if (isAttached(row))
				{
					children[row.getIndex()] = null;
				}
			}
			int length = children.length;
			while (length > 0 && children[length - 1] == null)
			{
				length--;
			}
			parent.setChildren(Arrays.copyOf(children, length));
		}
		entries.clear();
		parent.setHeightMode(originalListHeightMode);
		parent.setOriginalHeight(originalListHeight);
		parent.revalidate();
		resizeScroll(originalScrollHeight);
	}

	private void resizeScroll(int height)
	{
		Widget scroll = client.getWidget(InterfaceID.Music.SCROLLABLE);
		if (scroll != null)
		{
			scroll.setScrollHeight(height);
			scroll.revalidateScroll();
			client.runScript(ScriptID.UPDATE_SCROLLBAR, InterfaceID.Music.SCROLLBAR,
				InterfaceID.Music.SCROLLABLE, scroll.getScrollY());
		}
	}

	private void stop()
	{
		soundEngine.stopManagedClip();
		playing = null;
	}
}
