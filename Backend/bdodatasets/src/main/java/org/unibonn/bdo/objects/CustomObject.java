package org.unibonn.bdo.objects;

import java.io.Serializable;

/**
 * 
 * Object of the Kafka Connection
 *
 */

public class CustomObject implements Serializable{

	private static final long serialVersionUID = -3226340249579187184L;
	
	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}