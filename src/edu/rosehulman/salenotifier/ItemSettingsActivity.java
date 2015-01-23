package edu.rosehulman.salenotifier;

import java.util.ArrayList;

import edu.rosehulman.salenotifier.models.Item;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class ItemSettingsActivity extends SettingsActivity {

	public static final String KEY_ITEM_ID = "KEY_ITEM_ID";

	private Item mItem;
	private long mItemId;

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

		displayCachedOrSavedSettings(savedInstanceState);
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

	private void alertNonExistentItem() {
		Toast.makeText(this, R.string.item_settings_no_item, Toast.LENGTH_LONG)
				.show();
		Log.d(TrackedItemsActivity.LOG_TAG,
				"Attempt to create settings for unsaved item");
		finish();
	}

	@Override
	protected String getSettingsTarget() {
		return "" + mItemId;
	}
}
