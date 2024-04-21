package com.github.dappermickie.odablock;

import lombok.Getter;

public class RightClickAction
{
	public RightClickAction(String action, String link)
	{
		this.action = action;
		this.link = link;
	}

	@Getter
	private final String action;
	@Getter
	private final String link;
}
