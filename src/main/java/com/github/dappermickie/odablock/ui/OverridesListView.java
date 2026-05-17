package com.github.dappermickie.odablock.ui;

import com.github.dappermickie.odablock.overrides.SoundOverrideAction;
import com.github.dappermickie.odablock.overrides.SoundOverrideService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.SwingUtil;

class OverridesListView extends JPanel
{
	private final SoundOverrideService soundOverrideService;
	private final Runnable onAddOverrideRequested;
	private final Consumer<SoundOverrideAction> onEditOverrideRequested;

	private final VerticalScrollPane scrollPane;
	private final VerticalScrollPane.ScrollableContainer rowsContainer;
	private final PluginErrorPanel emptyPanel;
	private final JLabel toastLabel;
	private final Timer toastTimer;

	OverridesListView(
		SoundOverrideService soundOverrideService,
		Runnable onAddOverrideRequested,
		Consumer<SoundOverrideAction> onEditOverrideRequested)
	{
		this.soundOverrideService = soundOverrideService;
		this.onAddOverrideRequested = onAddOverrideRequested;
		this.onEditOverrideRequested = onEditOverrideRequested;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		this.scrollPane = new VerticalScrollPane();
		this.rowsContainer = scrollPane.getContainer();
		this.emptyPanel = new PluginErrorPanel();
		this.toastLabel = new JLabel();
		this.toastTimer = new Timer(1800, event -> toastLabel.setVisible(false));
		this.toastTimer.setRepeats(false);

		emptyPanel.setContent(
			"No overrides yet",
			"Click the + button above to create your first override."
		);

		add(buildHeader(), BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		refresh();
	}

	void refresh()
	{
		SwingUtilities.invokeLater(() -> rebuildRows(false));
	}

	private JPanel buildHeader()
	{
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);
		header.setBorder(new EmptyBorder(10, 10, 8, 10));

		JLabel titleLabel = new JLabel("Odablock Overrides");
		titleLabel.setFont(FontManager.getRunescapeBoldFont());
		titleLabel.setForeground(Color.WHITE);

		JButton exportButton = new JButton();
		stylizeIconButton(exportButton, ToolbarIcons.EXPORT, ToolbarIcons.EXPORT_HOVER);
		exportButton.setToolTipText("Copy overrides JSON to clipboard");
		exportButton.addActionListener(event -> exportToClipboard());

		JButton importButton = new JButton();
		stylizeIconButton(importButton, ToolbarIcons.IMPORT, ToolbarIcons.IMPORT_HOVER);
		importButton.setToolTipText("Import overrides from JSON");
		importButton.addActionListener(event -> openImportDialog());

		JButton addButton = new JButton();
		stylizeIconButton(addButton, ToolbarIcons.PLUS, ToolbarIcons.PLUS_HOVER);
		addButton.setToolTipText("Add override");
		addButton.addActionListener(event -> onAddOverrideRequested.run());

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		toolbar.setOpaque(false);
		toolbar.add(exportButton);
		toolbar.add(importButton);
		toolbar.add(addButton);

		JPanel titleRow = new JPanel(new BorderLayout());
		titleRow.setOpaque(false);
		titleRow.add(titleLabel, BorderLayout.WEST);
		titleRow.add(toolbar, BorderLayout.EAST);

		toastLabel.setForeground(new Color(95, 175, 95));
		toastLabel.setVisible(false);
		toastLabel.setBorder(new EmptyBorder(6, 2, 0, 0));

		header.add(titleRow);
		header.add(toastLabel);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, header.getPreferredSize().height));
		return header;
	}

	private void stylizeIconButton(JButton button, javax.swing.ImageIcon icon, javax.swing.ImageIcon rolloverIcon)
	{
		button.setIcon(icon);
		button.setRolloverIcon(rolloverIcon);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		SwingUtil.removeButtonDecorations(button);
		button.setBackground(ColorScheme.DARK_GRAY_COLOR);
	}

	private void rebuildRows(boolean resetScroll)
	{
		int previousScroll = scrollPane.getScrollValue();

		SwingUtil.fastRemoveAll(rowsContainer);

		Map<SoundOverrideAction, Integer> activeOverrides = new LinkedHashMap<>();
		for (SoundOverrideAction action : SoundOverrideAction.values())
		{
			int count = soundOverrideService.getOverrideFileNames(action).size();
			if (count > 0)
			{
				activeOverrides.put(action, count);
			}
		}

		if (activeOverrides.isEmpty())
		{
			JPanel emptyWrapper = new JPanel(new BorderLayout());
			emptyWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
			emptyWrapper.setBorder(new EmptyBorder(20, 8, 20, 8));
			emptyWrapper.add(emptyPanel, BorderLayout.CENTER);
			rowsContainer.add(emptyWrapper);
		}
		else
		{
			for (Map.Entry<SoundOverrideAction, Integer> entry : activeOverrides.entrySet())
			{
				OverrideRow row = new OverrideRow(
					entry.getKey(),
					entry.getValue(),
					onEditOverrideRequested,
					this::deleteOverride
				);
				JPanel margin = new JPanel(new BorderLayout());
				margin.setBackground(ColorScheme.DARK_GRAY_COLOR);
				margin.setBorder(new EmptyBorder(2, 8, 2, 8));
				margin.add(row, BorderLayout.CENTER);
				margin.setMaximumSize(new Dimension(Integer.MAX_VALUE, margin.getPreferredSize().height));
				rowsContainer.add(margin);
			}
		}

		rowsContainer.add(Box.createVerticalGlue());
		rowsContainer.revalidate();
		rowsContainer.repaint();

		if (resetScroll)
		{
			scrollPane.scrollToTop();
		}
		else
		{
			// Restore scroll AFTER layout has settled so the scrollbar's max
			// has been recomputed against the new contents.
			SwingUtilities.invokeLater(() -> scrollPane.setScrollValue(previousScroll));
		}
	}

	private void deleteOverride(SoundOverrideAction action)
	{
		soundOverrideService.clearOverrideFileNames(action);
		refresh();
	}

	private void exportToClipboard()
	{
		String json = soundOverrideService.exportOverridesAsJson();
		StringSelection selection = new StringSelection(json);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		showToast("Copied overrides JSON to clipboard");
	}

	private void openImportDialog()
	{
		Window owner = SwingUtilities.getWindowAncestor(this);
		ImportOverridesDialog dialog = new ImportOverridesDialog(
			owner,
			soundOverrideService,
			summary -> {
				refresh();
				showToast(summary);
			}
		);
		dialog.setVisible(true);
	}

	private void showToast(String message)
	{
		toastLabel.setText(message);
		toastLabel.setVisible(true);
		toastTimer.restart();
	}
}
