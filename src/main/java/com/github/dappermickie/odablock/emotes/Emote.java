package com.github.dappermickie.odablock.emotes;

import com.google.common.collect.ImmutableMap;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.util.ImageUtil;

public class Emote
{
	@Getter private String trigger;
	@Getter private String[] altTriggers = new String[0];
	private String file;
	private String overheadFile;
	private transient BufferedImage image;
	private transient BufferedImage overheadImage;
	private transient int index;
	private static Emote[] current = new Emote[0];
	private static Map<String, Emote> emojiMap = new java.util.HashMap<>();

	static Emote[] values() { return current; }
	int ordinal() { return index; }
	BufferedImage loadImage() { return image; }
	BufferedImage loadOverheadImage() { return overheadImage; }
	void prepare(java.nio.file.Path directory) throws java.io.IOException
	{
		image = read(directory, file);
		overheadImage = overheadFile == null ? image : read(directory, overheadFile);
	}
	private BufferedImage read(java.nio.file.Path directory, String name) throws java.io.IOException
	{
		BufferedImage result = javax.imageio.ImageIO.read(directory.resolve(name).toFile());
		if (result == null) throw new java.io.IOException("Invalid emote image: " + name);
		return toChatIconImage(result);
	}
	String[] files() { return overheadFile == null ? new String[]{file} : new String[]{file, overheadFile}; }
	static void install(Emote[] emotes)
	{
		Map<String, Emote> mapping = new java.util.HashMap<>();
		for (int i = 0; i < emotes.length; i++)
		{
			Emote emote = emotes[i];
			emote.index = i;
			java.util.List<String> triggers = new java.util.ArrayList<>(java.util.Arrays.asList(emote.altTriggers));
			triggers.add(emote.trigger);
			for (String trigger : triggers)
			{
				trigger = trigger.toLowerCase(java.util.Locale.ROOT);
				mapping.put(trigger, emote);
				if (!trigger.startsWith("oda") && !trigger.startsWith(":")) mapping.put("oda" + trigger, emote);
			}
		}
		current = emotes;
		emojiMap = mapping;
	}

	static BufferedImage toChatIconImage(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
		Set<Integer> colors = new HashSet<>();
		for (int pixel : pixels)
		{
			// RuneLite only draws fully opaque pixels in indexed chat sprites.
			if ((pixel >>> 24) == 255)
			{
				colors.add(pixel & 0xFFFFFF);
			}
		}
		if (colors.size() <= 255)
		{
			return image;
		}

		// Six levels per channel give at most 216 colors, leaving room for transparency.
		for (int i = 0; i < pixels.length; i++)
		{
			int pixel = pixels[i];
			if ((pixel >>> 24) != 255)
			{
				continue;
			}
			int red = (((pixel >>> 16) & 255) + 25) / 51 * 51;
			int green = (((pixel >>> 8) & 255) + 25) / 51 * 51;
			int blue = ((pixel & 255) + 25) / 51 * 51;
			int rgb = (red << 16) | (green << 8) | blue;
			// RuneLite reserves RGB zero for transparency; keep dark pixels visible.
			pixels[i] = 0xFF000000 | (rgb == 0 ? 1 : rgb);
		}
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		result.setRGB(0, 0, width, height, pixels, 0, width);
		return result;
	}

	static Emote getEmoji(String trigger)
	{
		return emojiMap.get(trigger);
	}
}
