package edu.rosehulman.salenotifier.notifications;

import edu.rosehulman.salenotifier.models.Item;

public interface INotificationPredicate {
	public boolean evaluate(Item item, double threshold);
}
