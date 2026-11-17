package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.Text;

@Singleton
@Slf4j
public class EmptyChestSound
{
	private static final String[] BARROWS_BROTHER_PREFIXES = {
		"ahrim",
		"dharok",
		"guthan",
		"karil",
		"torag",
		"verac",
	};

	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private ClientThread clientThread;

	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (!config.emptyChestSound())
		{
			return;
		}

		final int inventoryId;
		final boolean barrowsChest;

		switch (event.getGroupId())
		{
			case InterfaceID.BARROWS_REWARD:
				inventoryId = InventoryID.TRAIL_REWARDINV;
				barrowsChest = true;
				break;
			case InterfaceID.PMOON_REWARD:
				inventoryId = InventoryID.PMOON_REWARDINV;
				barrowsChest = false;
				break;
			default:
				return;
		}

		clientThread.invokeLater(() -> checkChestAndPlaySound(inventoryId, barrowsChest));
	}

	private void checkChestAndPlaySound(int inventoryId, boolean barrowsChest)
	{
		if (!config.emptyChestSound())
		{
			return;
		}

		ItemContainer container = client.getItemContainer(inventoryId);
		if (container == null)
		{
			return;
		}

		for (Item item : container.getItems())
		{
			if (item == null || item.getId() <= 0)
			{
				continue;
			}

			if (barrowsChest ? isBarrowsRewardItem(item.getId()) : isMoonsRewardItem(item.getId()))
			{
				return;
			}
		}

		soundEngine.playClip(Sound.FAHHHHH, executor);
	}

	private boolean isBarrowsRewardItem(int itemId)
	{
		String name = Text.standardize(client.getItemDefinition(itemId).getName());

		if (name.contains("amulet of the damned"))
		{
			return true;
		}

		for (String prefix : BARROWS_BROTHER_PREFIXES)
		{
			if (name.startsWith(prefix))
			{
				return true;
			}
		}

		return false;
	}

	private boolean isMoonsRewardItem(int itemId)
	{
		String name = Text.standardize(client.getItemDefinition(itemId).getName());

		return name.contains("eclipse moon")
			|| name.contains("blood moon")
			|| name.contains("frost moon")
			|| name.contains("blue moon")
			|| name.contains("frostmoon spear")
			|| name.contains("dual macuahuitl");
	}
}
