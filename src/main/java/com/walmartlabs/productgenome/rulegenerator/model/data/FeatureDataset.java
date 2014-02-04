package com.walmartlabs.productgenome.rulegenerator.model.data;

import java.util.List;

public class FeatureDataset {
	private String name;
	private List<Feature> features;
	private List<FeatureVector> featureVectors;
	
	public FeatureDataset(String name, List<Feature> features, List<FeatureVector> featureVectors) 
	{
		super();
		this.name = name;
		this.features = features;
		this.featureVectors = featureVectors;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public List<FeatureVector> getFeatureVectors() {
		return featureVectors;
	}

	public void setFeatureVectors(List<FeatureVector> featureVectors) {
		this.featureVectors = featureVectors;
	}
}
