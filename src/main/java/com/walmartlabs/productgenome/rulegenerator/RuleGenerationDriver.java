package com.walmartlabs.productgenome.rulegenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestResult;

import com.walmartlabs.productgenome.rulegenerator.algos.DecisionTreeLearner;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.service.FeatureGenerationService;
import com.walmartlabs.productgenome.rulegenerator.utils.ArffDataWriter;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.RestaurantDataParser;

/**
 * The main driver class for the auto-rule generation engine. It has the following functions :
 * 
 * 1) Load the cleaned data.
 * 2) Generate feature vectors using attribute simmetric recommender library and string matching
 *    core library.
 * 3) Generate matching rules using a learning algorithm.
 * 4) Evaluate the accuracy of each of the generated rules.
 * 5) Output summary as well as detailed output of the performance of rules.
 * @author excelsior
 *
 */
public class RuleGenerationDriver {

	private static Logger LOG = Logger.getLogger(RuleGenerationDriver.class.getName());
			
	public static void main(String[] args) {
		testRestaurantDataset();
		
	}

	private static void testRestaurantDataset()
	{
		LOG.info("Testing auto-rule learning on restaurant dataset ..");
		String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
		String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
		String datasetName = "Restaurant";
		
		Dataset dataset = parseDataset(matchFilePath, mismatchFilePath, datasetName);
		List<Rule> rules = generateMatchingRules(dataset);
	}
	
	/**
	 * Parse the raw dataset to generate in-memory item pairs.
	 */
	private static Dataset parseDataset(String matchFilePath, String mismatchFilePath, String datasetName)
	{
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		DataParser parser = new RestaurantDataParser();
		Dataset restaurantData = parser.parseData(matchFile, mismatchFile, datasetName);
		
		LOG.info("Generated in-memory itempairs for dataset : " + datasetName);
		return restaurantData;
	}
	
	private static List<Rule> generateMatchingRules(Dataset dataset)
	{
		// Step2 : Generate feature dataset from the raw dataset
		LOG.info("Generating feature vectors for dataset : " + dataset.getName());
		FeatureDataset featureDataset = FeatureGenerationService.generateFeatures(dataset);
		LOG.info("Generated feature vectors for dataset : " + dataset.getName());
		
		// Step3 : Stage the feature dataset in arff format
		LOG.info("Loading in-memory feature vectors into arff file ..");
		String arffFileLoc = null;
		try {
			arffFileLoc = ArffDataWriter.loadDataInArffFormat(featureDataset);
		} catch (IOException e) {
			LOG.severe("Failed to stage feature data in arff file .. " + e.getStackTrace());
		}
		LOG.info("Loaded the in-memory feature vectors into arff file : " + arffFileLoc);
		
		// Step4 : Launch the decision tree learning
		LOG.info("Generated decision tree learner ..");
		Learner learner = new DecisionTreeLearner();
		List<Rule> rules = learner.learnRules(arffFileLoc, false);
		LOG.info("Found " + rules.size() + " rules using decision tree learner ..");
		
		return rules;
	}
}
