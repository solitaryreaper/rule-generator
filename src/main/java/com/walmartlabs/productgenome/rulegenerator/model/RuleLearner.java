package com.walmartlabs.productgenome.rulegenerator.model;


public enum RuleLearner {
	J48("Decision Tree"),
	PART("Decision List"),
	RandomForest("Random Forest");
	
	private String learningAlgoName;
	
	private RuleLearner(String algoName)
	{
		this.learningAlgoName = algoName;
	}
	
	public static RuleLearner getRuleLearner(String learnerName)
	{
		RuleLearner learner = RuleLearner.RandomForest;
		for(RuleLearner entry : RuleLearner.values()) {
			if(entry.toString().equals(learnerName)) {
				learner = entry;
				break;
			}
		}
		
		return learner;
	}
} 
