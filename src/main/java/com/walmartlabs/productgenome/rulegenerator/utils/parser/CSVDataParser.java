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
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.Item;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.BlockingClause;
import com.walmartlabs.productgenome.rulegenerator.utils.SimilarityUtils;

public class CSVDataParser implements DataParser {

	private static Logger LOG = Logger.getLogger(CSVDataParser.class.getName());
	
	private static int ID_ATTR_INDEX = 0;
	
	private static String ATTRIBUTES_KEY = "attributes";
	private static String ITEMS_KEY = "items";
	
	public Dataset parseData(File matchFile, File mismatchFile, String datasetName) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public Dataset parseData(String datasetName, File sourceFile, File targetFile, File goldFile) 
	{
		Multimap<String, String> goldMap = getGoldenDataMap(goldFile);
		int goldPairs = goldMap.size();
		int totalMismatchPairsToRetain = 2*goldPairs;
		
		Map<String, List<?>> sourceResultMap = getItems(sourceFile);
		Map<String, List<?>> targetResultMap = getItems(targetFile);
		
		LOG.info("Source Items : " + sourceResultMap.get(ITEMS_KEY).size() + ", Target Items : " + 
				targetResultMap.get(ITEMS_KEY).size() + ", Golden Items : " + goldMap.size());
		
		List<Item> sourceItems = (List<Item>)sourceResultMap.get(ITEMS_KEY);
		List<Item> targetItems = (List<Item>)targetResultMap.get(ITEMS_KEY);
		
		int preBlockingItemPairs = sourceItems.size() * targetItems.size();
		int postBlockingItemPairs = 0;
		
		Set<String> sourceAttributes = Sets.newHashSet((List<String>)sourceResultMap.get(ATTRIBUTES_KEY));
		Set<String> targetAttributes = Sets.newHashSet((List<String>)targetResultMap.get(ATTRIBUTES_KEY));
		List<String> attributes = Lists.newArrayList(Sets.intersection(sourceAttributes, targetAttributes));
		LOG.info("Common attributes : " + attributes.toString());
		
		boolean isBlockingReqd = true;
		
		List<ItemPair> itemPairs = Lists.newArrayList();
		int numMismatchPairsAdded = 0;
		int numMatchPairsAdded = 0;
		for(Item srcItem : sourceItems) {
			for(Item tgtItem : targetItems) {
				MatchStatus matchStatus = MatchStatus.MISMATCH;
				
				if(goldMap.containsEntry(srcItem.getId().trim(), tgtItem.getId().trim())) {
					matchStatus = MatchStatus.MATCH;
					++numMatchPairsAdded;
					itemPairs.add(new ItemPair(srcItem, tgtItem, matchStatus));
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
				
				if(postBlockingItemPairs % 1000 == 0) {
					LOG.info("Added total itempairs " + postBlockingItemPairs + ", num mismatch added " + 
							numMismatchPairsAdded + " , num match added " + numMatchPairsAdded);
				}
			}
		}

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
	 * Applies basic blocking to ensure that the actual dataset to match is reduced considerably.
	 */
	private boolean isEligibleForMatching(Item srcItem, Item tgtItem, List<BlockingClause> blockingClauses)
	{
		boolean isEligibleForMatching = true;

		for(BlockingClause clause : blockingClauses) {
			String attrName = clause.getAttributeName();
			String val1 = srcItem.getValuesForAttr(attrName);
			String val2 = tgtItem.getValuesForAttr(attrName);
			double score = SimilarityUtils.getSimilarity(clause.getMetricToApply(), val1, val2);
			if(Double.compare(score, clause.getThreshold()) <= 0) {
				isEligibleForMatching = false;
				break;
			}
		}
		
		return isEligibleForMatching;
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
		String[] currLineTokens = null;
		try {
 			currLineTokens = null;
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
					
					// Skip the ID value at beginning
					for(int i=1; i < currLineTokens.length; i++) {
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
		
		// Don't include ID as an attribute while generating feature vector ..
		attributes.remove(Constants.ID_ATTRIBUTE);
		
		Map<String, List<?>> resultMap = Maps.newHashMap();
		resultMap.put(ATTRIBUTES_KEY, attributes);
		resultMap.put(ITEMS_KEY, items);
		
		return resultMap;
	}

}
