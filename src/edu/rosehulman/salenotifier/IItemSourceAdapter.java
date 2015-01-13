package edu.rosehulman.salenotifier;

public interface IItemSourceAdapter {
	public Item getItem(String productCode);
	public void saveItem(Item item);
}
