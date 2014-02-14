package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.utils.SimilarityUtils;

/**
 * Recommends the most appropriate similarity metrics to be used for each attribute
 * of the dataset.
 * 
 * This is important because this can help reduce the generation of unnecessary features.
 * 
 * @author excelsior
 *
 */
public class AttributeSimmetricsRecommender {

	private static Logger LOG = Logger.getLogger(AttributeSimmetricsRecommender.class.getName());
	
	/**
	 * For every attribute in the dataset, recommend the most relevant similarity
	 * metrics.
	 * 
	 * @return
	 */
	public static Map<String, List<Simmetrics>> getSimmetricRecommendations(Dataset rawDataset)
	{
		Map<String, List<Simmetrics>> recommendations = Maps.newHashMap();
		List<String> attributes = rawDataset.getAttributes();
		List<ItemPair> data = rawDataset.getItemPairs();
		for(String attr : attributes) {
			recommendations.put(attr, getTopNSimmetricsForAttribute(attr, data));
		}
		
		return recommendations;
	}
	
	/**
	 * Returns the top N suitable string similarity metrics suited for this attribute.
	 * 
	 * This can be achieved by looping over all the itempairs with MATCH status, running string metrics
	 * on the attribute values and retaining the metrics with best overall average values.
	 * @return
	 */
	private static List<Simmetrics> getTopNSimmetricsForAttribute(String attrName, List<ItemPair> itemPairs)
	{
		AttributeSimmetricsRecommender recommender = new AttributeSimmetricsRecommender();
		
		int numMetricsReqd = Constants.NUM_METRICS_PER_ATTRIBUTE;
		PriorityQueue<MetricScore> topNMetricScores = new PriorityQueue<MetricScore>(numMetricsReqd);
		for(Simmetrics metric : Simmetrics.values()) {
			double avgScore = getAvgSimScoreForSimmetric(attrName, metric, itemPairs);
			if(Double.isNaN(avgScore)) {
				continue;
			}
			
			if(topNMetricScores.size() < numMetricsReqd) {
				topNMetricScores.add(recommender.new MetricScore(metric, avgScore));
			}
			else {
				MetricScore head = topNMetricScores.peek();
				if(Double.compare(head.avgScore, avgScore) < 0) {
					topNMetricScores.remove(head);
					topNMetricScores.add(recommender.new MetricScore(metric, avgScore));
				}
			}
		}
		
		List<Simmetrics> topNMetrics = Lists.newArrayList();
		for(MetricScore m : topNMetricScores) {
			topNMetrics.add(m.metric);
		}
		
		return topNMetrics;
	}
	
	/**
	 * Returns the average similarity score for a similarity metric across the dataset
	 * @param attrName
	 * @param metric
	 * @return
	 */
	private static double getAvgSimScoreForSimmetric(String attrName, Simmetrics metric, List<ItemPair> itemPairs)
	{
		double totalScore = 0.0;
		int matchedItemPairs = 0;
		for(ItemPair pair : itemPairs) {
			if(pair.getMatchStatus().equals(MatchStatus.MISMATCH)) {
				continue;
			}
			
			++matchedItemPairs;
			String valA = pair.getItemAValByAttr(attrName);
			String valB = pair.getItemBValByAttr(attrName);
			double score = SimilarityUtils.getSimilarity(metric, valA, valB);
			
			// Only add valid scores ..
			if(!Double.isNaN(score)) {
				totalScore += score;
			}
		}
		
		return totalScore/(double)matchedItemPairs;
	}
	
	public class MetricScore implements Comparable<MetricScore>
	{
		Simmetrics metric;
		double avgScore;
		
		public MetricScore(Simmetrics metric, double avgScore)
		{
			this.metric = metric;
			this.avgScore = avgScore;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof MetricScore) {
				MetricScore that = (MetricScore)obj;
				return Objects.equal(this.metric, that.metric) &&
						Objects.equal(this.avgScore, that.avgScore);
			}

			return false;
		}

		@Override
		public int hashCode()
		{
			return Objects.hashCode(this.metric, this.avgScore);
		}

		public int compareTo(MetricScore that)
		{
			return Double.compare(this.avgScore, that.avgScore);
		}		
	}
}
