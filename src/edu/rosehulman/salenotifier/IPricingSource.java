package edu.rosehulman.salenotifier;

import java.util.List;

import android.content.Context;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

public interface IPricingSource {
	
	public List<Item> search(Context context, ItemQueryConstraints query) throws ApiException;
	public List<ItemPrice> searchForPrices(Context context, Item item) throws ApiException;
	
	public List<Item> searchForItems(String searchString) throws ApiException;
	public List<ItemPrice> getPrices(Item item) throws ApiException;
	public List<ItemPrice> getItemsByUpc(String upc) throws ApiException;
	public List<ItemPrice> getPricesByUpc(String upc) throws ApiException;
	public List<ItemPrice> getPricesLessThan(String upc, double lessThan) throws ApiException;
	
	
}
