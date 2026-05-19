package com.github.dappermickie.odablock.livestreams;

import lombok.Getter;

public class Livestream
{
	@Getter
	private boolean live;

	@Getter
	private String title;

	@Getter
	private String wentLiveAt;
}
