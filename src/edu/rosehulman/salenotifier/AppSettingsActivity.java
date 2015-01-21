package edu.rosehulman.salenotifier;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AppSettingsActivity extends Activity {

	private EditText mHistoryDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);

		mHistoryDuration = (EditText) findViewById(R.id.app_settings_history_duration);

		LinearLayout dataSourcesContainer = (LinearLayout) findViewById(R.id.app_settings_sources_container);
		String[] dataSources = getResources().getStringArray(
				R.array.settings_data_sources_options);
		for (String dataSource : dataSources) {
			CheckBox dataSourceCheckBox = new CheckBox(this);
			dataSourceCheckBox.setText(dataSource);
			dataSourceCheckBox.setChecked(true);
			dataSourcesContainer.addView(dataSourceCheckBox);
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
	
	private void saveSettings(){
		Toast.makeText(this, R.string.toast_settings_saved, Toast.LENGTH_SHORT).show();
	}
}
