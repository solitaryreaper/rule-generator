package com.walmartlabs.productgenome.rulegenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import com.walmartlabs.productgenome.rulegenerator.utils.WekaUtils;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.ItemDataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.DataParser;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.ItemPairDataParser;

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
		//driver.testRestaurantDataset();
		driver.testWalmartCNETDotcomDataset();
	}
	
	private void testDBLPACMDataset()
	{
		JobMetadata dblpAcmMeta = new JobMetadata();
		dblpAcmMeta.setJobName("DBLP-ACM");
		dblpAcmMeta.setDescription("Entity matching rules for DBLP-ACM dataset.");
		dblpAcmMeta.setSourceFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-ACM/DBLP_cleaned.csv");
		dblpAcmMeta.setTargetFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-ACM/ACM_cleaned.csv");
		dblpAcmMeta.setGoldFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/DBLP-ACM/gold.csv");
		
		dblpAcmMeta.setAttributesToEvaluate("title,authors,venue,year");

		JobEvaluationSummary jobSummary = runEntityMatching(dblpAcmMeta);
		LOG.info("TRAIN PHASE : " + jobSummary.getTrainPhaseSumary().toString());
		LOG.info("TEST PHASE : " + jobSummary.getTestPhaseSummary().toString());
	}
	
	private void testRestaurantDataset()
	{
		JobMetadata restaurantMeta = new JobMetadata();
		restaurantMeta.setJobName("Restaurant");
		restaurantMeta.setDescription("Entity matching rules for Restuarant dataset.");
		restaurantMeta.setSourceFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/zagats_final.csv");
		restaurantMeta.setTargetFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/fodors_final.csv");
		restaurantMeta.setGoldFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/restaurant/gold_final.csv");
		
		restaurantMeta.setAttributesToEvaluate("name,addr,city,type");
		
		JobEvaluationSummary jobSummary = runEntityMatching(restaurantMeta);
		//LOG.info("TRAIN PHASE : " + jobSummary.getTrainPhaseSumary().toString());
		LOG.info("TEST PHASE : " + jobSummary.getTestPhaseSummary().toString());
	}
	
	private void testWalmartCNETDotcomDataset()
	{
		JobMetadata cnetDotcomMeta = new JobMetadata();
		cnetDotcomMeta.setJobName("CNET-DOTCOM");
		cnetDotcomMeta.setDatasetName("CNET-DOCTOM");
		cnetDotcomMeta.setDescription("Entity matching rules for CNET-DOTCOM dataset.");
		
		cnetDotcomMeta.setItemPairFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/WALMART-DATA/CNET_DOTCOM_CLEANED.txt");
		cnetDotcomMeta.setGoldFile(Constants.DATA_FILE_PATH_PREFIX + "datasets/WALMART-DATA/GOLD_FINAL.txt");
		
		cnetDotcomMeta.setAttributesToEvaluate("pd_title,req_brand_name,req_category,req_color,req_manufacturer,req_part_number,req_upc_10,req_upc_11,req_upc_12,req_upc_13,req_upc_14");
		cnetDotcomMeta.setSetValuedAttributes("req_upc_10,req_upc_11,req_upc_12,req_upc_13,req_upc_14,req_category");
		
		cnetDotcomMeta.setColumnDelimiter(Constants.DEFAULT_ITEMPAIR_COLUMN_DELIMITER);
		cnetDotcomMeta.setSetValueDelimiter(Constants.DEFAULT_SET_VALUE_ATTRIBIUTE_DELIMITER);
		
		JobEvaluationSummary jobSummary = runEntityMatching(cnetDotcomMeta);
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
		BiMap<String, String> schemaMap = getSchemaMap(projectMeta.getAttributesToEvaluate());
		DatasetNormalizerMeta normalizerMeta = new DatasetNormalizerMeta(schemaMap);
		
		List<String> setValuedAttributes = projectMeta.getSetValuedAttributes();
		if(!(setValuedAttributes == null || setValuedAttributes.isEmpty())) {
			normalizerMeta.setSetValuedAttributes(setValuedAttributes);
		}
		
		return generateMatchingRules(normalizerMeta, projectMeta);
	}
	
	private BiMap<String, String> getSchemaMap(List<String> attributesToEvaluate)
	{
		BiMap<String, String> schemaMap = HashBiMap.create();
		for(String attribute : attributesToEvaluate) {
			schemaMap.put(attribute, attribute);
		}
		
		return schemaMap;
	}
	
	private JobEvaluationSummary generateMatchingRules(DatasetNormalizerMeta normalizerMeta, JobMetadata projectMeta)
	{
		String datasetName = projectMeta.getDatasetName();
		LOG.info("Testing " + datasetName + " project ..");
		
		StopWatch timer = new StopWatch();

		File goldFile = new File(projectMeta.getGoldFile());
		Dataset dataset = null;
		// Parse item format file data.
		if(!projectMeta.isDatasetInItemPairFormat()) {
			File srcFile = new File(projectMeta.getSourceFile());
			File tgtFile = new File(projectMeta.getTargetFile());
			
			timer.start();
			DataParser parser = new ItemDataParser();
			dataset = parser.parseData(datasetName, srcFile, tgtFile, goldFile, normalizerMeta);
			
			timer.stop();
			LOG.info("Time taken for parsing item format input file : " + timer.toString());			
		}
		// Parse item pair format file data.
		else {
			File itemPairFile = new File(projectMeta.getItemPairFile());
			timer.start();
			
			DataParser parser = new ItemPairDataParser();
			dataset = parser.parseData(datasetName, itemPairFile, goldFile, normalizerMeta);
			timer.stop();
			LOG.info("Time taken for parsing item pair input file : " + timer.toString());			
		}
		
		timer.reset();
		timer.start();
		String arffFileLoc = stageDataInArffFormat(dataset, normalizerMeta);
		timer.stop();
		LOG.info("Time taken for staging as ARFF file : " + timer.toString());

		
		timer.reset();
		timer.start();
		JobEvaluationSummary evalSummary = learnMatchingRules(arffFileLoc, projectMeta);
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
	
	private JobEvaluationSummary learnMatchingRules(String arffFileLoc, JobMetadata projectMeta)
	{
		int numCVFolds = projectMeta.getCrossValidations();
		double precisionFilter = projectMeta.getDesiredPrecision();
		double coverageFilter = projectMeta.getDesiredCoverage();
		RuleLearner ruleLearner = projectMeta.getLearner();
		
		Instances data = null;
		try {
			DataSource trainDataSource = new DataSource(arffFileLoc);			
			data = trainDataSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		
		Map<String, Instances> splitDataset = WekaUtils.getSplitDataset(data, numCVFolds);
		Instances trainDataset = splitDataset.get(Constants.TRAIN_DATASET);
		Instances tuneDataset = splitDataset.get(Constants.TUNE_DATASET);
		Instances testDataset = splitDataset.get(Constants.TEST_DATASET);
		
		LOG.info("Train : " + trainDataset.numInstances());
		LOG.info("Tune : " + tuneDataset.numInstances());
		LOG.info("Test : " + testDataset.numInstances());
		
		LOG.info("Loaded the feature training data in WEKA format ..");
		int totalFolds = numCVFolds;
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
		
		LOG.info("\n\nCross-Validation TRAINING phase ..");
		DatasetEvaluationSummary trainPhaseSummary = 
			CrossValidationService.getRulesViaNFoldCrossValidation(learner, trainDataset, totalFolds, 
					precisionFilter, coverageFilter);
		trainPhaseSummary.setReqdRulePrecision(precisionFilter);
		trainPhaseSummary.setReqdRuleCoverage(coverageFilter);
		List<Rule> rankedAndFilteredRules = trainPhaseSummary.getRankedAndFilteredRules();
		LOG.info("Rules after training phase : " + rankedAndFilteredRules.size());
		
		LOG.info("\n\nTUNING phase results ..");
		DatasetEvaluationSummary tunePhaseSummary = 
			RuleEvaluationService.evaluatePositiveRules(rankedAndFilteredRules, tuneDataset);
		rankedAndFilteredRules = tunePhaseSummary.getRankedAndFilteredRules();
		LOG.info("Rules after tuning phase : " + rankedAndFilteredRules.size());
		
		LOG.info("\n\nTEST phase results ..");
		DatasetEvaluationSummary testPhaseSummary = 
				RuleEvaluationService.evaluatePositiveRules(rankedAndFilteredRules, testDataset);
		
		return new JobEvaluationSummary(trainPhaseSummary, tunePhaseSummary, testPhaseSummary);
	}	
}
