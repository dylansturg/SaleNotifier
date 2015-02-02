package edu.rosehulman.salenotifier.service;

import java.util.List;

import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import edu.rosehulman.salenotifier.models.Item;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ItemUpdateBackgroundService extends IntentService {

	public ItemUpdateBackgroundService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (!SaleNotifierSQLHelper.isInit()) {
			SaleNotifierSQLHelper.init(this);
		}

		SQLiteAdapter dataSource = new SQLiteAdapter();
		List<Item> storedItems = dataSource.getAllItems();

		if (storedItems != null) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Received saved items from database, count = "
							+ storedItems.size());
		}

		stopSelf();

	}

}
