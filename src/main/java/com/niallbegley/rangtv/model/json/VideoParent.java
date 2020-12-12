package com.niallbegley.rangtv.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoParent {
	@JsonProperty(value="data")
	private Video video;

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}
}
