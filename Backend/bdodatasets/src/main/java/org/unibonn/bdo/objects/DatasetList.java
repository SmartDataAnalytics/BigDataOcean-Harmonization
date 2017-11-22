package org.unibonn.bdo.objects;

public class DatasetList {
	
	String title;
	String description;
	
	
	
	public DatasetList() {
	}
	
	public DatasetList(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
