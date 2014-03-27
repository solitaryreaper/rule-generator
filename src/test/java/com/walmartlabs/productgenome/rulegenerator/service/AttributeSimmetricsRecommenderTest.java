package com.walmartlabs.productgenome.rulegenerator.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.CSVDataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RestaurantDataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.WalmartDataParser;

public class AttributeSimmetricsRecommenderTest {

	private static Logger LOG = Logger.getLogger(AttributeSimmetricsRecommenderTest.class.getName());
	
	@Ignore
	public void testGetSimmetricsForDataset()
	{
		RestaurantDataParser parser = new RestaurantDataParser();
		
		String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
		String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
		
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		Dataset restuarantData = parser.parseData("Restaurant", matchFile, mismatchFile, null);
		
		Map<String, List<Simmetrics>> recommendations = AttributeSimmetricsRecommender.getSimmetricRecommendations(restuarantData, null);
		for(Map.Entry<String, List<Simmetrics>> entry : recommendations.entrySet()) {
			LOG.info("Attribute : " + entry.getKey() + ", Metrics : " + entry.getValue().toString());
		}
	}
	
	@Ignore
	public void testGetSimmetricsCSVDataset()
	{
		LOG.info("Testing Abt-Buy dataset ..");
		File srcFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Abt.csv");
		File tgtFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Buy.csv");
		File goldFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/abt_buy_perfectMapping.csv");
		String datasetName = "Abt-Buy";
		
		DataParser parser = new CSVDataParser();
		Dataset dataset = parser.parseData(datasetName, srcFile, tgtFile, goldFile, null);
		LOG.info("Parsed CSV file data");
		
		Map<String, List<Simmetrics>> recommendations = AttributeSimmetricsRecommender.getSimmetricRecommendations(dataset, null);
		for(Map.Entry<String, List<Simmetrics>> entry : recommendations.entrySet()) {
			LOG.info("Attribute : " + entry.getKey() + ", Metrics : " + entry.getValue().toString());
		}
	
	}
	
	@Test
	public void testGetSimmetricsWalmartDataset()
	{
		WalmartDataParser parser = new WalmartDataParser();
		
		String matchFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/WALMART-DATA/CNET_WALMART_DOTCOM_MATCHED.txt";
		String mismatchFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/WALMART-DATA/CNET_WALMART_DOTCOM_MISMATCHED.txt";
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("pd_title", "pd_title");
		schemaMap.put("req_brand_name", "req_brand_name");
		schemaMap.put("req_category", "req_category");
		schemaMap.put("req_color", "req_color");
		schemaMap.put("req_manufacturer", "req_manufacturer");
		schemaMap.put("req_part_number", "req_part_number");
		schemaMap.put("req_upc_10", "req_upc_10");
		schemaMap.put("req_upc_11", "req_upc_11");
		schemaMap.put("req_upc_12", "req_upc_12");
		schemaMap.put("req_upc_13", "req_upc_13");
		schemaMap.put("req_upc_14", "req_upc_14");
		
		List<String> setValuedAttrs = Lists.newArrayList("req_upc_10", "req_upc_11", "req_upc_12", "req_upc_13", "req_upc_14", "req_category");
		
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap, setValuedAttrs);
		
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		Dataset walmartData = parser.parseData("CNET-Dotcom", matchFile, mismatchFile, normalizerMeta);
		
		Map<String, List<Simmetrics>> recommendations = 
				AttributeSimmetricsRecommender.getSimmetricRecommendations(walmartData, normalizerMeta);
		for(Map.Entry<String, List<Simmetrics>> entry : recommendations.entrySet()) {
			LOG.info("Attribute : " + entry.getKey() + ", Metrics : " + entry.getValue().toString());
		}		
	}
}
