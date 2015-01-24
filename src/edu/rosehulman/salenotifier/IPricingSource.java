package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;

public interface IPricingSource {
	
	public List<Item> searchForItems(String searchString) throws ApiException;
	public List<ItemPrice> getPrices(Item item) throws ApiException;
	public List<ItemPrice> getItemsByUpc(String upc) throws ApiException;
	public List<ItemPrice> getPricesByUpc(String upc) throws ApiException;
	public List<ItemPrice> getPricesLessThan(String upc, double lessThan) throws ApiException;
	
	
}
