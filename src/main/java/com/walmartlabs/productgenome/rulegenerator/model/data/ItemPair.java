package com.walmartlabs.productgenome.rulegenerator.model.data;

import com.google.common.base.Objects;

/**
 * Models a pair of items that have to be matched with each other.
 * @author excelsior
 *
 */
public class ItemPair {
	
	public static enum MatchStatus {
		MATCH("match"),
		MISMATCH("mismatch"),
		UNKNOWN("unknown");
		
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
	private MatchStatus matchStatus = MatchStatus.UNKNOWN;


	
	public ItemPair(Item itemA, Item itemB, MatchStatus matchStatus) {
		super();
		this.itemA = itemA;
		this.itemB = itemB;
		this.matchStatus = matchStatus;
	}

	public ItemPair(Item itemA, Item itemB)
	{
		this(itemA, itemB, MatchStatus.UNKNOWN);
		this.itemA = itemA;
		this.itemB = itemB;
		this.matchStatus = MatchStatus.UNKNOWN;
	}
	
	public boolean equals(Object obj)
	{
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
	    final ItemPair that = (ItemPair) obj;
	    return 	Objects.equal(this.itemA, that.itemA) &&
	            Objects.equal(this.itemB, that.itemB);		
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
	public int getId()
	{
		String id = itemA.getId() + "#" + itemB.getId();
		return id.replace(" ", "").trim().hashCode();
	}
	
}
