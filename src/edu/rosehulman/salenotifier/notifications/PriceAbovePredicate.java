package edu.rosehulman.salenotifier.notifications;

import java.util.List;

import android.content.Context;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;

public class PriceAbovePredicate implements INotificationPredicate {

	private ItemPrice satisfyItemPrice;

	public PriceAbovePredicate() {
	}

	@Override
	public boolean evaluate(Item item, double threshold) {
		if (item == null) {
			throw new IllegalArgumentException(
					"Attempt to evaluate PriceAbovePredicate on null Item");
		}

		List<ItemPrice> currentPrices = item.getCurrentPrices();
		if (currentPrices == null || currentPrices.size() == 0) {
			return false;
		}

		for (ItemPrice itemPrice : currentPrices) {
			if (itemPrice.getPrice() > threshold) {
				satisfyItemPrice = itemPrice;
				return true;
			}
		}

		return false;
	}

	@Override
	public String getNotificationMessage(Context context, Item item,
			double threshold) {
		String format = "%.2f from %s (%s %.2f)";
		return String.format(format, satisfyItemPrice.getPrice(),
				satisfyItemPrice.getSellerName(), "Above", threshold);
	}
}
