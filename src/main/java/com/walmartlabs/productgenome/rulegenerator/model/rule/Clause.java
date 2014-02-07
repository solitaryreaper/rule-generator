package com.walmartlabs.productgenome.rulegenerator.model.rule;

import com.google.common.base.Objects;

/**
 * Represents a model class for a clause.
 * 
 * A clause is the smallest unit of a rule. The format of a clause is as follows :
 * <feature name> <logop> <threshold> e.g. name_lev >= 0.8
 * 
 * @author excelsior
 *
 */
public class Clause {

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
		this.featureName = featureName;
		this.logOp = logOp;
		this.threshold = threshold;
	}
	
	public int hashcode()
	{
		return Objects.hashCode(featureName, logOp, threshold);
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
		this.featureName = featureName;
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
	
}
