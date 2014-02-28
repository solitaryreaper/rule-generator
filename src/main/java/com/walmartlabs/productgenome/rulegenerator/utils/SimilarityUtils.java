package com.walmartlabs.productgenome.rulegenerator.utils;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.EuclideanDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;

import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.wcohen.ss.SoftTFIDF;

/**
 * Returns the similarity score between two strings employing various matching
 * algorithms.
 * 
 * @author excelsior
 *
 */
public class SimilarityUtils {

	public static Double getSimilarity(Simmetrics s,String s1,String s2){
		if(null == s1 || null == s2 || s1.isEmpty() || s2.isEmpty())
			return Double.NaN;
		AbstractStringMetric metric = null;
		double res = Double.NaN;
		switch(s){
		case COSINE:
			metric = new CosineSimilarity();
			break;
		case JACCARD:
			metric = new JaccardSimilarity();
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
				Double min = 0.0d;
				if(f1 == 0.0d && f2 != 0.0d)
					min = f2;
				else if(f1 != 0.0d && f2 == 0.0d)
					min = f1;
				else if(f1 != 0.0d && f2 != 0.0d)
					min = Math.min(f1,f2);
				else{
					res = 0.0d;
					break;
				}
				res = Math.abs(f1-f2)/min;
			}
			catch(NumberFormatException nfe){
				//do nothing
			}
			break;
		case EXACT_MATCH:
			if(s1.equals(s2))
				res = 1.0d;
			else
				res = 0.0d;
			break;
		}
		if(null != metric)
			res = metric.getSimilarity(s1, s2);
		
		return res;
	}

}
