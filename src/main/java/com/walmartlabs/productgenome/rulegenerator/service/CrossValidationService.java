package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import weka.core.Instances;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.DatasetEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.RuleEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class CrossValidationService {

	private static Logger LOG = Logger.getLogger(CrossValidationService.class.getName());

	/**
	 * Runs N-fold cross-validation across the labeled dataset, collects rules for each run, tests on the
	 * tune dataset and then returns all unique rules and its statistics averaged across runs.
	 * @param learner
	 * @param data
	 * @return
	 */
	public static DatasetEvaluationSummary getRulesViaNFoldCrossValidation(Learner learner, Instances data, int totalFolds)
	{
		List<DatasetEvaluationSummary> foldSummaryList = Lists.newArrayList();
		for(int foldId=0; foldId < totalFolds; foldId++) {
			Random rand = new Random(Constants.WEKA_DATA_SEED);
			Instances randData = new Instances(data);
			randData.randomize(rand);
			randData.stratify(totalFolds);
			Instances trainDataset = randData.trainCV(totalFolds, foldId);
			Instances tuneDataset = randData.testCV(totalFolds, foldId);

			List<Rule> rules = learner.learnRules(trainDataset);
			DatasetEvaluationSummary foldEvalSummary = RuleEvaluationService.evaluatePositiveRules(rules, tuneDataset);
			foldSummaryList.add(foldEvalSummary);
		}
		
		LOG.info("Generating average evaluation summary across " + totalFolds + "-folds ..");
		
		/**
		 * Generate the average precision-recall metrics for all the positive rules, then filter
		 * and retain only the rules which clear the precision cutoff and then again experiment
		 * with the data set using these ranked rules to generate the final precision-recall
		 * numbers. 
		 */
		return getNFoldAvgEvalSummary(foldSummaryList);
	}
	
	/**
	 * Accumulates the results across all the runs and then generates average statistics out of them.
	 * 
	 * @param foldSummaryList
	 * @return
	 */
	private static DatasetEvaluationSummary getNFoldAvgEvalSummary(List<DatasetEvaluationSummary> foldSummaryList)
	{
		DatasetEvaluationSummary nfoldEvalSummary = new DatasetEvaluationSummary();
		Map<Rule, RuleEvaluationSummary> uniqueRuleStatsMap = Maps.newHashMap();
		for(DatasetEvaluationSummary evalSummary : foldSummaryList) {
			nfoldEvalSummary.setTotalInstances(nfoldEvalSummary.getTotalInstances() + evalSummary.getTotalInstances());
			nfoldEvalSummary.setTruePositives(nfoldEvalSummary.getTruePositives() + evalSummary.getTruePositives());
			nfoldEvalSummary.setPredictedPositives(nfoldEvalSummary.getPredictedPositives() + evalSummary.getPredictedPositives());
			nfoldEvalSummary.setCorrectPredictions(nfoldEvalSummary.getCorrectPredictions() + evalSummary.getCorrectPredictions());
			
			List<RuleEvaluationSummary> ruleEvalSummaryList = evalSummary.getRuleSummary();
			for(RuleEvaluationSummary ruleEvalSummary : ruleEvalSummaryList) {
				Rule rule = ruleEvalSummary.getRule();
				RuleEvaluationSummary currRuleEvalSummary = null;
				if(uniqueRuleStatsMap.containsKey(rule)) {
					currRuleEvalSummary = uniqueRuleStatsMap.get(rule);
				}
				else {
					currRuleEvalSummary = new RuleEvaluationSummary(rule);
					currRuleEvalSummary.setNumOccurrenceAcrossNFolds(0);
				}
				
				currRuleEvalSummary.setTotalPositives(currRuleEvalSummary.getTotalPositives() + ruleEvalSummary.getTotalPositives());
				currRuleEvalSummary.setPositivePredictions(currRuleEvalSummary.getPositivePredictions() + ruleEvalSummary.getPositivePredictions());
				currRuleEvalSummary.setCorrectPredictions(currRuleEvalSummary.getCorrectPredictions() + ruleEvalSummary.getCorrectPredictions());
				currRuleEvalSummary.setNumOccurrenceAcrossNFolds(currRuleEvalSummary.getNumOccurrenceAcrossNFolds() + 1);
				currRuleEvalSummary.setTotalFolds(foldSummaryList.size());
				
				uniqueRuleStatsMap.put(rule, currRuleEvalSummary);
			}
		}
		
		List<RuleEvaluationSummary> ruleSummaryList = Lists.newArrayList(uniqueRuleStatsMap.values());
		nfoldEvalSummary.setRuleSummary(ruleSummaryList);
		return nfoldEvalSummary;
	}
}
