package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;

public class RestaurantDataParserTest {

	private static Logger LOG = Logger.getLogger(RestaurantDataParserTest.class.getName());
	
	private static RestaurantDataParser parser = null;
	
	@BeforeClass
	public static void testSetup() 
	{
		parser = new RestaurantDataParser();
	}
	
	@AfterClass
	public static void testCleanup() 
	{
		
	}
	
	@Test
	public void testParseData()
	{
		String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
		String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
		
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		Dataset restuarantData = parser.parseData(matchFile, mismatchFile, "Restaurant");
		
		assertNotNull(restuarantData);
		
		List<ItemPair> itemPairs = restuarantData.getItemPairs();
		assertNotNull(itemPairs);
		
		LOG.info("Found " + itemPairs.size() + " itempairs for restuarant dataset.");
		LOG.info("First itempair : " + itemPairs.get(0).toString());
	}
}
