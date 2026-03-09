package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
@Slf4j
public class FreezeSound {

	private static final String FROZEN_MESSAGE = "<col=ef1020>You have been frozen!</col>";

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	public boolean onChatMessage(ChatMessage chatMessage)
	{
        if (chatMessage.getMessage().equals(FROZEN_MESSAGE))
        {
            soundEngine.playClip(Sound.FREEZE, executor);
            return true;
        }
        return false;
    }
}
