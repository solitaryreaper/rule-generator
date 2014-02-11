package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Map;

import weka.core.Instance;
import weka.core.Instances;

import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.DatasetEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

/**
 * Evaluates the matching rules generated from the learning algorithms and generates summary report for the run.
 * 
 * @author skprasad
 *
 */
public class RuleEvaluationService {

	/**
	 * Evaluate the set of positive rules against the test data
	 * @param rules
	 * @param testData
	 */
	public static DatasetEvaluationSummary evaluatePositiveRules(List<Rule> rules, Instances testData)
	{
		int totalPairs = testData.numInstances();
		
		int truePositives = 0;
		int predictedPositives = 0;
		
		Map<Rule, Integer> ruleCorrectnessMap = Maps.newHashMap();
		
		for(Instance instance : testData) {
			MatchStatus actualLabel = null;
			if(actualLabel.equals(MatchStatus.MATCH)) {
				++truePositives;
			}
			
			boolean isMatch = false;
			for(Rule rule : rules) {
				MatchStatus label = applyRuleToInstance(rule, instance);
				if(label.equals(MatchStatus.MATCH)) {
					isMatch = true;
					
					int numPositiveForRule = 1;
					if(ruleCorrectnessMap.containsKey(rule)) {
						numPositiveForRule = numPositiveForRule + ruleCorrectnessMap.get(rule);
					}
					
					ruleCorrectnessMap.put(rule, numPositiveForRule);
				}
			}
			
			if(isMatch) {
				++predictedPositives;
			}
			
		}
	}
	
	/**
	 * Returns the class label for a test instance on application of a rule.
	 * @param rule
	 * @param instance
	 * @return
	 */
	private static MatchStatus applyRuleToInstance(Rule rule, Instance instance)
	{
		MatchStatus label = MatchStatus.MATCH;
		for(Clause clause : rule.getClauses()) {
			boolean isClauseSuccess = applyClauseToInstance(clause, instance);
			if(!isClauseSuccess) {
				label = MatchStatus.MISMATCH;
				break;
			}
		}
		
		return label;
	}
	
	/**
	 * Checks whether a clause is valid for a test instance.
	 * @param clause
	 * @param instance
	 */
	private static boolean applyClauseToInstance(Clause clause, Instance instance)
	{
		String featureName = clause.getFeatureName();
		LogicalOperator logOp = clause.getLogOp();
		double threshold = clause.getThreshold();
		
		double featureValueToTest = instance.value(featureName);
		
		boolean isClauseSuccess = true;
		switch(logOp) {
		case EQUALS:
			isClauseSuccess = Double.compare(threshold, featureValueToTest) == 0;
			break;
		}
		
		return isClauseSuccess;
	}
}
