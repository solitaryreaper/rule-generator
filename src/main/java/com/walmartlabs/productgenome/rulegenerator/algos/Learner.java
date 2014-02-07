package com.walmartlabs.productgenome.rulegenerator.algos;

import weka.core.Instances;

import com.walmartlabs.productgenome.rulegenerator.model.RuleModel;

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
	 * @return	A learned classifier model along with the rules
	 */
	public RuleModel learnRules(Instances trainData);
}
