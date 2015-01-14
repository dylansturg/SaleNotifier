package edu.rosehulman.salenotifier;

import java.util.List;

public interface IItemSourceAdapter {
	public List<Item> getAllItems();
	public Item getItem(String productCode);
	public void saveItem(Item item);
}
