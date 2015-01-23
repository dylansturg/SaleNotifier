package edu.rosehulman.salenotifier.db;

import java.util.List;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemNotification;
import edu.rosehulman.salenotifier.settings.Setting;

public class SettingsSQLiteAdapter {

	public SettingsSQLiteAdapter() {
	}

	public void saveSetting(Setting<?> setting) {
		SettingDataAdapter settingSource = new SettingDataAdapter();
		settingSource.update(setting);
	}

	public void saveAll(List<Setting<?>> settings) {
		for (Setting<?> setting : settings) {
			saveSetting(setting);
		}
	}

	public Enumerable<Setting<?>> getAllSettings() {
		SettingDataAdapter settingSource = new SettingDataAdapter();
		List<Setting<?>> settings = settingSource.getAll(null, null, null);
		return new Enumerable<Setting<?>>(settings);
	}

	public Enumerable<Setting<?>> getSettingsForTarget(String target) {
		SettingDataAdapter settingSource = new SettingDataAdapter();
		List<Setting<?>> settings = settingSource.getAll(
				SettingDataAdapter.DB_KEY_TARGET + " = \"" + target + "\"",
				null, null);
		return new Enumerable<Setting<?>>(settings);
	}

	public void deleteSetting(Setting<?> setting) {
		SettingDataAdapter settingSource = new SettingDataAdapter();
		settingSource.delete(setting);
	}

	public void deleteAll(List<Setting<?>> settings) {
		SettingDataAdapter settingSource = new SettingDataAdapter();
		for (Setting<?> setting : settings) {
			settingSource.delete(setting);
		}
	}

	public Enumerable<ItemNotification> getNotificationsForItem(Item target) {
		if (target == null || target.getId() < 0) {
			return null;
		}

		ItemNotificationDataAdapter notificationSource = new ItemNotificationDataAdapter();
		List<ItemNotification> notifications = notificationSource.getAll(
				ItemNotificationDataAdapter.DB_KEY_ITEM_ID + "="
						+ target.getId(), null, null);

		return new Enumerable<ItemNotification>(notifications);
	}

	public void saveNotification(ItemNotification notification) {
		ItemNotificationDataAdapter notificationSource = new ItemNotificationDataAdapter();
		notificationSource.update(notification);
	}

	public void saveNotifications(List<ItemNotification> notifications) {
		ItemNotificationDataAdapter notificationSource = new ItemNotificationDataAdapter();
		for (ItemNotification itemNotification : notifications) {
			notificationSource.update(itemNotification);
		}
	}

	public void deleteNotification(ItemNotification notification) {
		ItemNotificationDataAdapter notificationSource = new ItemNotificationDataAdapter();
		notificationSource.delete(notification);
	}

	public void deleteNotifications(List<ItemNotification> notifications) {
		ItemNotificationDataAdapter notificationSource = new ItemNotificationDataAdapter();
		for (ItemNotification itemNotification : notifications) {
			notificationSource.delete(itemNotification);
		}
	}

}
