package com.walmartlabs.productgenome.rulegenerator.model.rule;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Represents a model class for a clause.
 * 
 * A clause is the smallest unit of a rule. The format of a clause is as follows :
 * <feature name> <logop> <threshold> e.g. name_lev >= 0.8
 * 
 * @author excelsior
 *
 */
public class Clause implements Comparable<Clause> {

	/**
	 * List of valid logical operators.
	 * @author excelsior
	 *
	 */
	public static enum LogicalOperator {
		EQUALS("="),
		GREATER_THAN(">"),
		GREATER_THAN_EQUALS(">="),
		LESS_THAN("<"),
		LESS_THAN_EQUALS("<="),
		NOT_EQUALS("!=");
		
		private String lopOpAbbrev;
		
		private LogicalOperator(String logOpAbbrev)
		{
			this.lopOpAbbrev = logOpAbbrev;
		}
		
		public String getLopOpAbbrev() {
			return lopOpAbbrev;
		}

		public static LogicalOperator getLogicalOperator(String logOpAbbrev)
		{
			LogicalOperator logOp = null;
			for(LogicalOperator currLogOp : LogicalOperator.values()) {
				if(currLogOp.lopOpAbbrev.toLowerCase().equals(logOpAbbrev.toLowerCase())) {
					logOp = currLogOp;
					break;
				}
			}
			
			return logOp;
		}
	}
	
	private String featureName;
	private LogicalOperator logOp;
	private double threshold;
	
	public Clause(String featureName, LogicalOperator logOp, double threshold) {
		super();
		this.featureName = featureName.trim();
		this.logOp = logOp;
		this.threshold = threshold;
	}
	
	public Clause(Clause clause)
	{
		super();
		this.featureName = clause.getFeatureName();
		this.logOp = clause.getLogOp();
		this.threshold = clause.getThreshold();
	}
	
	public int hashCode()
	{
		return Objects.hashCode(this.featureName, this.logOp, this.threshold);
	}
	
	public boolean equals(Object obj)
	{
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
	    final Clause that = (Clause) obj;
	    return 	Objects.equal(this.featureName, that.featureName) && 
	    		Objects.equal(this.logOp, that.logOp) &&
	    		Objects.equal(this.threshold, that.threshold);		
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" ").append(featureName).append(" ").append(logOp.getLopOpAbbrev()).append(" ").append(threshold);
		return builder.toString();
	}

	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName.trim();
	}
	public LogicalOperator getLogOp() {
		return logOp;
	}
	public void setLogOp(LogicalOperator logOp) {
		this.logOp = logOp;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int compareTo(Clause that) {
		return ComparisonChain.start()
		        .compare(this.featureName, that.featureName)
		        .compare(this.logOp, that.logOp)
		        .compare(this.threshold, that.threshold)
		        .result();		
	}
	
}
