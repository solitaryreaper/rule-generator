package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;
import java.util.logging.Logger;

import weka.classifiers.trees.J48;
import weka.core.Instances;

import com.walmartlabs.productgenome.rulegenerator.model.RuleModel;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RuleParser;

/**
 * Models decision tree learning algorithm for learning matching rules.
 * 
 * Specifically, it uses the J48 d-tree implementation of Weka. 
 * 
 * 1) Generate a decision tree classifier using cross-validation.
 * 2) Divide the dataset into train and test dataset.
 * 3) Evaluate the accuracy of the test dataset.
 * 
 * @author excelsior
 *
 */
public class DecisionTreeLearner implements Learner {

	private static final Logger LOG = Logger.getLogger(DecisionTreeLearner.class.getName());
	
	public RuleModel learnRules(Instances trainData) 
	{
		J48 dtree = new J48();
		try {
			dtree.buildClassifier(trainData);
		} catch (Exception e) {
			LOG.severe("Failed to generate J48 decision tree model. Reason : " + e.getStackTrace());
		}
		
		List<String> textRules = dtree.getDecisionTreeRules();
		List<Rule> rules = RuleParser.parseRules(textRules);
		
		RuleModel model = new RuleModel(dtree, rules);
		return model;
	}

}
