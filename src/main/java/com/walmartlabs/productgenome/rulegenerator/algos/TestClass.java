package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserWhitespace;
import uk.ac.shef.wit.simmetrics.wordhandlers.DummyStopTermHandler;
import uk.ac.shef.wit.simmetrics.wordhandlers.InterfaceTermHandler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.utils.SimilarityUtils;

public class TestClass {

	private static InterfaceTermHandler stopWordHandler = new DummyStopTermHandler();
	private static final String delimiters = "\r\n\t \u00A0";
	
	public static void main(String[] args) throws InterruptedException
	{
//		String test1 = "Technical report, UniversitÃ  di Roma tre, 1997. Talk at the ";
//		String test2 = "Technical report, Universit di Roma tre, 1997. Talk at the ";
//		
//		System.out.println("Starting test ..");
//
//		/**
//		double score = SimilarityUtils.getSimilarity(Simmetrics.COSINE, test, t2);
//		System.out.println("Score : " + score);		
//		System.out.println(score);
//		**/
//		
//		System.out.println("before ..");
//		List<String> tokenizer = tokenizeToArrayList(test1);
//		System.out.println(tokenizer.toString());
		
//		String test = "1234";
//		boolean isDouble = NumberUtils.isNumber(test);
//		System.out.println(isDouble);
		
//		Multimap<String, String> testMap = ArrayListMultimap.create();
//		testMap.put("abc", "one");
//		testMap.put("two", "three");
//		testMap.put("abc", "str");
//		
//		System.out.println(testMap.toString());
//		if(testMap.containsEntry("abc", "onee")) {
//			System.out.println("hello world ..");
//		}
//		else {
//			System.out.println("bye world ..");
//		}
		
		String test = "hello|#world";
		String[] tokens = test.split("\\|#");
		System.out.println(Arrays.toString(tokens));
		
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
