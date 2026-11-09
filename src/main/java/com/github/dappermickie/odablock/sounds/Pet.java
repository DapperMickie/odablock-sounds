package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.client.util.Text;

@Singleton
@Slf4j
public class Pet
{

	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	private int receivedPetTick = -1;
	private final String normalPetMessage = "You have a funny feeling like you're being followed.";
	private final String backpackPetMessage = "You feel something weird sneaking into your backpack.";
	private final String wouldHavePetMessage = "You have a funny feeling like you would have been followed...";

	private boolean pendingPetReceived = false;
	private boolean heronReceived = false;

	public void reset()
	{
		pendingPetReceived = false;
		heronReceived = false;
		receivedPetTick = -1;
	}

	public void setHeronReceived()
	{
		this.heronReceived = true;
	}

	public boolean isPendingPetReceived()
	{
		return pendingPetReceived;
	}

	public boolean onChatMessage(ChatMessage chatMessage)
	{
		if (!config.receivedPet())
		{
			return false;
		}
		String message = Text.standardize(chatMessage.getMessage());
		// Make sure it's one of the pet messages and not sent by a user to troll
		if ((message.equalsIgnoreCase(normalPetMessage) || message.equalsIgnoreCase(backpackPetMessage) || message.equalsIgnoreCase(wouldHavePetMessage)) &&
			chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			receivedPetTick = client.getTickCount();
			pendingPetReceived = true;
			return true;
		}

		return false;
	}

	public void onGameTick(GameTick event)
	{
		if (pendingPetReceived)
		{
			if (!heronReceived)
			{
				soundEngine.playClip(Sound.NEW_PET, executor);
			}
			pendingPetReceived = false;
			heronReceived = false;
		}
	}

	public int getReceivedPetTick()
	{
		return receivedPetTick;
	}
}
