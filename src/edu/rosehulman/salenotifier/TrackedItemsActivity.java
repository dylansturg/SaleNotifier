package edu.rosehulman.salenotifier;

import java.util.List;

import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import edu.rosehulman.salenotifier.models.Item;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class TrackedItemsActivity extends StorageActivity {

	public static final String LOG_TAG = "SNL";

	private ListView listView;
	private TrackedItemsListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracked_items);

		listView = (ListView) findViewById(R.id.tracked_items_list);
		List<Item> items = itemSource.getAllItems();
		listAdapter = new TrackedItemsListAdapter(this, items);
		listView.setAdapter(listAdapter);

		registerForContextMenu(listView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.context_tracked_items, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.context_tracked_current:
			return true;
		case R.id.context_tracked_history:
			return true;
		case R.id.context_tracked_options:
			launchItemSettings(info.id);
			return true;
		case R.id.context_tracked_delete:
			confirmDeletion(info.id, info.position);
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracked_items, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_search:
			launchSearch();
			return true;
		case R.id.action_settings:
			launchSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void launchItemSettings(long id) {
		Intent itemSettings = new Intent(this, ItemSettingsActivity.class);
		itemSettings.putExtra(ItemSettingsActivity.KEY_ITEM_ID, id);
		startActivity(itemSettings);
	}

	private void launchSettings() {
		Intent settingsIntent = new Intent(this, AppSettingsActivity.class);
		startActivity(settingsIntent);
	}

	private void launchSearch() {
		Intent searchIntent = new Intent(this, ItemSearchActivity.class);
		startActivity(searchIntent);
	}

	private void confirmDeletion(final long id, final int position) {
		DialogFragment deleteDialog = new DialogFragment() {
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle(R.string.dialog_delete_title);
				String name = listAdapter.getItem(position).getDisplayName();
				builder.setMessage(getString(R.string.dialog_delete_message,
						name));
				builder.setNegativeButton(android.R.string.cancel, null);
				builder.setPositiveButton(R.string.dialog_delete_positive,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deleteItem(id);
							}
						});
				return builder.create();
			}
		};
		deleteDialog.show(getFragmentManager(), "delete_confirm");
	}

	private void deleteItem(long id) {
		itemSource.deleteItem(id);
		updateItemList();
	}

	private void updateItemList() {
		List<Item> items = itemSource.getAllItems();
		listAdapter = new TrackedItemsListAdapter(this, items);
		listView.setAdapter(listAdapter);
	}
}
