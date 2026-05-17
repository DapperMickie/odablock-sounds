package com.github.dappermickie.odablock.ui;

import com.github.dappermickie.odablock.SoundEngine;
import com.github.dappermickie.odablock.overrides.SoundOverrideAction;
import com.github.dappermickie.odablock.overrides.SoundOverrideService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Singleton
public class SoundOverridesPanel extends PluginPanel
{
	static final String CARD_LIST = "LIST";
	static final String CARD_ACTION_PICKER = "ACTION_PICKER";
	static final String CARD_SOUND_PICKER = "SOUND_PICKER";

	private final SoundOverrideService soundOverrideService;

	private final CardLayout cardLayout;
	private final OverridesListView listView;
	private final ActionPickerView actionPickerView;
	private final SoundPickerView soundPickerView;

	@Inject
	public SoundOverridesPanel(
		SoundOverrideService soundOverrideService,
		SoundEngine soundEngine,
		ScheduledExecutorService executor)
	{
		super(false);
		this.soundOverrideService = soundOverrideService;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		this.cardLayout = new CardLayout();
		setLayout(cardLayout);

		this.listView = new OverridesListView(
			soundOverrideService,
			this::showActionPicker,
			this::showSoundPicker
		);
		this.actionPickerView = new ActionPickerView(
			soundOverrideService,
			this::showSoundPicker,
			this::showList
		);
		this.soundPickerView = new SoundPickerView(
			soundOverrideService,
			soundEngine,
			executor,
			this::showList
		);

		add(listView, CARD_LIST);
		add(actionPickerView, CARD_ACTION_PICKER);
		add(soundPickerView, CARD_SOUND_PICKER);

		cardLayout.show(this, CARD_LIST);
	}

	@Override
	public void onActivate()
	{
		SwingUtilities.invokeLater(listView::refresh);
	}

	private void showList()
	{
		listView.refresh();
		cardLayout.show(this, CARD_LIST);
	}

	private void showActionPicker()
	{
		actionPickerView.refresh();
		cardLayout.show(this, CARD_ACTION_PICKER);
	}

	private void showSoundPicker(SoundOverrideAction action)
	{
		soundPickerView.loadAction(action);
		cardLayout.show(this, CARD_SOUND_PICKER);
	}
}
