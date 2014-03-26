package com.walmartlabs.productgenome.rulegenerator.model.data;

import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.Constants;

/**
 * Meta information that guides how the dataset is interpreted.
 * @author skprasad
 *
 */
public class DatasetNormalizerMeta {

	// Mapping of the schema elements across the data sources. Only the attributes specified here
	// would be considered for the dataset. Choose the first source's attribute name for schema
	// normalization
	private BiMap<String, String> schemaMap = HashBiMap.create();
	
	// Mapping of which attribute is multi-valued and it's value delimiter. If nothing is specified,
	// it is assumed that attributes are not multi-valued.
	private Map<String, String> multiValueAttrDelimiterMap = Maps.newHashMap();
	
	// Mapping of what tokenization delimiter is to be used for attribute value. If nothing is 
	// specified, default tokenizer is used.
	private Map<String, String> valueTokenizationDelimiterMap = Maps.newHashMap();

	public BiMap<String, String> getSchemaMap() {
		return schemaMap;
	}
	
	public void setSchemaMap(BiMap<String, String> schemaMap) {
		this.schemaMap = schemaMap;
	}

	public Map<String, String> getMultiValueAttrDelimiterMap() {
		return multiValueAttrDelimiterMap;
	}
	
	public void setMultiValueAttrDelimiterMap(
			Map<String, String> multiValueAttrDelimiterMap) {
		this.multiValueAttrDelimiterMap = multiValueAttrDelimiterMap;
	}

	public Map<String, String> getValueTokenizationDelimiterMap() {
		return valueTokenizationDelimiterMap;
	}
	
	public void setValueTokenizationDelimiterMap(
			Map<String, String> valueTokenizationDelimiterMap) {
		this.valueTokenizationDelimiterMap = valueTokenizationDelimiterMap;
	}
	
	public String getValueDelimiterForAttribute(String attrName)
	{
		String valueDelimiter = null;
		Map<String, String> setValueDelimiterMap = getMultiValueAttrDelimiterMap();
		if(setValueDelimiterMap.containsKey(attrName)) {
			valueDelimiter = setValueDelimiterMap.get(attrName);
		}
		else {
			BiMap<String, String> schemaMap = getSchemaMap();
			if(schemaMap.containsKey(attrName)) {
				if(setValueDelimiterMap.containsKey(schemaMap.get(attrName))) {
					valueDelimiter = setValueDelimiterMap.get(schemaMap.get(attrName));
				}
			}
			else if(schemaMap.containsValue(attrName)) {
				if(setValueDelimiterMap.containsKey(schemaMap.inverse().get(attrName))) {
					valueDelimiter = setValueDelimiterMap.get(schemaMap.inverse().get(attrName));
				}				
			}
		}		
		return valueDelimiter;
	}
	
	public String getTokenDelimiterForAttribute(String attrName)
	{
		String tokenDelimiter = Constants.DEFAULT_TOKENIZER;
		Map<String, String> tokenDelimiterMap = getValueTokenizationDelimiterMap();
		if(tokenDelimiterMap.containsKey(attrName)) {
			tokenDelimiter = tokenDelimiterMap.get(attrName);
		}
		else {
			BiMap<String, String> schemaMap = getSchemaMap();
			if(schemaMap.containsKey(attrName)) {
				if(tokenDelimiterMap.containsKey(schemaMap.get(attrName))) {
					tokenDelimiter = tokenDelimiterMap.get(schemaMap.get(attrName));
				}
			}
			else if(schemaMap.containsValue(attrName)) {
				if(tokenDelimiterMap.containsKey(schemaMap.inverse().get(attrName))) {
					tokenDelimiter = tokenDelimiterMap.get(schemaMap.inverse().get(attrName));
				}				
			}
		}
		
		return tokenDelimiter;
	}
}


