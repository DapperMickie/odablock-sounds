package com.github.dappermickie.odablock.overrides;

import java.io.File;
import lombok.Value;

@Value
public class SoundOverrideOption
{
	String directory;
	String fileName;
	String displayLabel;
	File file;
	boolean defaultForAction;

	public String getStorageKey()
	{
		return directory + "/" + fileName;
	}
}
