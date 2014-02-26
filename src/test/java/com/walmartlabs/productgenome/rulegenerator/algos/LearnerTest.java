package com.walmartlabs.productgenome.rulegenerator.algos;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class LearnerTest {
	private static Logger LOG = Logger.getLogger(LearnerTest.class.getName());
	
	private static Learner learner = null;
	private static Instances data = null;
	
	@BeforeClass
	public static void testSetup() 
	{
		data = getDataset();
	}
	
	@Ignore
	public void testDecisionTreeLearner()
	{
		LOG.info("Testing decision tree learning algorithm ..");
		learner = new DecisionTreeLearner();
		List<Rule> rules = learner.learnRules(data);
		assertNotNull(rules);		
	}
	
	@Ignore
	public void testDecisionListLearner()
	{
		LOG.info("Testing decision lists learning algorithm ..");
		learner = new DecisionListLearner();
		List<Rule> rules = learner.learnRules(data);
		assertNotNull(rules);
	}
	
	@Test
	public void testRandomForest()
	{
		LOG.info("Testing random forests learning algorithm ..");
		learner = new RandomForestLearner();
		List<Rule> rules = learner.learnRules(data);
		assertNotNull(rules);		
	}
	
	private static Instances getDataset()
	{
		String trainDataPath = Constants.DATA_FILE_PATH_PREFIX + "Restaurant.arff";
		Instances data = null;
		try {
			DataSource trainDataSource = new DataSource(trainDataPath);			
			data = trainDataSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}
}
