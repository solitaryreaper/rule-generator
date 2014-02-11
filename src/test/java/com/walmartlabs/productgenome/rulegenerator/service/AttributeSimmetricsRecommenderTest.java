package com.walmartlabs.productgenome.rulegenerator.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RestaurantDataParser;

public class AttributeSimmetricsRecommenderTest {

	private static Logger LOG = Logger.getLogger(AttributeSimmetricsRecommenderTest.class.getName());
	
	@Test
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
}
