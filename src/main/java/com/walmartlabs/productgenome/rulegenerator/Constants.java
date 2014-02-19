package com.walmartlabs.productgenome.rulegenerator;

import java.text.DecimalFormat;

public class Constants {

	public static final String DATA_FILE_PATH_PREFIX = 
			System.getProperty("user.dir") + "/src/main/resources/data/";
	
	public static final String TMP_FILE_PATH_PREFIX = 
			System.getProperty("user.dir") + "/src/main/resources/tmp/";
	
	public static final String MISSING_VALUE = "NA";
	
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
	
	public static final String DEFAULT_TOKENIZER = " ";
	
}
