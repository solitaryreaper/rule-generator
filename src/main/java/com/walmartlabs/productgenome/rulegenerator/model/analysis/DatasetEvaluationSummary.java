package com.walmartlabs.productgenome.rulegenerator.model.analysis;

import java.text.DecimalFormat;
import java.util.List;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

// TODO : Add a ranking function for all the rules. Also, a mechanism to compress similar rules.
public class DatasetEvaluationSummary {
	private int totalInstances = 0;
	private int truePositives = 0; // Total number of matched itempairs in dataset
	private int predictedPositives = 0;	// Total number of itempairs predicted as matched
	private int correctPositivePredictions = 0;	// Total number of itempairs predicted as matched and are actually matched
	
	private List<RuleEvaluationSummary> ruleSummary;
	
	public DatasetEvaluationSummary()
	{
		
	}
	
	public DatasetEvaluationSummary(int totalInstances, int truePositives,
			int predictedPositives, int correctPositivePredictions, List<RuleEvaluationSummary> ruleSummary) {
		super();
		this.totalInstances = totalInstances;
		this.truePositives = truePositives;
		this.predictedPositives = predictedPositives;
		this.correctPositivePredictions = correctPositivePredictions;
		this.ruleSummary = ruleSummary;
	}

	public String toString()
	{
		DecimalFormat df = Constants.FORMATTER;
		
		StringBuilder builder = new StringBuilder();
		builder.append("Total instances : ").append(totalInstances).append("\n");
		builder.append("True positives : ").append(truePositives).append("\n");
		builder.append("Predicted positives : ").append(predictedPositives).append("\n");
		builder.append("Correct predictions : ").append(correctPositivePredictions).append("\n");
		builder.append("Precision(%) : ").append(df.format(getPrecision())).append("\n");
		builder.append("Recall(%) : ").append(df.format(getRecall())).append("\n");
		
		builder.append("\n<--------------- RULES ------------------->\n");
		for(RuleEvaluationSummary ruleSummary : getRuleSummary()) {
			builder.append(ruleSummary.explainRule());
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	public int getTotalInstances() {
		return totalInstances;
	}

	public void setTotalInstances(int totalInstances) {
		this.totalInstances = totalInstances;
	}

	public int getTruePositives() {
		return truePositives;
	}

	public void setTruePositives(int truePositives) {
		this.truePositives = truePositives;
	}

	public int getPredictedPositives() {
		return predictedPositives;
	}

	public void setPredictedPositives(int predictedPositives) {
		this.predictedPositives = predictedPositives;
	}

	public int getCorrectPredictions() {
		return correctPositivePredictions;
	}

	public void setCorrectPredictions(int correctPredictions) {
		this.correctPositivePredictions = correctPredictions;
	}

	public List<RuleEvaluationSummary> getRuleSummary() {
		return ruleSummary;
	}

	public void setRuleSummary(List<RuleEvaluationSummary> ruleSummary) {
		this.ruleSummary = ruleSummary;
	}
	
	public double getPrecision()
	{
		return (correctPositivePredictions/(double)predictedPositives)*100;
	}
	
	public double getRecall()
	{
		return (correctPositivePredictions/(double)truePositives)*100;		
	}
	
	public List<Rule> getRules()
	{
		List<Rule> rules = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : getRuleSummary()) {
			rules.add(ruleSummary.getRule());
		}
		
		return rules;
	}
}
