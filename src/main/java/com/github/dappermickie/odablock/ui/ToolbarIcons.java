package com.github.dappermickie.odablock.ui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import javax.swing.ImageIcon;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.ImageUtil;

@Slf4j
final class ToolbarIcons
{
	private static final float HOVER_BRIGHTNESS = 1.2f;

	static final ImageIcon PLUS = loadIcon("new.png");
	static final ImageIcon PLUS_HOVER = brighten(PLUS);

	static final ImageIcon EXPORT = loadIcon("export.png");
	static final ImageIcon EXPORT_HOVER = brighten(EXPORT);

	static final ImageIcon IMPORT = loadIcon("import.png");
	static final ImageIcon IMPORT_HOVER = brighten(IMPORT);

	static final ImageIcon CLOSE = loadIcon("close.png");
	static final ImageIcon CLOSE_HOVER = brighten(CLOSE);

	static final ImageIcon BACK = loadIcon("back.png");
	static final ImageIcon BACK_HOVER = brighten(BACK);

	static final ImageIcon TRASH = loadIcon("trash.png");
	static final ImageIcon TRASH_HOVER = brighten(TRASH);

	static final ImageIcon PLAY = loadIcon("play.png");
	static final ImageIcon PLAY_HOVER = brighten(PLAY);

	static final ImageIcon CHECK = loadIcon("check.png");

	private ToolbarIcons()
	{
	}

	private static ImageIcon loadIcon(String resourceName)
	{
		try
		{
			BufferedImage image = ImageUtil.loadImageResource(ToolbarIcons.class, resourceName);
			if (image != null)
			{
				return new ImageIcon(image);
			}
			log.warn("Toolbar icon resource '{}' could not be loaded.", resourceName);
		}
		catch (Exception exception)
		{
			log.warn("Toolbar icon resource '{}' is missing.", resourceName, exception);
		}
		return new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
	}

	private static ImageIcon brighten(ImageIcon source)
	{
		if (source == null || source.getIconWidth() <= 1 || source.getIconHeight() <= 1)
		{
			return source;
		}
		try
		{
			BufferedImage src = toBufferedImage(source.getImage(),
				source.getIconWidth(), source.getIconHeight());
			RescaleOp op = new RescaleOp(HOVER_BRIGHTNESS, 0, null);
			BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
			op.filter(src, out);
			return new ImageIcon(out);
		}
		catch (Exception exception)
		{
			log.debug("Failed to derive hover icon; reusing source.", exception);
			return source;
		}
	}

	private static BufferedImage toBufferedImage(Image image, int width, int height)
	{
		BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = buffered.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return buffered;
	}
}
