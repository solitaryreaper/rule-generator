package com.walmartlabs.productgenome.rulegenerator.utils.similarity;

import java.util.logging.Logger;

import com.walmartlabs.productgenome.rulegenerator.Constants;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 * Implements the extended jaccard similarity metric.
 * 
 * Instead of assuming that similar tokens in two strings would be exactly similar, tokens are
 * considered similar if their similarity value based on some edit-distance metric is greater than
 * some threshold.
 * @author excelsior
 *
 */
public class ExtendedJaccard {

	private Logger LOG = Logger.getLogger(ExtendedJaccard.class.getName());
	
	private AbstractStringMetric simmetric = new Levenshtein();
	private double threshold = 0.75;
	
	public ExtendedJaccard()
	{
		
	}
	
	public ExtendedJaccard(AbstractStringMetric metric)
	{
		this.simmetric = metric;
	}
	
	public ExtendedJaccard(double threshold)
	{
		this.threshold = 0.75;
	}
	
	public ExtendedJaccard(AbstractStringMetric metric, double threshold)
	{
		this.simmetric = metric;
		this.threshold = threshold;
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
		String[] tokensA = str1.trim().split(Constants.DEFAULT_TOKENIZER);
		String[] tokensB = str2.trim().split(Constants.DEFAULT_TOKENIZER);
		
		int numTokensA = tokensA.length;
		int numTokensB = tokensB.length;
		
		int simTokens = 0;

		for(int i=0; i < tokensA.length; i++) {
			double maxScore = Double.MIN_VALUE;
			String outer = tokensA[i].toLowerCase().trim();
			for(int j=0; j < tokensB.length; j++) {
				String inner = tokensB[j].toLowerCase().trim();
				double score = simmetric.getSimilarity(outer, inner);
				if(Double.compare(score, maxScore) > 0) {
					maxScore = score;
				}
			}
			
			// Two tokens are similar if their string similarity score is greater than the threshold.
			if(Double.compare(maxScore, threshold) > 0) {
				++simTokens;
			}
		}
		
		return simTokens/(double)(numTokensA + numTokensB - simTokens);
	}
	
}
