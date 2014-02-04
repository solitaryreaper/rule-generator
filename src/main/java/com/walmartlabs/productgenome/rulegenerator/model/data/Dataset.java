package com.walmartlabs.productgenome.rulegenerator.model.data;

import java.util.List;

public class Dataset {
	private String name;
	private List<String> attributes;
	private List<ItemPair> itemPairs;
	
	public Dataset(String name, List<String> attributes, List<ItemPair> itemPairs) {
		super();
		this.name = name;
		this.attributes = attributes;
		this.itemPairs = itemPairs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

}
