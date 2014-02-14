package com.walmartlabs.productgenome.rulegenerator.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureVector;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.CSVDataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RestaurantDataParser;

public class FeatureGenerationServiceTest {

	private static Logger LOG = Logger.getLogger(FeatureGenerationServiceTest.class.getName());
	
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
	
	public void testGenerateFeatures()
	{
		String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
		String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
		
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		Dataset restuarantData = parser.parseData(matchFile, mismatchFile, "Restaurant");
		LOG.info("Generated dataset with " + restuarantData.getItemPairs().size() + " itempairs ..");
		
		FeatureDataset fDataset = FeatureGenerationService.generateFeatures(restuarantData);
		assertNotNull(fDataset);
		
		List<FeatureVector> fVectors = fDataset.getFeatureVectors();
		assertNotNull(fVectors);
		assertTrue(fVectors.size() > 0);
		LOG.info("Generated feature dataset with " + fDataset.getFeatures().size() + " features ..");
	}
	
	@Test
	public void testGenerateFeaturesCSVData()
	{
		LOG.info("Testing Abt-Buy dataset ..");
		File srcFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Abt.csv");
		File tgtFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Buy.csv");
		File goldFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/abt_buy_perfectMapping.csv");
		String datasetName = "Abt-Buy";
		
		DataParser parser = new CSVDataParser();
		Dataset dataset = parser.parseData(datasetName, srcFile, tgtFile, goldFile);
		LOG.info("Parsed CSV file data");
		
		FeatureDataset fDataset = FeatureGenerationService.generateFeatures(dataset);
		assertNotNull(fDataset);
		
	}
}
