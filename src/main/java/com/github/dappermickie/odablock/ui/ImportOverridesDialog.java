package com.github.dappermickie.odablock.ui;

import com.github.dappermickie.odablock.overrides.SoundOverrideService;
import com.google.gson.JsonSyntaxException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

@Slf4j
class ImportOverridesDialog extends JDialog
{
	private static final Color ERROR_COLOR = new Color(220, 95, 95);
	private static final Color SUCCESS_COLOR = new Color(95, 175, 95);

	private final SoundOverrideService soundOverrideService;
	private final Consumer<String> onImportSucceeded;

	private final JTextArea textArea;
	private final JCheckBox replaceAllToggle;
	private final JLabel statusLabel;
	private final JButton importButton;

	ImportOverridesDialog(
		Window owner,
		SoundOverrideService soundOverrideService,
		Consumer<String> onImportSucceeded)
	{
		super(owner, "Import Odablock Overrides", ModalityType.APPLICATION_MODAL);
		this.soundOverrideService = soundOverrideService;
		this.onImportSucceeded = onImportSucceeded;

		setLayout(new BorderLayout(0, 8));
		getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().setBackground(ColorScheme.DARKER_GRAY_COLOR);

		this.textArea = buildTextArea();
		this.replaceAllToggle = buildReplaceToggle();
		this.statusLabel = new JLabel(" ");
		this.statusLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		this.statusLabel.setBorder(new EmptyBorder(4, 2, 0, 0));

		this.importButton = new JButton("Import");
		this.importButton.addActionListener(event -> doImport());

		add(buildTopPanel(), BorderLayout.NORTH);
		add(buildCenterPanel(), BorderLayout.CENTER);
		add(buildBottomPanel(), BorderLayout.SOUTH);

		setMinimumSize(new Dimension(360, 280));
		pack();
		setLocationRelativeTo(owner);
	}

	private JPanel buildTopPanel()
	{
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		JLabel pasteLabel = new JLabel("Paste exported overrides JSON:");
		pasteLabel.setForeground(Color.WHITE);
		pasteLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
		topPanel.add(pasteLabel, BorderLayout.NORTH);
		return topPanel;
	}

	private JPanel buildCenterPanel()
	{
		JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
		centerPanel.setOpaque(false);

		JScrollPane textScroll = new JScrollPane(textArea);
		textScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		textScroll.setPreferredSize(new Dimension(360, 160));

		centerPanel.add(textScroll, BorderLayout.CENTER);
		centerPanel.add(replaceAllToggle, BorderLayout.SOUTH);
		return centerPanel;
	}

	private JPanel buildBottomPanel()
	{
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);

		bottomPanel.add(statusLabel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		buttonPanel.setOpaque(false);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(event -> dispose());

		buttonPanel.add(importButton);
		buttonPanel.add(cancelButton);

		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		return bottomPanel;
	}

	private JTextArea buildTextArea()
	{
		JTextArea area = new JTextArea(8, 36);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);

		JPopupMenu popup = new JPopupMenu();
		JMenuItem pasteItem = new JMenuItem("Paste");
		pasteItem.addActionListener(event -> {
			try
			{
				Object clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard().getData(DataFlavor.stringFlavor);
				if (clipboard instanceof String)
				{
					area.replaceSelection((String) clipboard);
				}
			}
			catch (Exception exception)
			{
				log.debug("Failed to paste from clipboard", exception);
			}
		});
		popup.add(pasteItem);
		area.setComponentPopupMenu(popup);

		return area;
	}

	private JCheckBox buildReplaceToggle()
	{
		JCheckBox toggle = new JCheckBox("Replace all existing overrides");
		toggle.setOpaque(false);
		toggle.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		toggle.setToolTipText(
			"<html>If checked, every action's overrides are cleared first.<br>"
				+ "If unchecked, only actions present in the JSON are replaced.</html>");
		return toggle;
	}

	private void doImport()
	{
		String json = textArea.getText() == null ? "" : textArea.getText().trim();
		if (json.isEmpty())
		{
			showError("Paste an exported overrides JSON first.");
			return;
		}

		try
		{
			SoundOverrideService.ImportResult result = replaceAllToggle.isSelected()
				? soundOverrideService.replaceAllOverridesFromJson(json)
				: soundOverrideService.importOverridesFromJson(json);

			SwingUtilities.invokeLater(() -> {
				if (onImportSucceeded != null)
				{
					onImportSucceeded.accept(buildSuccessSummary(result));
				}
				dispose();
			});
		}
		catch (JsonSyntaxException exception)
		{
			log.debug("Invalid overrides JSON pasted", exception);
			showError("Invalid JSON. Make sure you pasted an exported overrides payload.");
		}
		catch (Exception exception)
		{
			log.warn("Failed to import overrides", exception);
			showError("Import failed: " + exception.getMessage());
		}
	}

	private void showError(String message)
	{
		statusLabel.setForeground(ERROR_COLOR);
		statusLabel.setText(message);
	}

	private String buildSuccessSummary(SoundOverrideService.ImportResult result)
	{
		StringBuilder builder = new StringBuilder();
		int imported = result.getImportedActions();
		if (imported == 0)
		{
			builder.append("No matching actions to import.");
		}
		else if (imported == 1)
		{
			builder.append("Imported 1 action override.");
		}
		else
		{
			builder.append("Imported ").append(imported).append(" action overrides.");
		}
		if (result.getSkippedActions() > 0)
		{
			builder.append(" Skipped ").append(result.getSkippedActions()).append(" unknown action(s).");
		}
		return builder.toString();
	}
}
