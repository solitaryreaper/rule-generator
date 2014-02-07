package com.walmartlabs.productgenome.rulegenerator.algos;

import java.text.DecimalFormat;

public class TestClass {

	public static void main(String[] args)
	{
		String str = "|   |   name_smith_waterman <= 0.88: mismatch (20.0)";
		String relop = ">";
		if(str.contains("<=")) {
			relop = "<=";
		}
		System.out.println("Relop : " + relop);
		
		String[] temp = str.split(relop);
		String featureName = temp[0].replaceAll("\\|", "").replaceAll(" ", "").trim();
		System.out.println("Feature name : " + featureName);
		
		System.out.println("String : " + temp[1]);
		System.out.println("Index " + temp[1].indexOf(":"));
		String value = temp[1].substring(0, temp[1].indexOf(':') - 1).replaceAll(" ", "").trim();
		System.out.println("Value : " + value);
	}
}
