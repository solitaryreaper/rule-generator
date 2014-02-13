package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.Item;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

public class CSVDataParser implements DataParser {

	public Dataset parseData(File matchFile, File mismatchFile,
			String datasetName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Dataset parseData(String datasetName, File sourceFile, File targetFile, File goldFile) 
	{
		Map<String, String> goldMap = getGoldenDataMap(goldFile);
		List<Item> sourceItems = getItems(sourceFile);
		List<Item> targetItems = getItems(targetFile);

		List<ItemPair> itemPairs = Lists.newArrayList();
		for(Item srcItem : sourceItems) {
			for(Item tgtItem : targetItems) {
				MatchStatus matchStatus = MatchStatus.MISMATCH;
				if(goldMap.containsKey(srcItem.getId())) {
					if(tgtItem.getId().equals(goldMap.get(srcItem.getId()))) {
						matchStatus = MatchStatus.MATCH;						
					}
				}
				ItemPair itemPair = new ItemPair(srcItem, tgtItem, matchStatus);
				itemPairs.add(itemPair);
			}
		}

		List<String> attributes = Lists.newArrayList(sourceItems.get(0).getAttrMap().keySet());
		return new Dataset(datasetName, attributes, itemPairs);
	}
	
	private Map<String, String> getGoldenDataMap(File goldFile)
	{
		Map<String, String> goldMap = Maps.newHashMap();
		BufferedReader br = null; 
		try {
 			String currLine; 	
 			br = new BufferedReader(new FileReader(goldFile));
 			boolean isHeaderRead = false;
			while ((currLine = br.readLine()) != null) {
				if(!isHeaderRead) {
					isHeaderRead = true;
				}
				
				String[] tokens = currLine.split(",");
				goldMap.put(tokens[0].trim(), tokens[1].trim());
			}
			br.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return goldMap;
	}
	
	private List<Item> getItems(File dataFile)
	{
		List<Item> items = Lists.newArrayList();
		return items;
	}

}
