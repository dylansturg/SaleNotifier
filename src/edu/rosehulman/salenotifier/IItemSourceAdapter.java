package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.models.Item;

public interface IItemSourceAdapter {
	public List<Item> getAllItems();
	public Item getItem(String productCode);
	public void saveItem(Item item);
	public void deleteItem(Item item);
	public void deleteItem(long id);
}
