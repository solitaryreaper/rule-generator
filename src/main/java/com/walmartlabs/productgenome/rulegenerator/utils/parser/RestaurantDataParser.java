package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.Constants;
import com.walmartlabs.productgenome.rulegenerator.model.data.Dataset;
import com.walmartlabs.productgenome.rulegenerator.model.data.Item;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;

/**
 * Data parser for the restuarant dataset.
 * @author excelsior
 *
 */
public class RestaurantDataParser implements DataParser {

	private static final Logger LOG = Logger.getLogger(RestaurantDataParser.class.getName());
	
	private static String DATASET_NAME_IDENTIFIER = "@relation";
	private static String ATTRIBUTE_IDENTIFIER = "@attribute";
	private static String DATA_IDENTIFIER = "@data";
	
	private static String VALUE_DELIMITER = "\\|#";
	private static String WHITESPACE_DELIMITER = " ";
	
	/**
	 * {@inheritDoc}
	 */
	public Dataset parseData(File matchFile, File mismatchFile, String datasetName) 
	{
		Dataset matchSet = parseFile(matchFile, MatchStatus.MATCH);
		Dataset mismatchSet = parseFile(mismatchFile, MatchStatus.MISMATCH);
		
		List<ItemPair> allItemPairs = Lists.newArrayList();
		allItemPairs.addAll(matchSet.getItemPairs());
		allItemPairs.addAll(mismatchSet.getItemPairs());
		
		List<String> attributes = matchSet.getAttributes();
		
		LOG.info("Found " + allItemPairs.size() + " itempairs for " + datasetName + " dataset.");
		return new Dataset(datasetName, attributes, allItemPairs);
	}

	/**
	 * Parse the file to generate itempairs and set the match status for each itempair.
	 */
	private Dataset parseFile(File file, MatchStatus label)
	{
		List<ItemPair> itemPairs = Lists.newArrayList();
		String datasetName = Constants.MISSING_VALUE;
		
		ItemPair itemPair = null;
		Item sourceItem = null;
		Item targetItem = null;
		String sourceId = null;
		String targetId = null;
		
		Map<String, String> sourceAttrValMap = null;
		Map<String, String> targetAttrValMap = null;
		List<String> attributes = Lists.newArrayList();
		
		BufferedReader br = null; 
		boolean isDataReadStarted = false;
		try {
 			String currLine; 	
 			br = new BufferedReader(new FileReader(file));
			while ((currLine = br.readLine()) != null) {
				// When empty line found, save the running item pair.
				if(currLine.isEmpty()) {
					if(sourceAttrValMap != null) {
						sourceItem = new Item(sourceId, sourceAttrValMap);
						targetItem = new Item(targetId, targetAttrValMap);
						itemPair = new ItemPair(sourceItem, targetItem, label);
						itemPairs.add(itemPair);
					}
					
					// reset the values
					itemPair = null;
					sourceItem = null;
					targetItem = null;
					sourceId = null;
					targetId = null;
					sourceAttrValMap = null;
					targetAttrValMap = null;
					continue;
				}				
				
 				// Collect metadata before data read starts
 				if(!isDataReadStarted) {
 	 				if(currLine.startsWith(DATASET_NAME_IDENTIFIER)) {
 	 					String[] temp = currLine.split(WHITESPACE_DELIMITER);
 	 					datasetName = temp[1].trim().replace("\"", "");
 	 				}
 	 				else if(currLine.startsWith(ATTRIBUTE_IDENTIFIER)) {
 	 					String[] temp = currLine.split(WHITESPACE_DELIMITER);
 	 					String attrName = temp[1].trim();
 	 					attributes.add(attrName);
 	 				}
 	 				else if(currLine.startsWith(DATA_IDENTIFIER)) {
 	 					isDataReadStarted = true;
 	 				} 					
 				}
 				// Actual data read starts here
 				else {
 					// Get the item identifiers first
 					if(currLine.startsWith("ID")) {
 						String[] temp = currLine.split(VALUE_DELIMITER);
 						sourceId = temp[1].trim();
 						targetId = temp[2].trim();
 						
 						sourceAttrValMap = Maps.newHashMap();
 						targetAttrValMap = Maps.newHashMap();
 						
 						sourceAttrValMap.put("ID", sourceId);
 						targetAttrValMap.put("ID", targetId); 						
 					}
 					// Now fetch all the attributes populated for the item
 					else {
 						String[] temp = currLine.split(VALUE_DELIMITER);
 						String attrKey = temp[0].trim();
 						String sourceAttrValue = Constants.MISSING_VALUE;
 						String targetAttrValue = Constants.MISSING_VALUE;
 						if(temp.length > 1) {
 							sourceAttrValue = temp[1];
 						}
 						if(temp.length > 2) {
 							targetAttrValue = temp[2];
 						}
 						
 						sourceAttrValMap.put(attrKey, sourceAttrValue);
 						targetAttrValMap.put(attrKey, targetAttrValue);
 					} 					
 				}
			}
			
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		} 		

		return new Dataset(datasetName, attributes, itemPairs);
	}
}
