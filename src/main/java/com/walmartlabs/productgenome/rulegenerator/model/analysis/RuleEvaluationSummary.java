package com.walmartlabs.productgenome.rulegenerator.model.analysis;

import java.text.DecimalFormat;

import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class RuleEvaluationSummary {
	private Rule rule;

	private int predictedPositives;
	private int correctPositivePredictions;
	private int totalPositives;
	
	// Count of how many times this rule occurs across N fold cross-validation. Default value is 1 for one-fold.
	private int numOccurrenceAcrossNFolds = 1;
	
	public RuleEvaluationSummary(Rule rule)
	{
		this.rule = rule;
	}
	
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
		builder.append("Coverage(%) : ").append(df.format(getCoverage())).append("\t");
		builder.append("Fold frequency(%) : ").append(df.format(getFoldFrequency()));
		
		return builder.toString();
	}
	
	public String explainRule()
	{
		DecimalFormat df = Constants.FORMATTER;
		StringBuilder builder = new StringBuilder();
		builder.append("RULE : ").append(getRule().toString()).append("\n");
		builder.append(df.format(getPrecision())).append("% of POSITIVE predictions by this rule are correct.").append("\n");
		builder.append(df.format(getCoverage())).append("% positive examples are correctly covered by this rule.").append("\n");
		builder.append(df.format(getFoldFrequency())).append("% cross-validation folds generate this POSITIVE rule.").append("\n");
		
		return builder.toString();
	}
	
	public String showRuleStats()
	{
		DecimalFormat df = Constants.FORMATTER;
		StringBuilder builder = new StringBuilder();
		builder.append("( ")
			.append(df.format(getPrecision())).append("%, ")
			.append(df.format(getCoverage())).append("%, ")
			.append(df.format(getFoldFrequency())).append("% ")
			.append(" )");
		builder.append(" : ").append(getRule().toString());
		
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
	
	public double getCoverage()
	{
		return (correctPositivePredictions/(double)totalPositives)*100;
	}
	
	public double getFoldFrequency()
	{
		return (getNumOccurrenceAcrossNFolds()/(double)Constants.NUM_CV_FOLDS)*100;
	}

	public int getNumOccurrenceAcrossNFolds() {
		return numOccurrenceAcrossNFolds;
	}

	public void setNumOccurrenceAcrossNFolds(int numOccurrenceAcrossNFolds) {
		this.numOccurrenceAcrossNFolds = numOccurrenceAcrossNFolds;
	}
}
