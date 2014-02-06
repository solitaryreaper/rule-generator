package com.walmartlabs.productgenome.rulegenerator.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureVector;
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
	
	@Test
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
}
