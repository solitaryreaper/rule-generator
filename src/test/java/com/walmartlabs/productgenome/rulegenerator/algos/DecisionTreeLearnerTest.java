package com.walmartlabs.productgenome.rulegenerator.algos;

import static org.junit.Assert.*;

import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.RuleModel;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.utils.WekaUtils;

public class DecisionTreeLearnerTest {

	private static Logger LOG = Logger.getLogger(DecisionTreeLearnerTest.class.getName());
	
	private static DecisionTreeLearner learner = null;
	
	@BeforeClass
	public static void testSetup() 
	{
		learner = new DecisionTreeLearner();
	}
	
	@AfterClass
	public static void testCleanup() 
	{
		
	}
	  
	@Test
	public void testLearnRules()
	{
		LOG.info("Testing generation of matching rules using decision tree learner ..");
		
		String trainDataPath = Constants.DATA_FILE_PATH_PREFIX + "test.arff";
		Instances data = null;
		try {
			DataSource trainDataSource = new DataSource(trainDataPath);			
			data = trainDataSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		LOG.info("Loaded the feature training data in WEKA format ..");	
		
		Instance testInstance = data.get(0);
		System.out.println("Instance : " + testInstance.toString());
		//RuleModel model = learner.learnRules(data);
		
		System.out.println("Class : " + WekaUtils.getInstanceLabel(testInstance));
		Attribute attr = data.attribute("name_jaccard");
		System.out.println("Value : " + WekaUtils.getFeatureValue(testInstance, attr));
		
	}
	
}
