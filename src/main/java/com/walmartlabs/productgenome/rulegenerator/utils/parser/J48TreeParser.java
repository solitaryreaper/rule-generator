package com.walmartlabs.productgenome.rulegenerator.utils.parser;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmartlabs.productgenome.rulegenerator.model.data.ItemPair.MatchStatus;
import com.walmartlabs.productgenome.rulegenerator.model.rule.Rule;

public class J48TreeParser {

	private static final Logger LOG = Logger.getLogger(J48TreeParser.class.getName());
	
	private static final String TREE_LEVEL_DELIMITER = "|";
	private static final String TREE_LEAF_IDENTIFIER = ":";

	private static final String MAP_KEY_FEATURE_NAME = "fName";
	private static final String MAP_KEY_RELOP = "relop";
	private static final String MAP_KEY_VALUE = "value";
	private static final String MAP_KEY_LABEL = "label";
	private static final String MAP_KEY_LEVEL = "level";
	
	/**
	 * Parses the J48 tree dump to return a set of matching rules.
	 * 
	 * TODO : Ideally should extend Weka code, instead of this hack.
	 * 
	 * @param treeDump
	 * @return
	 */
	public static List<Rule> parseJ48TreeDump(List<String> treeDump) {
		if (treeDump == null || treeDump.isEmpty()) {
			LOG.severe("Please input a valid J48 tree dump for rule generation !!");
			System.exit(1);
		}

		J48RuleTreeNode root = getJ48RuleTree(treeDump);
		return getRules(root);
	}

	/**
	 * Iterate over the generated J48 rule tree to extract the rules from the decision tree.
	 * 
	 * @param node
	 * @return
	 */
	private static List<Rule> getRules(J48RuleTreeNode node)
	{
		
	}
	
	/**
	 * Returns a J48Tree representation from the tree dump string.
	 * 
	 * @param treeDump
	 * @return
	 */
	private static J48RuleTreeNode getJ48RuleTree(List<String> treeDump) 
	{
		J48TreeParser parser = new J48TreeParser();
		
		Map<Integer, J48RuleTreeNode> levelNodeMap = Maps.newHashMap();
		for(String str : treeDump) {
			Map<String, String> strMap = parseTreeDumpString(str);
			J48RuleTreeNode currNode = null;
			J48RuleTreeNode childNode = null;
			
			String currFeature = strMap.get(MAP_KEY_FEATURE_NAME);
			int level = Integer.parseInt(strMap.get(MAP_KEY_LEVEL));
			String relop = strMap.get(MAP_KEY_RELOP);
			String value = strMap.get(MAP_KEY_VALUE);
			String label = strMap.get(MAP_KEY_LABEL);
			
		}
	}

	// TODO : Need to learn java regular expressions. This is really bad coding !!
	private static Map<String, String> parseTreeDumpString(String str)
	{
		Map<String, String> strMetaMap = Maps.newHashMap();
		
		// find the level of the current string in tree representation
		if(str.contains(TREE_LEVEL_DELIMITER)) {
			int level = str.split(TREE_LEVEL_DELIMITER).length - 1;
			strMetaMap.put(MAP_KEY_LEVEL, Integer.toString(level));
		}
		else {
			strMetaMap.put(MAP_KEY_LEVEL, "0");
		}
		
		// find the label, if this is a leaf node
		if(str.contains(TREE_LEAF_IDENTIFIER)) {
			String label = "match";
			if(str.contains("mismatch")) {
				label = "match";
			}
			strMetaMap.put(MAP_KEY_LABEL, label);
		}
		
		// find the feature name, relop and value
		String relop = ">";
		if(str.contains("<=")) {
			relop = "<=";
		}
		strMetaMap.put(MAP_KEY_RELOP, relop);
		
		String[] temp = str.split(relop);
		String featureName = temp[0].replaceAll("|", "").replaceAll(" ", "").trim();
		strMetaMap.put(MAP_KEY_FEATURE_NAME, featureName);
		
		String value = temp[1].substring(0, temp[1].indexOf(':') - 1).replaceAll(" ", "").trim();
		strMetaMap.put(MAP_KEY_VALUE, value);
		
		return strMetaMap;
	}
	
	/**
	 * A rule tree representation of J48 classifier.
	 * 
	 * @author skprasad
	 * 
	 */
	public class J48RuleTreeNode {
		// Attributes for this child node
		private String featureName;
		private NodeType nodeType;

		// Attributes of the parent node, for this child node
		private String parentFeatureName = null;
		private Double parentFeatureLinkValue = null;
		private RelationalOperator parentFeatureRelOp = null;

		// Label of the node, if it is a leaf node
		private MatchStatus label = MatchStatus.MISMATCH;

		// List of child nodes for this tree node
		private List<J48RuleTreeNode> childNodes;
		
		public String getFeatureName() {
			return featureName;
		}

		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}

		public NodeType getNodeType() {
			return nodeType;
		}

		public void setNodeType(NodeType nodeType) {
			this.nodeType = nodeType;
		}

		public String getParentFeatureName() {
			return parentFeatureName;
		}

		public void setParentFeatureName(String parentFeatureName) {
			this.parentFeatureName = parentFeatureName;
		}

		public Double getParentFeatureLinkValue() {
			return parentFeatureLinkValue;
		}

		public void setParentFeatureLinkValue(Double parentFeatureLinkValue) {
			this.parentFeatureLinkValue = parentFeatureLinkValue;
		}

		public RelationalOperator getParentFeatureRelOp() {
			return parentFeatureRelOp;
		}

		public void setParentFeatureRelOp(RelationalOperator parentFeatureRelOp) {
			this.parentFeatureRelOp = parentFeatureRelOp;
		}

		public MatchStatus getLabel() {
			return label;
		}

		public void setLabel(MatchStatus label) {
			this.label = label;
		}

		public List<J48RuleTreeNode> getChildNodes() {
			return childNodes;
		}

		public void setChildNodes(List<J48RuleTreeNode> childNodes) {
			this.childNodes = childNodes;
		}
	}

	private J48RuleTreeNode createFeatureNode(String featureName)
	{
		J48RuleTreeNode node = new J48RuleTreeNode();
		node.setNodeType(NodeType.FEATURE_NODE);
		node.setFeatureName(featureName);
		return node;
	}
	
	private J48RuleTreeNode createLeafNode(MatchStatus label)
	{
		J48RuleTreeNode node = new J48RuleTreeNode();
		node.setNodeType(NodeType.LEAF_NODE);
		node.setLabel(label);
		return node;		
	}
	
	/**
	 * Type of the node in the rule tree. It can be either feature or leaf node.
	 * 
	 * @author skprasad
	 * 
	 */
	private enum NodeType {
		FEATURE_NODE, LEAF_NODE;
	}

	/**
	 * Relational operator chosen for traversing a specific branch of tree.
	 * 
	 * Values are : <= and >
	 * 
	 * @author skprasad
	 * 
	 */
	private enum RelationalOperator {
		LESS_THAN_OR_EQUAL_TO("<="), GREATER_THAN(">");

		private String operatorValue;

		private RelationalOperator(String operatorValue) {
			this.operatorValue = operatorValue;
		}

		public static List<RelationalOperator> getRelationalOperatorValues() {
			return Lists.newArrayList(RelationalOperator.values());
		}

		public String getOperatorToApply() {
			return operatorValue;
		}
	}
}
