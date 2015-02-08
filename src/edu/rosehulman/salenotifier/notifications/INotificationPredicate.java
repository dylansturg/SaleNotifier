package edu.rosehulman.salenotifier.notifications;

import android.content.Context;
import edu.rosehulman.salenotifier.models.Item;

public interface INotificationPredicate {
	public boolean evaluate(Item item, double threshold);
	public String getNotificationMessage(Context context, Item item, double threshold);
}
