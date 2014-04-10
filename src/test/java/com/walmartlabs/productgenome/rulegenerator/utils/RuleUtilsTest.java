package com.walmartlabs.productgenome.rulegenerator.utils;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class RuleUtilsTest {

	@Test
	public void testCompressRules1()
	{
		List<Rule> rules = Lists.newArrayList();
		
		List<Clause> clausesA = Lists.newArrayList();
		clausesA.add(new Clause("addr_cosine", LogicalOperator.GREATER_THAN_EQUALS, 0.5));
		clausesA.add(new Clause("name_euclidean ", LogicalOperator.GREATER_THAN_EQUALS, 0.7));
		
		List<Clause> clausesB = Lists.newArrayList();
		clausesB.add(new Clause("addr_cosine", LogicalOperator.GREATER_THAN_EQUALS, 0.4));
		clausesB.add(new Clause("name_euclidean ", LogicalOperator.GREATER_THAN_EQUALS, 0.6));		
		
		Rule ruleA = new Rule(clausesA, MatchStatus.MATCH);
		Rule ruleB = new Rule(clausesB, MatchStatus.MATCH);
		rules.add(ruleA);
		rules.add(ruleB);
		
		List<Rule> compressedRules = RuleUtils.compressRules(rules);
		assertNotNull(compressedRules);
		assertTrue(compressedRules.size() == 1);
	}
	
	@Test
	public void testCompressRules2()
	{
		List<Rule> rules = Lists.newArrayList();
		
		List<Clause> clausesA = Lists.newArrayList();
		clausesA.add(new Clause("addr_cosine", LogicalOperator.GREATER_THAN_EQUALS, 0.5));
		clausesA.add(new Clause("addr_cosine ", LogicalOperator.GREATER_THAN_EQUALS, 0.7));
		
		Rule ruleA = new Rule(clausesA, MatchStatus.MATCH);
		rules.add(ruleA);
		
		List<Rule> compressedRules = RuleUtils.compressRules(rules);
		assertNotNull(compressedRules);
		assertTrue(compressedRules.size() == 1);		
	}
	
	@Test
	public void testCompressRules3()
	{
		List<Rule> rules = Lists.newArrayList();
		
		List<Clause> clausesA = Lists.newArrayList();
		clausesA.add(new Clause("addr_cosine", LogicalOperator.GREATER_THAN_EQUALS, 0.5));
		clausesA.add(new Clause("name_euclidean ", LogicalOperator.GREATER_THAN_EQUALS, 0.7));
		
		List<Clause> clausesB = Lists.newArrayList();
		clausesB.add(new Clause("addr_cosine", LogicalOperator.GREATER_THAN_EQUALS, 0.4));
		clausesB.add(new Clause("addr_euclidean ", LogicalOperator.GREATER_THAN_EQUALS, 0.6));		
		
		Rule ruleA = new Rule(clausesA, MatchStatus.MATCH);
		Rule ruleB = new Rule(clausesB, MatchStatus.MATCH);
		rules.add(ruleA);
		rules.add(ruleB);
		
		List<Rule> compressedRules = RuleUtils.compressRules(rules);
		assertNotNull(compressedRules);
		assertTrue(compressedRules.size() == 2);
	}	
	
	@Test
	public void testCompressRules4()
	{
		List<Rule> rules = Lists.newArrayList();
		
		List<Clause> clausesA = Lists.newArrayList();
		clausesA.add(new Clause("name_jaro_winkler", LogicalOperator.GREATER_THAN_EQUALS, 0.86));
		
		List<Clause> clausesB = Lists.newArrayList();
		clausesB.add(new Clause("name_jaro_winkler", LogicalOperator.GREATER_THAN_EQUALS, 0.88));
		
		Rule ruleA = new Rule(clausesA, MatchStatus.MATCH);
		Rule ruleB = new Rule(clausesB, MatchStatus.MATCH);
		rules.add(ruleA);
		rules.add(ruleB);
		
		List<Rule> compressedRules = RuleUtils.compressRules(rules);
		assertNotNull(compressedRules);
		assertTrue(compressedRules.size() == 1);
	}		
}
