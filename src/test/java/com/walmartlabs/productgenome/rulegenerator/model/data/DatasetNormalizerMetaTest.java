package com.walmartlabs.productgenome.rulegenerator.model.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

public class DatasetNormalizerMetaTest {

	@Test
	public void testIsSetValuedAttribute()
	{
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("pd_title", "pd_title");
		schemaMap.put("req_upc_14", "upc_14");
		
		List<String> setValuedAttrs = Lists.newArrayList("req_upc_14");
		
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap, setValuedAttrs);
		assertTrue(normalizerMeta.isSetValuedAttribute("req_upc_14"));
		assertTrue(normalizerMeta.isSetValuedAttribute("upc_14"));
		assertFalse(normalizerMeta.isSetValuedAttribute("pd_title"));
	}
}
