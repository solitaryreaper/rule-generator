package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import weka.core.Instances;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.DatasetEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class CrossValidationService {

	private static Logger LOG = Logger.getLogger(CrossValidationService.class.getName());
	
	/**
	 * Runs cross-validation across the labeled dataset, collects rules, tests it on the
	 * test dataset and then returns the summary results for the run.
	 * @param dataset
	 * @return
	 */
	public static DatasetEvaluationSummary generateMatchingRules(Learner learner, Instances data)
	{
		int totalFolds = Constants.NUM_CV_FOLDS;
		Random rand = new Random(Constants.WEKA_DATA_SEED);
		Instances randData = new Instances(data);
		randData.randomize(rand);
		randData.stratify(totalFolds);
		
		// Get some random fold between [0 - (NUM_CV_FOLDS-1)]
		int randFold = 0 + (int)(Math.random() * ((totalFolds-1) + 1));
		Instances train = randData.trainCV(totalFolds, randFold);
		Instances test = randData.testCV(totalFolds, randFold);
		
		List<Rule> rules = learner.learnRules(train);
		
		return RuleEvaluationService.evaluatePositiveRules(rules, test);
	}
	
	/**
	 * Runs N-fold cross-validation across the labeled dataset, collects rules for each run, tests on the
	 * test dataset and then returns all unique rules and its statistics averaged across runs.
	 * @param learner
	 * @param data
	 * @return
	 */
	public static DatasetEvaluationSummary getRulesViaNFoldCrossValidation(Learner learner, Instances data, int totalFolds)
	{
		Set<Rule> allRules = Sets.newHashSet();
		int totalRules = 0;
		for(int foldId=0; foldId < totalFolds; foldId++) {
			Random rand = new Random(Constants.WEKA_DATA_SEED);
			Instances randData = new Instances(data);
			randData.randomize(rand);
			randData.stratify(totalFolds);
			Instances train = randData.trainCV(totalFolds, foldId);
			Instances test = randData.testCV(totalFolds, foldId);

			List<Rule> rules = learner.learnRules(train);
			DatasetEvaluationSummary foldEvalSummary = RuleEvaluationService.evaluatePositiveRules(rules, test);
			LOG.info("\nFold : " + foldId + ", Results : " + foldEvalSummary.toString());
			totalRules += foldEvalSummary.getRules().size();
			allRules.addAll(foldEvalSummary.getRules());
		}
		
		LOG.info("Total rules : " + totalRules);
		LOG.info("Unique rules : " + allRules.size());
		List<Rule> rules = Lists.newArrayList(allRules);
		
		for(Rule outerRule : rules) {
			LOG.info("Rule : " + outerRule.toString() + ", hashcode : " + outerRule.hashCode() + ", " + outerRule.getName().hashCode() + 
					", " + outerRule.getClauses().hashCode() + ", " + outerRule.getLabel().hashCode());
		}
		

		DatasetEvaluationSummary avgEvalSummary = null;
		return avgEvalSummary;
	}
}
