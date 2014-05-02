package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;

/**
 * Calculates entropy for all the candidate itempairs, using the learning algorithm and recommends the itempairs 
 * which have the most entropy. 
 * 
 * @author skprasad
 *
 */
public class EntropyCalculationService {

	/**
	 * Returns the most informative K item pairs from the dataset using the learnt matcher.
	 * 
	 * The item pairs for which the maximum entropy has been calculated by the matcher are the most informative examples
	 * because the matcher is most confused about these itempairs and learning rules for such itempairs would add more
	 * information to the existing matcher.
	 * 
	 * @return
	 */
	public static List<ItemPair> getTopKInformativeItemPairs(Learner learner, List<ItemPair> unlabelledItemPairs, int k)
	{
		RandomForest rf = (RandomForest)learner;
		for(ItemPair itemPair : unlabelledItemPairs) {
			Instance instance = null; // TODO
			
		}
		
		List<ItemPair> mostInformativePairs = Lists.newArrayList();
		return mostInformativePairs;
	}
	
	private class ItemPairEntropy
	{
		ItemPair itemPair;
		double entropy;
	}
}
