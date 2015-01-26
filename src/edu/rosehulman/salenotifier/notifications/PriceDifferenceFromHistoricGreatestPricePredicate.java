package edu.rosehulman.salenotifier.notifications;

import android.content.Context;
import edu.rosehulman.salenotifier.models.Item;

public class PriceDifferenceFromHistoricGreatestPricePredicate implements
		INotificationPredicate {

	public PriceDifferenceFromHistoricGreatestPricePredicate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean evaluate(Item item, double threshold) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNotificationMessage(Context context, Item item,
			double threshold) {
		// TODO Auto-generated method stub
		return null;
	}

}
