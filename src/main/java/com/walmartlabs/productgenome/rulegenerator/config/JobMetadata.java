package com.walmartlabs.productgenome.rulegenerator.config;

import java.util.List;

import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.RuleLearner;

/**
 * Contains the various metadata and configuration parameters for a single run of entity matching
 * against the specified dataset.
 * 
 * @author excelsior
 *
 */
public class JobMetadata {
	private String name;
	private String description;
	
	private String sourceFile;
	private String targetFile;
	private String goldFile;
	
	private List<String> attributesToEvaluate;
	private List<String> setValuedAttributes;
	
	private String columnDelimiter = Constants.DEFAULT_COLUMN_DELIMITER;
	private String setValueDelimiter = Constants.DEFAULT_SET_VALUE_ATTRIBIUTE_DELIMITER;
	
	private double desiredFScore;
	private double desiredPrecision;
	private double desiredRecall;
	
	private RuleLearner learner = RuleLearner.RandomForest;
	private int crossValidations = -1;
	
	public JobMetadata()
	{
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		if(this.description == null) {
			return "NA";
		}
		
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSourceFile() {
		return sourceFile;
	}
	
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public String getTargetFile() {
		return targetFile;
	}
	
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}
	
	public String getGoldFile() {
		return goldFile;
	}
	
	public void setGoldFile(String goldFile) {
		this.goldFile = goldFile;
	}
	
	public List<String> getAttributesToEvaluate() {
		return attributesToEvaluate;
	}
	
	public void setAttributesToEvaluate(String attributes) {
		String[] attributesAsArray = attributes.split(",");
		
		List<String> attributesAsList = Lists.newArrayList();
		for(String attribute : attributesAsArray) {
			attributesAsList.add(attribute.trim());
		}
		
		this.attributesToEvaluate = attributesAsList;
	}
	
	public List<String> getSetValuedAttributes() {
		return setValuedAttributes;
	}
	
	public void setSetValuedAttributes(String attributes) {
		String[] attributesAsArray = attributes.split(",");
		
		List<String> setValuedAttributesAsList = Lists.newArrayList();
		for(String attribute : attributesAsArray) {
			setValuedAttributesAsList.add(attribute.trim());
		}
		
		this.setValuedAttributes = setValuedAttributesAsList;
	}
	
	public String getColumnDelimiter() {
		return columnDelimiter;
	}
	
	public void setColumnDelimiter(String columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
	}
	
	public String getSetValueDelimiter() {
		return setValueDelimiter;
	}
	
	public void setSetValueDelimiter(String setValueDelimiter) {
		this.setValueDelimiter = setValueDelimiter;
	}
	
	public double getDesiredFScore() {
		return desiredFScore;
	}
	
	public void setDesiredFScore(String desiredFScore) {
		this.desiredFScore = Double.parseDouble(desiredFScore);
	}
	
	public double getDesiredPrecision() {
		return desiredPrecision;
	}
	
	public void setDesiredPrecision(String desiredPrecision) {
		this.desiredPrecision = Double.parseDouble(desiredPrecision);
	}
	
	public double getDesiredRecall() {
		return desiredRecall;
	}
	
	public void setDesiredRecall(String desiredRecall) {
		this.desiredRecall = Double.parseDouble(desiredRecall);
	}
	
	public RuleLearner getLearner() {
		return learner;
	}
	
	public void setLearner(String learner) {
		this.learner = RuleLearner.getRuleLearner(learner);
	}
	
	public int getCrossValidations() {
		if(this.crossValidations == -1) {
			return Constants.NUM_CV_FOLDS;
		}
		
		return crossValidations;
	}
	
	public void setCrossValidations(int crossValidations) {
		this.crossValidations = crossValidations;
	}
}