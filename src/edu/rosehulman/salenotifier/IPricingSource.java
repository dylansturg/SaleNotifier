package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;

public interface IPricingSource {
	public List<ItemPrice> getPrices(Item item);
}
