package com.walmartlabs.productgenome.rulegenerator.utils.similarity;

import org.junit.Test;

public class ExtendedJaccardTest {

	private ExtendedJaccard extJaccard = new ExtendedJaccard();
	
	@Test
	public void testExtendedJaccard()
	{
		String str1 = "00027242551923, 00002724255192";
		String str2 = "00027242551923";
		
		double score = extJaccard.getSimilarity(str1, str2);
		System.out.println(score);
	}
}
