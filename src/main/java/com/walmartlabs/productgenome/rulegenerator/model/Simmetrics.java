package com.walmartlabs.productgenome.rulegenerator.model;

/**
 * Dictionary of various similariy metrics that can be used for entity matching.
 * 
 * @author excelsior
 *
 */
public enum Simmetrics {
	COSINE("cosine"),
	EUCLIDEAN("euclidean"),
	EXACT_MATCH_STRING("exact_string"),
	EXACT_MATCH_NUMERIC("exact_number"),	
	JACCARD("jaccard"),
	JARO("jaro"),
	JARO_WINKLER("jaro_winkler"),
	LEVENSHTEIN("lev"),
	MONGE_ELKAN("mongeelkan"),
	QGRAM("qg"),
	NUM_SCORE("num"), 
	SMITH_WATERMAN_GOTOH_WINDOWED_AFFINE("smith_waterman"),
	SOFT_TFIDF("soft_tfidf"),
	EXTENDED_JACCARD("ext_jaccard");
	
	private String metricAbbrv;
	
	private Simmetrics(String abbrv)
	{
		this.metricAbbrv = abbrv;
	}
		
	public String getSimmetricAbbrv(Simmetrics metric)
	{
		String metricAbbrv = null;
		for(Simmetrics m : Simmetrics.values()) {
			if(m.equals(metric)) {
				metricAbbrv = m.metricAbbrv;
				break;
			}
		}
		
		return metricAbbrv;
	}
	
	@Override
	public String toString()
	{
		return metricAbbrv;
	}
}
