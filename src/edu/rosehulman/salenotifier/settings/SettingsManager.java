package edu.rosehulman.salenotifier.settings;

import java.util.List;

import android.util.Log;

import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.SettingsSQLiteAdapter;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemNotification;

public class SettingsManager {
	public static final String KEY_APP_SETTINGS = "app";

	private SettingsSQLiteAdapter sqlSource;

	private static SettingsManager instance;

	public static SettingsManager getManager() {
		if (instance == null) {
			instance = new SettingsManager();
		}
		return instance;
	}

	public SettingsManager() {
		sqlSource = new SettingsSQLiteAdapter();
	}

	public boolean appAllowsNotifications() {
		Setting<?> appSetting = getAppSettings()
				.firstOrDefault(
						Setting.createNamePredicate(Setting.SETTING_NAME_NOTIFICATIONS));
		if (appSetting != null) {
			try {
				@SuppressWarnings("unchecked")
				Setting<Boolean> allowsNotifications = (Setting<Boolean>) appSetting;
				return allowsNotifications.getValue();
			} catch (Exception failure) {
				Log.d(TrackedItemsActivity.LOG_TAG,
						"Failed to retrieve app's notification setting");
			}
		}
		// Notifications enabled by default
		return true;
	}

	public boolean itemAllowsNotifications(Item item) {
		Setting<?> itemSetting = getItemSettings(item)
				.firstOrDefault(
						Setting.createNamePredicate(Setting.SETTING_NAME_NOTIFICATIONS));
		if (itemSetting != null) {
			try {
				@SuppressWarnings("unchecked")
				Setting<Boolean> allowsNotifications = (Setting<Boolean>) itemSetting;
				return allowsNotifications.getValue();
			} catch (Exception failure) {
				Log.d(TrackedItemsActivity.LOG_TAG, "Failed to retrieve item ("
						+ item.getProductCode() + ")'s notification setting");
			}
		}
		// Notifications enabled by default
		return true;
	}

	public Enumerable<Setting<?>> getSettingsForTarget(String target) {
		return sqlSource.getSettingsForTarget(target);
	}

	public Enumerable<Setting<?>> getAppSettings() {
		return sqlSource.getSettingsForTarget(KEY_APP_SETTINGS);
	}

	/**
	 * Get the collection of notifications registered for the item. May be null.
	 * 
	 * @param item
	 * @return collection of notification
	 */
	public Enumerable<ItemNotification> getItemNotifications(Item item) {
		if (item == null) {
			throw new IllegalArgumentException(
					"Attempt to get notifications for null Item");
		}

		if (!appAllowsNotifications() || !itemAllowsNotifications(item)) {
			return null;
		}

		return sqlSource.getNotificationsForItem(item);
	}

	public void saveItemNotification(ItemNotification notification) {
		sqlSource.saveNotification(notification);
	}

	public void saveItemNotifications(List<ItemNotification> notifications) {
		sqlSource.saveNotifications(notifications);
	}

	public void deleteItemNotification(ItemNotification notification) {
		sqlSource.deleteNotification(notification);
	}

	public void deleteItemNotifications(List<ItemNotification> notifications) {
		sqlSource.deleteNotifications(notifications);
	}

	public Enumerable<Setting<?>> getItemSettings(Item item) {
		if (item == null) {
			throw new IllegalArgumentException(
					"Cannot get settings for null item.");
		}
		return sqlSource.getSettingsForTarget("" + item.getId());
	}

	public void saveSettings(List<Setting<?>> settings) {
		if (settings == null) {
			throw new IllegalArgumentException(
					"Cannot save settings for null collection.");
		}
		sqlSource.saveAll(settings);
	}

	public void saveSetting(Setting<?> setting) {
		if (setting == null) {
			throw new IllegalArgumentException(
					"Cannot save settings for null item.");
		}
		sqlSource.saveSetting(setting);
	}

	public void deleteSetting(Setting<?> setting) {
		if (setting == null) {
			throw new IllegalArgumentException(
					"Cannot delete settings for null item.");
		}
		sqlSource.deleteSetting(setting);
	}

	public void deleteAll(List<Setting<?>> settings) {
		if (settings == null) {
			throw new IllegalArgumentException(
					"Cannot delete settings for null collection.");
		}
		sqlSource.deleteAll(settings);
	}
}
