package com.walmartlabs.productgenome.rulegenerator.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class WekaUtilsTest {

	@Test
	public void testCreateWekaInstance()
	{
		ArrayList<Attribute> atts = new ArrayList<Attribute>(2);
		atts.add(new Attribute("weight"));
		atts.add(new Attribute("length"));
		atts.add(new Attribute("crap"));
		
		List<String> classVal = Lists.newArrayList();
		classVal.add("A");
		classVal.add("B");
		atts.add(new Attribute("class",classVal));
		
		Instances dataRaw = new Instances("Test", atts, 0);
		
		double[] values = new double[4];
		values[0] = 3.0;
		values[1] = 13.0;
		values[2] = 43.0;
		values[3] = 1;
		
		Instance inst = new DenseInstance(1.0, values);
		dataRaw.add(inst);
		dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
		
		inst.setDataset(dataRaw);
		
//		String val = inst.stringValue(1);
//		System.out.println("Value : " + val);

		
//
//		
//		System.out.println(dataRaw);
//
//		System.out.println(dataRaw.classIndex());
//		System.out.println(dataRaw.get(0).stringValue(1));
//		
		Enumeration<Attribute> e = dataRaw.enumerateAttributes();
		while(e.hasMoreElements()) {
			Attribute param = (Attribute) e.nextElement();
			System.out.println(param);
		}
	}
}
