package com.walmartlabs.productgenome.rulegenerator;

import java.io.File;

import org.junit.Test;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.algos.RandomForestLearner;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.utils.WekaUtils;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.ItemDataParser;

/**
 * A integration test for the active learning web application built on top of this library ..
 * @author skprasad
 *
 */
public class IntegrationTest {

	@Test
	public void testActiveEMMSWorkflow()
	{
		Dataset dataset = getRestaurantDataset();
		String arffFileLoc = WekaUtils.stageDataInArffFormat(dataset);
		
		Instances data = null;
		try {
			DataSource trainDataSource = new DataSource(arffFileLoc);			
			data = trainDataSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		
		RandomForestLearner learner = new RandomForestLearner();
		learner.learnRules(data);
		
		System.out.println("Rules : " + learner.getRules());
	}
	
	private static Dataset getRestaurantDataset()
	{
		DataParser parser = new ItemDataParser();
		File srcFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/zagats_active_emms_test.csv");
		File tgtFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/fodors_active_emms_test.csv");
		File goldFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/seed_active_emms_test.csv");
		
		BiMap<String, String> schemaMap = WekaUtils.getDefaultSchemaMap(Lists.newArrayList("name","addr","city","type"));
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap);
		
		return parser.parseData("Restaurant", srcFile, tgtFile, goldFile, normalizerMeta);
	}
}
