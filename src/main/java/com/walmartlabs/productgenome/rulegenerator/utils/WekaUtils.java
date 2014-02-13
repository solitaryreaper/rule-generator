package com.walmartlabs.productgenome.rulegenerator.utils;

import weka.core.Attribute;
import weka.core.Instance;

import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

public class WekaUtils {

	/**
	 * Returns the class label for this instance
	 */
	public static MatchStatus getInstanceLabel(Instance instance)
	{
		return MatchStatus.getMatchStatus(instance.stringValue(instance.classIndex()));
	}
	
	/**
	 * Returns the numeric feature value for the given feature name.
	 */
	public static double getFeatureValue(Instance instance, Attribute feature)
	{
		return instance.value(feature);
	}
}
