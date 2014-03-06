package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserWhitespace;
import uk.ac.shef.wit.simmetrics.wordhandlers.DummyStopTermHandler;
import uk.ac.shef.wit.simmetrics.wordhandlers.InterfaceTermHandler;

import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.utils.SimilarityUtils;

public class TestClass {

	private static InterfaceTermHandler stopWordHandler = new DummyStopTermHandler();
	private static final String delimiters = "\r\n\t \u00A0";
	
	public static void main(String[] args) throws InterruptedException
	{
		String test1 = "Technical report, UniversitÃ  di Roma tre, 1997. Talk at the ";
		String test2 = "Technical report, Universit di Roma tre, 1997. Talk at the ";
		
		System.out.println("Starting test ..");

		/**
		double score = SimilarityUtils.getSimilarity(Simmetrics.COSINE, test, t2);
		System.out.println("Score : " + score);		
		System.out.println(score);
		**/
		
		System.out.println("before ..");
		List<String> tokenizer = tokenizeToArrayList(test1);
		System.out.println(tokenizer.toString());
		
	}
	
	  public static final ArrayList<String> tokenizeToArrayList(final String input) {
	        final ArrayList<String> returnVect = new ArrayList<String>();
	        int curPos = 0;
	        while (curPos < input.length()) {
	            final char ch = input.charAt(curPos);
	            if (Character.isWhitespace(ch)) {
	                curPos++;
	            }
	            int nextGapPos = input.length();
	            //check delimitors
	            for (int i = 0; i < delimiters.length(); i++) {
	                final int testPos = input.indexOf(delimiters.charAt(i), curPos);
	                if (testPos < nextGapPos && testPos != -1) {
	                    nextGapPos = testPos;
	                }
	            }
	            //add new token
	            final String term = input.substring(curPos, nextGapPos);
	            if(!stopWordHandler.isWord(term) && !term.trim().equals("")) {
	                returnVect.add(term);
	            }
	            curPos = nextGapPos;
	        }

	        return returnVect;
	    }	
}
