package edu.rosehulman.salenotifier.notifications;

import edu.rosehulman.salenotifier.models.Item;

public class PriceBelowPredicate implements INotificationPredicate {

	public PriceBelowPredicate() {
	}

	@Override
	public boolean evaluate(Item item, double threshold) {
		return false;
	}

}
