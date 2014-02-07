package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class J48TreeParserTest {

	private static final Logger LOG = Logger.getLogger(J48TreeParserTest.class.getName());
	
	@Test
	public void testRuleGeneration()
	{
		List<String> treeDump = Lists.newArrayList();
		treeDump.add("name_jaccard <= 0.4");
		treeDump.add("|   name_jaccard <= 0.167: mismatch (1285.0)");
		treeDump.add("|   name_jaccard > 0.167");
		treeDump.add("|   |   name_smith_waterman <= 0.88: mismatch (20.0)");
		treeDump.add("|   |   name_smith_waterman > 0.88: match (7.0)");
		treeDump.add("name_jaccard > 0.4: match (106.0/1.0)");
		
		List<Rule> rules = J48TreeParser.parseJ48TreeDump(treeDump);
		assertNotNull(rules);
		LOG.info("Rules : " + rules.toString());
	}
}
