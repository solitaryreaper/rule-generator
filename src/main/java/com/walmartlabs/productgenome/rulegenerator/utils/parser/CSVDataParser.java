package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.Item;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

public class CSVDataParser implements DataParser {

	private static Logger LOG = Logger.getLogger(CSVDataParser.class.getName());
	
	private static int ID_ATTR_INDEX = 0;
	
	private static String ATTRIBUTES_KEY = "attributes";
	private static String ITEMS_KEY = "items";
	
	public Dataset parseData(File matchFile, File mismatchFile,
			String datasetName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Dataset parseData(String datasetName, File sourceFile, File targetFile, File goldFile) 
	{
		Multimap<String, String> goldMap = getGoldenDataMap(goldFile);
		
		Map<String, List<?>> sourceResultMap = getItems(sourceFile);
		Map<String, List<?>> targetResultMap = getItems(targetFile);
		
		
		List<Item> sourceItems = (List<Item>)sourceResultMap.get(ITEMS_KEY);
		List<Item> targetItems = (List<Item>)targetResultMap.get(ITEMS_KEY);

		Set<String> sourceAttributes = Sets.newHashSet((List<String>)sourceResultMap.get(ATTRIBUTES_KEY));
		Set<String> targetAttributes = Sets.newHashSet((List<String>)targetResultMap.get(ATTRIBUTES_KEY));
		List<String> attributes = Lists.newArrayList(Sets.intersection(sourceAttributes, targetAttributes));
		LOG.info("Common attributes : " + attributes.toString());
		
		List<ItemPair> itemPairs = Lists.newArrayList();
		for(Item srcItem : sourceItems) {
			for(Item tgtItem : targetItems) {
				MatchStatus matchStatus = MatchStatus.MISMATCH;
				if(goldMap.containsEntry(srcItem.getId(), tgtItem.getId())) {
					matchStatus = MatchStatus.MATCH;
				}
				ItemPair itemPair = new ItemPair(srcItem, tgtItem, matchStatus);
				itemPairs.add(itemPair);
			}
		}

		return new Dataset(datasetName, attributes, itemPairs);
	}
	
	/**
	 * Fetch the golden data map. Golden data is the set of itempairs that do actually match.
	 * @param goldFile
	 * @return
	 */
	private Multimap<String, String> getGoldenDataMap(File goldFile)
	{
		Multimap<String, String> goldMap = ArrayListMultimap.create();
		CSVReader reader = null;
		try {
 			String[] currLineTokens;
 			reader = new CSVReader(new FileReader(goldFile));
 			boolean isHeaderRead = false;
			while ((currLineTokens = reader.readNext()) != null) {
				if(!isHeaderRead) {
					isHeaderRead = true;
					continue;
				}
				
				goldMap.put(currLineTokens[0].trim(), currLineTokens[1].trim());
			}
			reader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return goldMap;
	}
	
	/**
	 * Returns the list of items in the dataset file.
	 * @param dataFile
	 * @return
	 */
	private Map<String, List<?>> getItems(File dataFile)
	{
		List<Item> items = Lists.newArrayList();
		CSVReader reader = null;
		List<String> attributes = Lists.newArrayList();
		try {
 			String[] currLineTokens;
 			reader = new CSVReader(new FileReader(dataFile));
 			boolean isHeaderRead = false;
			while ((currLineTokens = reader.readNext()) != null) {
				if(!isHeaderRead) {
					isHeaderRead = true;
					for(String header : currLineTokens) {
						attributes.add(header.trim());
					}
					continue;
				}
				
				try {
					Map<String, String> itemAttrMap = Maps.newHashMap();
					for(int i=0; i < currLineTokens.length; i++) {
						// No need to put garbage values in map.
						if(Strings.isNullOrEmpty(currLineTokens[i])) {
							continue;
						}
						itemAttrMap.put(attributes.get(i), currLineTokens[i]);
					}
					String id = currLineTokens[ID_ATTR_INDEX].trim();
					items.add(new Item(id, itemAttrMap));					
				}
				catch(Exception e) {
					LOG.severe("Failed to parse item : " + Arrays.toString(currLineTokens));
				}

			}
			reader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}	
		
		Map<String, List<?>> resultMap = Maps.newHashMap();
		resultMap.put(ATTRIBUTES_KEY, attributes);
		resultMap.put(ITEMS_KEY, items);
		
		return resultMap;
	}

}
