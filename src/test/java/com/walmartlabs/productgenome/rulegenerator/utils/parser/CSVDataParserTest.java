package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

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
	
	@Ignore
	public void testParsingAbtBuyData()
	{
		File srcFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/Abt.csv");
		File tgtFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/Buy.csv");
		File goldFile = new File(System.getProperty("user.dir") + "/src/main/resources/data/datasets/Abt-Buy/abt_buy_perfectMapping.csv");
		
		Dataset abtBuyData = parser.parseData("Abt-Buy", srcFile, tgtFile, goldFile, "name");
		assertNotNull(abtBuyData);
		
		List<ItemPair> itemPairs = abtBuyData.getItemPairs();
		assertNotNull(itemPairs);
	}
	
	@Test
	public void testParsingDBLPScholarData()
	{
		File srcFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/DBLP1.csv");
		File tgtFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/Scholar.csv");
		File goldFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/DBLP-Scholar_perfectMapping.csv");
		
		Dataset abtBuyData = parser.parseData("DBLP-Scholar", srcFile, tgtFile, goldFile, "title");
		assertNotNull(abtBuyData);
		
		List<ItemPair> itemPairs = abtBuyData.getItemPairs();
		assertNotNull(itemPairs);
	}
	
}
