package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.io.File;

import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;

/**
 * Parses the raw dataset to generate item pairs to match.
 * 
 * @author excelsior
 *
 */
public interface DataParser {

	public Dataset parseData(File matchFile, File mismatchFile, String datasetName);
	
}
