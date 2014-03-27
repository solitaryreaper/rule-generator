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
import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.Item;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

public class CSVDataParser implements DataParser {

	private static Logger LOG = Logger.getLogger(CSVDataParser.class.getName());
	
	private static int ID_ATTR_INDEX = 0;
	
	public Dataset parseData(String datasetName, File matchFile, File mismatchFile, DatasetNormalizerMeta normalizerMeta) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Dataset parseData(String datasetName, File sourceFile, File targetFile, File goldFile, DatasetNormalizerMeta normalizerMeta) 
	{
		BiMap<String, String> schemaMap = normalizerMeta.getSchemaMap();
		
		Multimap<String, String> goldMap = getGoldenDataMap(goldFile);
		int goldPairs = goldMap.size();
		int totalMismatchPairsToRetain = 2*goldPairs < 3000 ? 3000 : 2*goldPairs;
		
		List<Item> sourceItems = getItems(sourceFile, schemaMap);
		List<Item> targetItems = getItems(targetFile, schemaMap);
		
		int preBlockingItemPairs = sourceItems.size() * targetItems.size();
		int postBlockingItemPairs = 0;

		List<String> attributes = Lists.newArrayList(schemaMap.keySet());
		LOG.info("Common attributes : " + attributes.toString());
		
		// If cartesian product is small, no need for blocking. match the whole set ..
		boolean isBlockingReqd = preBlockingItemPairs > 10000 ? true : false;
		
		List<ItemPair> itemPairs = Lists.newArrayList();
		int numMismatchPairsAdded = 0;
		int numMatchPairsAdded = 0;
		for(Item srcItem : sourceItems) {
			for(Item tgtItem : targetItems) {
				MatchStatus matchStatus = MatchStatus.MISMATCH;
				
				// Always add the golden pair to the final dataset to be evaluated ..
				if(goldMap.containsEntry(srcItem.getId().trim(), tgtItem.getId().trim())) {
					matchStatus = MatchStatus.MATCH;
					++numMatchPairsAdded;
					
					itemPairs.add(new ItemPair(srcItem, tgtItem, matchStatus));
					++postBlockingItemPairs;
					continue;
				}
				
				// Only apply blocking to itempairs not present in golden file ..
				if(isBlockingReqd && matchStatus.equals(MatchStatus.MISMATCH)) {
					boolean retainForMatching = shouldRetainForMatching(numMismatchPairsAdded, totalMismatchPairsToRetain);
					if(!retainForMatching) {
						continue;
					}
					else {
						++numMismatchPairsAdded;
					}
				}
				
				itemPairs.add(new ItemPair(srcItem, tgtItem, matchStatus));
				++postBlockingItemPairs;
			}
		}
		
		if(numMatchPairsAdded == 0) {
			LOG.severe("No itempairs could be added from the golden dataset. Please fix this issue first !!");
			System.exit(1);
		}

		LOG.info("Match pairs added : " + numMatchPairsAdded + ", Mismatch pairs added : " + numMismatchPairsAdded);
		LOG.info("Stats : Before blocking : " + preBlockingItemPairs + ", After blocking : " + postBlockingItemPairs);
		return new Dataset(datasetName, attributes, itemPairs);
	}
	
	/**
	 * Hack : randomly sample a fixed number of mismatch item pairs.
	 * 
	 * @param numMismatchPairsAdded
	 * @param totalMismatchPairsToAdd
	 * @return
	 */
	private boolean shouldRetainForMatching(int numMismatchPairsAdded, int totalMismatchPairsToAdd)
	{
		// Don't add any more mismatched pairs to training dataset.
		if(numMismatchPairsAdded >= totalMismatchPairsToAdd) {
			return false;
		}
		
		boolean shouldBeRetained = true;
		int min = 1;
		int max = 10;;
		int randNum = min + (int)(Math.random() * ((max - min) + 1));
		if(randNum > 50) {
			shouldBeRetained = false;
		}
		
		return shouldBeRetained;
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
	private List<Item> getItems(File dataFile, BiMap<String, String> schemaMap)
	{
		List<Item> items = Lists.newArrayList();
		CSVReader reader = null;
		List<String> attributes = Lists.newArrayList();
		String[] currLineTokens = null;
		Set<String> attributesToIgnore = Sets.newHashSet();
		
		try {
 			currLineTokens = null;
 			reader = new CSVReader(new FileReader(dataFile));
 			boolean isHeaderRead = false;
			while ((currLineTokens = reader.readNext()) != null) {
				if(!isHeaderRead) {
					isHeaderRead = true;
					for(String header : currLineTokens) {
						header = header.trim();
						
						// normalize the attribute names in source and target items, to use the same attribute name
						if(schemaMap.containsKey(header)) {
							attributes.add(header);
						}
						else if(schemaMap.containsValue(header)) {
							attributes.add(schemaMap.inverse().get(header));
						}
						else {
							attributes.add(header);
							attributesToIgnore.add(header);
						}
					}
					continue;
				}
				
				try {
					Map<String, String> itemAttrMap = Maps.newHashMap();
					
					// Skip the ID value at beginning
					for(int i=1; i < currLineTokens.length; i++) {
						// No need to put garbage values or unimportant attributes in map.
						if(Strings.isNullOrEmpty(currLineTokens[i]) || attributesToIgnore.contains(attributes.get(i))) {
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
		
		attributes.remove(ID_ATTR_INDEX);
		
		return items;
	}

}
