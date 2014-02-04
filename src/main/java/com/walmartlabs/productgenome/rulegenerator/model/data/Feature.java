package com.walmartlabs.productgenome.rulegenerator.model.data;

import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;

public class Feature {
	private String attrName;
	private Simmetrics simMetric;
	
	public Feature(String attrName, Simmetrics simMetric) {
		super();
		this.attrName = attrName;
		this.simMetric = simMetric;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public Simmetrics getSimMetric() {
		return simMetric;
	}

	public void setSimMetric(Simmetrics simMetric) {
		this.simMetric = simMetric;
	}
	
	public String getName()
	{
		Simmetrics metric = getSimMetric();
		return getAttrName() + "_" + metric.getSimmetricAbbrv(metric);
	}
}
