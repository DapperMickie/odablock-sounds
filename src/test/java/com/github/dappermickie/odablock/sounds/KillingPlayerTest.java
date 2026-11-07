package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.PlayerKillLineManager;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KillingPlayerTest
{
	private static final Pattern[] KILL_PATTERNS = {Pattern.compile("rip.*")};

	@Mock private Client client;
	@Mock private OdablockConfig config;
	@Mock private SoundEngine soundEngine;
	@Mock private ScheduledExecutorService executor;
	@InjectMocks private KillingPlayer handler;

	@Test
	public void disabledToggleSuppressesKillAndWinMessages()
	{
		assertChatPlayback(false, "", "rip arthur.", false);
		assertChatPlayback(false, "", "local has won!", false);
	}

	@Test
	public void namedSenderCannotTriggerKillOrWinSounds()
	{
		assertChatPlayback(true, "Someone", "rip arthur.", false);
		assertChatPlayback(true, "Someone", "local has won!", false);
	}

	@Test
	public void percentageMessageDoesNotTriggerKillSound()
	{
		assertChatPlayback(true, "", "rip arthur 50%", false);
	}

	@Test
	public void enabledKillMessagePlaysSound()
	{
		assertChatPlayback(true, "", "rip arthur.", true);
	}

	@Test
	public void enabledWinMessagePlaysSound()
	{
		assertChatPlayback(true, "", "local has won!", true);
	}

	private void assertChatPlayback(boolean enabled, String sender, String message, boolean expected)
	{
		Player player = mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		lenient().when(player.getName()).thenReturn("local");
		when(config.playerKilling()).thenReturn(enabled);
		ChatMessage chat = mock(ChatMessage.class);
		when(chat.getMessage()).thenReturn(message);
		lenient().when(chat.getName()).thenReturn(sender);
		try (MockedStatic<PlayerKillLineManager> lines = mockStatic(PlayerKillLineManager.class))
		{
			lines.when(PlayerKillLineManager::getPatterns).thenReturn(KILL_PATTERNS);
			org.junit.Assert.assertEquals(expected, handler.onChatMessage(chat));
		}
		if (expected)
		{
			verify(soundEngine).playClip(Sound.KILLING_SOMEONE_1, executor);
		}
		else
		{
			verifyNoInteractions(soundEngine);
		}
	}

	@Test
	public void lootBroadcastFromPlayerNamedRipIsNotAPlayerKill()
	{
		assertFalse(KillingPlayer.isPlayerKillMessage(
			"rip arthur received a drop: 2 x rune platelegs",
			KILL_PATTERNS));
	}

	@Test
	public void actualRipPlayerKillMessageStillMatches()
	{
		assertTrue(KillingPlayer.isPlayerKillMessage("rip arthur.", KILL_PATTERNS));
	}
}
