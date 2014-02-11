package com.walmartlabs.productgenome.rulegenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.walmartlabs.productgenome.rulegenerator.algos.DecisionTreeLearner;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.model.RuleModel;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		LOG.info("Loaded the feature training data in WEKA format ..");
		
		// Step5 : Split into train and test data set
		Set<Rule> allRules = Sets.newHashSet();
		double avgAccuracy = 0.0;
		int totalFolds = Constants.NUM_CV_FOLDS;
		for(int i=0; i < totalFolds; i++) {
			Random rand = new Random(Constants.WEKA_DATA_SEED);
			Instances randData = new Instances(data);
			randData.randomize(rand);
			randData.stratify(totalFolds);
			Instances train = randData.trainCV(totalFolds, i);
			Instances test = randData.testCV(totalFolds, i);
			
			// Step6 : Launch the decision tree learning
			Learner learner = new DecisionTreeLearner();
			RuleModel model = learner.learnRules(train);
			
			Classifier classifier = model.getClassifier();
			try {
				Evaluation eval = new Evaluation(train);
				eval.evaluateModel(classifier, test);
				avgAccuracy += eval.pctCorrect();
				
				LOG.info("Run " + i + " summary : " + eval.toSummaryString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			List<Rule> rules = model.getRules();
			allRules.addAll(rules);
		}

		avgAccuracy = avgAccuracy/totalFolds;
		LOG.info("Average accuracy across " + totalFolds + " folds is : " + Constants.FORMATTER.format(avgAccuracy) + " % ");
		for(Rule rule : allRules) {
			LOG.info(rule.toString());
		}
		return Lists.newArrayList(allRules);
	}
}
