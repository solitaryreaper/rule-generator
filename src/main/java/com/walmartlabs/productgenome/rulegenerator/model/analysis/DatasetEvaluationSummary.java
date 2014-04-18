package com.walmartlabs.productgenome.rulegenerator.model.analysis;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;
import com.walmartlabs.productgenome.rulegenerator.utils.RuleUtils;

public class DatasetEvaluationSummary {
	
	private static final Logger LOG = Logger.getLogger(DatasetEvaluationSummary.class.getName());
			
	private int totalInstances = 0;
	private int truePositives = 0; // Total number of matched itempairs in dataset
	private int predictedPositives = 0;	// Total number of itempairs predicted as matched
	private int correctPositivePredictions = 0;	// Total number of itempairs predicted as matched and are actually matched
	
	private double reqdRuleCoverage = Constants.RULE_COVERAGE_CUTOFF_PERCENT;
	private double reqdRulePrecision = Constants.RULE_PRECISION_CUTOFF_PERCENT;
	
	private List<RuleEvaluationSummary> ruleSummary;
	private Map<String, Integer> rulesetSummaryMeta;
	
	public DatasetEvaluationSummary()
	{
		
	}
	
	public DatasetEvaluationSummary(int totalInstances, int truePositives, int predictedPositives, 
			int correctPositivePredictions, List<RuleEvaluationSummary> ruleSummary, Map<String, Integer> rulesetSummaryMeta) 
	{
		super();
		this.totalInstances = totalInstances;
		this.truePositives = truePositives;
		this.predictedPositives = predictedPositives;
		this.correctPositivePredictions = correctPositivePredictions;
		this.ruleSummary = ruleSummary;
		this.rulesetSummaryMeta = rulesetSummaryMeta;
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
		builder.append("Average F-Score(%) : ").append(df.format(getFScore())).append("\n");		
		
		builder.append("\n<--------------- RANKED RULES {(Precision, Coverage, Fold Frequency : Rule Definition)} ------------------->\n");
		for(RuleEvaluationSummary ruleSummary : getRankedRuleSummaries(getRuleSummary())) {
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

	public int getTrueNegatives()
	{
		return getTotalInstances() - getTruePositives();
	}
	
	public int getFalsePositives()
	{
		return getPredictedPositives() - getTruePositives();
	}
	
	public int getFalseNegatives()
	{
		return getTotalInstances() - getPredictedPositives() - getTrueNegatives();
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
	
	public Map<String, Integer> getRulesetSummaryMeta() {
		return rulesetSummaryMeta;
	}

	public void setRulesetSummaryMeta(Map<String, Integer> rulesetSummaryMeta) {
		this.rulesetSummaryMeta = rulesetSummaryMeta;
	}

	public int getTotalRules()
	{
		return getAllRules().size();
	}
	
	public double getPrecision()
	{
		return (correctPositivePredictions/(double)predictedPositives)*100;
	}
	
	public double getRecall()
	{
		return (correctPositivePredictions/(double)truePositives)*100;		
	}
	
	public double getFScore()
	{
		return 2*getPrecision()*getRecall()/(getPrecision() + getRecall());
	}
	
	public double getReqdRuleCoverage() {
		return reqdRuleCoverage;
	}

	public void setReqdRuleCoverage(double reqdRuleCoverage) {
		this.reqdRuleCoverage = reqdRuleCoverage;
	}

	public double getReqdRulePrecision() {
		return reqdRulePrecision;
	}

	public void setReqdRulePrecision(double reqdRulePrecision) {
		this.reqdRulePrecision = reqdRulePrecision;
	}

	public List<Rule> getAllRules()
	{
		List<Rule> rules = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : getRuleSummary()) {
			rules.add(ruleSummary.getRule());
		}
		
		return RuleUtils.compressRules(rules);
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
	public List<RuleEvaluationSummary> getRankedAndFilteredRuleSummaries()
	{
		return getRankedRuleSummaries(getFilteredRuleSummaries(getRuleSummary()));
	}
	
	public List<Rule> getRankedAndFilteredRules()
	{
		List<Rule> rankedAndFilteredRules = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : getRankedAndFilteredRuleSummaries()) {
			rankedAndFilteredRules.add(ruleSummary.getRule());
		}
		
		return RuleUtils.compressRules(rankedAndFilteredRules);		
	}
	
	private List<RuleEvaluationSummary> getFilteredRuleSummaries(List<RuleEvaluationSummary> rules)
	{
		List<RuleEvaluationSummary> filteredRuleSummaries = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : getRuleSummary()) {
			// Filter all rules that don't meet precision or coverage cutoff
			if((Double.compare(ruleSummary.getPrecision(), getReqdRulePrecision()) < 0) ||
			   (Double.compare(ruleSummary.getCoverage(), getReqdRuleCoverage()) < 0))
			{
				continue;
			}
			
			filteredRuleSummaries.add(ruleSummary);
		}
		
		return filteredRuleSummaries;
	}
	
	public List<RuleEvaluationSummary> getRankedRuleSummaries(List<RuleEvaluationSummary> ruleSummaries)
	{
		List<RankedRuleSummary> rankedRules = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : ruleSummaries) {
			double score = getRuleScore(ruleSummary);
			rankedRules.add(new RankedRuleSummary(score, ruleSummary));
		}
		
		Collections.sort(rankedRules);
		Collections.reverse(rankedRules);
		
		List<RuleEvaluationSummary> rankedRuleSummaries = Lists.newArrayList();
		for(RankedRuleSummary rankedRule : rankedRules) {
			rankedRuleSummaries.add(rankedRule.getRuleSummary());
		}
		
		return rankedRuleSummaries;		
	}
	
	public List<Rule> getRankedRules()
	{
		List<Rule> rankedRules = Lists.newArrayList();
		for(RuleEvaluationSummary ruleSummary : getRankedRuleSummaries(getRuleSummary())) {
			rankedRules.add(ruleSummary.getRule());
		}
		
		return RuleUtils.compressRules(rankedRules);
	}
	
	public int getTotalRankedFilteredRules()
	{
		return getRankedAndFilteredRules().size();
	}
	
	/**
	 * Ranking function of the rule.
	 * 
	 * Most important weightage is for the f-score. 
	 * For same f-score, other important signals to consider are how many folds contained this rule,
	 * number of different attributes in the rule.
	 * 
	 * TODO : Come up with a good linear equation with apt weights. This is important. As of now just normalized all the
	 * results and assign the following weights on a scale of (1-5) based on my intuiton :
	 * 
	 *  5 - Most important - F-score, Any clauses with < or <= operator (heavily penalize it)
	 *  4 - Important - Average clause score
	 *  3 - Fold frequency, Unique attributes in clause, Number of clauses.
	 * @param ruleSummary
	 * @return
	 */
	private double getRuleScore(RuleEvaluationSummary ruleSummary)
	{
		double score = 0.0;
		double betaSquare = Constants.BETA_F_SCORE*Constants.BETA_F_SCORE;
		double precision = ruleSummary.getPrecision();
		double recall = ruleSummary.getCoverage();
		
		double fscore = ((1 + betaSquare)* precision*recall)/(betaSquare*precision + recall);
		
		score = score + 5*fscore/100;
		
		// Greater the number of folds in which a rule occurs, more representative it is of the overall data.
		score = fscore + 3*ruleSummary.getFoldFrequency()/(double)rulesetSummaryMeta.get(Constants.TOTAL_FOLDS);
		
		// More the number of unique attributes in a rule, the more tolerant is it against false positives.
		Rule rule = ruleSummary.getRule();
		score = score + 3*getUniqueAttrsInRule(rule).size()/(double)rulesetSummaryMeta.get(Constants.TOTAL_ATTRIBUTES);
		
		// Greater the number of clauses in the rule, the more tolerant is it against false positives.
		score = score + 3*rule.getClauses().size()/(double)rulesetSummaryMeta.get(Constants.MAX_NUM_CLAUSES);
		
		// Favor rules which have higher average clause score and devoid of LESS THAN operator in clauses.
		// Higher average clause score protects against false positives.
		score = score + 4*getAverageClauseScoreInRule(ruleSummary.getRule());
		
		// Punish rules which have < or <= operator in their clauses
		score = score + -5*getNumLessThanOpClauses(ruleSummary.getRule())/(double)rulesetSummaryMeta.get(Constants.MAX_NUM_CLAUSES);
				
		return score;
	}
	
	private double getAverageClauseScoreInRule(Rule rule)
	{
		double totalScore = 0.0;
		List<Clause> clauses = rule.getClauses();
		for(Clause clause : rule.getClauses()) {
			// Should discourage rules with LESS THAN operator in clauses
			if(clause.getLogOp().equals(LogicalOperator.LESS_THAN) || clause.getLogOp().equals(LogicalOperator.LESS_THAN_EQUALS)) {
				totalScore += -1* clause.getThreshold();
			}
			else {
				totalScore += 1* clause.getThreshold();
			}
		}
		
		
		return totalScore/clauses.size();
	}
	
	private double getNumLessThanOpClauses(Rule rule)
	{
		int numLessThanOpClauses = 0;
		for(Clause clause : rule.getClauses()) {
			LogicalOperator logOp = clause.getLogOp();
			if(logOp.equals(LogicalOperator.LESS_THAN) || logOp.equals(LogicalOperator.LESS_THAN_EQUALS)) {
				++numLessThanOpClauses;
			}
		}
		return numLessThanOpClauses;
	}
	
	private Set<String> getUniqueAttrsInRule(Rule rule)
	{
		Set<String> uniqueAttrs = Sets.newHashSet();
		for(Clause clause : rule.getClauses()) {
			String feature = clause.getFeatureName();
			String attrName = feature.substring(0, feature.indexOf("_"));
			uniqueAttrs.add(attrName);
		}
		
		return uniqueAttrs;
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
