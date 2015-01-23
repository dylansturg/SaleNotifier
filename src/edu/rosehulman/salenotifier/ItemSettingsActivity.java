package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.Enumerable.IPredicate;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemNotification;
import edu.rosehulman.salenotifier.models.NotificationPredicate;
import edu.rosehulman.salenotifier.notifications.NotificationPredicateFactory;
import edu.rosehulman.salenotifier.settings.Setting;
import edu.rosehulman.salenotifier.settings.SettingsManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class ItemSettingsActivity extends SettingsActivity {

	public static final String KEY_ITEM_ID = "KEY_ITEM_ID";

	private Item mItem;

	private LinearLayout mNotificationsContainer;
	private List<ItemNotificationView> notificationViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_settings);

		Intent launcher = getIntent();
		if (launcher == null || !launcher.hasExtra(KEY_ITEM_ID)) {
			alertNonExistentItem();
			return;
		}

		long itemId = launcher.getLongExtra(KEY_ITEM_ID, -1);
		mItem = itemSource.getItemById(itemId);
		if (mItem == null) {
			alertNonExistentItem();
			return;
		}

		// Set mItem before querying settings
		refreshSettings();

		mHistoryDuration = (EditText) findViewById(R.id.item_settings_history_duration);
		mDataSourcesContainer = (LinearLayout) findViewById(R.id.item_settings_sources_container);
		mDataSourcesCheckBoxes = new ArrayList<CheckBox>();
		mNotificationsSwitch = (Switch) findViewById(R.id.item_settings_notifications_switch);
		mNotificationsContainer = (LinearLayout) findViewById(R.id.item_settings_notifications_container);
		notificationViews = new ArrayList<ItemNotificationView>();

		displayCachedOrSavedSettings(savedInstanceState);
		displayNotificationSettings();
	}

	public void addNotificationView(View clicked) {
		ItemNotificationView view = new ItemNotificationView(this,
				NotificationPredicateFactory.getAvailablePredicates());
		mNotificationsContainer.addView(view);
		notificationViews.add(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings_save:
			saveSettings();
			return true;
		case R.id.action_settings_close:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void removeNotification(View clicked) {
		ItemNotificationView view = (ItemNotificationView) clicked.getParent()
				.getParent();
		mNotificationsContainer.removeView(view);
		notificationViews.remove(view);
	}

	private void displayNotificationSettings() {
		Enumerable<ItemNotification> notifications = SettingsManager
				.getManager().getItemNotifications(mItem);
		if (notifications == null) {
			// None to display
			return;
		}

		List<NotificationPredicate> availabledPreds = NotificationPredicateFactory
				.getAvailablePredicates();

		for (ItemNotification notification : notifications) {
			ItemNotificationView view = new ItemNotificationView(this,
					availabledPreds, notification.getPredicate(),
					notification.getThreshold(), notification.getId());
			mNotificationsContainer.addView(view);
			notificationViews.add(view);
		}
	}

	private void alertNonExistentItem() {
		Toast.makeText(this, R.string.item_settings_no_item, Toast.LENGTH_LONG)
				.show();
		Log.d(TrackedItemsActivity.LOG_TAG,
				"Attempt to create settings for unsaved item");
		finish();
	}

	@Override
	protected void saveSettings() {
		super.saveSettings();

		Enumerable<ItemNotification> itemNotifications = SettingsManager
				.getManager().getItemNotifications(mItem);
		if (itemNotifications == null) {
			itemNotifications = new Enumerable<ItemNotification>();
		}
		List<ItemNotification> remainingNotifications = new ArrayList<ItemNotification>();

		for (ItemNotificationView notificationView : notificationViews) {
			if (notificationView.getThresholdValue() == null) {
				continue;
			}

			ItemNotification notification = null;
			if (notificationView.getNotificationId() >= 0) {
				final long settingId = notificationView.getNotificationId();
				notification = itemNotifications
						.firstOrDefault(new IPredicate<ItemNotification>() {
							@Override
							public boolean match(ItemNotification element) {
								return element.getId() == settingId;
							}
						});
			}

			if (notification == null) {
				notification = new ItemNotification();
				notification.setItemId(mItem.getId());
			}

			remainingNotifications.add(notification);
			notification.setPredicate(notificationView
					.getNotificationPredicate());
			notification.setThreshold(notificationView.getThresholdValue());
			SettingsManager.getManager().saveItemNotification(notification);
		}

		itemNotifications.removeAll(remainingNotifications);
		SettingsManager.getManager().deleteItemNotifications(itemNotifications);
	}

	@Override
	protected String getSettingsTarget() {
		return "" + mItem.getId();
	}
}
