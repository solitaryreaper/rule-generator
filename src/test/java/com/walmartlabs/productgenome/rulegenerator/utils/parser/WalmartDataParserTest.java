package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;

public class WalmartDataParserTest {
	private static Logger LOG = Logger.getLogger(WalmartDataParserTest.class.getName());
	private static DataParser parser = null;
	
	@BeforeClass
	public static void testSetup() 
	{
		parser = new WalmartDataParser();
	}
	
	@AfterClass
	public static void testCleanup() 
	{
		
	}
	
	@Test
	public void testParseCNETDotcomData()
	{
		File matchFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/WALMART-DATA/CNET_WALMART_DOTCOM_MATCHED.txt");
		File mismatchFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/WALMART-DATA/CNET_WALMART_DOTCOM_MISMATCHED.txt");
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("ID", "ID");
		schemaMap.put("normalized_upc_1", "normalized_upc_1");
		schemaMap.put("normalized_upc_2", "normalized_upc_2");
		schemaMap.put("pd_title", "pd_title");
		schemaMap.put("req_brand_name", "req_brand_name");
		schemaMap.put("req_category", "req_category");
		schemaMap.put("req_color", "req_color");
		schemaMap.put("req_description", "req_description");
		schemaMap.put("req_manufacturer", "req_manufacturer");
		schemaMap.put("req_part_number", "req_part_number");
		schemaMap.put("req_raw_part_number", "req_raw_part_number");
		schemaMap.put("req_upc", "req_upc");
		schemaMap.put("req_upc_10", "req_upc_10");
		schemaMap.put("req_upc_11", "req_upc_11");
		schemaMap.put("req_upc_12", "req_upc_12");
		schemaMap.put("req_upc_13", "req_upc_13");
		schemaMap.put("req_upc_14", "req_upc_14");
		schemaMap.put("Source", "Source");
		schemaMap.put("Title", "Title");
		schemaMap.put("UPC", "UPC");
		Dataset cnetDotcomData = parser.parseData("CNET-Dotcom", matchFile, mismatchFile, schemaMap);
		assertNotNull(cnetDotcomData);
		
		List<ItemPair> itemPairs = cnetDotcomData.getItemPairs();
		assertNotNull(itemPairs);		
	}
}
