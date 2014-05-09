package com.walmartlabs.productgenome.rulegenerator.utils;

public class DatasetUtils {

	public static int getItemPairId(String item1Id, String item2Id)
	{
		String id = item1Id + "#" + item2Id;
		return id.replace(" ", "").trim().hashCode();
	}
}
