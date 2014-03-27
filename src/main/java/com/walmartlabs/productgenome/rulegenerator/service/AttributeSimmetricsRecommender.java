package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;

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
	
	private static int SAMPLE_SIZE = 100;
	
	/**
	 * For every attribute in the dataset, recommend the most relevant similarity
	 * metrics.
	 * 
	 * @return
	 */
	public static Map<String, List<Simmetrics>> getSimmetricRecommendations(Dataset rawDataset, DatasetNormalizerMeta normalizerMeta)
	{
		Map<String, List<Simmetrics>> recommendations = Maps.newHashMap();
		List<String> attributes = rawDataset.getAttributes();
		List<ItemPair> data = rawDataset.getItemPairs();
		for(String attr : attributes) {
			recommendations.put(attr, getTopNSimmetricsForAttribute(attr, data, normalizerMeta));
		}
		
		return recommendations;
	}
	
	/**
	 * Returns the most suitable string similarity metrics suited for this attribute.
	 * 
	 * This can be achieved by looping over all the itempairs with MATCH status, running string metrics
	 * on the attribute values and retaining the metrics with best overall average values.
	 * @return
	 */
	private static List<Simmetrics> getTopNSimmetricsForAttribute(String attrName, List<ItemPair> itemPairs, DatasetNormalizerMeta normalizerMeta)
	{
		List<ItemPair> sampleSet = getSampleItemPairs(itemPairs, Constants.SAMPLE_SIZE);
		AttributeStats attributeStats = getAttributeStats(attrName, sampleSet, normalizerMeta);
		return getSimmetricsForAttribute(attributeStats);
	}
	
	private static List<ItemPair> getSampleItemPairs(List<ItemPair> itemPairs, int count)
	{
		return (itemPairs.size() > count) ? itemPairs.subList(0, count) : itemPairs;
	}
	
	/**
	 * Generate statistics about the attribute.
	 */
	private static AttributeStats getAttributeStats(String attrName, List<ItemPair> sampleSet, DatasetNormalizerMeta normalizerMeta)
	{
		int totalOccurences = 0;
		int totalLength = 0;
		int totalTokens = 0;

		String tokenDelimiter = normalizerMeta.getTokenDelimiter();
		
		Set<String> sampleValuesForTypeDetermination = Sets.newHashSet();
		for(ItemPair pair : sampleSet) {
			String valA = pair.getItemA().getValuesForAttr(attrName);
			String valB = pair.getItemB().getValuesForAttr(attrName);
			
			if(!Strings.isNullOrEmpty(valA)) {
				++totalOccurences;
				totalLength += valA.length();
				totalTokens += valA.split(tokenDelimiter).length;
				if(sampleValuesForTypeDetermination.size() < SAMPLE_SIZE) {
					sampleValuesForTypeDetermination.add(valA);
				}
			}
			
			if(!Strings.isNullOrEmpty(valB)) {
				++totalOccurences;
				totalLength += valB.length();
				totalTokens += valB.split(tokenDelimiter).length;
				if(sampleValuesForTypeDetermination.size() < SAMPLE_SIZE) {
					sampleValuesForTypeDetermination.add(valB);					
				}
			}
		}
		
		int stringTypeCnt = 0;
		DataType type = DataType.STRING;
		for(String str : sampleValuesForTypeDetermination) {
			if(!NumberUtils.isNumber(str)) {
				++stringTypeCnt;
			}
		}
		
		if(stringTypeCnt < sampleValuesForTypeDetermination.size()/2) {
			type = DataType.NUMERIC;
		}
		
		double avgLength = totalLength/(double)totalOccurences;
		double avgNumTokens = totalTokens/(double)totalOccurences;
		
		return new AttributeStats(attrName, type, avgLength, avgNumTokens, normalizerMeta.isSetValuedAttribute(attrName));
	}
	
	private static List<Simmetrics> getSimmetricsForAttribute(AttributeStats stats)
	{
		boolean isSetValuedAttr = stats.isSetValued();
		
		// Filter first on the data type of attribute
		if(stats.getDataType().equals(DataType.NUMERIC)) {
			List<Simmetrics> metrics = Lists.newArrayList();
			if(!isSetValuedAttr) {
				metrics.add(Simmetrics.NUM_SCORE);
				metrics.add(Simmetrics.EXACT_MATCH_NUMERIC);
				metrics.add(Simmetrics.LEVENSHTEIN);				
			}
			/**
			 * For set-valued numeric attributes like UPC, jaccard with levenstein as the similarity
			 * metric on each value, should be sufficient.
			 */
			else {
				metrics.add(Simmetrics.EXTENDED_JACCARD);
			}
			
			return metrics;
		}
		
		double avgLength = stats.getAvgLength();
		double avgNumTokens = stats.getAvgNumTokens();

		List<Simmetrics> metrics = getAllSimmetrics();		
		metrics.remove(Simmetrics.NUM_SCORE);
		metrics.remove(Simmetrics.EXACT_MATCH_NUMERIC);
		
		if(!isSetValuedAttr) {
			metrics.remove(Simmetrics.EXTENDED_JACCARD);
		}
		
		// JARO is good only for matching short strings ..
		if(Double.compare(avgLength, 10) > 0) {
			metrics.remove(Simmetrics.JARO);
		}
		
		// For long multi-word strings, prefer set-based similarity metrics ..
		if(Double.compare(avgLength, 20) > 0 && Double.compare(avgNumTokens, 2) > 0) {
			metrics.remove(Simmetrics.JARO_WINKLER);
			metrics.remove(Simmetrics.LEVENSHTEIN);
			metrics.remove(Simmetrics.EUCLIDEAN);
		}
		
		// For single word attributes, set-based similarity metrics can be avoided ..
		if(Double.compare(avgNumTokens, 1) <= 0) {
			metrics.remove(Simmetrics.JACCARD);
			metrics.remove(Simmetrics.SOFT_TFIDF);
			metrics.remove(Simmetrics.MONGE_ELKAN);
		}
		
		return metrics;
	}
	
	private enum DataType
	{
		STRING,
		NUMERIC
	}
	
	public static class AttributeStats
	{
		private String attrName;
		private DataType dataType;
		private double avgLength;
		private double avgNumTokens;
		private boolean isSetValued;
		
		public AttributeStats(String attrName, DataType dataType, double avgLength, double avgNumTokens, boolean isSetValued)
		{
			this.attrName = attrName;
			this.dataType = dataType;
			this.avgLength = avgLength;
			this.avgNumTokens = avgNumTokens;
			this.isSetValued = isSetValued;
		}

		public String getAttrName() {
			return attrName;
		}

		public DataType getDataType() {
			return dataType;
		}

		public double getAvgLength() {
			return avgLength;
		}

		public double getAvgNumTokens() {
			return avgNumTokens;
		}

		public boolean isSetValued() {
			return isSetValued;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AttributeStats [attrName=").append(attrName)
					.append(", dataType=").append(dataType)
					.append(", avgLength=").append(avgLength)
					.append(", avgNumTokens=").append(avgNumTokens)
					.append(", isSetValued=").append(isSetValued).append("]");
			return builder.toString();
		}
	}
	
	public static List<Simmetrics> getAllSimmetrics()
	{
		List<Simmetrics> metrics = Lists.newArrayList();
		for(Simmetrics metric : Simmetrics.values()) {
			metrics.add(metric);
		}
		
		// TODO : Hack 3-QGRAM giving bad results. Need to fix this !!
		metrics.remove(Simmetrics.QGRAM);
		//metrics.remove(Simmetrics.COSINE);
		return metrics;
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
