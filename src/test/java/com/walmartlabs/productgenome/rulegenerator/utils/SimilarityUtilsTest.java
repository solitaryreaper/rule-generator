package com.walmartlabs.productgenome.rulegenerator.utils;

import org.junit.Test;

import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;

public class SimilarityUtilsTest {

	@Test
	public void testJaccard()
	{
		String str1 = "hello world";
		String str2 = "hello the world";
		double score = SimilarityUtils.getSimilarity(Simmetrics.JACCARD, str1, str2);
		System.out.println("JACCARD Score : " + score);
	}
	
	@Test
	public void testSmithWaterman()
	{
		String str1 = "stars cafe";
		String str2 = "stars";
		double score = SimilarityUtils.getSimilarity(Simmetrics.SMITH_WATERMAN_GOTOH_WINDOWED_AFFINE, str1, str2);
		System.out.println("Simmetrics SMITH WATERMAN score : " + score);	
	}
	
	@Test
	public void testFailureCase1()
	{
		String str1 = "palace court";
		String str2 = "cafe roma";
		double score = SimilarityUtils.getSimilarity(Simmetrics.SMITH_WATERMAN_GOTOH_WINDOWED_AFFINE, str1, str2);
		System.out.println("Simmetrics SMITH WATERMAN score : " + score + " for strings (" + str1 + "," + str2 + ")");	

		score = SimilarityUtils.getSimilarity(Simmetrics.JARO_WINKLER, str1, str2);
		System.out.println("Simmetrics JARO WINKLER score : " + score  + " for strings (" + str1 + "," + str2 + ")");	
	}
	
	@Test 
	public void testNumeric()
	{
		String str1 = "1999";
		String str2 = "1999";
		
		double score = SimilarityUtils.getSimilarity(Simmetrics.NUM_SCORE, str1, str2);
		System.out.println("#Score : " + score);
		
		str1 = "1999";
		str2 = "2000";
		score = SimilarityUtils.getSimilarity(Simmetrics.NUM_SCORE, str1, str2);
		System.out.println("#Score : " + score);		
	}
	
	@Test
	public void testExtendedJaccard()
	{
		String str1 = "00027242551923, 00002724255192";
		String str2 = "00027242551923";
		
		double score = SimilarityUtils.getSimilarity(Simmetrics.EXTENDED_JACCARD, str1, str2);
		System.out.println("#Extended Jaccard Score : " + score);	
	}
}
