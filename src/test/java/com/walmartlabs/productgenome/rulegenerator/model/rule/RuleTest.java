package com.walmartlabs.productgenome.rulegenerator.model.rule;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;

public class RuleTest {

	@Test
	public void testRuleEquality()
	{
		List<Clause> clausesA = Lists.newArrayList(
				new Clause("name_addr", LogicalOperator.EQUALS, 0.337),
				new Clause("name_jaro_winkler", LogicalOperator.EQUALS, 0.334)
			);
		List<Clause> clausesB = Lists.newArrayList(
				new Clause("name_jaro_winkler", LogicalOperator.EQUALS, 0.334),
				new Clause("name_addr", LogicalOperator.EQUALS, 0.337)
			);
		
		Rule ruleA = new Rule(clausesA, MatchStatus.MATCH);
		Rule ruleB = new Rule(clausesB, MatchStatus.MATCH);
		
		if(ruleA.equals(ruleB)) {
			System.out.println("Equal ..");
		}
		else {
			System.out.println("Not equal ..");
		}
		System.out.println(ruleA.getName());
		System.out.println(ruleB.getName());
	}
}
