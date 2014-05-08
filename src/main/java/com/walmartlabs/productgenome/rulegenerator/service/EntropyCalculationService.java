package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import weka.core.Instance;
import weka.core.Instances;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.algos.RandomForestLearner;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.utils.WekaUtils;

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
	public static List<ItemPair> getTopKInformativeItemPairs(Learner learner, Dataset unlabelledDataset, 
			DatasetNormalizerMeta normalizerMeta, int K)
	{
		RandomForestLearner rf = (RandomForestLearner)learner;
		
		PriorityQueue<InstanceEntropy> mostInfoItemPairs = 
			new PriorityQueue<EntropyCalculationService.InstanceEntropy>(K);
		
		double maxEntropy = 0.0;
		Instances data = WekaUtils.getWekaInstances(unlabelledDataset, normalizerMeta);
		for(Instance instance : data) {
			double entropy = 0.0;
			try {
				entropy = rf.getVotingEntropyForInstance(instance);
				if(Double.compare(entropy, maxEntropy) > 0) {
					maxEntropy = entropy;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(mostInfoItemPairs.size() < K) {
				mostInfoItemPairs.add(new InstanceEntropy(instance, entropy));
			}
			else {
				InstanceEntropy head = mostInfoItemPairs.peek();
				if(Double.compare(head.entropy,entropy) < 0) {
					mostInfoItemPairs.remove(head);
					mostInfoItemPairs.add(new InstanceEntropy(instance, entropy));
				}				
			}
		}
		
		// Optimizaton : Create an id to itempair lookup structure for quick search by itempair id.
		Map<Integer, ItemPair> idToItemPairMap = Maps.newHashMap();
		for(ItemPair itemPair : unlabelledDataset.getItemPairs()) {
			idToItemPairMap.put(itemPair.getId(), itemPair);
		}
		
		List<ItemPair> topKInfoPairs = Lists.newArrayList();
		for(InstanceEntropy instanceEntropy : mostInfoItemPairs) {
			Instance instance = instanceEntropy.instance;
			
			// Get the first attribute value from weka instance which acts as the bridge to the
			// original itempair.
			topKInfoPairs.add(idToItemPairMap.get((int)instance.value(0)));
		}
		
		return topKInfoPairs;
	}
	
	private static class InstanceEntropy implements Comparable<InstanceEntropy>
	{
		private Instance instance;
		private double entropy;
		
		public InstanceEntropy(Instance instance, double entropy)
		{
			this.instance = instance;
			this.entropy = entropy;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof InstanceEntropy) {
				InstanceEntropy that = (InstanceEntropy)obj;
				return Objects.equal(this.instance, that.instance) &&
						Objects.equal(this.entropy, that.entropy);
			}

			return false;
		}

		@Override
		public int hashCode()
		{
			return Objects.hashCode(this.instance, this.entropy);
		}
		
		public int compareTo(InstanceEntropy that) {
			return Double.compare(this.entropy, that.entropy);
		}
	}
}
