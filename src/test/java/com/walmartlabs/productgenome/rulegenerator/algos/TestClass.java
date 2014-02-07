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
		DataSource trainDataSource = new DataSource(Constants.DATA_FILE_PATH_PREFIX + "heart-train.arff");
		Instances data = trainDataSource.getDataSet();
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		
		J48 dtree = new J48();
		dtree.buildClassifier(data);
		List<String> rules = dtree.getDecisionTreeRules();
		System.out.println("Rules : " + rules);
	}
}
