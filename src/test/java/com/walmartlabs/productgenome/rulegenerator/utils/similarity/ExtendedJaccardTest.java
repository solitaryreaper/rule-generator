package com.walmartlabs.productgenome.rulegenerator.utils.similarity;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExtendedJaccardTest {

	private ExtendedJaccard extJaccard = new ExtendedJaccard();
	
	@Test
	public void testExtendedJaccard()
	{
		String str1 = "Hello world";
		String str2 = "Hello worl";
		
		double score = extJaccard.getSimilarity(str1, str2);
		assertTrue(score == 1.0d);
		
		str1 = "how are you";
		str2 = "how r u";
		
		score = extJaccard.getSimilarity(str1, str2);
		assertTrue(score == 0.2d);	
		
		// test example from : https://github.com/Simmetrics/simmetrics/blob/master/src/uk/ac/shef/wit/simmetrics/similaritymetrics/JaccardSimilarityTest.java
		str1 = "Test String1";
		str2 = "Test String2";
		
		score = extJaccard.getSimilarity(str1, str2);
		assertTrue(score == 1.0d);		
	}
}
