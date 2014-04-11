package com.walmartlabs.productgenome.rulegenerator.utils;

import java.util.Map;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

public class WekaUtils {

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
}
