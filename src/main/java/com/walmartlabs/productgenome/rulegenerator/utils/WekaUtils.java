package com.walmartlabs.productgenome.rulegenerator.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.service.FeatureGenerationService;

public class WekaUtils {

	private static Logger LOG = Logger.getLogger(WekaUtils.class.getSimpleName());
	
	/**
	 * Returns the class label for this instance
	 */
	public static MatchStatus getInstanceLabel(Instance instance)
	{
		return MatchStatus.getMatchStatus(instance.stringValue(instance.classIndex()));
	}
	
	/**
	 * Returns the numeric feature value for the given feature name.
	 */
	public static double getFeatureValue(Instance instance, Attribute feature)
	{
		return instance.value(feature);
	}
	
	/**
	 * Splits the parents Weka format dataset into train, tune and test dataset for analysis.
	 * @param data
	 * @return
	 */
	public static Map<String, Instances> getSplitDataset(Instances data, int numCVFolds)
	{
		// Split parent dataset into train and test dataset.
		Random rand = new Random(Constants.WEKA_DATA_SEED);
		Instances randData = new Instances(data);
		randData.randomize(rand);
		randData.stratify(numCVFolds);
		
		int randFold = 0 + (int)(Math.random() * ((numCVFolds - 1) + 1));
		Instances nonTestDataset = randData.trainCV(numCVFolds, randFold);
		Instances testDataset = randData.testCV(numCVFolds, randFold);
		
		// Split train dataset further into pure train and tune dataset.
		Random rand2 = new Random(Constants.WEKA_DATA_SEED);
		Instances randData2 = new Instances(nonTestDataset);
		randData.randomize(rand2);
		randData.stratify(numCVFolds);
		
		int randFold2 = 0 + (int)(Math.random() * ((numCVFolds - 1) + 1));
		Instances trainDataset = randData2.trainCV(numCVFolds, randFold2);
		Instances tuneDataset = randData2.testCV(numCVFolds, randFold2);
		
		Map<String, Instances> splitDataset = Maps.newHashMap();
		splitDataset.put(Constants.TRAIN_DATASET, trainDataset);
		splitDataset.put(Constants.TUNE_DATASET, tuneDataset);
		splitDataset.put(Constants.TEST_DATASET, testDataset);
		
		return splitDataset;
	}
	
	/**
	 * Converts an itempair to the required weka instance format. 
	 */
	public static Instances getWekaInstances(Dataset dataset, DatasetNormalizerMeta normalizerMeta)
	{
		String arffFileLoc = stageDataInArffFormat(dataset, normalizerMeta);
		return parseArffFile(arffFileLoc);
	}
	
	private static String stageDataInArffFormat(Dataset dataset, DatasetNormalizerMeta normalizerMeta)
	{
		FeatureDataset featureDataset = FeatureGenerationService.generateFeatures(dataset, normalizerMeta);
		LOG.info("Generated feature vectors for dataset : " + dataset.getName());
		
		String arffFileLoc = null;
		try {
			arffFileLoc = ArffDataWriter.loadDataInArffFormat(featureDataset);
		} catch (IOException e) {
			LOG.severe("Failed to stage feature data in arff file .. " + e.getStackTrace());
			e.printStackTrace();
		}
		LOG.info("Loaded the in-memory feature vectors into arff file : " + arffFileLoc);

		return arffFileLoc;
	}
	
	private static Instances parseArffFile(String arffFileLoc)
	{
		Instances instances = null;
		try {
			DataSource dataSource = new DataSource(arffFileLoc);			
			instances = dataSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (instances.classIndex() == -1)
			instances.setClassIndex(instances.numAttributes() - 1);		
		
		return instances;
	}
	
}
