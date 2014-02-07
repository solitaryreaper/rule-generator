package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class RuleParserTest {
	private static Logger LOG = Logger.getLogger(RuleParserTest.class.getName());


	@Test
	public void testParseRule()
	{
		List<String> textRules = Lists.newArrayList("IF thal = 0.2 AND trestbps > 108 AND cp = 0.9 THEN match (4.0/1.0)");
		List<Rule> rules = RuleParser.parseRules(textRules);
		
		assertNotNull(rules);
		assertFalse(rules.isEmpty());
		LOG.info("Generated rules : " + rules.toString());
	}
}
