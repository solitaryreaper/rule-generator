package com.walmartlabs.productgenome.rulegenerator.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.CSVDataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RestaurantDataParser;

public class AttributeSimmetricsRecommenderTest {

	private static Logger LOG = Logger.getLogger(AttributeSimmetricsRecommenderTest.class.getName());
	
	public void testGetSimmetricsForDataset()
	{
		RestaurantDataParser parser = new RestaurantDataParser();
		
		String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
		String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
		
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		Dataset restuarantData = parser.parseData(matchFile, mismatchFile, "Restaurant");
		
		Map<String, List<Simmetrics>> recommendations = AttributeSimmetricsRecommender.getSimmetricRecommendations(restuarantData);
		for(Map.Entry<String, List<Simmetrics>> entry : recommendations.entrySet()) {
			LOG.info("Attribute : " + entry.getKey() + ", Metrics : " + entry.getValue().toString());
		}
	}
	
	@Test
	public void testGetSimmetricsCSVDataset()
	{
		LOG.info("Testing Abt-Buy dataset ..");
		File srcFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Abt.csv");
		File tgtFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Buy.csv");
		File goldFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/abt_buy_perfectMapping.csv");
		String datasetName = "Abt-Buy";
		
		DataParser parser = new CSVDataParser();
		Dataset dataset = parser.parseData(datasetName, srcFile, tgtFile, goldFile);
		LOG.info("Parsed CSV file data");
		
		Map<String, List<Simmetrics>> recommendations = AttributeSimmetricsRecommender.getSimmetricRecommendations(dataset);
		for(Map.Entry<String, List<Simmetrics>> entry : recommendations.entrySet()) {
			LOG.info("Attribute : " + entry.getKey() + ", Metrics : " + entry.getValue().toString());
		}
	
	}
}
