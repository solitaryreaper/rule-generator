package com.walmartlabs.productgenome.rulegenerator.model.data;

/**
 * Models a pair of items that have to be matched with each other.
 * @author excelsior
 *
 */
public class ItemPair {
	public static enum MatchStatus {
		MATCH,
		MISMATCH
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
		String attrVal = null;
		return attrVal;
	}
	
	public String getItemBValByAttr(String attrName)
	{
		String attrVal = null;
		return attrVal;
	}
	
}
