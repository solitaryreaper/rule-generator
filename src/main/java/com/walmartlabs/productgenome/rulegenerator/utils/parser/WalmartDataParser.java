package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.DatasetNormalizerMeta;
import com.walmartlabs.productgenome.rulegenerator.model.data.Item;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

/**
 * Data parser for parsing walmart itempair data format
 * @author skprasad
 *
 */
public class WalmartDataParser implements DataParser {

	private static final Logger LOG = Logger.getLogger(WalmartDataParser.class.getName());
	
	private static final String SOURCE_ITEM_ATTR = "source_item_attr";
	private static final String TARGET_ITEM_ATTR = "target_item_attr";
	
	public static final String COMMA						= ",";
	public static final String COLUMN_DELIMITER				= "\\|#";
	
	public static final String ID 							= "ID";
	public static final String SOURCE 						= "Source";
	public static final String TITLE 						= "Title";
	public static final String PD_TITLE 					= "pd_title";
	public static final String NULL_STRING					= "null";
	
	public Dataset parseData(String datasetName, File matchFile, File mismatchFile, DatasetNormalizerMeta normalizerMeta) {
		BiMap<String, String> schemaMap = normalizerMeta.getSchemaMap();
		Dataset matchSet = parseFile(datasetName, matchFile, MatchStatus.MATCH, schemaMap);
		Dataset mismatchSet = parseFile(datasetName, mismatchFile, MatchStatus.MISMATCH, schemaMap);

		List<ItemPair> allItemPairs = Lists.newArrayList();
		allItemPairs.addAll(matchSet.getItemPairs());
		allItemPairs.addAll(mismatchSet.getItemPairs());
		
		List<String> attributes = matchSet.getAttributes();
		
		LOG.info("Found " + allItemPairs.size() + " itempairs for " + datasetName + " dataset.");
		return new Dataset(datasetName, attributes, allItemPairs);		
	}

	/**
	 * Returns item pair objects from a file containing item pair in raw form.
	 * @param itemPairDataFile	A file object representing the file containg itempairs.
	 * @return	List of itempair objects.
	 */
	public Dataset parseFile(String datasetName, File itemPairDataFile, MatchStatus matchStatus, BiMap<String, String> schemaMap)
	{
		String firstItemID = null;
		Map<String, String> sourceItemAttributes = Maps.newHashMap();
		
		String secondItemID = null;
		Map<String, String> targetItemAttributes = Maps.newHashMap();
		
		Item firstItem = null;
		Item secondItem = null;
		
		Set<String> attributes = Sets.newHashSet();
		List<ItemPair> itemPairs = Lists.newArrayList();
		BufferedReader br = null; 
		try {
 			String currLine; 	
 			br = new BufferedReader(new FileReader(itemPairDataFile));

			// TODO : Ask Aswath for a better itempair delimiter. Relying on an empty line seems
			// too error-prone. Shall we have a more explicit itempair end row delimiter ?
 			// This hack is to ensure that presence of unnecessary empty lines don't fail the
 			// parsing logic.
 			boolean isOneItemPairFullyRead = true;
 			while ((currLine = br.readLine()) != null) {
				// Itempair information is finished. Create itempair and reinitialise all the
				// item state variables for the next itempair.
				if(currLine.isEmpty() && !isOneItemPairFullyRead) {
					// This state variable marks the end of reading of a single itempair.
					isOneItemPairFullyRead = true;
					
					Map<String, String>  sourceAttrsMap = Maps.newHashMap(sourceItemAttributes);
					firstItem = new Item(firstItemID, sourceAttrsMap);
					
					Map<String, String> targetAttrsMap = Maps.newHashMap(targetItemAttributes);
					secondItem = new Item(secondItemID, targetAttrsMap);
					
					ItemPair itemPair = new ItemPair(firstItem, secondItem, matchStatus);
					itemPairs.add(itemPair);
					
					firstItem = null; 
					firstItemID = null; 
					sourceItemAttributes = Maps.newHashMap();
					
					secondItem = null; 
					secondItemID = null; 
					targetItemAttributes = Maps.newHashMap();
				}
				else {
					// Set the IDs of items
					if(currLine.startsWith(ID)) {
						// This state variable marks the beginning of reading of an itempair
						isOneItemPairFullyRead = false;
						
						Map<String, String> idMap = parseItemIDs(currLine);
						firstItemID = idMap.get(SOURCE_ITEM_ATTR);
						secondItemID = idMap.get(TARGET_ITEM_ATTR);
					}
					// Set the common attributes for both the items
					else {
						Map<String, Map<String, String>> attrMap = parseCommonAttributesOfItems(currLine, schemaMap);
						
						if(attrMap.containsKey(SOURCE_ITEM_ATTR)) {
							Map<String, String> sourceAttrMap = attrMap.get(SOURCE_ITEM_ATTR);
							for(Map.Entry<String, String> entry : sourceAttrMap.entrySet()) {
								sourceItemAttributes.put(entry.getKey(), entry.getValue());
								attributes.add(entry.getKey());
							}
						}

						if(attrMap.containsKey(TARGET_ITEM_ATTR)) {
							Map<String, String> targetAttrMap = attrMap.get(TARGET_ITEM_ATTR);
							for(Map.Entry<String, String> entry : targetAttrMap.entrySet()) {
								targetItemAttributes.put(entry.getKey(), entry.getValue());
							}
						}
					}
				}
				
			} // end of while loop
 			
 			// TODO : Hack. What if someone put to forgot to put an empty line as itempair delimiter
 			// for the very last itempair in the file. Handle that corner case here. We should have
 			// an explicit itempair delimiter.
 			if(!isOneItemPairFullyRead) {
				// This state variable marks the end of reading of a single itempair.
				isOneItemPairFullyRead = true;
				
				Map<String, String>  sourceAttrsMap = Maps.newHashMap(sourceItemAttributes);
				firstItem = new Item(firstItemID, sourceAttrsMap);
				
				Map<String, String> targetAttrsMap = Maps.newHashMap(targetItemAttributes);
				secondItem = new Item(secondItemID, targetAttrsMap);
				
				ItemPair itemPair = new ItemPair(firstItem, secondItem, matchStatus);
				itemPairs.add(itemPair);		
 			}
 		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		
		return new Dataset(datasetName, Lists.newArrayList(attributes), itemPairs);
	}
	
	/**
	 * Parses the item ids of the item pair.
	 * @param itemIdsLine
	 * @return
	 */
	public static Map<String, String> parseItemIDs(String itemIdsLine)
	{
		String[] idTokens = itemIdsLine.split(COLUMN_DELIMITER);
		return getKeyValuePair(idTokens);
	}

	/**
	 * Parses the source of each item.
	 * @param sourceLine
	 * @return
	 */
	public static Map<String, String> parseSources(String sourcesLine)
	{
		String[] idTokens = sourcesLine.split(COLUMN_DELIMITER);
		return getKeyValuePair(idTokens);
	}

	/**
	 * Parses the title/name of the items.
	 */
	public static Map<String, String> parseItemNames(String itemNameLine)
	{
		String[] titleTokens = itemNameLine.split(COLUMN_DELIMITER);
		return getKeyValuePair(titleTokens);
	}

	private static Map<String, String> getKeyValuePair(String[] tokens)
	{
		Map<String, String> keyValueMap = Maps.newHashMap();
		keyValueMap.put(SOURCE_ITEM_ATTR, tokens[1].trim());
		keyValueMap.put(TARGET_ITEM_ATTR, tokens[2].trim());
		
		return keyValueMap;
	}

	/**
	 * Parses the attributes and their values for all the attributes which occur for both source
	 * and target item.
	 */
	public static Map<String, Map<String, String>> parseCommonAttributesOfItems(String commonAttrLine, BiMap<String, String> schemaMap)
	{
		Map<String, Map<String, String>> commonAttrMap = Maps.newHashMap();
		
		String[] attrTokens = commonAttrLine.split(COLUMN_DELIMITER);
		String attrName = attrTokens[0].trim();
		
		// Check if this attribute should be included in the parsed result set or not.
		// The idea is to exclude source specific attributes and only concentrate on common/generic attributes.
		if(!(schemaMap.containsKey(attrName) || schemaMap.containsValue(attrName))) {
			return commonAttrMap;
		}
		
		String sourceAttrValue = attrTokens[1].trim();
		String targetAttrValue = attrTokens[2].trim();
		
		if(!(Strings.isNullOrEmpty(sourceAttrValue) || sourceAttrValue.toLowerCase().equals("null"))) {
			Map<String, String> sourceAttrMap = Maps.newHashMap();
			sourceAttrMap.put(attrName, trimSpecialCharacters(attrTokens[1]));
			commonAttrMap.put(SOURCE_ITEM_ATTR, sourceAttrMap);
		}
		if(!(Strings.isNullOrEmpty(targetAttrValue) || targetAttrValue.toLowerCase().equals("null"))) {
			Map<String, String> targetAttrMap = Maps.newHashMap();
			targetAttrMap.put(attrName, trimSpecialCharacters(attrTokens[2]));
			commonAttrMap.put(TARGET_ITEM_ATTR, targetAttrMap);
		}
		
		return commonAttrMap;
	}
	
	private static String trimSpecialCharacters(String value)
	{
		return value.replace("[", "").replace("]", "").trim();
	}
	
	public Dataset parseData(String datasetName, File srcFile, File tgtFile, File goldFile, DatasetNormalizerMeta normalizerMeta) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	

}
