package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
		
		Dataset abtBuyData = parser.parseData("Abt-Buy", srcFile, tgtFile, goldFile);
		assertNotNull(abtBuyData);
		
		List<ItemPair> itemPairs = abtBuyData.getItemPairs();
		assertNotNull(itemPairs);
	}

}
