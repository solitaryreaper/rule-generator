package com.walmartlabs.productgenome.rulegenerator.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.google.common.base.Strings;
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
		File parentDir = new File(Constants.TMP_FILE_PATH_PREFIX);
		if(!parentDir.exists()) {
			parentDir.mkdirs();
		}
		
		int randSeed = getRandomNum(1, 10000);
		String tmpFileLoc = Constants.TMP_FILE_PATH_PREFIX + dataset.getName() + "_" + randSeed + ".arff";
		
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
			fStr.append("@ATTRIBUTE ").append(f.getName()).append(" NUMERIC ");
			bw.write(fStr.toString());bw.newLine();
		}
		
		// 3) Write the class attribute
		StringBuilder classes = new StringBuilder();
		classes.append("@ATTRIBUTE").append(" class { match, mismatch }");
		bw.write(classes.toString());bw.newLine();bw.newLine();

		// 4) Write the actual data
		bw.write("@DATA");bw.newLine();
		for(FeatureVector v : dataset.getFeatureVectors()) {
			if(Strings.isNullOrEmpty(v.getFeatureString().trim())) {
				continue;
			}
			bw.write(v.getFeatureString());bw.newLine();
		}
		
		bw.close();
		
		return tmpFileLoc;
	}
	
	private static int getRandomNum(int min, int max)
	{
	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;		
	}
}
