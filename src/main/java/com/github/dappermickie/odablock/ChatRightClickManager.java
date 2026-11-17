package com.github.dappermickie.odablock;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.Text;

@Singleton
public class ChatRightClickManager
{
	private final Map<String, RightClickAction> rightClickable = new LinkedHashMap<>();

	@Inject
	private Client client;

	public void onMenuOpened(MenuOpened event)
	{
		for (MenuEntry entry : event.getMenuEntries())
		{
			if (entry.getParam1() >>> 16 != InterfaceID.CHATBOX)
			{
				continue;
			}
			Widget row = entry.getWidget();
			if (row == null || row.getText() == null)
			{
				continue;
			}
			RightClickAction action = rightClickable.get(Text.standardize(row.getText()));
			if (action != null)
			{
				// Add only our context-menu entry; preserve native and other plugins' listeners.
				client.createMenuEntry(-1)
					.setOption(action.getAction())
					.setTarget("")
					.setType(MenuAction.RUNELITE)
					.onClick(clicked -> LinkBrowser.browse(action.getLink()));
				return;
			}
		}
	}

	public void putInMap(String message, RightClickAction action)
	{
		rightClickable.put(Text.standardize(message), action);
		if (rightClickable.size() > 128)
		{
			rightClickable.remove(rightClickable.keySet().iterator().next());
		}
	}

	public void clear()
	{
		rightClickable.clear();
	}
}
