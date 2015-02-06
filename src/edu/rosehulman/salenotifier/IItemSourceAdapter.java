package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;

public interface IItemSourceAdapter {
	public List<Item> getAllItems();
	public Item getItem(String productCode);
	public void saveItem(Item item);
	public void deleteItem(Item item);
	public void deleteItem(long id);
	
	public void deleteItemPrice(ItemPrice price);
	public void deleteItemPrice(long id);
}
