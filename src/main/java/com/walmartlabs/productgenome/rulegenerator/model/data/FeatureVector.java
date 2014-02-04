package com.walmartlabs.productgenome.rulegenerator.model.data;

import java.util.List;

import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

public class FeatureVector {
	private List<Double> featureValues;
	private MatchStatus matchStatus;
	
	public FeatureVector(List<Double> featureValues, MatchStatus matchStatus) {
		super();
		this.featureValues = featureValues;
		this.matchStatus = matchStatus;
	}

	public List<Double> getFeatureValues() {
		return featureValues;
	}

	public void setFeatureValues(List<Double> featureValues) {
		this.featureValues = featureValues;
	}

	public MatchStatus getMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(MatchStatus matchStatus) {
		this.matchStatus = matchStatus;
	}
	
	public String getFeatureString()
	{
		StringBuilder fString = new StringBuilder();
		for(Double val : getFeatureValues()) {
			if(val == null) {
				fString.append("?");
			}
			else {
				fString.append(val.toString());				
			}
			fString.append(",");
		}
		
		fString.append(getMatchStatus().toString().toLowerCase());
		
		return fString.toString();
	}
	
}
