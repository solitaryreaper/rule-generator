package com.walmartlabs.productgenome.rulegenerator.algos;

import java.text.DecimalFormat;

public class TestClass {

	public static void main(String[] args)
	{
		DecimalFormat df = new DecimalFormat("#.###");
		System.out.println("Helow or");
		System.out.println(df.format(0.4));
	}
}
