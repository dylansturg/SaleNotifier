package edu.rosehulman.salenotifier;

import java.io.File;
import java.io.IOException;
import java.util.List;
import edu.rosehulman.salenotifier.R;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.service.ItemUpdateBackgroundService;
import edu.rosehulman.salenotifier.service.SaleNotifierWakefulReceiver;
import edu.rosehulman.salenotifier.service.UpdateResultReceiver;
import edu.rosehulman.salenotifier.service.UpdateResultReceiver.IOnItemsUpdated;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class TrackedItemsActivity extends StorageActivity implements
		OnItemClickListener, OnItemLongClickListener {

	public static final String LOG_TAG = "SNL";
	protected static final String HTTP_CACHE = "httpCache";
	protected static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10MiB

	private ListView listView;
	private TrackedItemsListAdapter listAdapter;

	private ActionMode mActiveActionMode;
	private TrackedItemsActionMode mActionModeCallback = new TrackedItemsActionMode();
	private MenuItem mRefreshMenuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_tracked_items);

		listView = (ListView) findViewById(R.id.tracked_items_list);
		List<Item> items = itemSource.getAllItems();
		listAdapter = new TrackedItemsListAdapter(this, items);
		listView.setAdapter(listAdapter);

		if (items == null || items.size() == 0) {
			findViewById(R.id.tracked_items_search).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.tracked_items_search).setVisibility(View.GONE);
		}

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

		// Doesn't create a new alarm if it is already set
		new SaleNotifierWakefulReceiver().setupRegularAlarm(this);

		if (HttpResponseCache.getInstalled() == null) {
			installHTTPCache();
		}

	}

	private void installHTTPCache() {
		File cacheDir = getExternalCacheDir();
		if (cacheDir == null) {
			cacheDir = getCacheDir();
		}

		if (cacheDir != null) {
			File httpCacheDir = new File(cacheDir + HTTP_CACHE);
			try {
				HttpResponseCache.install(httpCacheDir, HTTP_CACHE_SIZE);
			} catch (IOException e) {
				Log.d(LOG_TAG,
						"Failed to install HTTP Cache - continuing w/o cache");
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		List<Item> items = itemSource.getAllItems();
		listAdapter = new TrackedItemsListAdapter(this, items);
		listView.setAdapter(listAdapter);

		if (items == null || items.size() == 0) {
			findViewById(R.id.tracked_items_search).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.tracked_items_search).setVisibility(View.GONE);
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
		case R.id.action_refresh:
			mRefreshMenuItem = item;
			item.setEnabled(false);
			setProgressBarIndeterminateVisibility(true);
			UpdateResultReceiver updatedResultReceiver = new UpdateResultReceiver(
					new Handler());
			updatedResultReceiver.setOnItemsUpdated(new IOnItemsUpdated() {
				@Override
				public void onUpdateFinished() {
					setProgressBarIndeterminateVisibility(false);
					mRefreshMenuItem.setEnabled(true);
				}
			});

			Intent launchUpdater = new Intent(this,
					ItemUpdateBackgroundService.class);
			launchUpdater.putExtra(
					ItemUpdateBackgroundService.KEY_RESULT_RECEIVER,
					updatedResultReceiver);
			startService(launchUpdater);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void launchItemCurrent(long id) {
		Intent itemCurrent = new Intent(this, ItemCurrentActivity.class);
		itemCurrent.putExtra(ItemCurrentActivity.KEY_ITEM_ID, id);
		startActivity(itemCurrent);
	}

	private void launchItemSettings(long id) {
		Intent itemSettings = new Intent(this, ItemSettingsActivity.class);
		itemSettings.putExtra(ItemSettingsActivity.KEY_ITEM_ID, id);
		startActivity(itemSettings);
	}

	private void launchDetailedItem(long id) {
		Intent itemDatabaseDetails = new Intent(this,
				ItemDatabaseContentActivity.class);
		itemDatabaseDetails.putExtra(ItemDatabaseContentActivity.KEY_ITEM_ID,
				id);
		startActivity(itemDatabaseDetails);
	}

	private void launchSettings() {
		Intent settingsIntent = new Intent(this, AppSettingsActivity.class);
		startActivity(settingsIntent);
	}

	private void launchSearch() {
		Intent searchIntent = new Intent(this, ItemSearchActivity.class);
		startActivity(searchIntent);
	}

	private void confirmDeletion(final Item deleted) {
		final long id = deleted.getId();
		final String name = deleted.getDisplayName();

		DialogFragment deleteDialog = new DialogFragment() {
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle(R.string.dialog_delete_title);
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

	// OnItemClick listener for the tracked items list
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Item item = (Item) parent.getAdapter().getItem(position);
		if (!mActionModeCallback.isActive()) {
			launchItemCurrent(item.getId());
		} else {
			mActionModeCallback.setSelected(view, item);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Item selected = (Item) parent.getAdapter().getItem(position);
		if (mActionModeCallback != null) {
			mActionModeCallback.setSelected(view, selected);
			if (mActiveActionMode == null) {
				mActiveActionMode = startActionMode(mActionModeCallback);
			}
		}

		return true;
	}

	public void startSearching(View sender) {
		launchSearch();
	}

	private class TrackedItemsActionMode implements ActionMode.Callback {

		private View mSelectedView;
		private Item mSelectedItem;
		private boolean mActive = false;

		public boolean isActive() {
			return mActive;
		}

		public void setSelected(View view, Item item) {
			if (mSelectedView != null) {
				if (mSelectedItem != null) {
					listAdapter.setActivated(false, mSelectedItem.getId());
				} else {
					listAdapter.clearAllActivated();
				}
				mSelectedView.setActivated(false);
			}

			if (item == mSelectedItem && mActiveActionMode != null) {
				mActiveActionMode.finish(); // exit when unselect
				return;
			}

			listAdapter.setActivated(true, item.getId());
			view.setActivated(true);
			mSelectedView = view;
			mSelectedItem = item;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			getMenuInflater().inflate(R.menu.context_tracked_items, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			mActive = true;
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.context_tracked_current:
				launchItemCurrent(mSelectedItem.getId());
				break;
			case R.id.context_tracked_history:
				Intent historyIntent = new Intent(TrackedItemsActivity.this,
						ItemHistoryActivity.class);
				historyIntent.putExtra(ItemHistoryActivity.KEY_ITEM_ID,
						mSelectedItem.getId());
				startActivity(historyIntent);
				break;
			case R.id.context_tracked_options:
				launchItemSettings(mSelectedItem.getId());
				break;
			case R.id.context_tracked_delete:
				confirmDeletion(mSelectedItem);
				break;
			default:
				return false;
			}

			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActive = false;
			if (mSelectedView != null) {
				mSelectedView.setActivated(false);
			}
			mActiveActionMode = null;

			listAdapter.clearAllActivated();
		}
	}
}
