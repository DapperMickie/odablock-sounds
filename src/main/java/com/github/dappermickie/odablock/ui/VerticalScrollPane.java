package com.github.dappermickie.odablock.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import net.runelite.client.ui.ColorScheme;

class VerticalScrollPane extends JScrollPane
{
	VerticalScrollPane()
	{
		super(new ScrollableContainer());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(null);

		JScrollBar verticalBar = getVerticalScrollBar();
		verticalBar.setUI(new FlatScrollBarUI());
		verticalBar.setPreferredSize(new Dimension(8, 0));
		verticalBar.setUnitIncrement(16);
		verticalBar.setBackground(ColorScheme.DARK_GRAY_COLOR);
		verticalBar.setOpaque(true);

		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}

	ScrollableContainer getContainer()
	{
		return (ScrollableContainer) getViewport().getView();
	}

	void scrollToTop()
	{
		getVerticalScrollBar().setValue(0);
	}

	int getScrollValue()
	{
		return getVerticalScrollBar().getValue();
	}

	void setScrollValue(int value)
	{
		JScrollBar bar = getVerticalScrollBar();
		int clamped = Math.max(0, Math.min(value, bar.getMaximum() - bar.getVisibleAmount()));
		bar.setValue(clamped);
	}

	static class ScrollableContainer extends JPanel implements Scrollable
	{
		private ScrollableContainer()
		{
			setBackground(ColorScheme.DARK_GRAY_COLOR);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}

		@Override
		public Dimension getPreferredScrollableViewportSize()
		{
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
		}

		@Override
		public boolean getScrollableTracksViewportWidth()
		{
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight()
		{
			return false;
		}
	}

	private static final class FlatScrollBarUI extends BasicScrollBarUI
	{
		private static final Color TRACK_COLOR = ColorScheme.DARK_GRAY_COLOR;
		private static final Color THUMB_COLOR = new Color(80, 80, 80);
		private static final Color THUMB_HOVER_COLOR = new Color(120, 120, 120);
		private static final int THUMB_PADDING = 2;
		private static final int THUMB_ARC = 6;

		@Override
		protected void configureScrollBarColors()
		{
			this.thumbColor = THUMB_COLOR;
			this.trackColor = TRACK_COLOR;
		}

		@Override
		protected JButton createDecreaseButton(int orientation)
		{
			return createInvisibleButton();
		}

		@Override
		protected JButton createIncreaseButton(int orientation)
		{
			return createInvisibleButton();
		}

		private JButton createInvisibleButton()
		{
			JButton button = new JButton();
			Dimension zero = new Dimension(0, 0);
			button.setPreferredSize(zero);
			button.setMinimumSize(zero);
			button.setMaximumSize(zero);
			button.setFocusable(false);
			return button;
		}

		@Override
		protected void paintTrack(Graphics g, javax.swing.JComponent c, Rectangle trackBounds)
		{
			g.setColor(TRACK_COLOR);
			g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
		}

		@Override
		protected void paintThumb(Graphics g, javax.swing.JComponent c, Rectangle thumbBounds)
		{
			if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
			{
				return;
			}

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Color color = (isThumbRollover() || isDragging) ? THUMB_HOVER_COLOR : THUMB_COLOR;
			g2.setColor(color);

			int x = thumbBounds.x + THUMB_PADDING;
			int y = thumbBounds.y + THUMB_PADDING;
			int w = Math.max(0, thumbBounds.width - THUMB_PADDING * 2);
			int h = Math.max(0, thumbBounds.height - THUMB_PADDING * 2);
			g2.fillRoundRect(x, y, w, h, THUMB_ARC, THUMB_ARC);
			g2.dispose();
		}
	}
}
