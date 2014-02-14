package com.walmartlabs.productgenome.rulegenerator.algos;

import java.text.DecimalFormat;
import java.util.List;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Clause.LogicalOperator;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TestClass {

	public static void main(String[] args) throws Exception
	{
		Clause a = new Clause("abc", LogicalOperator.EQUALS, 0.837);
		Clause b = new Clause("abc", LogicalOperator.EQUALS, 0.837);
		
		Clause c = new Clause("def", LogicalOperator.EQUALS, 0.837);
		Clause d = new Clause("def", LogicalOperator.EQUALS, 0.837);
		
		List<Clause> ac = Lists.newArrayList(a, c);
		List<Clause> bc = Lists.newArrayList(b, d);
		
		Rule one = new Rule(Lists.newArrayList(a), MatchStatus.MATCH);
		Rule two = new Rule(Lists.newArrayList(b), MatchStatus.MATCH);
		
		System.out.println(one.hashCode());
		System.out.println(two.hashCode());
		
		System.out.println(ac.hashCode());
		System.out.println(bc.hashCode());
		if(ac.equals(bc)) {
			System.out.println("Equals works for same lists ..");
		}
		
		System.out.println(one.getName().hashCode());
		System.out.println(two.getName().hashCode());
		
		System.out.println(one.getLabel().hashCode());
		System.out.println(two.getLabel().hashCode());
		
		if(one.equals(two)) {
			System.out.println("Yes ..");
		}
		else {
			System.out.println("No ..");
		}
		
		if(ac.equals(bc)) {
			System.out.println("same hashcode for lists ..");
		}
		else {
			System.out.println("diff hashcode for lists ..");
		}
	}
}
