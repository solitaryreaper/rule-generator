package com.walmartlabs.productgenome.rulegenerator;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.walmartlabs.productgenome.rulegenerator.algos.DecisionTreeLearner;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.DatasetEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.service.CrossValidationService;
import com.walmartlabs.productgenome.rulegenerator.service.FeatureGenerationService;
import com.walmartlabs.productgenome.rulegenerator.utils.ArffDataWriter;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.CSVDataParser;
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
		//testAbtBuyDataset();
		
	}

	private static void testRestaurantDataset()
	{
		LOG.info("Testing auto-rule learning on restaurant dataset ..");
		String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
		String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
		String datasetName = "Restaurant";
		
		DataParser parser = new RestaurantDataParser();
		Dataset dataset = parseDataset(parser, matchFilePath, mismatchFilePath, datasetName);
		DatasetEvaluationSummary evalSummary = generateMatchingRules(dataset);
		LOG.info("Decision Tree Learning results on restuarant dataset :");
		//LOG.info(evalSummary.toString());
	}
	
	private static void testAbtBuyDataset()
	{
		LOG.info("Testing Abt-Buy dataset ..");
		File srcFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Abt.csv");
		File tgtFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Buy.csv");
		File goldFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/abt_buy_perfectMapping.csv");
		String datasetName = "Abt-Buy";
		
		DataParser parser = new CSVDataParser();
		Dataset dataset = parser.parseData(datasetName, srcFile, tgtFile, goldFile);
		LOG.info("Parsed CSV file data");
		DatasetEvaluationSummary evalSummary = generateMatchingRules(dataset);
		LOG.info("Decision Tree Learning results on restuarant dataset :");
		LOG.info(evalSummary.toString());		
	}
	
	/**
	 * Parse the raw dataset to generate in-memory item pairs.
	 */
	private static Dataset parseDataset(DataParser parser, String matchFilePath, 
			String mismatchFilePath, String datasetName)
	{
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		Dataset restaurantData = parser.parseData(matchFile, mismatchFile, datasetName);
		
		LOG.info("Generated in-memory itempairs for dataset : " + datasetName);
		return restaurantData;
	}
	
	private static DatasetEvaluationSummary generateMatchingRules(Dataset dataset)
	{
		// Step2 : Generate feature dataset from the raw dataset
		FeatureDataset featureDataset = FeatureGenerationService.generateFeatures(dataset);
		LOG.info("Generated feature vectors for dataset : " + dataset.getName());
		
		// Step3 : Stage the feature dataset in arff format
		String arffFileLoc = null;
		try {
			arffFileLoc = ArffDataWriter.loadDataInArffFormat(featureDataset);
		} catch (IOException e) {
			LOG.severe("Failed to stage feature data in arff file .. " + e.getStackTrace());
		}
		LOG.info("Loaded the in-memory feature vectors into arff file : " + arffFileLoc);
		
		// Step4 : Load the feature training data in weka format
		Instances data = null;
		try {
			DataSource trainDataSource = new DataSource(arffFileLoc);			
			data = trainDataSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		LOG.info("Loaded the feature training data in WEKA format ..");
		
		// Step5 : Generate the rules and test their accuracy
		Learner learner = new DecisionTreeLearner();
		return CrossValidationService.getRulesViaNFoldCrossValidation(learner, data, Constants.NUM_CV_FOLDS);
	}
}
