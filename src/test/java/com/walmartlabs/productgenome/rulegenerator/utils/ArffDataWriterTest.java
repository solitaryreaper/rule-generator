package com.walmartlabs.productgenome.rulegenerator.utils;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;
import com.walmartlabs.productgenome.rulegenerator.model.data.Feature;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureDataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.FeatureVector;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;


public class ArffDataWriterTest {

	private static Logger LOG = Logger.getLogger(ArffDataWriterTest.class.getName());
	private static FeatureDataset dataset = null;
	
	@BeforeClass
	public static void testSetup() 
	{
		List<Feature> features = Lists.newArrayList();
		features.add(new Feature("name", Simmetrics.JACCARD));
		features.add(new Feature("age", Simmetrics.EXACT_MATCH_STRING));
		
		List<FeatureVector> fVectors = Lists.newArrayList();
		fVectors.add(new FeatureVector(Lists.newArrayList(0.95, 1.0), MatchStatus.MATCH));
		fVectors.add(new FeatureVector(Lists.newArrayList(1.0, 0.0), MatchStatus.MISMATCH));
		
		String datasetName = "Test";
		
		dataset = new FeatureDataset(datasetName, features, fVectors);
	}
	
	@AfterClass
	public static void testCleanup() 
	{
		
	}
	
	@Test
	public void testLoadDataInArffFormat()
	{
		LOG.info("Loading data in ARFF format ..");
		String arffFileLoc = null;
		try {
			arffFileLoc = ArffDataWriter.loadDataInArffFormat(dataset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertNotNull(arffFileLoc);
		LOG.info("ARFF file location : " + arffFileLoc);
	}
}
