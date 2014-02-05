package com.walmartlabs.productgenome.rulegenerator.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.Feature;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureVector;

/**
 * Writes the feature-based itempair data in ARFF format, so that it can be
 * easily ingested by WEKA's machine learning library.
 * 
 * @author excelsior
 *
 */
public class ArffDataWriter {

	public static String loadDataInArffFormat(FeatureDataset dataset) throws IOException
	{
		// TODO : This should have a randomly generated name.
		String tmpFileLoc = Constants.TMP_FILE_PATH_PREFIX + dataset.getName() + ".arff";
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFileLoc));
		
		// 1) Write the relation name
		StringBuilder datasetName = new StringBuilder();
		datasetName.append("@RELATION ").append(dataset.getName());
		bw.write(datasetName.toString());
		bw.newLine();
		bw.newLine();
		
		// 2) Write the attributes
		for(Feature f : dataset.getFeatures()) {
			StringBuilder fStr = new StringBuilder();
			fStr.append("@ATTRIBUTE ").append(f.getName()).append(" real");
			bw.write(fStr.toString());bw.newLine();
		}
		bw.newLine();
		
		// 3) Write the actual data
		bw.write("@DATA");bw.newLine();
		for(FeatureVector v : dataset.getFeatureVectors()) {
			bw.write(v.getFeatureString());bw.newLine();
		}
		
		bw.close();
		
		return tmpFileLoc;
	}
}
