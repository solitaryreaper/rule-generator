package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;

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
	 * @param trainDataPath			- Path to training dataset. This file should be in the ARFF
	 * 								  format.
	 * @param isCrossValidationReqd	- Should learning be done via cross-validation ?
	 * @return
	 */
	public List<Rule> learnRules(String trainDataPath, boolean isCrossValidationReqd);
}
