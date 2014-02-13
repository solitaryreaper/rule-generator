package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;

import weka.core.Instances;

import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

/**
 * Models a learning algorithm that can be used to generate a representative model using
 * supervised learning techniques.
 * 
 * The algorithms here primarily target to learn matching rules from the training dataset.
 * 
 * @author excelsior
 *
 */
public interface Learner {

	/**
	 * Learns the appropriate matching rules from the training data set.
	 * 
	 * @param data		- Training dataset
	 * @return	Set of learned rules
	 */
	public List<Rule> learnRules(Instances trainData);
}
