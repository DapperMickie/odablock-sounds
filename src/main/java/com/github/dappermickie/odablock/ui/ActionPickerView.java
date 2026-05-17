package com.github.dappermickie.odablock.ui;

import com.github.dappermickie.odablock.overrides.SoundOverrideAction;
import com.github.dappermickie.odablock.overrides.SoundOverrideService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.SwingUtil;

class ActionPickerView extends JPanel
{
	private static final String CARD_RESULTS = "RESULTS";
	private static final String CARD_INFO = "INFO";

	private final SoundOverrideService soundOverrideService;
	private final Consumer<SoundOverrideAction> onActionSelected;
	private final Runnable onCancel;

	private final IconTextField searchField;
	private final VerticalScrollPane scrollPane;
	private final VerticalScrollPane.ScrollableContainer rowsContainer;
	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	private final PluginErrorPanel infoPanel;

	ActionPickerView(
		SoundOverrideService soundOverrideService,
		Consumer<SoundOverrideAction> onActionSelected,
		Runnable onCancel)
	{
		this.soundOverrideService = soundOverrideService;
		this.onActionSelected = onActionSelected;
		this.onCancel = onCancel;

		this.searchField = new IconTextField();
		this.scrollPane = new VerticalScrollPane();
		this.rowsContainer = scrollPane.getContainer();
		this.cardLayout = new CardLayout();
		this.cardPanel = new JPanel(cardLayout);
		this.infoPanel = new PluginErrorPanel();

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		configureSearchField();
		buildCardPanel();

		add(buildHeader(), BorderLayout.NORTH);
		add(cardPanel, BorderLayout.CENTER);
	}

	void refresh()
	{
		searchField.setText("");
		SwingUtilities.invokeLater(() -> {
			rebuildRows();
			searchField.requestFocusInWindow();
		});
	}

	private JPanel buildHeader()
	{
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);
		header.setBorder(new EmptyBorder(10, 10, 6, 10));

		JLabel titleLabel = new JLabel("New Override");
		titleLabel.setFont(FontManager.getRunescapeBoldFont());
		titleLabel.setForeground(Color.WHITE);

		JButton closeButton = new JButton();
		closeButton.setIcon(ToolbarIcons.CLOSE);
		closeButton.setRolloverIcon(ToolbarIcons.CLOSE_HOVER);
		closeButton.setToolTipText("Cancel");
		closeButton.setFocusPainted(false);
		SwingUtil.removeButtonDecorations(closeButton);
		closeButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		closeButton.setBackground(ColorScheme.DARK_GRAY_COLOR);
		closeButton.addActionListener(event -> {
			if (onCancel != null)
			{
				onCancel.run();
			}
		});

		JPanel titleRow = new JPanel(new BorderLayout());
		titleRow.setOpaque(false);
		titleRow.add(titleLabel, BorderLayout.WEST);
		titleRow.add(closeButton, BorderLayout.EAST);

		JLabel subtitle = new JLabel("Choose which action to override");
		subtitle.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		subtitle.setBorder(new EmptyBorder(4, 0, 8, 0));

		header.add(titleRow);
		header.add(subtitle);
		header.add(searchField);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, header.getPreferredSize().height));
		return header;
	}

	private void configureSearchField()
	{
		searchField.setIcon(IconTextField.Icon.SEARCH);
		searchField.setPreferredSize(new Dimension(100, 28));
		searchField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchField.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchField.addClearListener(this::rebuildRows);
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				rebuildRows();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				rebuildRows();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				rebuildRows();
			}
		});
	}

	private void buildCardPanel()
	{
		cardPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JPanel infoWrapper = new JPanel(new BorderLayout());
		infoWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		infoWrapper.setBorder(new EmptyBorder(20, 8, 20, 8));
		infoPanel.setContent("No matches", "No actions match your search.");
		infoWrapper.add(infoPanel, BorderLayout.NORTH);

		cardPanel.add(scrollPane, CARD_RESULTS);
		cardPanel.add(infoWrapper, CARD_INFO);
		cardLayout.show(cardPanel, CARD_RESULTS);
	}

	private void rebuildRows()
	{
		SwingUtil.fastRemoveAll(rowsContainer);

		String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ENGLISH);
		int matchCount = 0;
		for (SoundOverrideAction action : SoundOverrideAction.values())
		{
			if (!query.isEmpty() && !action.getDisplayName().toLowerCase(Locale.ENGLISH).contains(query))
			{
				continue;
			}

			boolean alreadyOverridden = !soundOverrideService.getOverrideFileNames(action).isEmpty();
			JPanel row = createRow(action, alreadyOverridden);

			JPanel margin = new JPanel(new BorderLayout());
			margin.setBackground(ColorScheme.DARK_GRAY_COLOR);
			margin.setBorder(new EmptyBorder(2, 8, 2, 8));
			margin.add(row, BorderLayout.CENTER);
			margin.setMaximumSize(new Dimension(Integer.MAX_VALUE, margin.getPreferredSize().height));
			rowsContainer.add(margin);
			matchCount++;
		}

		if (matchCount == 0)
		{
			cardLayout.show(cardPanel, CARD_INFO);
		}
		else
		{
			rowsContainer.add(Box.createVerticalGlue());
			cardLayout.show(cardPanel, CARD_RESULTS);
		}

		rowsContainer.revalidate();
		rowsContainer.repaint();
		scrollPane.scrollToTop();
	}

	private JPanel createRow(SoundOverrideAction action, boolean alreadyOverridden)
	{
		JPanel row = new JPanel(new BorderLayout(6, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		Color accent = alreadyOverridden ? new Color(95, 175, 95) : ColorScheme.BORDER_COLOR;
		row.setBorder(new CompoundBorder(
			BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
			new EmptyBorder(8, 10, 8, 10)
		));

		JLabel nameLabel = new JLabel(action.getDisplayName());
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(FontManager.getRunescapeFont());

		row.add(nameLabel, BorderLayout.CENTER);

		if (alreadyOverridden)
		{
			JLabel statusLabel = new JLabel("override set");
			statusLabel.setForeground(new Color(95, 175, 95));
			statusLabel.setFont(FontManager.getRunescapeSmallFont());
			row.add(statusLabel, BorderLayout.EAST);
		}

		row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		row.setToolTipText(alreadyOverridden
			? "Edit existing override for " + action.getDisplayName()
			: "Create override for " + action.getDisplayName());

		final Color hoverBackground = ColorScheme.DARK_GRAY_HOVER_COLOR;
		final Color normalBackground = ColorScheme.DARKER_GRAY_COLOR;
		row.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				row.setBackground(hoverBackground);
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				row.setBackground(normalBackground);
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (event.getButton() == MouseEvent.BUTTON1 && onActionSelected != null)
				{
					onActionSelected.accept(action);
				}
			}
		});
		return row;
	}
}
