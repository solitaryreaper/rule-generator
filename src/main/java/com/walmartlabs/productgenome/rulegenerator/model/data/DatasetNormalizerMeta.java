package com.walmartlabs.productgenome.rulegenerator.model.data;

import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.walmartlabs.productgenome.rulegenerator.Constants;

/**
 * Meta information that guides how the dataset is interpreted.
 * @author skprasad
 *
 */
public class DatasetNormalizerMeta {

	private String columnDelimiter = Constants.DEFAULT_COLUMN_DELIMITER;
	private String setValueDelimiter = Constants.DEFAULT_SET_VALUE_ATTRIBIUTE_DELIMITER;
	private String tokenDelimiter = Constants.DEFAULT_TOKENIZATION_DELIMITER;
	
	private List<String> setValuedAttributes = null;
	
	// Mapping of the schema elements across the data sources. Only the attributes specified here
	// would be considered for the dataset. Choose the first source's attribute name for schema
	// normalization.
	private BiMap<String, String> schemaMap = null;
	
	public DatasetNormalizerMeta()
	{
		
	}
	
	public DatasetNormalizerMeta(BiMap<String, String> schemaMap, List<String> setValuedAttributes)
	{
		this.schemaMap = schemaMap;
		this.setValuedAttributes = setValuedAttributes;
	}
	
	public BiMap<String, String> getSchemaMap() {
		return schemaMap;
	}
	
	public void setSchemaMap(BiMap<String, String> schemaMap) {
		this.schemaMap = schemaMap;
	}

	public String getColumnDelimiter() {
		return columnDelimiter;
	}

	public void setColumnDelimiter(String columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
	}

	public String getSetValueDelimiter() {
		return setValueDelimiter;
	}

	public void setSetValueDelimiter(String setValueDelimiter) {
		this.setValueDelimiter = setValueDelimiter;
	}

	public String getTokenDelimiter() {
		return tokenDelimiter;
	}

	public void setTokenDelimiter(String tokenDelimiter) {
		this.tokenDelimiter = tokenDelimiter;
	}

	public List<String> getSetValuedAttributes() {
		return setValuedAttributes;
	}

	public void setSetValuedAttributes(List<String> setValuedAttributes) {
		this.setValuedAttributes = setValuedAttributes;
	}
	
	public boolean isSetValuedAttribute(String attrName)
	{
		return getSetValuedAttributes().contains(attrName);
	}
}


