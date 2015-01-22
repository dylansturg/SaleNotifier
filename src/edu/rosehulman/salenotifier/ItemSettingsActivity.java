package edu.rosehulman.salenotifier;

import java.util.ArrayList;

import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.settings.Setting;
import edu.rosehulman.salenotifier.settings.SettingsManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class ItemSettingsActivity extends SettingsActivity {

	public static final String KEY_ITEM_ID = "KEY_ITEM_ID";

	private Item mItem;

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

		mHistoryDuration = (EditText) findViewById(R.id.item_settings_history_duration);
		mDataSourcesContainer = (LinearLayout) findViewById(R.id.item_settings_sources_container);
		mDataSourcesCheckBoxes = new ArrayList<CheckBox>();
		mNotificationsSwitch = (Switch) findViewById(R.id.item_settings_notifications_switch);

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
		return "" + mItem.getId();
	}
}
