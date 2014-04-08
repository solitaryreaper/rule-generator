package com.walmartlabs.productgenome.rulegenerator.utils;

import java.text.DecimalFormat;

/**
 * Simple utility class for formatting various data formats.
 * 
 * @author excelsior
 *
 */
public class FormatUtils {

	private static DecimalFormat df = new DecimalFormat("#.00"); 
	
	public static String formatDoubleToString(double value)
	{
		return df.format(value);
	}
}
