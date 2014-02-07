package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import weka.classifiers.Evaluation;
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
 * 1) Generate a decision tree classifier using cross-validation.
 * 2) Divide the dataset into train and test dataset.
 * 3) Evaluate the accuracy of the test dataset.
 * 
 * @author excelsior
 *
 */
public class DecisionTreeLearner implements Learner {

	private static final Logger LOG = Logger.getLogger(DecisionTreeLearner.class.getName());
	
	// Number of cross-validations
	private static final Integer NUM_CV = 10;
	
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
			// Setup the complete labelled dataset
			DataSource trainDataSource = new DataSource(trainDataPath);
			Instances data = trainDataSource.getDataSet();
			
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1);
			
			for(int i=0; i < NUM_CV; i++) {
				int seed = i+1;
				Random rand = new Random(seed);
				Instances randData = new Instances(data);
				randData.randomize(rand);
				if (randData.classAttribute().isNominal())
					randData.stratify(NUM_CV); // TODO : Check this
				
				Evaluation eval = new Evaluation(randData);
				for(int n=0; n < NUM_CV; n++) {
					Instances train = randData.trainCV(NUM_CV, n);
					Instances test = randData.testCV(NUM_CV, n);
					
					dtree = new J48();
					dtree.buildClassifier(train);
					eval.evaluateModel(dtree, test);
					
					LOG.info("Generated decision tree : " + dtree.toString());
				}
				
			      // output evaluation
			      LOG.info("=== Setup run " + (i+1) + " ===");
			      LOG.info("Classifier: J48 tree");
			      LOG.info("Dataset: " + data.relationName());
			      LOG.info("Folds: " + NUM_CV);
			      LOG.info("Seed: " + seed);
			      LOG.info(eval.toSummaryString("=== " + NUM_CV + "-fold Cross-validation run " + (i+1) + "===", false));
			}

		} catch (Exception e) {
			LOG.severe("Error while generating the decision tree. Please fix !!");
			e.printStackTrace();
		}
		
		// Extract each root-leaf path as rules from the decision tree ..
		List<Rule> rules = Lists.newArrayList();
		return rules;
	}

}
