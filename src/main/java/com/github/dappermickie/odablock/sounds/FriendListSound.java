package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.*;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.callback.ClientThread;

@Singleton
@Slf4j
public class FriendListSound {
    private static final int FRIEND_LIST_GROUP_ID = 164;
    private static final String ZIKLOVER_NAME = "<col=ff9040>ziklover69</col>";
    private static final Random random = new Random();

    @Inject
    private SoundEngine soundEngine;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
	private ClientThread clientThread;

    @Inject
    private Client client;

	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (random.nextDouble() > 0.1) {
			return;
		}

        int groupId = OdablockPlugin.TO_GROUP(menuOptionClicked.getParam1());
        if (groupId == FRIEND_LIST_GROUP_ID && menuOptionClicked.getMenuOption().equals("Friends List"))
        {
			clientThread.invokeLater(this::playFriendListSound);
        }
    }

    private void playFriendListSound()
    {
		var friendListWidget = client.getWidget(InterfaceID.Friends.LIST);

        if (friendListWidget.isHidden())
		{
			return;
		}

        var friendListListContainerWidget = client.getWidget(InterfaceID.Friends.LIST);
        boolean nextIsZiklover = false;
        for (var friendWidget : friendListListContainerWidget.getDynamicChildren()) {
        	if (friendWidget.getName().equalsIgnoreCase(ZIKLOVER_NAME)) {
				nextIsZiklover = true;
				continue;
			}

        	if (nextIsZiklover) {
        		if (friendWidget.getText().equals("Offline")) {
					soundEngine.playClip(Sound.ZIKLOVER_IS_NOT_ON, executor);
				} else {
					soundEngine.playClip(Sound.ZIKLOVER_IS_ON, executor);
				}
        		break;
			}
		}

    }
}
