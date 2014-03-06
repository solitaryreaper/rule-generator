package com.walmartlabs.productgenome.rulegenerator.model.rule;

import com.walmartlabs.productgenome.rulegenerator.model.Simmetrics;

/**
 * Represents a blocking rule.
 * 
 * Blocking rules are simple rules that can easily remove certain mismatched itempairs before the actual computationally
 * intensive matching process starts.
 * 
 * @author skprasad
 *
 */
public class BlockingClause {
	private String attributeName;
	private Simmetrics metricToApply;
	private double threshold;
	
	public BlockingClause(String attributeName, Simmetrics metricToApply, double threshold) {
		super();
		this.attributeName = attributeName;
		this.metricToApply = metricToApply;
		this.threshold = threshold;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Simmetrics getMetricToApply() {
		return metricToApply;
	}

	public void setMetricToApply(Simmetrics metricToApply) {
		this.metricToApply = metricToApply;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
}
