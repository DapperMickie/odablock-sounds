package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.Units;

@Singleton
@Slf4j
public class WelcomeScreenSound
{
    private static final Random random = new Random();

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

	public void onLogin()
	{
		if (!config.welcomeScreen()) {
			return;
		}

		if (random.nextDouble() > 0.1) {
			return;
		}

		executor.schedule(()-> {
			clientThread.invokeLater(this::checkWelcomeScreenAndPlay);
		}, 300, TimeUnit.MILLISECONDS);
    }

    private void checkWelcomeScreenAndPlay() {
		var widget = client.getWidget(InterfaceID.WelcomeScreen.TITLE);
		if (widget != null && !widget.isHidden()) {
			soundEngine.playClip(Sound.WELCOME_SCREEN_SOUND, executor);
		}
	}
}
