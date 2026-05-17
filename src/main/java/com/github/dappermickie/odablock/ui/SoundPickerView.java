package com.github.dappermickie.odablock.ui;

import com.github.dappermickie.odablock.SoundEngine;
import com.github.dappermickie.odablock.overrides.SoundOverrideAction;
import com.github.dappermickie.odablock.overrides.SoundOverrideOption;
import com.github.dappermickie.odablock.overrides.SoundOverrideService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.SwingUtil;

class SoundPickerView extends JPanel
{
	private static final String CARD_RESULTS = "RESULTS";
	private static final String CARD_INFO = "INFO";

	private final SoundOverrideService soundOverrideService;
	private final SoundEngine soundEngine;
	private final ScheduledExecutorService executor;
	private final Runnable onClose;

	private final JLabel titleLabel;
	private final JLabel summaryLabel;
	private final IconTextField searchField;
	private final VerticalScrollPane scrollPane;
	private final VerticalScrollPane.ScrollableContainer rowsContainer;
	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	private final PluginErrorPanel infoPanel;

	private SoundOverrideAction currentAction;
	private List<SoundOverrideOption> availableOptions = new ArrayList<>();
	private final Set<String> selectedKeys = new LinkedHashSet<>();
	private boolean hasUserEditedSelection;

	SoundPickerView(
		SoundOverrideService soundOverrideService,
		SoundEngine soundEngine,
		ScheduledExecutorService executor,
		Runnable onClose)
	{
		this.soundOverrideService = soundOverrideService;
		this.soundEngine = soundEngine;
		this.executor = executor;
		this.onClose = onClose;

		this.titleLabel = new JLabel();
		this.summaryLabel = new JLabel();
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

	void loadAction(SoundOverrideAction action)
	{
		this.currentAction = action;
		titleLabel.setText(action.getDisplayName());

		Set<String> existingPool = soundOverrideService.getOverrideFileNames(action);
		selectedKeys.clear();
		if (existingPool.isEmpty())
		{
			// Pre-check the action's default sounds so users can uncheck the
			// ones they don't want. Not persisted until they edit.
			selectedKeys.addAll(soundOverrideService.getDefaultStorageKeys(action, true));
			hasUserEditedSelection = false;
		}
		else
		{
			selectedKeys.addAll(existingPool);
			hasUserEditedSelection = false;
		}

		availableOptions = soundOverrideService.getAllSoundOptions(action, true);
		searchField.setText("");
		rebuildRows(true);
		SwingUtilities.invokeLater(searchField::requestFocusInWindow);
	}

	private JPanel buildHeader()
	{
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);
		header.setBorder(new EmptyBorder(10, 10, 6, 10));

		JButton backButton = new JButton();
		backButton.setIcon(ToolbarIcons.BACK);
		backButton.setRolloverIcon(ToolbarIcons.BACK_HOVER);
		backButton.setToolTipText("Back to overrides");
		backButton.setFocusPainted(false);
		SwingUtil.removeButtonDecorations(backButton);
		backButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		backButton.setBackground(ColorScheme.DARK_GRAY_COLOR);
		backButton.addActionListener(event -> {
			persistSelections();
			if (onClose != null)
			{
				onClose.run();
			}
		});

		titleLabel.setFont(FontManager.getRunescapeBoldFont());
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBorder(new EmptyBorder(0, 6, 0, 0));

		JPanel titleRow = new JPanel(new BorderLayout());
		titleRow.setOpaque(false);
		titleRow.add(backButton, BorderLayout.WEST);
		titleRow.add(titleLabel, BorderLayout.CENTER);

		summaryLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		summaryLabel.setBorder(new EmptyBorder(4, 0, 8, 0));
		summaryLabel.setAlignmentX(LEFT_ALIGNMENT);

		header.add(titleRow);
		header.add(summaryLabel);
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
		searchField.addClearListener(() -> rebuildRows(true));
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				rebuildRows(true);
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				rebuildRows(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				rebuildRows(true);
			}
		});
	}

	private void buildCardPanel()
	{
		cardPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel infoWrapper = new JPanel(new BorderLayout());
		infoWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		infoWrapper.setBorder(new EmptyBorder(20, 8, 20, 8));
		infoPanel.setContent("No sounds", "No sound files were found for this action.");
		infoWrapper.add(infoPanel, BorderLayout.NORTH);

		cardPanel.add(scrollPane, CARD_RESULTS);
		cardPanel.add(infoWrapper, CARD_INFO);
		cardLayout.show(cardPanel, CARD_RESULTS);
	}

	private void rebuildRows(boolean resetScroll)
	{
		if (currentAction == null)
		{
			return;
		}

		SwingUtilities.invokeLater(() -> {
			int previousScroll = scrollPane.getScrollValue();
			SwingUtil.fastRemoveAll(rowsContainer);

			if (availableOptions.isEmpty())
			{
				infoPanel.setContent(
					"No sounds found",
					"Open the plugin once in-game so the assets can download, then come back here."
				);
				cardLayout.show(cardPanel, CARD_INFO);
				updateSummary();
				return;
			}

			String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ENGLISH);
			int matchCount = 0;
			for (SoundOverrideOption option : availableOptions)
			{
				boolean matches = query.isEmpty()
					|| option.getDisplayLabel().toLowerCase(Locale.ENGLISH).contains(query)
					|| option.getFileName().toLowerCase(Locale.ENGLISH).contains(query);
				if (!matches)
				{
					continue;
				}

				boolean selected = selectedKeys.contains(option.getStorageKey());
				OptionRow row = new OptionRow(option, selected, this::toggleSelection, this::previewOption);

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
				infoPanel.setContent("No matches", "Try a different search.");
				cardLayout.show(cardPanel, CARD_INFO);
			}
			else
			{
				rowsContainer.add(Box.createVerticalGlue());
				cardLayout.show(cardPanel, CARD_RESULTS);
			}

			rowsContainer.revalidate();
			rowsContainer.repaint();

			if (resetScroll)
			{
				scrollPane.scrollToTop();
			}
			else
			{
				SwingUtilities.invokeLater(() -> scrollPane.setScrollValue(previousScroll));
			}
			updateSummary();
		});
	}

	private void updateSummary()
	{
		int totalSelected = selectedKeys.size();
		if (totalSelected == 0)
		{
			summaryLabel.setText("0 selected");
		}
		else if (totalSelected == 1)
		{
			summaryLabel.setText("1 selected");
		}
		else
		{
			summaryLabel.setText(totalSelected + " selected");
		}
	}

	private void toggleSelection(SoundOverrideOption option)
	{
		String key = option.getStorageKey();
		if (selectedKeys.contains(key))
		{
			selectedKeys.remove(key);
		}
		else
		{
			selectedKeys.add(key);
		}
		hasUserEditedSelection = true;
		persistSelections();
		rebuildRows(false);
	}

	private void previewOption(SoundOverrideOption option)
	{
		soundEngine.playFile(option.getFile(), executor);
	}

	private void persistSelections()
	{
		if (currentAction == null || !hasUserEditedSelection)
		{
			return;
		}
		soundOverrideService.setOverrideFileNames(currentAction, new ArrayList<>(selectedKeys));
		hasUserEditedSelection = false;
	}
}
