package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.R;
import edu.rosehulman.salenotifier.amazon.SearchAmazonItemsTask;
import edu.rosehulman.salenotifier.amazon.SearchAmazonItemsTask.ISearchAmazonCallback;
import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import edu.rosehulman.salenotifier.ebay.SearchEbayItemsTask;
import edu.rosehulman.salenotifier.ebay.SearchEbayItemsTask.ISearchEbayCallback;
import edu.rosehulman.salenotifier.ebay.SearchEbayItemsTask.ISearchEbayIncrementalResultListener;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResultsActivity extends StorageActivity implements
		ISearchEbayCallback, ISearchEbayIncrementalResultListener,
		ISearchAmazonCallback {

	public static final String KEY_SEARCH_ITEM = "KEY_SEARCH_ITEM";

	private ItemQueryConstraints mSearched;

	private SearchResultsAdapter mAdapter;
	private ListView mResultsList;
	private List<Item> mSearchResults;

	private IItemSourceAdapter mItemStorage;

	private List<AsyncTask<?, ?, ?>> mSearchTasks = new ArrayList<AsyncTask<?, ?, ?>>();

	private boolean mResultsViewSet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results_loading);

		Intent launcher = getIntent();
		mSearched = launcher.getParcelableExtra(KEY_SEARCH_ITEM);

		mItemStorage = new SQLiteAdapter();

		displaySearchToast();

		ItemSearchTask task = new ItemSearchTask(this, mSearched.getName());
		task.execute();
		mSearchTasks.add(task);

		SearchEbayItemsTask ebaySearch = new SearchEbayItemsTask(this, this,
				this);
		ebaySearch.execute(mSearched);
		mSearchTasks.add(ebaySearch);

		SearchAmazonItemsTask amazonSearch = new SearchAmazonItemsTask(this,
				this);
		amazonSearch.execute(mSearched);
		mSearchTasks.add(amazonSearch);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mSearchTasks != null) {
			for (AsyncTask<?, ?, ?> search : mSearchTasks) {
				search.cancel(true);
			}
		}
	}

	private void displaySearchToast() {
		if (mSearched == null) {
			Toast.makeText(this, "Please provide something to search for!",
					Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(
					this,
					"Searching for " + mSearched.getName() + "("
							+ mSearched.getProductCode() + ") within "
							+ mSearched.getSearchRadius() + " miles.",
					Toast.LENGTH_LONG).show();
		}
	}

	private void setResultsContentView() {
		mResultsViewSet = true;
		setContentView(R.layout.activity_search_results);
		mResultsViewSet = true;
		mResultsList = (ListView) findViewById(R.id.search_results_list);

		mResultsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mResultsList.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			private ArrayList<Integer> mSelected;

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				getMenuInflater().inflate(R.menu.actionmode_search_results,
						menu);
				mode.setTitle(R.string.actionmode_search_result_title);
				mSelected = new ArrayList<Integer>();
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.action_search_results_track:
					trackItems(getItems(mSelected));
					mode.finish();

					Intent result = new Intent();
					result.putExtra(ItemSearchActivity.KEY_SEARCH_FINISHED,
							true);
					setResult(RESULT_OK, result);
					finish();
					return true;
				case R.id.action_search_results_find:
					mode.finish();
					return true;

				default:
					return false;
				}
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked) {
					mSelected.add(position);
				} else {
					mSelected.remove((Integer) position);
				}
			}
		});
	}

	protected void onResultsLoaded(List<Item> searchResults) {
		if (searchResults == null || searchResults.size() == 0) {
			return; // nothing to do
		}

		if (!mResultsViewSet) {
			setResultsContentView();
		}

		if (mSearchResults == null) {
			mSearchResults = new ArrayList<Item>();
		}
		mSearchResults.addAll(searchResults);

		if (mAdapter == null) {
			mAdapter = new SearchResultsAdapter(this, new ArrayList<Item>());
			mResultsList.setAdapter(mAdapter);
		}
		mAdapter.addAll(searchResults);
	}

	private List<Item> getItems(List<Integer> itemPositions) {
		ArrayList<Item> items = new ArrayList<Item>();
		for (Integer position : itemPositions) {
			items.add((Item) mResultsList.getItemAtPosition(position));
		}
		return items;
	}

	private void trackItems(List<Item> items) {
		for (Item item : items) {
			mItemStorage.saveItem(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (mSearchTasks != null) {
			for (AsyncTask<?, ?, ?> searchTask : mSearchTasks) {
				searchTask.cancel(true);
			}
		}
		super.onBackPressed();
	}

	@Override
	public boolean publishPartialResults(List<Item> results) {
		if (results != null && results.size() > 0) {
			onResultsLoaded(results);
			return true;
		}
		return false;
	}

	@Override
	public void onFinished(List<Item> results) {
		if (results != null && results.size() != 0) {
			onResultsLoaded(results);
		}

		if (mSearchResults == null || mSearchResults.size() == 0) {
			// TODO Implement "No Results Found" message"
			Toast.makeText(this, R.string.no_results_found, Toast.LENGTH_LONG)
					.show();
		}
	}
}
