package com.github.dappermickie.odablock.sounds;


import com.github.dappermickie.odablock.OdablockConfig;
import com.github.dappermickie.odablock.Sound;
import com.github.dappermickie.odablock.SoundEngine;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;

public class OdablockWarriors
{
	@Inject
	private Client client;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private OdablockConfig config;

	private Widget warriorWidget = null;

	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (!config.warriors())
		{
			return;
		}
		Widget musicWidget = client.getWidget(15663110);

		if (musicWidget == null)
		{
			return;
		}

		for (Widget w : musicWidget.getDynamicChildren())
		{
			if (w.getText().equals("7th Realm") || w.getText().equals("Odablock Warriors"))
			{
				w.setText("Odablock Warriors");
				w.setName("<col=ff9040>Odablock Warriors</col>");
				w.setTextColor(901389);
				w.revalidate();
				warriorWidget = w;
				return;
			}
		}
	}

	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (warriorWidget == null)
		{
			return;
		}
		if (event.getWidget() != warriorWidget)
		{
			return;
		}

		event.consume();

		soundEngine.playClip(Sound.WARRIOR, executor);
	}
}
