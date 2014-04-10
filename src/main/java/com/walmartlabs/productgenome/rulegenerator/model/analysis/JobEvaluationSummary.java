package com.walmartlabs.productgenome.rulegenerator.model.analysis;

/**
 * A summary of the entity matching job run, containing detailed information about both the training
 * and testing phase.
 * @author excelsior
 *
 */
public class JobEvaluationSummary {
	private DatasetEvaluationSummary trainPhaseSumary;
	private DatasetEvaluationSummary tunePhaseSummary;
	private DatasetEvaluationSummary testPhaseSummary;
	
	public JobEvaluationSummary(DatasetEvaluationSummary trainPhaseSummary, DatasetEvaluationSummary tunePhaseSummary, 
			DatasetEvaluationSummary testPhaseSummary)
	{
		this.trainPhaseSumary = trainPhaseSummary;
		this.tunePhaseSummary = tunePhaseSummary;
		this.testPhaseSummary = testPhaseSummary;
	}

	public DatasetEvaluationSummary getTrainPhaseSumary() 
	{
		return trainPhaseSumary;
	}

	public DatasetEvaluationSummary getTunePhaseSummary()
	{
		return tunePhaseSummary;
	}
	
	public DatasetEvaluationSummary getTestPhaseSummary() {
		return testPhaseSummary;
	}
	
	
}
