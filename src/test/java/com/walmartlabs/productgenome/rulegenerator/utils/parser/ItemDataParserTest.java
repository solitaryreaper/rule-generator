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
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;

public class ItemDataParserTest {

	private static Logger LOG = Logger.getLogger(ItemDataParserTest.class.getName());
	private static DataParser parser = null;
	
	@BeforeClass
	public static void testSetup() 
	{
		parser = new ItemDataParser();
	}
	
	@AfterClass
	public static void testCleanup() 
	{
		
	}
	
	@Ignore
	public void testParsingAbtBuyData()
	{
		File srcFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/Abt.csv");
		File tgtFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/Buy.csv");
		File goldFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/abt_buy_perfectMapping.csv");
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("name", "name");
		schemaMap.put("description", "description");
		schemaMap.put("price", "price");
		
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap);
		
		Dataset abtBuyData = parser.parseData("Abt-Buy", srcFile, tgtFile, goldFile, normalizerMeta);
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
		
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap);
		
		Dataset abtBuyData = parser.parseData("DBLP-Scholar", srcFile, tgtFile, goldFile, normalizerMeta);
		assertNotNull(abtBuyData);
		
		List<ItemPair> itemPairs = abtBuyData.getItemPairs();
		System.out.println("#Itempairs : " + itemPairs.size());
		assertNotNull(itemPairs);
	}
	
	@Test
	public void testParsingRestaurantData()
	{
		File srcFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/restaurant/zagats_final.csv");
		File tgtFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/restaurant/fodors_final.csv");
		File goldFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/restaurant/gold_final.csv");
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("name", "name");
		schemaMap.put("addr", "addr");
		schemaMap.put("type", "type");
		schemaMap.put("city", "city");
		
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap);
		
		Dataset restaurantData = parser.parseData("Restaurant", srcFile, tgtFile, goldFile, normalizerMeta);
		assertNotNull(restaurantData);
		
		List<ItemPair> itemPairs = restaurantData.getItemPairs();
		assertNotNull(itemPairs);
		
		LOG.info("Found " + itemPairs.size() + " itempairs ..");
	}	
}
