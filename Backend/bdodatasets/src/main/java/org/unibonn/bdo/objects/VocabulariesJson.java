package org.unibonn.bdo.objects;

import java.io.Serializable;

/**
 *  
 * @author Jaime M Trillos
 *
 * Object of the vocabularies to convert in JSON and be used in the Frontend
 *
 */

public class VocabulariesJson implements Serializable{

	private static final long serialVersionUID = -8901682809735151149L;
	
	private String value;
	private String text;
	private String name;
	
	public VocabulariesJson() {
		
	}

	public VocabulariesJson(String text, String value) {
		this.value = value;
		this.text = text;
	}
	
	public VocabulariesJson(String text, String value, String name) {
		this.value = value;
		this.text = text;
		this.setName(name);
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
