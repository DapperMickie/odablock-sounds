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
	PFACE(":p", EmoteType.GIF),
	RLY("rly", EmoteType.GIF),
	SMILE(":)", EmoteType.GIF),
	TUNE("tune", EmoteType.GIF),
	WHAT("what", EmoteType.PNG),
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

	static
	{
		ImmutableMap.Builder<String, Emote> builder = new ImmutableMap.Builder<>();

		for (final Emote emoji : values())
		{
			builder.put(emoji.trigger, emoji);
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