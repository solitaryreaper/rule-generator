package com.walmartlabs.productgenome.rulegenerator.model.data;

import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

/**
 * Meta information that guides how the dataset is interpreted.
 * @author skprasad
 *
 */
public class DatasetNormalizerMeta {

	// Mapping of the schema elements across the data sources
	private BiMap<String, String> schemaMap = HashBiMap.create();
	
	// Mapping of which attribute is multi-valued and it's value delimiter. If nothing is specified,
	// it is assumed that attributes are not multi-valued.
	private Map<String, String> multiValueAttrDelimiterMap = Maps.newHashMap();
	
	// Mapping of what tokenization delimiter is to be used for attribute value. If nothing is 
	// specified, default tokenizer is used.
	private Map<String, String> valueTokenizerDelimiterMap = Maps.newHashMap();
	
}
