package com.walmartlabs.productgenome.rulegenerator.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.Item;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.utils.parser.ItemPairDataParser;

public class WalmartTrainingDataGeneratorTest {

	List<String> attrs = Lists.newArrayList("pd_title", "req_brand_name", "req_category", "req_color", "req_manufacturer", 
			"req_part_number", "req_upc_10", "req_upc_11", "req_upc_12", "req_upc_13", "req_upc_14");
	
	@Test
	public void testGenerateMismatchDataset() throws IOException
	{
		BiMap<String, String> schemaMap = HashBiMap.create();
		schemaMap.put("pd_title", "pd_title");
		schemaMap.put("req_brand_name", "req_brand_name");
		schemaMap.put("req_category", "req_category");
		schemaMap.put("req_color", "req_color");
		schemaMap.put("req_manufacturer", "req_manufacturer");
		schemaMap.put("req_part_number", "req_part_number");
		schemaMap.put("req_upc_10", "req_upc_10");
		schemaMap.put("req_upc_11", "req_upc_11");
		schemaMap.put("req_upc_12", "req_upc_12");
		schemaMap.put("req_upc_13", "req_upc_13");
		schemaMap.put("req_upc_14", "req_upc_14");
		
		File itemPairDataFile = new File(Constants.DATA_FILE_PATH_PREFIX + "datasets/WALMART-DATA/CNET_DOTCOM_MATCHED_CLEANED.txt");
		ItemPairDataParser parser = new ItemPairDataParser();
		Dataset matchSet = parser.parseFile("CNET-Dotcom", itemPairDataFile, MatchStatus.MISMATCH, schemaMap);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/cnet_dotcom_mismatch_synthetic.txt"));
		
		System.out.println("#ItemPairs " + matchSet.getItemPairs().size());
		for(ItemPair exPair : matchSet.getItemPairs()) {
			int count = 0;
			for(ItemPair inPair : matchSet.getItemPairs()) {
				if(inPair.equals(exPair)) {
					continue;
				}
				
				if(count > 5) {
					break;
				}
				
				Item exItemA = exPair.getItemA();
				Item exItemB = exPair.getItemB();
				
				Item inItemA = inPair.getItemA();
				Item inItemB = inPair.getItemB();
				
				Item srcItem = null;
				Item tgtItem = null;
				
				if(NumberUtils.isNumber(exItemA.getId())) {
					srcItem = exItemA;
				}
				else {
					srcItem = exItemB;
				}
				
				if(!NumberUtils.isNumber(inItemA.getId())) {
					tgtItem = inItemA;
				}
				else {
					tgtItem = inItemB;
				}
				
				insertNewItemPair(srcItem, tgtItem, bw);
				bw.newLine();
				
				++count;
			}
		}
		
		bw.close();
	}
	
	private void insertNewItemPair(Item itemA, Item itemB, BufferedWriter bw) throws IOException
	{
		bw.write("ID|#" + itemA.getId() + "|#" + itemB.getId() + "|#"); bw.newLine();
		bw.write("Source|#WALMART_DOTCOM|#CNET|#"); bw.newLine();
		
		for(String attr : attrs) {
			String valA = itemA.getValuesForAttr(attr);
			String valB = itemB.getValuesForAttr(attr);
			
			if(valA == null) {
				valA = "null";
			}
			if(valB == null) {
				valB = "null"; 
			}
			bw.write(attr + "|#" + valA + "|#" + valB + "|#"); bw.newLine();
		}
	}
}
