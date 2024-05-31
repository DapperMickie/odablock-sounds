package com.github.dappermickie.odablock.emotes;

import com.google.common.collect.ImmutableMap;
import java.awt.image.BufferedImage;
import java.util.Map;
import lombok.AllArgsConstructor;
import net.runelite.client.util.ImageUtil;

@AllArgsConstructor
public enum Emote
{
	AIAIAI("aiaiai", EmoteType.GIF),
	AWKWARD("awkward", EmoteType.GIF),
	FUFU("fufu", EmoteType.GIF),
	GNEAH("gneah", EmoteType.PNG),
	HUH("huh", EmoteType.PNG),
	HYPERWANKGE("hyperwankge", EmoteType.GIF),
	JANITOR("janitor", EmoteType.PNG),
	KEK("kek", EmoteType.GIF),
	KISS("kiss", EmoteType.PNG),
	NOWAY("noway", EmoteType.PNG),
	PFACE(":p", EmoteType.GIF, new String[]{"pface"}),
	RLY("rly", EmoteType.GIF),
	SMILE(":)", EmoteType.GIF, new String[]{"smile"}),
	TUNE("tune", EmoteType.GIF),
	ODAWHAT("odawhat", EmoteType.PNG),
	DAP("dap", EmoteType.PNG),
	WHENITREGISTERS("whenitregisters", EmoteType.GIF),
	WINK("wink", EmoteType.PNG);

	private enum EmoteType
	{
		PNG,
		GIF
	}

	private static final Map<String, Emote> emojiMap;

	private final String trigger;
	private final EmoteType emoteType;
	private final String[] altTriggers;

	Emote(final String trigger, EmoteType emoteType)
	{
		this(trigger, emoteType, new String[]{});
	}

	static
	{
		ImmutableMap.Builder<String, Emote> builder = new ImmutableMap.Builder<>();

		for (final Emote emoji : values())
		{
			builder.put(emoji.trigger, emoji);
			if (!emoji.trigger.startsWith("oda") && !emoji.trigger.startsWith(":"))
			{
				builder.put("oda" + emoji.trigger, emoji);
			}
			for (final String altTrigger : emoji.altTriggers)
			{
				builder.put(altTrigger, emoji);
				if (!altTrigger.startsWith("oda") && !altTrigger.startsWith(":"))
				{
					builder.put("oda" + altTrigger, emoji);
				}
			}
		}

		emojiMap = builder.build();
	}

	BufferedImage loadImage()
	{
		return ImageUtil.loadImageResource(getClass(), this.name().toLowerCase() + getEmoteTypeExtension());
	}

	private String getEmoteTypeExtension()
	{
		return emoteType == EmoteType.GIF ? ".gif" : ".png";
	}

	static Emote getEmoji(String trigger)
	{
		return emojiMap.get(trigger);
	}
}