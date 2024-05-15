package com.github.dappermickie.odablock;

import com.github.dappermickie.odablock.notifications.Notification;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptEvent;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.Text;
import okhttp3.OkHttpClient;

@Slf4j
@Singleton
public class ChatRightClickManager
{
	private final Map<String, RightClickAction> rightClickable = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private Gson gson;

	@Inject
	private ClientThread clientThread;

	public void onGameTick(GameTick event)
	{
		Widget chatWidget = client.getWidget(ComponentID.CHATBOX_MESSAGE_LINES);
		if (chatWidget != null)
		{
			for (Widget w : chatWidget.getDynamicChildren())
			{
				String untaggedText = Text.standardize(w.getText());

				if (rightClickable.containsKey(untaggedText))
				{
					RightClickAction rightClickAction = rightClickable.get(untaggedText);
					clientThread.invokeLater(() -> {
						w.setAction(5, rightClickAction.getAction());
						w.setOnOpListener((JavaScriptCallback) (ScriptEvent ev) -> {
							openLink(rightClickAction);
						});
						w.setHasListener(true);
						w.setNoClickThrough(false);
						w.revalidate();
					});
				}
				else
				{
					clientThread.invokeLater(() -> {
						w.setHasListener(false);
						w.setNoClickThrough(false);
						w.revalidate();
					});
				}
			}
		}
	}

	public void putInMap(String message, RightClickAction rightClickAction)
	{
		String key = Text.standardize(message);
		rightClickable.put(key, rightClickAction);
	}

	private void openLink(RightClickAction rightClickAction)
	{
		LinkBrowser.browse(rightClickAction.getLink());
		//log.info("Opened a link to Odablocks notification!");
	}
}
