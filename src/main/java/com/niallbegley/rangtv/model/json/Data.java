package com.niallbegley.rangtv.model.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

	private List<VideoParent> children;
	
	public Data() { }
	
	public List<VideoParent> getChildren() {
		return children;
	}

	public void setChildren(List<VideoParent> children) {
		this.children = children;
	}
}
