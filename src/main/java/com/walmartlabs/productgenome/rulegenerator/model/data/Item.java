package com.walmartlabs.productgenome.rulegenerator.model.data;

import java.util.Map;

import com.google.common.base.Objects;

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

	public boolean equals(Object obj)
	{
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
	    final Item that = (Item) obj;
	    return 	Objects.equal(this.id, that.id);		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Map<String, String> attrMap) {
		this.attrMap = attrMap;
	}
	
	public String getValuesForAttr(String attrName)
	{
		String value = null;
		if(attrMap.containsKey(attrName)) {
			value = attrMap.get(attrName);
		}
		
		return value;
	}
	
}
