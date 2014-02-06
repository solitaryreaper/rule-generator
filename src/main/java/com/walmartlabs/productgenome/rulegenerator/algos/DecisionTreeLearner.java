package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;
import java.util.logging.Logger;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

/**
 * Models decision tree learning algorithm for learning matching rules.
 * 
 * Specifically, it uses the J48 d-tree implementation of Weka.
 * 
 * @author excelsior
 *
 */
public class DecisionTreeLearner implements Learner {

	private static final Logger LOG = Logger.getLogger(DecisionTreeLearner.class.getName());
	
	/**
	 * {@inheritDoc}
	 * 
	 * TODO : Add support for cross-validation.
	 */
	public List<Rule> learnRules(String trainDataPath, boolean isCrossValidationReqd) 
	{
		if(Strings.isNullOrEmpty(trainDataPath)) {
			LOG.severe("Please specify valid train data path.");
			System.exit(1);
		}
		
		LOG.info("Generating decision tree for train data : " + trainDataPath);
		J48 dtree = null;
		try {
			DataSource trainDataSource = new DataSource(trainDataPath);
			Instances trainData = trainDataSource.getDataSet();
			
			// setting class attribute if the data format does not provide this information
			// For example, the XRFF format saves the class attribute information as well
			if (trainData.classIndex() == -1)
				trainData.setClassIndex(trainData.numAttributes() - 1);
			 
			dtree = new J48();
			dtree.buildClassifier(trainData);
			
			LOG.info("Generated decision tree : " + dtree.toString());
		} catch (Exception e) {
			LOG.severe("Error while generating the decision tree. Please fix !!");
			e.printStackTrace();
		}
		
		// Extract each root-leaf path as rules from the decision tree ..
		List<Rule> rules = Lists.newArrayList();
		return rules;
	}

}
