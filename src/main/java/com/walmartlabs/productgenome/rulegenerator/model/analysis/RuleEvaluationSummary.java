package com.walmartlabs.productgenome.rulegenerator.model.analysis;

import java.text.DecimalFormat;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class RuleEvaluationSummary {
	private Rule rule;

	private int predictedPositives;
	private int correctPositivePredictions;
	private int totalPositives;
	
	public RuleEvaluationSummary(Rule rule, int predictedPositives,
			int correctPositivePredictions, int totalPositives) {
		super();
		this.rule = rule;
		this.predictedPositives = predictedPositives;
		this.correctPositivePredictions = correctPositivePredictions;
		this.totalPositives = totalPositives;
	}
	
	public String toSummaryString()
	{
		DecimalFormat df = Constants.FORMATTER;
		StringBuilder builder = new StringBuilder();
		builder.append("Precision(%) : ").append(df.format(getPrecision())).append("\t");
		builder.append("Coverage(%) : ").append(df.format(getRecall()));
		
		return builder.toString();
	}	
	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	public int getPositivePredictions() {
		return predictedPositives;
	}
	public void setPositivePredictions(int positivePredictions) {
		this.predictedPositives = positivePredictions;
	}
	public int getCorrectPredictions() {
		return correctPositivePredictions;
	}
	public void setCorrectPredictions(int correctPredictions) {
		this.correctPositivePredictions = correctPredictions;
	}
	public int getTotalPositives() {
		return totalPositives;
	}
	public void setTotalPositives(int totalPositives) {
		this.totalPositives = totalPositives;
	}
	
	public double getPrecision()
	{
		return (correctPositivePredictions/(double)predictedPositives)*100;		
	}
	
	public double getRecall()
	{
		return (correctPositivePredictions/(double)totalPositives)*100;
	}
}
