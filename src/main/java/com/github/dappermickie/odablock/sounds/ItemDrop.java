package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;

@Singleton
@Slf4j
public class ItemDrop
{
	private static final int COINS = ItemID.COINS;

	@Inject
	private OdablockConfig config;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ScheduledExecutorService executor;

	public void onNpcLootReceived(final NpcLootReceived event)
	{
		checkLootAndPlay(event.getItems(), event.getNpc().getName());
	}

	public void onPlayerLootReceived(final PlayerLootReceived event)
	{
		checkLootAndPlay(event.getItems(), event.getPlayer().getName());
	}

	private void checkLootAndPlay(final Collection<ItemStack> items, final String sourceName)
	{
		if (!config.itemDrop())
		{
			return;
		}

		final int minGeValue = config.itemDropMinGeValue();
		if (minGeValue <= 0)
		{
			return;
		}

		for (final ItemStack item : items)
		{
			final long geValue = getGeValue(item);
			if (geValue >= minGeValue)
			{
				final ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
				log.debug("Playing item drop sound for {} from {} (GE value: {}, threshold: {})",
					itemComposition.getName(), sourceName, geValue, minGeValue);
				soundEngine.playClip(Sound.ITEM_DROP, executor);
				return;
			}
		}
	}

	private long getGeValue(final ItemStack item)
	{
		final int itemId = item.getId();
		final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		final int realItemId = itemComposition.getNote() != -1 ? itemComposition.getLinkedNoteId() : itemId;
		final int quantity = item.getQuantity();

		if (realItemId == COINS)
		{
			return quantity;
		}

		return (long) itemManager.getItemPrice(realItemId) * quantity;
	}
}
