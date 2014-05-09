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
import com.walmartlabs.productgenome.rulegenerator.utils.DatasetUtils;

public class ItemDataParser implements DataParser {

	private static Logger LOG = Logger.getLogger(ItemDataParser.class.getName());
	
	private static int ID_ATTR_INDEX = 0;
	
	public Dataset parseData(String datasetName, File itemPairFile, File goldFile, DatasetNormalizerMeta normalizerMeta) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Dataset parseData(String datasetName, File sourceFile, File targetFile, File goldFile, DatasetNormalizerMeta normalizerMeta) 
	{
		BiMap<String, String> schemaMap = normalizerMeta.getSchemaMap();
		
		Map<Integer, GoldItemPair> goldDataMap = getGoldDatasetMap(goldFile);
		//Multimap<String, String> goldMap = getGoldenDataMap(goldFile);
		int goldPairs = goldDataMap.size();
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
		int numUnlabelledPairsAdded = 0;
		int numLabelledPairsAdded = 0;
		for(Item srcItem : sourceItems) {
			for(Item tgtItem : targetItems) {
				MatchStatus matchStatus = MatchStatus.MISMATCH;
				
				String srcItemId = srcItem.getId().trim();
				String tgtItemId = tgtItem.getId().trim();
				
				Integer srcToTgtItemId = DatasetUtils.getItemPairId(srcItemId, tgtItemId);
				Integer tgtToSrcItemId = DatasetUtils.getItemPairId(tgtItemId, srcItemId);
				
				// Always add the golden pair to the final dataset to be evaluated ..
				if((goldDataMap.containsKey(srcToTgtItemId)))
				{
					GoldItemPair goldPair = goldDataMap.get(srcToTgtItemId);
					matchStatus = goldPair.getMatchStatus();
					++numLabelledPairsAdded;
					
					itemPairs.add(new ItemPair(srcItem, tgtItem, matchStatus));
					++postBlockingItemPairs;
					continue;
				}
				else if(goldDataMap.containsKey(tgtToSrcItemId)) {
					GoldItemPair goldPair = goldDataMap.get(tgtToSrcItemId);
					matchStatus = goldPair.getMatchStatus();
					++numLabelledPairsAdded;
					
					itemPairs.add(new ItemPair(srcItem, tgtItem, matchStatus));
					++postBlockingItemPairs;
					continue;					
				}
				
				// Only apply blocking to itempairs not present in golden file ..
				if(isBlockingReqd && matchStatus.equals(MatchStatus.MISMATCH)) {
					boolean retainForMatching = shouldRetainForMatching(numUnlabelledPairsAdded, totalMismatchPairsToRetain);
					if(!retainForMatching) {
						continue;
					}
					else {
						++numUnlabelledPairsAdded;
					}
				}
				
				itemPairs.add(new ItemPair(srcItem, tgtItem, matchStatus));
				++postBlockingItemPairs;
			}
		}
		
		if(numLabelledPairsAdded == 0) {
			LOG.severe("No itempairs could be added from the golden dataset. Please fix this issue first !!");
			System.exit(1);
		}

		LOG.info("Labelled pairs added : " + numLabelledPairsAdded + ", Unlabelled pairs added : " + numUnlabelledPairsAdded);
		LOG.info("Stats : Before blocking : " + preBlockingItemPairs + ", After blocking : " + postBlockingItemPairs);
		LOG.info("ItemPairs added : " + itemPairs.size());
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
	
	private Map<Integer, GoldItemPair> getGoldDatasetMap(File goldFile)
	{
		List<GoldItemPair> goldItemPairs = Lists.newArrayList();
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
				
				String item1Id = currLineTokens[0].trim();
				String item2Id = currLineTokens[1].trim();
				String matchStatus = currLineTokens[2].trim();
				if(matchStatus == null) {
					matchStatus = "mismatch";
				}
				
				goldItemPairs.add(new GoldItemPair(item1Id, item2Id, MatchStatus.getMatchStatus(matchStatus)));
			}
			reader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		Map<Integer, GoldItemPair> goldDataMap = Maps.newHashMap();
		for(GoldItemPair goldPair : goldItemPairs) {
			String item1Id = goldPair.getItem1Id();
			String item2Id = goldPair.getItem2Id();
			Integer itemPairId = DatasetUtils.getItemPairId(item1Id, item2Id);
			goldDataMap.put(itemPairId, goldPair);
		}
		
		return goldDataMap;
	}
	
	private static class GoldItemPair
	{
		private String item1Id;
		private String item2Id;
		private MatchStatus matchStatus;
		
		public GoldItemPair(String item1Id, String item2Id, MatchStatus matchStatus) {
			super();
			this.item1Id = item1Id;
			this.item2Id = item2Id;
			this.matchStatus = matchStatus;
		}
		
		public GoldItemPair(String item1Id, String item2Id) {
			super();
			this.item1Id = item1Id;
			this.item2Id = item2Id;
			this.matchStatus = MatchStatus.MATCH;			
		}

		public String getItem1Id() {
			return item1Id;
		}

		public String getItem2Id() {
			return item2Id;
		}

		public MatchStatus getMatchStatus() {
			return matchStatus;
		}	
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
