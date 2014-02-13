package com.walmartlabs.productgenome.rulegenerator.model.rule;

import java.util.List;

import com.google.common.base.Objects;
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

	public Rule(List<Clause> clauses, MatchStatus label) {
		super();
		this.clauses = clauses;
		this.label = label;
	}
	
	public int hashCode()
	{
		return Objects.hashCode(this.name, this.clauses, this.label);
	}
	
	public boolean equals(Object obj)
	{
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
	    final Rule that = (Rule) obj;
	    return 	Objects.equal(this.name, that.name) && 
	    		Objects.equal(this.clauses, that.clauses) &&
	    		Objects.equal(this.label, that.label);		
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" IF ");
		
		StringBuilder clauseBuilder = new StringBuilder();
		for(Clause clause : clauses) {
			clauseBuilder.append(clause.toString()).append(" AND ");
		}
		String clauseString = clauseBuilder.substring(0, clauseBuilder.lastIndexOf("AND") - 1).trim();
		builder.append(clauseString);
		
		builder.append(" THEN ").append(label.toString().toUpperCase()).append(" ;");
		return builder.toString();
	}

	public String getName() {
		StringBuilder name = new StringBuilder();
		for(Clause c : getClauses()) {
			name.append(c.getFeatureName()).append("_");
		}
		name.append(getLabel().name().toLowerCase());
		
		return name.toString();
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
	
	public boolean isMatchingRule()
	{
		return getLabel().equals(MatchStatus.MATCH);
	}
}
