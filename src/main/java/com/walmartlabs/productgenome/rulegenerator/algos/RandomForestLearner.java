package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;
import java.util.logging.Logger;

import weka.classifiers.trees.RandomForest;
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
public class RandomForestLearner implements Learner{

	private static final Logger LOG = Logger.getLogger(RandomForestLearner.class.getName());
	
	public List<Rule> learnRules(Instances trainData) 
	{
		RandomForest randForest = new RandomForest();
		randForest.setPrintTrees(true);
		randForest.setNumTrees(Constants.NUM_CV_FOLDS);
		
		int numFeatures = (int)(Constants.NUM_PERCENT_FEATURES*trainData.numAttributes())/100;
		randForest.setNumFeatures(numFeatures);
		try {
			randForest.buildClassifier(trainData);
		} catch (Exception e) {
			LOG.severe("Failed to generate random forest model. Reason : " + e.getStackTrace());
		}
		
		LOG.fine("Random Forest rules : " + randForest.toString());
		List<String> textRules = randForest.getRandomForestRules();
		return RuleParser.parseRules(textRules);
	}

}
