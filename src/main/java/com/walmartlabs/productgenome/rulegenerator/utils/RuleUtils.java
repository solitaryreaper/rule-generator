package com.walmartlabs.productgenome.rulegenerator.utils;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

/**
 * A utility class for effective management and post-processing of entity matching rules.
 * @author excelsior
 *
 */
public class RuleUtils {
	
	/**
	 * Compresses similar rules to create a single rule with the same effect.
	 * @param rules
	 * @return
	 */
	public static List<Rule> compressRules(List<Rule> rules)
	{
		List<Rule> compressedRules = Lists.newArrayList();
		
		// Collect similar rules in buckets.
		Map<String, List<Rule>> simRulesMap = Maps.newHashMap();
		for(Rule rule : rules) {
			String ruleKey = getRuleClausesString(rule);
			List<Rule> similarRules = null;
			if(simRulesMap.containsKey(ruleKey)) {
				similarRules = simRulesMap.get(ruleKey);
			}
			else {
				similarRules = Lists.newArrayList();
			}
			
			similarRules.add(rule);
			simRulesMap.put(ruleKey, similarRules);
		}
		
		// Compress each bucket to emit minimum representative rules.
		for(Map.Entry<String, List<Rule>> entry : simRulesMap.entrySet()) {
			List<Rule> rulesToCompress = entry.getValue();
			List<Rule> minEquivalentRules = getMinimumEquivalentRules(rulesToCompress);
			compressedRules.addAll(minEquivalentRules);			
		}
		
		return compressedRules;
	}
	
	private static List<Rule> getMinimumEquivalentRules(List<Rule> rulesToCompress)
	{
		Map<String, Clause> newClausesMap = Maps.newHashMap();
		for(Rule rule : rulesToCompress) {
			for(Clause clause : rule.getClauses()) {
				String featureName = clause.getFeatureName();
				if(newClausesMap.containsKey(featureName)) {
					Clause existingClause = newClausesMap.get(featureName);
					LogicalOperator logop = clause.getLogOp();
					double oldThreshold = existingClause.getThreshold();
					double currThreshold = clause.getThreshold();
					
					double newThreshold = -1.0;
					if(logop.equals(LogicalOperator.GREATER_THAN_EQUALS) || logop.equals(LogicalOperator.GREATER_THAN)) {
						newThreshold = Math.min(oldThreshold, currThreshold);
					}
					else if(logop.equals(LogicalOperator.LESS_THAN) || logop.equals(LogicalOperator.LESS_THAN_EQUALS)) {
						newThreshold = Math.max(oldThreshold, currThreshold);
					}
					
					if(Double.compare(newThreshold, oldThreshold) != 0) {
						existingClause.setThreshold(newThreshold);
						existingClause.setLogOp(logop);
					}
					
					newClausesMap.put(featureName, existingClause);
				}
				else {
					newClausesMap.put(featureName, new Clause(clause));
				}
			}
		}
		
		List<Clause> newClauses = Lists.newArrayList(newClausesMap.values());
		Rule newRule = new Rule(newClauses, MatchStatus.MATCH);
		return Lists.newArrayList(newRule);
	}
	
	private static String getRuleClausesString(Rule rule)
	{
		StringBuilder builder = new StringBuilder();
		for(Clause clause : rule.getClauses()) {
			builder.append(clause.getFeatureName()).append(clause.getLogOp().getLopOpAbbrev()).append("_");
		}
		
		return builder.toString();
	}

}
