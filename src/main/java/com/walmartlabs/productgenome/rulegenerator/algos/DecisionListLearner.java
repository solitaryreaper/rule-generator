package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.PART;
import weka.core.Instances;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RuleParser;

public class DecisionListLearner implements Learner{

	private static final Logger LOG = Logger.getLogger(DecisionListLearner.class.getName());
	
	public List<Rule> learnRules(Instances trainData) 
	{
		PART dlist = new PART();
		try {
			dlist.buildClassifier(trainData);
		} catch (Exception e) {
			LOG.severe("Failed to generate J48 decision tree model. Reason : " + e.getStackTrace());
		}
		
		List<Rule> rules = null;
		// TODO : Implement this by modifying weka.
		/*
		LOG.info("Decision Lists : " + dlist.toString());
		List<String> textRules = dlist.getRules();
		rules = RuleParser.parseRules(textRules);
		*/
		return rules;
	}

}
