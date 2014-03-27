package com.walmartlabs.productgenome.rulegenerator.utils;

import java.util.logging.Logger;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.EuclideanDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;

import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.utils.similarity.ExtendedJaccard;
import com.wcohen.ss.Jaccard;
import com.wcohen.ss.SoftTFIDF;

/**
 * Returns the similarity score between two strings employing various matching
 * algorithms.
 * 
 * @author excelsior
 *
 */
public class SimilarityUtils {

	private static Logger LOG = Logger.getLogger(SimilarityUtils.class.getName());
	
	public static Double getSimilarity(Simmetrics s,String s1,String s2){
		if(null == s1 || null == s2 || s1.isEmpty() || s2.isEmpty())
			return Double.NaN;

		AbstractStringMetric metric = null;
		Double res = Double.NaN;
		switch(s){
		case COSINE:
			metric = new CosineSimilarity();
			break;
		case JACCARD:
			Jaccard jaccard = new Jaccard();
			res = jaccard.score(s1, s2);
			break;
		case EXTENDED_JACCARD:
			ExtendedJaccard extJaccard = new ExtendedJaccard();
			res = extJaccard.getSimilarity(s1, s2);
			break;
		case JARO:
			metric = new Jaro();
			break;
		case JARO_WINKLER:
			metric = new JaroWinkler();
			break;
		case LEVENSHTEIN:
			metric = new Levenshtein();
			break;
		case MONGE_ELKAN:
			metric = new MongeElkan();
			break;
		case QGRAM:
			metric = new QGramsDistance();
			break;
		case SMITH_WATERMAN_GOTOH_WINDOWED_AFFINE:
			metric = new SmithWatermanGotohWindowedAffine();
			break;
		case EUCLIDEAN:
			metric = new EuclideanDistance();
			break;
		case SOFT_TFIDF:
			SoftTFIDF tfidf = new SoftTFIDF();
			res = tfidf.score(s1, s2);
			break;
		case NUM_SCORE:
			try{
				Double f1 = Double.parseDouble(s1);
				Double f2 = Double.parseDouble(s2);
				if(Double.compare(f1, 0.0d) == 0 && Double.compare(f2, 0.0d) == 0) {
					res = 0.0d;
					break;
				}
				
				if(Double.compare(f1, f2) == 0) {
					res = 1.0d;
				}
				else {
					double diff = Math.abs(f1-f2);
					double sum = f1 + f2;
					res = 1/(diff/sum);					
				}
			}
			catch(NumberFormatException nfe){
				LOG.info("Error parsing " + s1 + ", " + s2 + " as string ..");
				res = 0.0d;
			}
			break;
		case EXACT_MATCH_STRING:
			if(s1.equals(s2))
				res = 1.0d;
			else
				res = 0.0d;
			break;
		case EXACT_MATCH_NUMERIC:
			try {
				Double f1 = Double.parseDouble(s1);
				Double f2 = Double.parseDouble(s2);
				if(Double.compare(f1, f2) == 0) {
					res = 1.0d;
				}
				else {
					res = 0.0d;
				}				
			}
			catch(Exception e) {
				LOG.info("Error parsing " + s1 + ", " + s2 + " as string ..");
				res = 0.0d;				
			}

			break;
		}
		
		if(null != metric) {
			res = (double) metric.getSimilarity(s1, s2);			
		}
		
		return res;
	}

}
