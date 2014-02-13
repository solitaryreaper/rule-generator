package com.walmartlabs.productgenome.rulegenerator.service;

import java.util.List;
import java.util.Random;

import weka.core.Instances;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.DatasetEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class CrossValidationService {

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
}
