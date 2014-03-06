package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.io.File;
import java.util.List;

import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.rule.BlockingClause;

/**
 * Parses the raw dataset to generate item pairs to match.
 * 
 * @author excelsior
 *
 */
public interface DataParser {

	public Dataset parseData(File matchFile, File mismatchFile, String datasetName);
	
	/**
	 * 
	 * @param srcFile	- Items from the source
	 * @param tgtFile	- Items from the target
	 * @param goldFile	- List of matching items from both the sources.
	 * @return
	 */
	public Dataset parseData(String datasetName, File srcFile, File tgtFile, File goldFile, List<BlockingClause> blockingClauses);
	
}
