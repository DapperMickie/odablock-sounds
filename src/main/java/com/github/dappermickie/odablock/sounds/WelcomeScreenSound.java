package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.callback.ClientThread;

@Singleton
@Slf4j
public class WelcomeScreenSound
{
	private static final int WELCOME_SCREEN_GROUP_ID = InterfaceID.WelcomeScreen.TITLE >> 16;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (!config.welcomeScreen())
		{
			return;
		}

		if (event.getGroupId() != WELCOME_SCREEN_GROUP_ID)
		{
			return;
		}

		clientThread.invokeLater(this::checkWelcomeScreenAndPlay);
	}

	private void checkWelcomeScreenAndPlay()
	{
		var widget = client.getWidget(InterfaceID.WelcomeScreen.TITLE);
		if (widget != null && !widget.isHidden())
		{
			soundEngine.playClip(Sound.WELCOME_SCREEN_SOUND, executor);
		}
	}
}
