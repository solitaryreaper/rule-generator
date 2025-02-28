package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.DatasetEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.RuleEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.utils.SimilarityUtils;
import com.walmartlabs.productgenome.rulegenerator.utils.WekaUtils;

/**
 * Evaluates the matching rules generated from the learning algorithms and generates summary report for the run.
 * 
 * @author skprasad
 *
 */
public class RuleEvaluationService {

	private static Logger LOG = Logger.getLogger(RuleEvaluationService.class.getName());
	
	/**
	 * Collect rum-time statistics for every matching rule
	 * @author excelsior
	 *
	 */
	private static class RuleStats {
		private int positivePredictions = 0;
		private int actualPositives = 0;
		
		public RuleStats()
		{
			positivePredictions = 0;
			actualPositives = 0;
		}
		
		public int getPositivePredictions() {
			return positivePredictions;
		}

		public void setPositivePredictions(int positivePredictions) {
			this.positivePredictions = positivePredictions;
		}

		public int getActualPositives() {
			return actualPositives;
		}

		public void setActualPositives(int actualPositives) {
			this.actualPositives = actualPositives;
		}

	}
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
		int correctPositivePredictions = 0;
		
		Map<Rule, RuleStats> perRuleStatsMap = Maps.newHashMap();
		for(Instance instance : testData) {
			MatchStatus actualLabel = WekaUtils.getInstanceLabel(instance);
			boolean isTruePositive = actualLabel.equals(MatchStatus.MATCH);
			if(isTruePositive) {
				++truePositives;
			}
			
			boolean isMatch = false;
			Rule matchRule = null;
			for(Rule rule : rules) {
				// Only evaluate positive rules.
				if(!rule.isMatchingRule()) {
					continue;
				}
				
				MatchStatus label = applyRuleToInstance(rule, instance, testData);
				if(label.equals(MatchStatus.MATCH)) {
					isMatch = true;
					matchRule = rule;
					
					// Update the number of predicted MATCHES by this rule.
					RuleStats stats = null;
					if(perRuleStatsMap.containsKey(rule)) {
						stats = perRuleStatsMap.get(rule);
					}
					else {
						stats = new RuleStats();
					}
					
					stats.setPositivePredictions(stats.getPositivePredictions() + 1);
					if(isTruePositive) {
						stats.setActualPositives(stats.getActualPositives() + 1);
					}
					perRuleStatsMap.put(rule, stats);
				}
			}
			
			// If atleast one rule passes, then predict MATCH for this instance.
			if(isMatch) {
				++predictedPositives;
				if(isTruePositive) {
					++correctPositivePredictions;
				}
				else {
					LOG.fine("False positive for instance : " + instance.toString() + " for rule : " + matchRule.toString());
				}
			}
			else if(!isMatch && isTruePositive) {
				LOG.fine("False negative for instance : " + instance.toString());
			}
		}
		
		// Generate the evaluation metrics for the current run
		List<RuleEvaluationSummary> allRulesSummary = Lists.newArrayList();
		for(Map.Entry<Rule, RuleStats> entry : perRuleStatsMap.entrySet()) {
			Rule rule = entry.getKey();
			RuleStats stats = entry.getValue();
			int positivePredictions = stats.getPositivePredictions();
			int correctPredictions = stats.getActualPositives();

			RuleEvaluationSummary ruleSummary = 
				new RuleEvaluationSummary(rule, positivePredictions, correctPredictions, truePositives);
			allRulesSummary.add(ruleSummary);
		}
		
		Map<String, Integer> rulesetSummaryMeta = getRulesetSummaryMeta(testData, allRulesSummary);
		return new DatasetEvaluationSummary(totalPairs, truePositives, predictedPositives, 
				correctPositivePredictions, allRulesSummary, rulesetSummaryMeta);
	}
	
	/**
	 * Returns summary meta information about the ruleset.
	 */
	private static Map<String, Integer> getRulesetSummaryMeta(Instances data, List<RuleEvaluationSummary> allRuleSummary)
	{
		Map<String, Integer> rulesetSummaryMeta = Maps.newHashMap();

		int totalAttrs = data.numAttributes();
		int maxNumClauses = Integer.MIN_VALUE;;
		int maxNumFolds = Integer.MIN_VALUE;
		
		for(RuleEvaluationSummary ruleSummary : allRuleSummary) {
			Rule rule = ruleSummary.getRule();
			int numClauses = rule.getClauses().size();
			int numFolds = ruleSummary.getTotalFolds();
			
			maxNumClauses = (numClauses > maxNumClauses) ? numClauses : maxNumClauses;
			maxNumFolds = (numFolds > maxNumFolds) ? numFolds : maxNumFolds;
		}
		
		rulesetSummaryMeta.put(Constants.TOTAL_ATTRIBUTES, totalAttrs);
		rulesetSummaryMeta.put(Constants.TOTAL_FOLDS, maxNumFolds);
		rulesetSummaryMeta.put(Constants.MAX_NUM_CLAUSES, maxNumClauses);
		
		return rulesetSummaryMeta;
	}
	
	/**
	 * Returns the class label for a test weka-type instance on application of a rule.
	 * @param rule
	 * @param instance
	 * @return
	 */
	public static MatchStatus applyRuleToInstance(Rule rule, Instance instance, Instances data)
	{
		MatchStatus label = MatchStatus.MATCH;
		for(Clause clause : rule.getClauses()) {
			boolean isClauseSuccess = applyClauseToInstance(clause, instance, data);
			if(!isClauseSuccess) {
				label = MatchStatus.MISMATCH;
				break;
			}
		}
		
		return label;
	}
	
	/**
	 * Checks whether a clause is valid for a test weka-type instance.
	 * @param clause
	 * @param instance
	 */
	public static boolean applyClauseToInstance(Clause clause, Instance instance, Instances data)
	{
		String featureName = clause.getFeatureName();
		LogicalOperator logOp = clause.getLogOp();
		double threshold = clause.getThreshold();
		
		Attribute feature = data.attribute(featureName);
		double featureValueToTest = WekaUtils.getFeatureValue(instance, feature);
		
		return checkIfLogicalOperationTrue(featureValueToTest, threshold, logOp);
	}
	
	/**
	 * Returns the class label for an itempair on application of a rule.
	 * @param rule
	 * @param itemPair
	 * @return
	 */
	public static MatchStatus applyRuleToItemPair(Rule rule, ItemPair itemPair, Simmetrics metric)
	{
		MatchStatus label = MatchStatus.MATCH;
		for(Clause clause : rule.getClauses()) {
			boolean isClauseSuccess = applyClauseToItemPair(clause, itemPair, metric);
			if(!isClauseSuccess) {
				label = MatchStatus.MISMATCH;
				break;
			}
		}
		
		return label;		
	}
	
	/**
	 * Checks whether the clause is valid for an itempair.
	 * @param clause
	 * @param itemPair
	 * @return
	 */
	public static boolean applyClauseToItemPair(Clause clause, ItemPair itemPair, Simmetrics metric)
	{
		String featureName = clause.getFeatureName();
		LogicalOperator logOp = clause.getLogOp();
		double threshold = clause.getThreshold();
		
		String valA = itemPair.getItemBValByAttr(featureName);
		String valB = itemPair.getItemAValByAttr(featureName);
		double scoreToTest = SimilarityUtils.getSimilarity(metric, valA, valB);
		
		return checkIfLogicalOperationTrue(scoreToTest, threshold, logOp);		
	}
	
	private static boolean checkIfLogicalOperationTrue(double featureValueToTest, double threshold, LogicalOperator logOp)
	{
		boolean isLogicalOpTrue = true;
		switch(logOp) {
		case EQUALS:
			isLogicalOpTrue = Double.compare(featureValueToTest, threshold) == 0;
			break;
		case NOT_EQUALS:
			isLogicalOpTrue = Double.compare(featureValueToTest, threshold) != 0;
			break;
		case GREATER_THAN:
			isLogicalOpTrue = Double.compare(featureValueToTest, threshold) > 0;
			break;
		case GREATER_THAN_EQUALS:
			isLogicalOpTrue = Double.compare(featureValueToTest, threshold) > 0 || 
				Double.compare(featureValueToTest, threshold) == 0;
			break;			
		case LESS_THAN:
			isLogicalOpTrue = Double.compare(featureValueToTest, threshold) < 0;
			break;
		case LESS_THAN_EQUALS:
			isLogicalOpTrue = Double.compare(featureValueToTest, threshold) < 0 || 
				Double.compare(featureValueToTest, threshold) == 0;
			break;			
		}
		
		return isLogicalOpTrue;
	}
}
