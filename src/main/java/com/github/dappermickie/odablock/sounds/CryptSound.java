package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.*;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
@Slf4j
public class CryptSound {
    private static final String CRYPT_MESSAGE = "You've broken into a crypt!";
    private static final Random random = new Random();

    @Inject
    private SoundEngine soundEngine;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private OdablockConfig config;

	public boolean onChatMessage(ChatMessage chatMessage)
	{
	    if (!config.cryptSound()) {
	        return false;
        }
        if (chatMessage.getMessage().equals(CRYPT_MESSAGE))
        {
            if (random.nextDouble() < 0.1)
            {
                soundEngine.playClip(Sound.CRYPT, executor);
            }
            return true;
        }
        return false;
    }
}
