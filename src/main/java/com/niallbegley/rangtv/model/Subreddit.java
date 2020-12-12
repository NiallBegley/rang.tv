package com.niallbegley.rangtv.model;

public class Subreddit {
	private final String value;
	private final String name;
	
	public Subreddit(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
}
