package com.walmartlabs.productgenome.rulegenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.StopWatch;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.google.common.base.Strings;
import com.walmartlabs.productgenome.rulegenerator.algos.DecisionListLearner;
import com.walmartlabs.productgenome.rulegenerator.algos.DecisionTreeLearner;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.algos.RandomForestLearner;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.DatasetEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.service.CrossValidationService;
import com.walmartlabs.productgenome.rulegenerator.service.FeatureGenerationService;
import com.walmartlabs.productgenome.rulegenerator.service.RuleEvaluationService;
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
			
	/**
	 * List of available learners to test
	 * @author excelsior
	 *
	 */
	public enum RuleLearner
	{
		J48,
		PART,
		RandomForest
	}
	
	public static void main(String[] args) {
		//String arffFileLoc = System.getProperty("user.dir") + "/src/main/resources/data/Abt-Buy.arff";
		//String arffFileLoc = "/afs/cs.wisc.edu/u/s/k/skprasad/RA/rule-generator/src/main/resources/tmp/DBLP-Scholar_9909.arff";
		String arffFileLoc = null;
		/*
		for(RuleLearner learner : RuleLearner.values()) {
			LOG.info("Testing for " + learner.toString() + " learning algorithm ..");
			testRestaurantDataset(learner);
			//testAbtBuyDataset(learner);
		}
		*/
		//testRestaurantDataset(RuleLearner.J48, arffFileLoc);
		//testRestaurantDataset(RuleLearner.PART, arffFileLoc);
		//testRestaurantDataset(RuleLearner.RandomForest, arffFileLoc);
		
		//testAbtBuyDataset(RuleLearner.J48, arffFileLoc);
		//testAbtBuyDataset(RuleLearner.PART, arffFileLoc);
		//testAbtBuyDataset(RuleLearner.RandomForest, arffFileLoc);
		
		//testDBLPScholarDataset(RuleLearner.J48, arffFileLoc);
		//testDBLPScholarDataset(RuleLearner.PART, arffFileLoc);
		//testDBLPScholarDataset(RuleLearner.RandomForest, arffFileLoc);
		
		testAmazonGoogleProductsDataset(RuleLearner.RandomForest, arffFileLoc);
	}

	private static void testRestaurantDataset(RuleLearner learner, String arffFileLoc)
	{
		LOG.info("Testing auto-rule learning on restaurant dataset ..");
		if(Strings.isNullOrEmpty(arffFileLoc)) {
			String matchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_match.txt";
			String mismatchFilePath = System.getProperty("user.dir") + "/src/main/resources/data/restaurant/res_mismatch.txt";
			String datasetName = "Restaurant";
			
			DataParser parser = new RestaurantDataParser();
			Dataset dataset = parseDataset(parser, matchFilePath, mismatchFilePath, datasetName);
			arffFileLoc = stageDataInArffFormat(dataset);
		}
		
		DatasetEvaluationSummary evalSummary = generateMatchingRules(arffFileLoc, learner);
		LOG.info("Decision Tree Learning results on restaurant dataset :");
		LOG.info(evalSummary.toString());
		evalSummary.getRankedAndFilteredRules();
	}
	
	private static void testAbtBuyDataset(RuleLearner learner, String arffFileLoc)
	{
		String srcFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Abt.csv";
		String tgtFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/Buy.csv";
		String goldFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/Abt-Buy/abt_buy_perfectMapping.csv";
		
		testDataset("Abt-Buy", learner, arffFileLoc, srcFilePath, tgtFilePath, goldFilePath);
	}
	
	private static void testDBLPScholarDataset(RuleLearner learner, String arffFileLoc)
	{
		String srcFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/DBLP1_cleaned.csv";
		String tgtFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/DBLP1_cleaned.csv";
		String goldFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-Scholar/DBLP1_cleaned.csv";
		
		testDataset("DBLP-Scholar", learner, arffFileLoc, srcFilePath, tgtFilePath, goldFilePath);
	}
	
	private static void testAmazonGoogleProductsDataset(RuleLearner learner, String arffFileLoc)
	{
		String srcFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/Amazon-GoogleProducts/Amazon_cleaned.csv";
		String tgtFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/Amazon-GoogleProducts/GoogleProducts_cleaned.csv";
		String goldFilePath = Constants.DATA_FILE_PATH_PREFIX + "datasets/Amazon-GoogleProducts/Amzon_GoogleProducts_perfectMapping.csv";
		
		testDataset("Amazon-Google-Products", learner, arffFileLoc, srcFilePath, tgtFilePath, goldFilePath);
	}
	
	private static void testDataset(String datasetName, RuleLearner learner, String arffFileLoc, String srcFilePath, 
			String tgtFilePath, String goldFilePath)
	{
		LOG.info("Testing " + datasetName + " dataset ..");
		
		StopWatch timer = new StopWatch();
		if(Strings.isNullOrEmpty(arffFileLoc)) {
			File srcFile = new File(srcFilePath);
			File tgtFile = new File(tgtFilePath);
			File goldFile = new File(goldFilePath);
			
			timer.start();
			DataParser parser = new CSVDataParser();
			Dataset dataset = parser.parseData(datasetName, srcFile, tgtFile, goldFile);
			timer.stop();
			LOG.info("Time taken for parsing CSV input file : " + timer.toString());
			
			timer.reset();
			timer.start();
			arffFileLoc = stageDataInArffFormat(dataset);
			timer.stop();
			LOG.info("Time taken for staging as ARFF file : " + timer.toString());
		}
		
		timer.reset();
		timer.start();
		DatasetEvaluationSummary evalSummary = generateMatchingRules(arffFileLoc, learner);
		timer.stop();
		LOG.info("Time taken for generating matching rules : " + timer.toString());
		
		LOG.info("Decision Tree Learning results on " + datasetName + " dataset :");
		LOG.info(evalSummary.toString());
		evalSummary.getRankedAndFilteredRules();			
	}
	
	/**
	 * Parse the raw dataset to generate in-memory item pairs.
	 */
	private static Dataset parseDataset(DataParser parser, String matchFilePath, String mismatchFilePath, String datasetName)
	{
		File matchFile = new File(matchFilePath);
		File mismatchFile = new File(mismatchFilePath);
		Dataset restaurantData = parser.parseData(matchFile, mismatchFile, datasetName);
		
		LOG.info("Generated in-memory itempairs for dataset : " + datasetName);
		return restaurantData;
	}
	
	private static String stageDataInArffFormat(Dataset dataset)
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

		return arffFileLoc;
	}
	
	private static DatasetEvaluationSummary generateMatchingRules(String arffFileLoc, RuleLearner ruleLearner)
	{
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
		
		Random rand = new Random(Constants.WEKA_DATA_SEED);
		Instances randData = new Instances(data);
		randData.randomize(rand);
		randData.stratify(Constants.NUM_CV_FOLDS);
		
		int randFold = 0 + (int)(Math.random() * ((Constants.NUM_CV_FOLDS - 1) + 1));
		Instances trainDataset = randData.trainCV(Constants.NUM_CV_FOLDS, randFold);
		Instances testDataset = randData.testCV(Constants.NUM_CV_FOLDS, randFold);
		
		LOG.info("Loaded the feature training data in WEKA format ..");
		
		// Step5 : Generate the rules and test their accuracy
		int totalFolds = Constants.NUM_CV_FOLDS;
		Learner learner = null;
		if(ruleLearner.equals(RuleLearner.J48)) {
			learner = new DecisionTreeLearner();
		}
		else if(ruleLearner.equals(RuleLearner.PART)) {
			learner = new DecisionListLearner();
		}
		else if(ruleLearner.equals(RuleLearner.RandomForest)) {
			learner = new RandomForestLearner();
			totalFolds = 1;
		}
		
		LOG.info("\n\n10-fold CROSS-VALIDATION ..");
		DatasetEvaluationSummary overallSummary = 
			CrossValidationService.getRulesViaNFoldCrossValidation(learner, trainDataset, totalFolds);
		List<Rule> finalRankedRules = overallSummary.getRankedRules();
		
		LOG.info("\n\nFINAL RULE EVALUATION RESULTS ..");
		return RuleEvaluationService.evaluatePositiveRules(finalRankedRules, testDataset);
	}
}
