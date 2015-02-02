package edu.rosehulman.salenotifier.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;

public class PriceBelowPredicate implements INotificationPredicate {

	private ItemPrice satisfyingPrice;

	public PriceBelowPredicate() {
	}

	@Override
	public boolean evaluate(Item item, double threshold) {
		if (item == null) {
			throw new IllegalArgumentException(
					"Attempt to evaluate PriceBelowPredicate on null Item");
		}

		List<ItemPrice> currentPrices = item.getCurrentPrices();
		if (currentPrices == null || currentPrices.size() == 0) {
			return false;
		}

		for (ItemPrice itemPrice : currentPrices) {
			if (itemPrice.getPrice() < threshold) {
				satisfyingPrice = itemPrice;
				return true;
			}
		}

		return false;
	}

	@Override
	public String getNotificationMessage(Context context, Item item,
			double threshold) {
		// TODO Improve message
		return "Item " + item.getDisplayName() + " price has fallen to "
				+ satisfyingPrice.getPrice();
	}

}
