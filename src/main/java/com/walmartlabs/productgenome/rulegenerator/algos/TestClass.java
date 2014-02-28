package com.walmartlabs.productgenome.rulegenerator.algos;

import com.wcohen.ss.SoftTFIDF;

public class TestClass {

	public static void main(String[] args)
	{
		String str1 = "hello world";
		String str2 = "hello wor";
		SoftTFIDF tfidf = new SoftTFIDF();
		double score = tfidf.score(str1, str2);
		System.out.println("Simmetrics Soft TFIDF score : " + score);
		System.out.println(tfidf.explainScore(str1, str2));		
	}
}
