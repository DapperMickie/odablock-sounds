package com.github.dappermickie.odablock.ui;

import com.github.dappermickie.odablock.overrides.SoundOverrideOption;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

class OptionRow extends JPanel
{
	private static final Color SELECTED_ACCENT = new Color(95, 175, 95);

	OptionRow(
		SoundOverrideOption option,
		boolean selected,
		Consumer<SoundOverrideOption> onToggle,
		Consumer<SoundOverrideOption> onPreview)
	{
		setLayout(new BorderLayout(8, 0));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new CompoundBorder(
			BorderFactory.createMatteBorder(0, 3, 0, 0, selected ? SELECTED_ACCENT : ColorScheme.BORDER_COLOR),
			new EmptyBorder(6, 8, 6, 6)
		));

		JLabel selectionIcon = new JLabel(selected ? ToolbarIcons.CHECK : null);
		selectionIcon.setPreferredSize(new Dimension(16, 16));

		JLabel nameLabel = new JLabel(option.getDisplayLabel());
		nameLabel.setForeground(selected ? Color.WHITE : ColorScheme.LIGHT_GRAY_COLOR);
		nameLabel.setFont(FontManager.getRunescapeFont());
		nameLabel.setToolTipText(option.getDirectory() + "/" + option.getFileName());

		JPanel textColumn = new JPanel();
		textColumn.setLayout(new javax.swing.BoxLayout(textColumn, javax.swing.BoxLayout.Y_AXIS));
		textColumn.setOpaque(false);
		textColumn.add(nameLabel);

		if (option.isDefaultForAction())
		{
			JLabel defaultBadge = new JLabel("default");
			defaultBadge.setForeground(SELECTED_ACCENT);
			defaultBadge.setFont(FontManager.getRunescapeSmallFont());
			textColumn.add(defaultBadge);
		}

		JPanel centerPanel = new JPanel(new BorderLayout(8, 0));
		centerPanel.setOpaque(false);
		centerPanel.add(selectionIcon, BorderLayout.WEST);
		centerPanel.add(textColumn, BorderLayout.CENTER);

		JButton previewButton = new JButton();
		previewButton.setIcon(ToolbarIcons.PLAY);
		previewButton.setRolloverIcon(ToolbarIcons.PLAY_HOVER);
		previewButton.setToolTipText("Preview this sound");
		previewButton.setFocusPainted(false);
		SwingUtil.removeButtonDecorations(previewButton);
		previewButton.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 4));
		previewButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		previewButton.addActionListener(event -> {
			if (onPreview != null)
			{
				onPreview.accept(option);
			}
		});

		add(centerPanel, BorderLayout.CENTER);
		add(previewButton, BorderLayout.EAST);

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		final Color hoverBackground = ColorScheme.DARK_GRAY_HOVER_COLOR;
		final Color normalBackground = ColorScheme.DARKER_GRAY_COLOR;
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				setBackground(hoverBackground);
				centerPanel.setBackground(hoverBackground);
				previewButton.setBackground(hoverBackground);
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				setBackground(normalBackground);
				centerPanel.setBackground(normalBackground);
				previewButton.setBackground(normalBackground);
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (event.getButton() != MouseEvent.BUTTON1)
				{
					return;
				}
				if (previewButton.getBounds().contains(event.getPoint()))
				{
					return;
				}
				if (onToggle != null)
				{
					onToggle.accept(option);
				}
			}
		});

		setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
	}
}
