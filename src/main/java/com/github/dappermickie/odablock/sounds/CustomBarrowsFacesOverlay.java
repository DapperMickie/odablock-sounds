package com.github.dappermickie.odablock.sounds;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

// Based on code/ideas from VancedBarrowsOverlay in VancedBarrows plugin
// (https://github.com/codyduong/VancedBarrows)
@Singleton
public class CustomBarrowsFacesOverlay extends Overlay
{
	private BufferedImage image;
	private boolean visible = false;
	private java.awt.Point overlayLocation = new java.awt.Point(150, 150);
	private float alpha = 1.0f;
	private int width = 128;
	private int height = 128;

	@Inject
	public CustomBarrowsFacesOverlay()
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	public void setImage(BufferedImage image)
	{
		this.image = image;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public void setAlpha(float alpha)
	{
		this.alpha = Math.max(0f, Math.min(1f, alpha));
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public void setOverlayLocation(Point widgetLocation)
	{
		if (widgetLocation != null)
		{
			this.overlayLocation = new java.awt.Point(widgetLocation.getX(), widgetLocation.getY());
		}
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!visible || image == null || overlayLocation == null)
		{
			return null;
		}

		Composite original = graphics.getComposite();
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		graphics.drawImage(image, overlayLocation.x, overlayLocation.y, width, height, null);
		graphics.setComposite(original);

		return new Dimension(width, height);
	}
}
