package com.walmartlabs.productgenome.rulegenerator.service;

import static org.junit.Assert.*;

import org.junit.Test;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class RuleEvaluationServiceTest {

	/*
	public void testApplyRuleToInstance()
	{
		Instances instances = getTestInstances();
		Instance testInstance = instances.get(0);
		System.out.println("Instance : " + testInstance.toString());
		
		Clause testClause = new Clause("name_jaccard", LogicalOperator.GREATER_THAN, 0.95);
		Rule rule = new Rule(Lists.newArrayList(testClause), MatchStatus.MATCH);
		MatchStatus label = RuleEvaluationService.applyRuleToInstance(rule, testInstance, instances);
		assertTrue(label.equals(MatchStatus.MATCH));
	}
	
	public void testLogicalOperator()
	{
		// Check true cases
		boolean result = RuleEvaluationService.checkIfLogicalOperationTrue(2.0, 2.0, LogicalOperator.EQUALS);
		assertTrue(result);
		result = RuleEvaluationService.checkIfLogicalOperationTrue(3.0, 2.0, LogicalOperator.NOT_EQUALS);
		assertTrue(result);
		result = RuleEvaluationService.checkIfLogicalOperationTrue(3.0, 2.0, LogicalOperator.GREATER_THAN);
		assertTrue(result);
		result = RuleEvaluationService.checkIfLogicalOperationTrue(3.0, 2.0, LogicalOperator.GREATER_THAN_EQUALS);
		assertTrue(result);
		result = RuleEvaluationService.checkIfLogicalOperationTrue(2.0, 3.0, LogicalOperator.LESS_THAN);
		assertTrue(result);
		result = RuleEvaluationService.checkIfLogicalOperationTrue(2.0, 3.0, LogicalOperator.LESS_THAN_EQUALS);
		assertTrue(result);
		
		// check false cases
		result = RuleEvaluationService.checkIfLogicalOperationTrue(3.0, 2.0, LogicalOperator.EQUALS);
		assertTrue(!result);
		result = RuleEvaluationService.checkIfLogicalOperationTrue(2.0, 2.0, LogicalOperator.NOT_EQUALS);
		assertTrue(!result);
		result = RuleEvaluationService.checkIfLogicalOperationTrue(3.0, 2.0, LogicalOperator.LESS_THAN);
		assertTrue(!result);		
	}
	
	*/
	private Instances getTestInstances()
	{
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
		
		return data;
	}
}
