package com.walmartlabs.productgenome.rulegenerator.model.rule;

import java.util.List;

import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

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
	private MatchStatus label;
	
	public Rule(String name, List<Clause> clauses, MatchStatus label) {
		super();
		this.name = name;
		this.clauses = clauses;
		this.label = label;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rule [name=").append(name).append(", clauses=")
				.append(clauses).append(", label=").append(label).append("]");
		return builder.toString();
	}

	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	public List<Clause> getClauses() {
		return clauses;
	}
	public void setClauses(List<Clause> clauses) {
		this.clauses = clauses;
	}
	public MatchStatus getLabel() {
		return label;
	}
	public void setLabel(MatchStatus label) {
		this.label = label;
	}
	
	
}
