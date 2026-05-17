package com.github.dappermickie.odablock.ui;

import com.github.dappermickie.odablock.overrides.SoundOverrideAction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

class OverrideRow extends JPanel
{
	private static final Color ACCENT = new Color(95, 175, 95);

	OverrideRow(
		SoundOverrideAction action,
		int selectedCount,
		Consumer<SoundOverrideAction> onEdit,
		Consumer<SoundOverrideAction> onDelete)
	{
		setLayout(new BorderLayout(8, 0));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new CompoundBorder(
			BorderFactory.createMatteBorder(0, 3, 0, 0, ACCENT),
			new EmptyBorder(8, 10, 8, 6)
		));

		JLabel nameLabel = new JLabel(action.getDisplayName());
		nameLabel.setFont(FontManager.getRunescapeBoldFont());
		nameLabel.setForeground(Color.WHITE);

		JLabel statusLabel = new JLabel(buildCountText(selectedCount));
		statusLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		statusLabel.setBorder(new EmptyBorder(2, 0, 0, 0));

		JPanel textColumn = new JPanel();
		textColumn.setLayout(new BoxLayout(textColumn, BoxLayout.Y_AXIS));
		textColumn.setOpaque(false);
		textColumn.add(nameLabel);
		textColumn.add(statusLabel);

		JButton deleteButton = new JButton();
		deleteButton.setIcon(ToolbarIcons.TRASH);
		deleteButton.setRolloverIcon(ToolbarIcons.TRASH_HOVER);
		deleteButton.setToolTipText("Remove this override");
		deleteButton.setFocusPainted(false);
		SwingUtil.removeButtonDecorations(deleteButton);
		deleteButton.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 4));
		deleteButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		deleteButton.addActionListener(event -> {
			if (onDelete != null)
			{
				onDelete.accept(action);
			}
		});

		add(textColumn, BorderLayout.CENTER);
		add(deleteButton, BorderLayout.EAST);

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setToolTipText("Click to edit sounds for " + action.getDisplayName());

		final Color hoverBackground = ColorScheme.DARK_GRAY_HOVER_COLOR;
		final Color normalBackground = ColorScheme.DARKER_GRAY_COLOR;
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				setBackground(hoverBackground);
				deleteButton.setBackground(hoverBackground);
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				setBackground(normalBackground);
				deleteButton.setBackground(normalBackground);
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (event.getButton() != MouseEvent.BUTTON1)
				{
					return;
				}
				if (deleteButton.getBounds().contains(event.getPoint()))
				{
					return;
				}
				if (onEdit != null)
				{
					onEdit.accept(action);
				}
			}
		});

		setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
	}

	private String buildCountText(int selectedCount)
	{
		if (selectedCount == 1)
		{
			return "1 sound";
		}
		return selectedCount + " sounds";
	}
}
