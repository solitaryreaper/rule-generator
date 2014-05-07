package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;
import java.util.logging.Logger;

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RuleParser;

/**
 * Learns a random forest using bagging and generates rules.
 * 
 * @author skprasad
 * 
 */
public class RandomForestLearner implements Learner {

	private RandomForest randForest;
	
	private static final Logger LOG = Logger.getLogger(RandomForestLearner.class.getName());

	public RandomForestLearner()
	{
		randForest = new RandomForest();
		randForest.setPrintTrees(true);
		randForest.setNumTrees(Constants.NUM_CV_FOLDS);		
	}
	
	public RandomForest getRandForest() {
		return randForest;
	}

	public List<Rule> learnRules(Instances trainData) {
		try {
			randForest.buildClassifier(trainData);
		} catch (Exception e) {
			LOG.severe("Failed to generate random forest model. Reason : " + e.getStackTrace());
		}

		LOG.fine("Random Forest rules : " + randForest.toString());
		return RuleParser.parseRules(getRules());
	}
	
	public List<String> getRules()
	{
		return randForest.getRandomForestRules();
	}
	
	public double getVotingEntropyForInstance(Instance instance)
	{
		double entropy = 0.0;
		try {
			entropy = getRandForest().getVotingEntropyForInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return entropy;
	}

}
