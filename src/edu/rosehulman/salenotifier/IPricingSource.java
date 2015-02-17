package edu.rosehulman.salenotifier;

import java.util.List;

import android.content.Context;

import edu.rosehulman.salenotifier.ItemSearchTask.IPartialSearchResultsCallback;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

public abstract class IPricingSource {
	public abstract String getSourceName();

	public boolean allowsLocalSearches() {
		return false;
	}

	public abstract List<Item> search(Context context,
			ItemQueryConstraints query,
			IPartialSearchResultsCallback partialCallback) throws ApiException;

	public abstract List<ItemPrice> searchForPrices(Context context, Item item)
			throws ApiException;
}
