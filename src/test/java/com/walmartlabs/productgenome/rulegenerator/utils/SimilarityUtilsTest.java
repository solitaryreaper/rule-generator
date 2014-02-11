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
		System.out.println("Score : " + score);
	}
}
