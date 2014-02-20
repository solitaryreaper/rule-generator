package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;
import java.util.logging.Logger;

import weka.classifiers.rules.PART;
import weka.core.Instances;

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
		
		LOG.info("Decision rules : " + dlist.toString());
		List<String> textRules = dlist.getDecisionListRules();
		return RuleParser.parseRules(textRules);
	}

}
