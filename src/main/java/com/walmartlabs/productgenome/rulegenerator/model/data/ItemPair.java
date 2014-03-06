package com.walmartlabs.productgenome.rulegenerator.model.data;

/**
 * Models a pair of items that have to be matched with each other.
 * @author excelsior
 *
 */
public class ItemPair {
	public static enum MatchStatus {
		MATCH("match"),
		MISMATCH("mismatch");
		
		private String label;
		
		private MatchStatus(String label)
		{
			this.label = label;
		}
		
		public static MatchStatus getMatchStatus(String label)
		{
			MatchStatus status = null;
			for(MatchStatus currStatus : MatchStatus.values()) {
				if(currStatus.label.toLowerCase().equals(label.toLowerCase())) {
					status = currStatus;
					break;
				}
			}
			
			return status;
		}
	}
	
	private Item itemA;
	private Item itemB;
	private MatchStatus matchStatus;

	public ItemPair(Item itemA, Item itemB, MatchStatus matchStatus) {
		super();
		this.itemA = itemA;
		this.itemB = itemB;
		this.matchStatus = matchStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemPair [itemA=").append(itemA).append(", itemB=")
				.append(itemB).append(", matchStatus=").append(matchStatus)
				.append("]");
		return builder.toString();
	}


	public Item getItemA() {
		return itemA;
	}

	public void setItemA(Item itemA) {
		this.itemA = itemA;
	}

	public Item getItemB() {
		return itemB;
	}

	public void setItemB(Item itemB) {
		this.itemB = itemB;
	}

	public MatchStatus getMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(MatchStatus matchStatus) {
		this.matchStatus = matchStatus;
	}
	
	public String getItemAValByAttr(String attrName)
	{
		return itemA.getValuesForAttr(attrName);
	}
	
	public String getItemBValByAttr(String attrName)
	{
		return itemB.getValuesForAttr(attrName);
	}
	
	/**
	 * Hack to identify this itempair after it is converted into a feature vector
	 */
	public String getItempPairIdentifier()
	{
		return itemA.getId() + "#" + itemB.getId();
	}
	
}
