package com.walmartlabs.productgenome.rulegenerator;

import java.text.DecimalFormat;

public class Constants {

	public static final String DATA_FILE_PATH_PREFIX = 
			System.getProperty("user.dir") + "/src/main/resources/data/";
	
	public static final String TMP_FILE_PATH_PREFIX = 
			System.getProperty("user.dir") + "/src/main/resources/tmp/";
	
	public static final String MISSING_VALUE = "NA";
	
	public static final String ID_ATTRIBUTE = "id";
	
	/**
	 * Format similarity values to 3 decimal places
	 */
	public static DecimalFormat FORMATTER = new DecimalFormat("#.###");
	
	public static final int WEKA_DATA_SEED = 99;
	public static final int NUM_CV_FOLDS = 10;
	
	/**
	 * Number of top N string metrics to retain for every attribute.
	 */
	public static final int NUM_METRICS_PER_ATTRIBUTE = 4;
	
	public static final String DEFAULT_TOKENIZATION_DELIMITER = " ";
	public static final String DEFAULT_SET_VALUE_ATTRIBIUTE_DELIMITER = ",";
	public static final String DEFAULT_COLUMN_DELIMITER = "\\|#";
	public static final String DEFAULT_ROW_DELIMITER = "\n";
	
	/**
	 * Percentage of features in each random tree in the random forest as compared to the original
	 * dataset.
	 */
	public static final int NUM_PERCENT_FEATURES = 50;
	
	public static final double RULE_PRECISION_CUTOFF_PERCENT = 98.0;
	public static final double RULE_COVERAGE_CUTOFF_PERCENT = 20.0;
	public static final double BETA_F_SCORE = 0.5;
	public static final int NUM_RULES_REQUIRED = 10;
	
	public static final int SAMPLE_SIZE = 1000;
}
