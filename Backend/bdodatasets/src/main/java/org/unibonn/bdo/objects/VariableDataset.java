package org.unibonn.bdo.objects;

import java.io.Serializable;

/**
 *  
 * @author Jaime M Trillos
 *
 * Object of the Variable Dataset Metadata
 *
 */

public class VariableDataset implements Serializable{
	
	private static final long serialVersionUID = 1835225915683815041L;
	
	private String name;
	private String canonicalName;
	private String unit;
	
	public VariableDataset() {
		
	}
	
	public VariableDataset(String name, String canonicalName, String unit) {
		super();
		this.name = name;
		this.canonicalName = canonicalName;
		this.unit = unit;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCanonicalName() {
		return canonicalName;
	}
	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	

}
