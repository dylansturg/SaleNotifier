package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.Enumerable.IPredicate;
import edu.rosehulman.salenotifier.settings.Setting;
import edu.rosehulman.salenotifier.settings.SettingsManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public abstract class SettingsActivity extends StorageActivity {

	protected static final String KEY_CHECKED_DATA_SOURCES = "KEY_CHECKED_DATA_SOURCES";
	protected static final String KEY_CHECKED_NOTIFICATIONS = "KEY_CHECKED_NOTIFICATIONS";
	protected static final String KEY_DELETE_AFTER_SETTING = "KEY_DELETE_AFTER_SETTING";

	protected static final int DEFAULT_DAYS_TO_SAVE = 30;

	protected EditText mHistoryDuration;
	protected LinearLayout mDataSourcesContainer;
	protected List<CheckBox> mDataSourcesCheckBoxes;
	protected Switch mNotificationsSwitch;

	protected Enumerable<Setting<?>> mSettings;

	protected abstract String getSettingsTarget();

	protected void refreshSettings() {
		mSettings = SettingsManager.getManager().getSettingsForTarget(
				getSettingsTarget());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		refreshSettings();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		int deleteAfterSetting;
		try {
			deleteAfterSetting = Integer.parseInt(mHistoryDuration.getText()
					.toString());
			outState.putInt(KEY_DELETE_AFTER_SETTING, deleteAfterSetting);
		} catch (NumberFormatException parseFailed) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to parse Delete After text field");
		}

		outState.putBoolean(KEY_CHECKED_NOTIFICATIONS,
				mNotificationsSwitch.isChecked());

		ArrayList<String> checkedSources = new ArrayList<String>();
		for (CheckBox checkBox : mDataSourcesCheckBoxes) {
			if (checkBox.isChecked()) {
				checkedSources.add(checkBox.getText().toString());
			}
		}
		outState.putStringArrayList(KEY_CHECKED_DATA_SOURCES, checkedSources);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void saveSettings() {
		refreshSettings();

		Enumerable<Setting<Boolean>> dataSourceSettings = getDataSourceSettings();
		captureCurrentDataSourceSettings(dataSourceSettings);
		SettingsManager.getManager().saveSettings(
				(Enumerable) dataSourceSettings);

		saveNotificationsSetting();
		saveDeleteAfterSetting();

		Toast.makeText(this, R.string.toast_settings_saved, Toast.LENGTH_SHORT)
				.show();

	}

	private void saveDeleteAfterSetting() {
		@SuppressWarnings("unchecked")
		Setting<Integer> deleteAfter = (Setting<Integer>) mSettings
				.firstOrDefault(new IPredicate<Setting<?>>() {
					@Override
					public boolean match(Setting<?> element) {
						return element.getName().equalsIgnoreCase(
								Setting.SETTING_NAME_DELETE_AFTER);
					}
				});

		if (deleteAfter == null) {
			deleteAfter = new Setting<Integer>();
			deleteAfter.setName(Setting.SETTING_NAME_DELETE_AFTER);
			deleteAfter.setTarget(getSettingsTarget());
		}

		int daysToSave;
		try {
			daysToSave = Integer
					.parseInt(mHistoryDuration.getText().toString());
		} catch (NumberFormatException exp) {
			daysToSave = DEFAULT_DAYS_TO_SAVE;
		}

		deleteAfter.setValue(daysToSave);
		SettingsManager.getManager().saveSetting(deleteAfter);
	}

	private void saveNotificationsSetting() {
		@SuppressWarnings("unchecked")
		Setting<Boolean> allowNotifications = (Setting<Boolean>) mSettings
				.firstOrDefault(new IPredicate<Setting<?>>() {
					@Override
					public boolean match(Setting<?> element) {
						return element.getName().equalsIgnoreCase(
								Setting.SETTING_NAME_NOTIFICATIONS);
					}
				});

		if (allowNotifications == null) {
			allowNotifications = new Setting<Boolean>();
			allowNotifications.setTarget(getSettingsTarget());
			allowNotifications.setName(Setting.SETTING_NAME_NOTIFICATIONS);
		}

		allowNotifications.setValue(mNotificationsSwitch.isChecked());

		SettingsManager.getManager().saveSetting(allowNotifications);
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Enumerable<Setting<Boolean>> getDataSourceSettings() {
		Enumerable<Setting<Boolean>> dataSourceSettings = new Enumerable<Setting<Boolean>>();
		try {
			dataSourceSettings = (Enumerable) mSettings
					.where(new IPredicate<Setting<?>>() {
						@Override
						public boolean match(Setting<?> element) {
							return element.getName().contains(
									Setting.DATA_SOURCE_PREFIX);
						}

					});
		} catch (Exception settingsFailure) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to query and parse App settins for data sources");
		}
		return dataSourceSettings;
	}

	private void captureCurrentDataSourceSettings(
			Enumerable<Setting<Boolean>> settings) {
		assert (settings != null);

		for (final CheckBox checkBox : mDataSourcesCheckBoxes) {
			final String dataSource = checkBox.getText().toString();
			IPredicate<Setting<Boolean>> sourceSettingPred = new IPredicate<Setting<Boolean>>() {
				@Override
				public boolean match(Setting<Boolean> element) {
					return element.getName().equalsIgnoreCase(
							String.format(Setting.DATA_SOURCE_NAME_FORMAT,
									dataSource));
				}
			};

			Setting<Boolean> sourceSetting = settings
					.firstOrDefault(sourceSettingPred);
			if (sourceSetting == null) {
				sourceSetting = new Setting<Boolean>();
				sourceSetting.setTarget(getSettingsTarget());
				sourceSetting.setName(String.format(
						Setting.DATA_SOURCE_NAME_FORMAT, dataSource));
				settings.add(sourceSetting);
			}
			sourceSetting.setValue(checkBox.isChecked());
		}
	}

}
