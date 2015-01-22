package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class AppSettingsActivity extends Activity {

	public static final String KEY_CHECKED_DATA_SOURCES = "KEY_CHECKED_DATA_SOURCES";

	private EditText mHistoryDuration;
	private LinearLayout mDataSourcesContainer;
	private Switch mNotificationsSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);

		ArrayList<String> previouslyChecked = new ArrayList<String>();
		if(savedInstanceState != null){
			String[] checkedSources = savedInstanceState.getStringArray(KEY_CHECKED_DATA_SOURCES);
			if(checkedSources != null){
				previouslyChecked = new ArrayList<String>(Arrays.asList(checkedSources));
			}
		}
		
		mHistoryDuration = (EditText) findViewById(R.id.app_settings_history_duration);
		mDataSourcesContainer = (LinearLayout) findViewById(R.id.app_settings_sources_container);
		mNotificationsSwitch = (Switch) findViewById(R.id.app_settings_notifications_switch);

		String[] dataSources = getResources().getStringArray(
				R.array.settings_data_sources_options);
		for (String dataSource : dataSources) {
			CheckBox dataSourceCheckBox = new CheckBox(this);
			dataSourceCheckBox.setText(dataSource);
			mDataSourcesContainer.addView(dataSourceCheckBox);
			
			if(previouslyChecked.contains(dataSource)){
				dataSourceCheckBox.setChecked(true);
			}
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ArrayList<String> checkedSources = new ArrayList<String>();
		for (int i = 0; i < mDataSourcesContainer.getChildCount(); i++) {
			View child = mDataSourcesContainer.getChildAt(i);
			try {
				CheckBox checkBox = (CheckBox) child;
				if(checkBox.isChecked()){
					checkedSources.add(checkBox.getText().toString());
				}
			} catch (Exception e) {

			}
		}

		outState.putStringArray(KEY_CHECKED_DATA_SOURCES, checkedSources.toArray(new String[checkedSources.size()]));
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

	private void saveSettings() {
		Toast.makeText(this, R.string.toast_settings_saved, Toast.LENGTH_SHORT)
				.show();
	}
}
