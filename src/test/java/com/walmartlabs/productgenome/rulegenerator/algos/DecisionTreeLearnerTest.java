package com.walmartlabs.productgenome.rulegenerator.algos;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

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
		
		String trainDataPath = Constants.DATA_FILE_PATH_PREFIX + "heart-train.arff";
		List<Rule> rules = learner.learnRules(trainDataPath, false);
		assertNotNull(rules);
		assertFalse(rules.isEmpty());
		
		LOG.info("Generated rules : " + rules.toString());
	}
}
