package edu.rosehulman.salenotifier.amazon;

import java.util.List;

import android.content.Context;
import edu.rosehulman.salenotifier.ApiException;
import edu.rosehulman.salenotifier.IPricingSource;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

public class AmazonPricingSource implements IPricingSource {
	protected static final String AMAZON_SOURCE_NAME = "AMAZON";

	public AmazonPricingSource() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSourceName() {
		return AMAZON_SOURCE_NAME;
	}

	@Override
	public List<Item> search(Context context, ItemQueryConstraints query)
			throws ApiException {
		try {
			AmazonRequest request = new AmazonRequest(query);
			List<Item> results = request.performQuery();

			return results;
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}

	@Override
	public List<ItemPrice> searchForPrices(Context context, Item item)
			throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchForItems(String searchString) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemPrice> getPrices(Item item) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemPrice> getItemsByUpc(String upc) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemPrice> getPricesByUpc(String upc) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemPrice> getPricesLessThan(String upc, double lessThan)
			throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

}
