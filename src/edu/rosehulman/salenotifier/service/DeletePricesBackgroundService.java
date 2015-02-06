package edu.rosehulman.salenotifier.service;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import edu.rosehulman.salenotifier.IItemSourceAdapter;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.settings.Setting;
import edu.rosehulman.salenotifier.settings.SettingsManager;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DeletePricesBackgroundService extends IntentService {
	protected static final int DEFAULT_DELETE_AFTER = 30;

	public DeletePricesBackgroundService() {
		super("DeletePricesBackgroundService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!SaleNotifierSQLHelper.isInit()) {
			SaleNotifierSQLHelper.init(this);
		}

		IItemSourceAdapter dataSource = new SQLiteAdapter();

		Enumerable<Setting<?>> appSettings = SettingsManager.getManager()
				.getAppSettings();
		@SuppressWarnings("unchecked")
		Setting<Integer> appDeleteAfter = ((Setting<Integer>) appSettings
				.firstOrDefault(Setting
						.createNamePredicate(Setting.SETTING_NAME_DELETE_AFTER)));

		List<Item> trackedItems = dataSource.getAllItems();
		for (Item item : trackedItems) {
			Enumerable<Setting<?>> itemSettings = SettingsManager.getManager()
					.getItemSettings(item);
			@SuppressWarnings("unchecked")
			Setting<Integer> itemDeleteAfter = ((Setting<Integer>) itemSettings
					.firstOrDefault(Setting
							.createNamePredicate(Setting.SETTING_NAME_DELETE_AFTER)));

			int deleteAfterDays = itemDeleteAfter != null ? itemDeleteAfter
					.getValue() : (appDeleteAfter != null ? appDeleteAfter
					.getValue() : DEFAULT_DELETE_AFTER);
			GregorianCalendar expirationBeforeDate = new GregorianCalendar();
			expirationBeforeDate.add(GregorianCalendar.DAY_OF_YEAR, -1
					* deleteAfterDays);

			List<ItemPrice> itemPrices = item.getPrices();

			// TODO Prevent inconsistency errors from "checked out" ItemPrice
			// collections
			for (ItemPrice itemPrice : itemPrices) {
				if (itemPrice.getDate().before(expirationBeforeDate)) {
					dataSource.deleteItemPrice(itemPrice.getId());
				}
			}
		}
		stopSelf();
	}
}
