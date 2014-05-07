package com.walmartlabs.productgenome.rulegenerator.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.algos.RandomForestLearner;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RestaurantDataParser;

public class EntropyCalculationServiceTest {

	@Test
	public void testTopKEntropyPairs()
	{
		Dataset dataset = getDataset();
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("name", "name");
		schemaMap.put("adddr", "addr");
		schemaMap.put("city", "city");
		schemaMap.put("type", "type");
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap);
		
		Learner learner = getLearner();
		
		List<ItemPair> topKEntropyPairs = 
			EntropyCalculationService.getTopKInformativeItemPairs(learner, dataset, normalizerMeta, 20);
		assertNotNull(topKEntropyPairs);
		assertTrue(topKEntropyPairs.size() == 20);
	}
	
	private Learner getLearner()
	{
		Learner learner = new RandomForestLearner();
		
		String arffFileLoc = "/home/excelsior/workspace/rule-generator/src/main/resources/tmp/Restaurant_9288.arff";
		Instances data = null;
		try {
			DataSource trainDataSource = new DataSource(arffFileLoc);			
			data = trainDataSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);		
		
		learner.learnRules(data);
		
		return learner;
	}
	
	private Dataset getDataset()
	{
		String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
		String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
		
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		
		BiMap<String, String> schemaMap = HashBiMap.create();
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap, null);
		
		DataParser parser = new RestaurantDataParser();
		return parser.parseData("Restaurant", matchFile, mismatchFile, normalizerMeta);
	}
}
