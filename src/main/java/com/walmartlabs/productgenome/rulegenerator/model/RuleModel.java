package com.walmartlabs.productgenome.rulegenerator.model;

import java.util.List;

import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

import weka.classifiers.Classifier;

/**
 * Represents the learn model from the passive learning algorithms. It also contains the set of extracted
 * matching rules.
 * 
 * @author skprasad
 *
 */
public class RuleModel {
	private Classifier classifier;
	private List<Rule> rules;
	
	public RuleModel(Classifier classifier, List<Rule> rules) {
		super();
		this.classifier = classifier;
		this.rules = rules;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
	
}
