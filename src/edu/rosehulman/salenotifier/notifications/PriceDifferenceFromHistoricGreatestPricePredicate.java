package edu.rosehulman.salenotifier.notifications;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;

public class PriceDifferenceFromHistoricGreatestPricePredicate implements
		INotificationPredicate {

	private ItemPrice satisfyingItemPrice;
	private ItemPrice historicGreatest;

	public PriceDifferenceFromHistoricGreatestPricePredicate() {
	}

	@Override
	public boolean evaluate(Item item, double threshold) {

		List<ItemPrice> prices = item.getPrices();
		if (prices == null || prices.size() == 0) {
			return false;
		}

		Collections.sort(prices, new Comparator<ItemPrice>() {
			@Override
			public int compare(ItemPrice lhs, ItemPrice rhs) {
				// Not too worried about the double comparison here
				return lhs.getPrice() > rhs.getPrice() ? -1
						: lhs.getPrice() == rhs.getPrice() ? 0 : 1;
			}
		});

		historicGreatest = prices.get(0);
		double greatestPrice = historicGreatest.getPrice();
		List<ItemPrice> currentPrices = item.getCurrentPrices();
		if (currentPrices == null || currentPrices.size() == 0) {
			return false;
		}

		for (ItemPrice itemPrice : currentPrices) {
			double difference = greatestPrice - itemPrice.getPrice();
			if (difference >= threshold) {
				satisfyingItemPrice = itemPrice;
				return true;
			}
		}

		return false;
	}

	@Override
	public String getNotificationMessage(Context context, Item item,
			double threshold) {
		String format = "Item (%s) is available for %f, which is less than the greatest price of %f";
		return String.format(format, item.getDisplayName(),
				satisfyingItemPrice.getPrice(), historicGreatest.getPrice());
	}

}
