package com.walmartlabs.productgenome.rulegenerator.model.analysis;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

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
		builder.append("\n\n");
		builder.append("Total instances : ").append(totalInstances).append("\n");
		builder.append("True positives : ").append(truePositives).append("\n");
		builder.append("Predicted positives : ").append(predictedPositives).append("\n");
		builder.append("Correct predictions : ").append(correctPositivePredictions).append("\n");
		builder.append("Average Precision(%) : ").append(df.format(getPrecision())).append("\n");
		builder.append("Average Recall(%) : ").append(df.format(getRecall())).append("\n");
		
		builder.append("\n<--------------- RANKED RULES {(Precision, Coverage, Fold Frequency : Rule Definition)} ------------------->\n");
		for(RuleEvaluationSummary ruleSummary : getRankedAndFilteredRules()) {
			builder.append(ruleSummary.showRuleStats());
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
	
	public List<Rule> getAllRules()
	{
		List<Rule> rules = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : getRuleSummary()) {
			rules.add(ruleSummary.getRule());
		}
		
		return rules;
	}
	
	/**
	 * Returns a ranked list of rules. The ranking is based on the following factors :
	 * 
	 * 1) Precision
	 * 2) Recall/Coverage
	 * 3) Frequency across folds
	 * 
	 * Score = F-score(beta=0.5) + fold frequency
	 * 
	 * It also filters all rules which have a precision below the cutoff, since it is the most
	 * important factor for the rule.
	 * @return
	 */
	public List<RuleEvaluationSummary> getRankedAndFilteredRules()
	{
		List<RankedRuleSummary> rankedRules = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : getRuleSummary()) {
			// Filter all rules that don't meet precision cutoff
			if(Double.compare(ruleSummary.getPrecision(), Constants.RULE_PRECISION_CUTOFF_PERCENT) < 0) {
				continue;
			}
			
			double score = getRuleScore(ruleSummary);
			rankedRules.add(new RankedRuleSummary(score, ruleSummary));
		}
		
		Collections.sort(rankedRules);
		Collections.reverse(rankedRules);
		
		List<RuleEvaluationSummary> recommendedRules = Lists.newArrayList();
		for(RankedRuleSummary rankedRule : rankedRules) {
			recommendedRules.add(rankedRule.getRuleSummary());
		}
		
		return recommendedRules;
	}
	
	public List<Rule> getRankedRules()
	{
		List<Rule> rankedRules = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : getRankedAndFilteredRules()) {
			rankedRules.add(ruleSummary.getRule());
		}
		
		return rankedRules;
	}
	
	private double getRuleScore(RuleEvaluationSummary ruleSummary)
	{
		double score = 0.0;
		double betaSquare = Constants.BETA_F_SCORE*Constants.BETA_F_SCORE;
		double precision = ruleSummary.getPrecision();
		double recall = ruleSummary.getCoverage();
		
		double fscore = ((1 + betaSquare)* precision*recall)/(betaSquare*precision + recall);
		
		score = fscore + ruleSummary.getFoldFrequency()/100;
		return score;
	}
	
	public static class RankedRuleSummary implements Comparable<RankedRuleSummary>
	{
		private double score;
		private RuleEvaluationSummary ruleSummary;
		
		public RankedRuleSummary(double score, RuleEvaluationSummary ruleSummary) {
			super();
			this.score = score;
			this.ruleSummary = ruleSummary;
		}

		public double getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public RuleEvaluationSummary getRuleSummary() {
			return ruleSummary;
		}

		public void setRuleSummary(RuleEvaluationSummary ruleSummary) {
			this.ruleSummary = ruleSummary;
		}

		public int compareTo(RankedRuleSummary that) {
			return Double.compare(this.score, that.score);
		}
		
	}
}
