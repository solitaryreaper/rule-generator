package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
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
			recommendations.put(attr, getSimmetricsForAttribute(attr, data));
		}
		
		return recommendations;
	}
	
	/**
	 * Returns a list of simmetrics for an attribute
	 * @return
	 */
	private static List<Simmetrics> getSimmetricsForAttribute(String attrName, List<ItemPair> itemPairs)
	{
		// TODO : Add logic to determine the type of similarity metrics applicable for
		// each attribute.
		
		List<Simmetrics> metrics = Lists.newArrayList();
		metrics.add(Simmetrics.COSINE);
		metrics.add(Simmetrics.JACCARD);
		metrics.add(Simmetrics.JARO);
		metrics.add(Simmetrics.JARO_WINKLER);
		metrics.add(Simmetrics.LEVENSHTEIN);
		metrics.add(Simmetrics.MONGE_ELKAN);
		metrics.add(Simmetrics.QGRAM);
		metrics.add(Simmetrics.SMITH_WATERMAN_GOTOH_WINDOWED_AFFINE);
		metrics.add(Simmetrics.EXACT_MATCH);
		
		return metrics;
	}

}
