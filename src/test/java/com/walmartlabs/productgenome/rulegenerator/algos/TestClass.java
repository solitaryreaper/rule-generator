package com.walmartlabs.productgenome.rulegenerator.algos;

import java.text.DecimalFormat;
import java.util.List;

import com.walmartlabs.productgenome.rulegenerator.Constants;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TestClass {

	public static void main(String[] args) throws Exception
	{
		double a = 3.0;
		double b = 3.1;
		System.out.println(Double.compare(a, b) < 0);
	}
}
