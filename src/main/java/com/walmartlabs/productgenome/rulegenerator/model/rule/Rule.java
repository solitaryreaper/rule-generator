package com.walmartlabs.productgenome.rulegenerator.model.rule;

import java.util.List;

/**
 * Represents a model class for a matching rule.
 * 
 * A rule is a conjunction of clauses.
 * 
 * @author excelsior
 *
 */
public class Rule {

	private String name;
	private List<Clause> clauses;
}
