package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;

/**
 * Parses the textual rules to generated fully decorated in-memory rule objects.
 * 
 * @author skprasad
 *
 */
public class RuleParser {

	private static Logger LOG = Logger.getLogger(RuleParser.class.getName());
	
	private static final String RULE_BEGIN_IDENTIFIER = "if";
	private static final String CLAUSES_END_IDENTIFIER = "then";
	private static final String INTER_CLAUSE_SEPARATOR = "and";
	private static final String INTRA_CLAUSE_SEPARATOR = " ";
	
	/**
	 * Parses the textual rules to generate in-memory rule objects.
	 * 
	 * @param textRules
	 * @return
	 */
	public static List<Rule> parseRules(List<String> textRules)
	{
		List<Rule> rules = Lists.newArrayList();
		for(String rule : textRules) {
			Rule ruleObj = parseRule(rule);
			if(ruleObj != null) {
				rules.add(ruleObj);
			}
		}
		
		return rules;
	}
	
	private static Rule parseRule(String rule)
	{
		rule = rule.toLowerCase();
		int clausesBeginIndex = rule.indexOf(RULE_BEGIN_IDENTIFIER) + RULE_BEGIN_IDENTIFIER.length() + 1;
		int clausedEndIndex = rule.indexOf(CLAUSES_END_IDENTIFIER) - 1;
		if(clausesBeginIndex >= clausedEndIndex) {
			LOG.warning("No clause present for rule : " + rule);
			return null;
		}
		
		String allClausesString = rule.substring(clausesBeginIndex, clausedEndIndex).trim();
		
		int labelStartIndex = rule.indexOf(CLAUSES_END_IDENTIFIER) + CLAUSES_END_IDENTIFIER.length() + 1; 
		int labelEndIndex = rule.indexOf("(") - 1;
		String labelString = rule.substring(labelStartIndex, labelEndIndex).trim();
		
		List<Clause> clauses = Lists.newArrayList();
		String[] textClauses = allClausesString.split(INTER_CLAUSE_SEPARATOR);
		for(String textClause : textClauses) {
			textClause = textClause.trim();
			String[] clauseParts = textClause.split(INTRA_CLAUSE_SEPARATOR);
			
			String featureName = clauseParts[0].trim();
			LogicalOperator logOp = LogicalOperator.getLogicalOperator(clauseParts[1].trim());
			double threshold = Double.parseDouble(clauseParts[2].trim());
			clauses.add(new Clause(featureName, logOp, threshold));
		}
		
		MatchStatus label = MatchStatus.getMatchStatus(labelString);
		
		return new Rule(clauses, label);
		
	}
}
