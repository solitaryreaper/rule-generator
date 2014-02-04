package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;

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
		return recommendations;
	}
}
