package org.unibonn.bdo.objects;

import java.io.Serializable;

/**
 *  
 * @author Jaime M Trillos
 *
 * Object of the Ontologies
 *
 */

public class Ontology implements Serializable{
	
	private static final long serialVersionUID = 4927388376216696931L;
	
	private String uri; 
	private String label; 
	private String url;
	private String canonicalName;
	
	public Ontology() {
		super();
	}
	
	public Ontology(String uri, String label) {
		this.label = label;
		this.uri = uri;
	}
	
	public Ontology(String uri, String label,  String url) {
		this.uri = uri;
		this.label = label;
		this.url = url;
	}
	
	public Ontology(String uri, String label,  String url, String canonicalName) {
		this.uri = uri;
		this.label = label;
		this.url = url;
		this.canonicalName = canonicalName;
	}
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCanonicalName() {
		return canonicalName;
	}
	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}
	
}
