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

		List<ItemPrice> prices = item.getPrices();
		if (prices == null) {
			return false;
		}
		Collections.sort(prices, new Comparator<ItemPrice>() {
			@Override
			public int compare(ItemPrice lhs, ItemPrice rhs) {
				int sellerComp = Double.compare(lhs.getSellerId(),
						rhs.getSellerId());
				if (sellerComp == 0) {
					return lhs.getDate().compareTo(rhs.getDate());
				}
				return sellerComp;
			}
		});

		List<Long> seenSellers = new ArrayList<Long>();
		List<ItemPrice> currentPrices = new ArrayList<ItemPrice>();
		for (int i = 0; i < prices.size(); i++) {
			long seller = prices.get(i).getSellerId();
			if (seenSellers.contains(seller)) {
				continue;
			}
			seenSellers.add(seller);
			currentPrices.add(prices.get(i));
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
