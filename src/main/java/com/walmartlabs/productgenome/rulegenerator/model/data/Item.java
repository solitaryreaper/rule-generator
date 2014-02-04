package com.walmartlabs.productgenome.rulegenerator.model.data;

import java.util.Map;

/**
 * Models a single item like a product etc.
 * 
 * @author excelsior
 *
 */
public class Item {
	private String id;
	private Map<String, String> attrMap;
	
	public Item(String id, Map<String, String> attrMap) {
		super();
		this.id = id;
		this.attrMap = attrMap;
	}
	
	
}
