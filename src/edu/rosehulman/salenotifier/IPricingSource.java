package edu.rosehulman.salenotifier;

import java.util.List;

public interface IPricingSource {
	
	public List<Item> searchForItems(String searchString) throws ApiException;
	public List<ItemPrice> getPrices(Item item) throws ApiException;
	public List<ItemPrice> getPrices(String upc) throws ApiException;
	public List<ItemPrice> getPricesLessThan(String upc, double lessThan) throws ApiException;
	
	
}
