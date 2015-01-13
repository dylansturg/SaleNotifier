package edu.rosehulman.salenotifier;

import java.util.List;

public interface IPricingSource {
	public List<ItemPrice> getPrices(Item item);
}
