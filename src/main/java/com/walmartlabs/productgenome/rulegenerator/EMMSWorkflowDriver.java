package com.walmartlabs.productgenome.rulegenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.StopWatch;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.walmartlabs.productgenome.rulegenerator.algos.DecisionListLearner;
import com.walmartlabs.productgenome.rulegenerator.algos.DecisionTreeLearner;
import com.walmartlabs.productgenome.rulegenerator.algos.Learner;
import com.walmartlabs.productgenome.rulegenerator.algos.RandomForestLearner;
import com.walmartlabs.productgenome.rulegenerator.config.JobMetadata;
import com.walmartlabs.productgenome.rulegenerator.model.RuleLearner;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.DatasetEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.analysis.JobEvaluationSummary;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.service.CrossValidationService;
import com.walmartlabs.productgenome.rulegenerator.service.FeatureGenerationService;
import com.walmartlabs.productgenome.rulegenerator.service.RuleEvaluationService;
import com.walmartlabs.productgenome.rulegenerator.utils.ArffDataWriter;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.CSVDataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;

/**
 * Test the workflow of EMMS application.
 * 
 * @author excelsior
 *
 */
public class EMMSWorkflowDriver {

	private static Logger LOG = Logger.getLogger(EMMSWorkflowDriver.class.getSimpleName());
	
	public static void main(String[] args)
	{
		EMMSWorkflowDriver driver = new EMMSWorkflowDriver();
		//driver.testDBLPACMDataset();
		driver.testRestaurantDataset();
	}
	
	private void testDBLPACMDataset()
	{
		JobMetadata dblpAcmMeta = new JobMetadata();
		dblpAcmMeta.setName("DBLP-ACM");
		dblpAcmMeta.setDescription("Entity matching rules for DBLP-ACM dataset.");
		dblpAcmMeta.setSourceFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-ACM/DBLP_cleaned.csv");
		dblpAcmMeta.setTargetFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-ACM/ACM_cleaned.csv");
		dblpAcmMeta.setGoldFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-ACM/gold.csv");
		
		dblpAcmMeta.setAttributesToEvaluate("title,authors,venue,year");
		dblpAcmMeta.setLearner("Random Forest");
		
		JobEvaluationSummary jobSummary = runEntityMatching(dblpAcmMeta);
		LOG.info("TRAIN PHASE : " + jobSummary.getTrainPhaseSumary().toString());
		LOG.info("TEST PHASE : " + jobSummary.getTestPhaseSummary().toString());
	}
	
	private void testRestaurantDataset()
	{
		JobMetadata restaurantMeta = new JobMetadata();
		restaurantMeta.setName("Restaurant");
		restaurantMeta.setDescription("Entity matching rules for Restuarant dataset.");
		restaurantMeta.setSourceFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/zagats_final.csv");
		restaurantMeta.setTargetFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/fodors_final.csv");
		restaurantMeta.setGoldFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/gold_final.csv");
		
		restaurantMeta.setAttributesToEvaluate("name,addr,city,type");
		restaurantMeta.setLearner("Random Forest");
		
		JobEvaluationSummary jobSummary = runEntityMatching(restaurantMeta);
		LOG.info("TRAIN PHASE : " + jobSummary.getTrainPhaseSumary().toString());
		LOG.info("TEST PHASE : " + jobSummary.getTestPhaseSummary().toString());
	}
	
	/**
	 * Using the project metadata gathered from EMMS application, invokes the actual entity matching
	 * process to generate the matching rules.
	 * 
	 * @param projectMeta
	 */
	public JobEvaluationSummary runEntityMatching(JobMetadata projectMeta)
	{
		String srcFilePath = projectMeta.getSourceFile();
		String tgtFilePath = projectMeta.getTargetFile();
		String goldFilePath = projectMeta.getGoldFile();
		
		BiMap<String, String> schemaMap = getSchemaMap(projectMeta.getAttributesToEvaluate());
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap);
		
		List<String> setValuedAttributes = projectMeta.getSetValuedAttributes();
		if(!(setValuedAttributes == null || setValuedAttributes.isEmpty())) {
			normalizerMeta.setSetValuedAttributes(setValuedAttributes);
		}

		String projectName = projectMeta.getName();
		RuleLearner learner = projectMeta.getLearner();
		
		return generateMatchingRules(projectName, learner, srcFilePath, tgtFilePath, goldFilePath, normalizerMeta);
	}
	
	private BiMap<String, String> getSchemaMap(List<String> attributesToEvaluate)
	{
		BiMap<String, String> schemaMap = HashBiMap.create();
		for(String attribute : attributesToEvaluate) {
			schemaMap.put(attribute, attribute);
		}
		
		return schemaMap;
	}
	
	private JobEvaluationSummary generateMatchingRules(String projectName, RuleLearner learner, String srcFilePath, 
			String tgtFilePath, String goldFilePath, DatasetNormalizerMeta normalizerMeta)
	{
		LOG.info("Testing " + projectName + " project ..");
		
		StopWatch timer = new StopWatch();

		File srcFile = new File(srcFilePath);
		File tgtFile = new File(tgtFilePath);
		File goldFile = new File(goldFilePath);
		
		timer.start();
		DataParser parser = new CSVDataParser();
		Dataset dataset = parser.parseData(projectName, srcFile, tgtFile, goldFile, normalizerMeta);
		
		timer.stop();
		LOG.info("Time taken for parsing CSV input file : " + timer.toString());
		
		timer.reset();
		timer.start();
		String arffFileLoc = stageDataInArffFormat(dataset, normalizerMeta);
		timer.stop();
		LOG.info("Time taken for staging as ARFF file : " + timer.toString());

		
		timer.reset();
		timer.start();
		JobEvaluationSummary evalSummary = learnMatchingRules(arffFileLoc, learner);
		timer.stop();
		LOG.info("Time taken for generating matching rules : " + timer.toString());
		
		return evalSummary;
	}
	
	private String stageDataInArffFormat(Dataset dataset, DatasetNormalizerMeta normalizerMeta)
	{
		FeatureDataset featureDataset = FeatureGenerationService.generateFeatures(dataset, normalizerMeta);
		LOG.info("Generated feature vectors for dataset : " + dataset.getName());
		
		String arffFileLoc = null;
		try {
			arffFileLoc = ArffDataWriter.loadDataInArffFormat(featureDataset);
		} catch (IOException e) {
			LOG.severe("Failed to stage feature data in arff file .. " + e.getStackTrace());
			e.printStackTrace();
		}
		LOG.info("Loaded the in-memory feature vectors into arff file : " + arffFileLoc);

		return arffFileLoc;
	}	
	
	private JobEvaluationSummary learnMatchingRules(String arffFileLoc, RuleLearner ruleLearner)
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
		}
		
		LOG.info("\n\n10-fold CROSS-VALIDATION ..");
		DatasetEvaluationSummary trainPhaseSummary = 
			CrossValidationService.getRulesViaNFoldCrossValidation(learner, trainDataset, totalFolds);
		List<Rule> finalRankedRules = trainPhaseSummary.getRankedRules();
		
		LOG.info("\n\nFINAL RULE EVALUATION RESULTS ..");
		DatasetEvaluationSummary testPhaseSummary = 
				RuleEvaluationService.evaluatePositiveRules(finalRankedRules, testDataset);
		
		return new JobEvaluationSummary(trainPhaseSummary, testPhaseSummary);
	}	
}
