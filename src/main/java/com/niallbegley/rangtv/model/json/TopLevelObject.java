package com.niallbegley.rangtv.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TopLevelObject {
	
	private Data data;
	
	public TopLevelObject()  {  }

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	} 
}
