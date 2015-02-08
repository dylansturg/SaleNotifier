package edu.rosehulman.salenotifier.service;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.ApiException;
import edu.rosehulman.salenotifier.IPricingSource;
import edu.rosehulman.salenotifier.Semantics3PriceSource;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemNotification;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.notifications.INotificationPredicate;
import edu.rosehulman.salenotifier.notifications.NotificationLauncher;
import edu.rosehulman.salenotifier.notifications.NotificationPredicateFactory;
import edu.rosehulman.salenotifier.settings.SettingsManager;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ItemUpdateBackgroundService extends IntentService {

	ArrayList<IPricingSource> priceSources;
	
	public ItemUpdateBackgroundService() {
		super("ItemUpdateBackgroundService");
		priceSources = new ArrayList<IPricingSource>();
		priceSources.add(new Semantics3PriceSource());
		// TODO: add more sources here
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TrackedItemsActivity.LOG_TAG,
				"ItemUpdateBackgroundService starting");

		if (!SaleNotifierSQLHelper.isInit()) {
			SaleNotifierSQLHelper.init(this);
			Log.d(TrackedItemsActivity.LOG_TAG,
					"ItemUpdateBackgroundService init database");
		}

		SQLiteAdapter dataSource = new SQLiteAdapter();
		List<Item> storedItems = dataSource.getAllItems();

		if (storedItems != null) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Received saved items from database, count = "
							+ storedItems.size());
		}
		
		SettingsManager manager = SettingsManager.getManager();
		for(Item item : storedItems) {
			// Collect the new prices
			List<ItemPrice> prices = new ArrayList<ItemPrice>();
			for(IPricingSource source : priceSources) {
				try {
					prices.addAll(source.getPrices(item));
				} catch (ApiException e) {
					// Default give up if not able to retrieve prices from a source
					Log.e(TrackedItemsActivity.LOG_TAG, e.getMessage());
				}
			}
			item.addPrices(prices);
			
			// Check the notifications
			List<ItemNotification> notifications = manager.getItemNotifications(item);
			for(ItemNotification notif : notifications) {
				INotificationPredicate pred = NotificationPredicateFactory.resolvePredicate(notif.getPredicate());
				if(pred.evaluate(item, notif.getThreshold())) {
					String message = pred.getNotificationMessage(this, item, notif.getThreshold());
					NotificationLauncher.launch(this, item, message);
				}
			}
			
			// Save the new information in the datastore
			dataSource.saveItem(item);
		}

		Log.d(TrackedItemsActivity.LOG_TAG,
				"ItemUpdateBackgroundService finishing");
		stopSelf();

	}

}
