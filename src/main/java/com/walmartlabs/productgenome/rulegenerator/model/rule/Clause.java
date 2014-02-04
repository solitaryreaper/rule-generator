package com.walmartlabs.productgenome.rulegenerator.model.rule;

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
		EQUALS,
		GREATER_THAN,
		GREATER_THAN_EQUALS,
		LESS_THAN,
		LESS_THAN_EQUALS,
		NOT_EQUALS
	}
	
	private String featureName;
	private LogicalOperator logOp;
	private double threshold;
}
