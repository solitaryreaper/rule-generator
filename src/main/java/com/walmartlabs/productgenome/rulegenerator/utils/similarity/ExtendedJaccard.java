package com.walmartlabs.productgenome.rulegenerator.utils.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import com.walmartlabs.productgenome.rulegenerator.Constants;

/**
 * Implements the extended jaccard similarity metric. Use this specifically for set-valued attributes.
 * 
 * Instead of assuming that similar tokens in two strings would be exactly similar, tokens are
 * considered similar if their similarity value based on some edit-distance metric is greater than
 * some threshold.
 * @author excelsior
 *
 */
public class ExtendedJaccard {

	private String DUMMY_STRING = "dummy123999#";
	
	private AbstractStringMetric metric = null;
	private AbstractStringMetric DEFAULT_METRIC = new Levenshtein();
	
	private String setValueDelimiter = Constants.DEFAULT_SET_VALUE_ATTRIBIUTE_DELIMITER;
	
	private double threshold = 0.75;
	
	public ExtendedJaccard()
	{
		this.metric = DEFAULT_METRIC;
	}
	
	public ExtendedJaccard(AbstractStringMetric metric)
	{
		this.metric = metric;
	}
	
	public ExtendedJaccard(double threshold)
	{
		this.threshold = 0.75;
	}
	
	public ExtendedJaccard(AbstractStringMetric metric, double threshold)
	{
		this.metric = metric;
		this.threshold = threshold;
	}	
	
	public String getSetValueDelimiter() {
		return setValueDelimiter;
	}

	public void setSetValueDelimiter(String setValueDelimiter) {
		setValueDelimiter = setValueDelimiter;
	}

	/**
	 * TODO :
	 * 1) Add maximum weighted bipartite matching logic.
	 * 2) Assign weights to the matched tokens by using similarity scores.
	 * @param str1
	 * @param str2
	 * @return
	 */
	public double getSimilarity(String str1, String str2)
	{
		String[] tokensA = str1.trim().split(setValueDelimiter);
		String[] tokensB = str2.trim().split(setValueDelimiter);
		
		int numTokensA = tokensA.length;
		int numTokensB = tokensB.length;
		
		int simTokens = 0;
		double simScore = 0.0;

		for(int i=0; i < tokensA.length; i++) {
			double maxScore = Double.MIN_VALUE;
			int maxIndex = -1;
			String outer = tokensA[i].toLowerCase().trim();
			for(int j=0; j < tokensB.length; j++) {
				String inner = tokensB[j].toLowerCase().trim();
				double score = metric.getSimilarity(outer, inner);
				if(Double.compare(score, maxScore) > 0) {
					maxScore = score;
					maxIndex = j;
				}
			}
			
			// Two tokens are similar if their string similarity score is greater than the threshold.
			if(Double.compare(maxScore, threshold) > 0) {
				++simTokens;
				simScore += maxScore;
				// Hack to ensure that the same token is not again used for comparison
				tokensB[maxIndex] = DUMMY_STRING;
			}
		}
		
		return simScore/(double)(numTokensA + numTokensB - simTokens);
	}
	
}
