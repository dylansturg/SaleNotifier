package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.settings.Setting;
import edu.rosehulman.salenotifier.settings.SettingsManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

public class AppSettingsActivity extends SettingsActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);

		mHistoryDuration = (EditText) findViewById(R.id.app_settings_history_duration);
		mDataSourcesContainer = (LinearLayout) findViewById(R.id.app_settings_sources_container);
		mDataSourcesCheckBoxes = new ArrayList<CheckBox>();
		mNotificationsSwitch = (Switch) findViewById(R.id.app_settings_notifications_switch);

		ArrayList<String> previouslyChecked = null;
		Boolean previousNotifications = null;
		Integer previousDeleteAfter = null;
		if (savedInstanceState != null) {
			previouslyChecked = savedInstanceState
					.getStringArrayList(KEY_CHECKED_DATA_SOURCES);

			if (savedInstanceState.containsKey(KEY_CHECKED_NOTIFICATIONS)) {
				previousNotifications = savedInstanceState
						.getBoolean(KEY_CHECKED_NOTIFICATIONS);
			}
			if (savedInstanceState.containsKey(KEY_DELETE_AFTER_SETTING)) {
				previousDeleteAfter = savedInstanceState
						.getInt(KEY_DELETE_AFTER_SETTING);
			}
		}

		presentDataSourceSettings(previouslyChecked);
		presentNotificationsSetting(previousNotifications);
		presentDeleteAfterSetting(previousDeleteAfter);

	}

	private void presentDeleteAfterSetting(Integer localVersion) {
		if (localVersion != null) {
			mHistoryDuration.setText(localVersion.toString());
		} else {
			@SuppressWarnings("unchecked")
			Setting<Integer> preference = (Setting<Integer>) mSettings
					.firstOrDefault(Setting
							.createNamePredicate(Setting.SETTING_NAME_DELETE_AFTER));
			if (preference != null) {
				mHistoryDuration.setText(preference.getValue().toString());
			}
		}
	}

	private void presentNotificationsSetting(Boolean localVersion) {
		if (localVersion != null) {
			mNotificationsSwitch.setChecked(localVersion);
		} else {
			@SuppressWarnings("unchecked")
			Setting<Boolean> preference = (Setting<Boolean>) mSettings
					.firstOrDefault(Setting
							.createNamePredicate(Setting.SETTING_NAME_NOTIFICATIONS));
			if (preference != null) {
				mNotificationsSwitch.setChecked(preference.getValue());
			}
		}
	}

	private void presentDataSourceSettings(List<String> localVersion) {
		String[] dataSources = getResources().getStringArray(
				R.array.settings_data_sources_options);
		for (final String dataSource : dataSources) {
			CheckBox dataSourceCheckBox = new CheckBox(this);
			dataSourceCheckBox.setText(dataSource);
			mDataSourcesContainer.addView(dataSourceCheckBox);
			mDataSourcesCheckBoxes.add(dataSourceCheckBox);

			if (localVersion != null) {
				dataSourceCheckBox
						.setChecked(localVersion.contains(dataSource));
			} else {
				@SuppressWarnings("unchecked")
				Setting<Boolean> preference = (Setting<Boolean>) mSettings
						.firstOrDefault(Setting.createNamePredicate(String
								.format(Setting.DATA_SOURCE_NAME_FORMAT,
										dataSource)));
				if (preference != null) {
					dataSourceCheckBox.setChecked(preference.getValue());
				} else {
					dataSourceCheckBox.setChecked(false);
				}
			}
		}
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
			return false;
		}
	}

	
	@Override
	protected String getSettingsTarget() {
		return SettingsManager.KEY_APP_SETTINGS;
	}
}
