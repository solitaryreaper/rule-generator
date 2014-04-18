package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.io.File;

import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;

/**
 * Parses the raw dataset to generate item pairs to match.
 * 
 * @author excelsior
 *
 */
public interface DataParser {

	/**
	 * Parses the data in item file format.
	 * 
	 * Item format implies separate data files for both the sources where the data is represented in the following format :
	 * 
	 * Header - List of attribute names in a row, separated by some column delimiter.
	 * Rows	- Attribute values for the attributes in header, separated by some column delimiter.
	 * 
	 * @param datasetName	- Name of the dataset.
	 * @param srcFile		- Items from the source
	 * @param tgtFile		- Items from the target
	 * @param goldFile		- List of matching items from both the sources.
	 * @param normalizerMeta	- Metadata that guides the data parsing process.
	 * 
	 * @return	Dataset
	 */
	public Dataset parseData(String datasetName, File srcFile, File tgtFile, File goldFile, DatasetNormalizerMeta normalizerMeta);
	
	/**
	 * Parses the data in item pair file format.
	 * 
	 * Item Pair format implies a single data file containing the attributes for a source and target item, adjacent to
	 * each other in the following format :
	 * 
	 * <Attribute Name> <Col Delmiter> Source Item Attribute Value <Col Delimiter> Target Item Attribute Value
	 * 
	 * @param datasetName	- Name of the dataset
	 * @param itemPairFile	- File containing the itempairs.
	 * @param goldFile		- List of matching item ids from both the sources
	 * @param normalizerMeta	- Metadata that guides the data parsing process.
	 * @return
	 */
	public Dataset parseData(String datasetName, File itemPairFile, File goldFile, DatasetNormalizerMeta normalizerMeta);
	
}
