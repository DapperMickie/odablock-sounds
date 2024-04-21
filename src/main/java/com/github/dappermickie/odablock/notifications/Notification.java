package com.github.dappermickie.odablock.notifications;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class Notification
{
	@Getter
	private String title;

	@Getter
	private String message;

	@Getter
	private String link;

	@Getter
	@SerializedName("unique_identifier")
	private String uniqueIdentifier;
}
