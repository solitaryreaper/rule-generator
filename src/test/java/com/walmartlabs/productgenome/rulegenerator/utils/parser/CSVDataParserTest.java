package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;

public class CSVDataParserTest {

	private static Logger LOG = Logger.getLogger(CSVDataParserTest.class.getName());
	private static DataParser parser = null;
	
	@BeforeClass
	public static void testSetup() 
	{
		parser = new CSVDataParser();
	}
	
	@AfterClass
	public static void testCleanup() 
	{
		
	}
	
	@Test
	public void testParsingAbtBuyData()
	{
		File srcFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/Abt.csv");
		File tgtFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/Buy.csv");
		File goldFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/abt_buy_perfectMapping.csv");
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("name", "name");
		schemaMap.put("description", "description");
		schemaMap.put("price", "price");
		Dataset abtBuyData = parser.parseData("Abt-Buy", srcFile, tgtFile, goldFile, schemaMap);
		assertNotNull(abtBuyData);
		
		List<ItemPair> itemPairs = abtBuyData.getItemPairs();
		assertNotNull(itemPairs);
	}
	
	@Ignore
	public void testParsingDBLPScholarData()
	{
		File srcFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/DBLP1_cleaned.csv");
		File tgtFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/Scholar_cleaned.csv");
		File goldFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/DBLP-Scholar_perfectMapping.csv");
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("title", "title");
		schemaMap.put("authors", "authors");
		schemaMap.put("venue", "venue");
		schemaMap.put("year", "year");		
		Dataset abtBuyData = parser.parseData("DBLP-Scholar", srcFile, tgtFile, goldFile, schemaMap);
		assertNotNull(abtBuyData);
		
		List<ItemPair> itemPairs = abtBuyData.getItemPairs();
		assertNotNull(itemPairs);
	}
	
}
