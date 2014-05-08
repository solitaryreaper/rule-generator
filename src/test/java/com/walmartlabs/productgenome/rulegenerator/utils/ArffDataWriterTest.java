package com.walmartlabs.productgenome.rulegenerator.utils;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.service.FeatureGenerationService;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RestaurantDataParser;


public class ArffDataWriterTest {

	private static Logger LOG = Logger.getLogger(ArffDataWriterTest.class.getName());
	private static Dataset dataset = null;
	private static FeatureDataset featureDataset = null;
	
	@BeforeClass
	public static void testSetup() 
	{
		String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
		String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
		
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("name", "name");
		schemaMap.put("adddr", "addr");
		schemaMap.put("city", "city");
		schemaMap.put("type", "type");
		
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap);
		
		DataParser parser = new RestaurantDataParser();
		dataset = parser.parseData("Restaurant", matchFile, mismatchFile, normalizerMeta);
		
		featureDataset = FeatureGenerationService.generateFeatures(dataset, normalizerMeta);
	}
	
	@Test
	public void testLoadDataInArffFormat()
	{
		LOG.info("Loading data in ARFF format ..");
		String arffFileLoc = null;
		try {
			arffFileLoc = ArffDataWriter.loadDataInArffFormat(featureDataset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertNotNull(arffFileLoc);
		LOG.info("ARFF file location : " + arffFileLoc);
	}
}
