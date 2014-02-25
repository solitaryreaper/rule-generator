package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;

import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;


/**
 * An entity matching algorithm proposed here : http://www.purdue.edu/discoverypark/cyber/qdb2012/papers/3Learning%20Method.pdf
 * 
 * TODO : Main idea ?
 * @author skprasad
 *
 */
public class OptimalFScoreLearner {

	/**
	 * An inner class that captures an attribute group and associated metadata like maximum f-score etc.
	 * @author skprasad
	 *
	 */
	public static class AttributeGroup
	{
		private List<String> attributes;
		private double maxFScore;
		private Simmetrics properSimmetric;
		private double properThreshold;
	}
}

