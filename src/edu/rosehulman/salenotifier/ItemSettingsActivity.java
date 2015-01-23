package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.Enumerable.IPredicate;
import edu.rosehulman.salenotifier.models.Item;
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

	private static final String NOTIFICATION_SEPARATOR = ":";

	private Item mItem;
	private long mItemId;

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

		mItemId = launcher.getLongExtra(KEY_ITEM_ID, -1);
		mItem = itemSource.getItemById(mItemId);
		if (mItem == null) {
			alertNonExistentItem();
			return;
		}

		// Set mItemId before refreshSettings
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
		ItemNotificationView view = (ItemNotificationView) clicked.getParent().getParent();
		mNotificationsContainer.removeView(view);
		notificationViews.remove(view);
	}

	private void displayNotificationSettings() {
		Enumerable<Setting<?>> notificationSettings = mSettings
				.where(Setting
						.createNamePredicate(Setting.SETTING_NAME_SPECIFIC_NOTIFICATIONS));

		List<NotificationPredicate> availabledPreds = NotificationPredicateFactory
				.getAvailablePredicates();
		for (Setting<?> setting : notificationSettings) {
			String value = (String) setting.getValue();
			String[] options = value.split(NOTIFICATION_SEPARATOR);
			ItemNotificationView view = new ItemNotificationView(this,
					availabledPreds, options[0], options[1], setting.getId());
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

	@SuppressWarnings("unchecked")
	@Override
	protected void saveSettings() {
		super.saveSettings();

		List<Setting<?>> itemNotifications = mSettings
				.where(Setting
						.createNamePredicate(Setting.SETTING_NAME_SPECIFIC_NOTIFICATIONS));
		List<Setting<?>> remainingNotifications = new ArrayList<Setting<?>>();

		for (ItemNotificationView notificationView : notificationViews) {
			if(notificationView.getThresholdValue().isEmpty()){
				continue;
			}
			
			Setting<String> notificationSetting = null;
			if (notificationView.getNotificationId() >= 0) {
				final long settingId = notificationView.getNotificationId();
				notificationSetting = (Setting<String>) mSettings
						.firstOrDefault(new IPredicate<Setting<?>>() {
							@Override
							public boolean match(Setting<?> element) {
								return element.getId() == settingId;
							}
						});
			}

			if (notificationSetting == null) {
				notificationSetting = new Setting<String>();
				notificationSetting
						.setName(Setting.SETTING_NAME_SPECIFIC_NOTIFICATIONS);
				notificationSetting.setTarget(getSettingsTarget());
			}

			remainingNotifications.add(notificationSetting);

			notificationSetting.setValue(notificationView
					.getNotificationPredicate()
					+ NOTIFICATION_SEPARATOR
					+ notificationView.getThresholdValue());

			SettingsManager.getManager().saveSetting(notificationSetting);
		}
		
		itemNotifications.removeAll(remainingNotifications);
		SettingsManager.getManager().deleteAll(itemNotifications);
	}

	@Override
	protected String getSettingsTarget() {
		return "" + mItemId;
	}
}
