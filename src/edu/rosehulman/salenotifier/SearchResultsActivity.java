package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.db.SQLiteAdapter;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResultsActivity extends Activity {

	public static final String KEY_SEARCH_ITEM = "KEY_SEARCH_ITEM";

	private static ArrayList<Item> MOCK_SEARCH_RESULTS = new ArrayList<Item>();
	static {
		MOCK_SEARCH_RESULTS.add(new Item("Puppies", "imgur.com/r/aww",
				"http://i.imgur.com/N6g331A.jpg"));
		MOCK_SEARCH_RESULTS.add(new Item("Kittens", "imgur.com/r/aww",
				"http://i.imgur.com/1NW6KMM.jpg"));
		MOCK_SEARCH_RESULTS.add(new Item("Red Panda", "imgur.com/r/aww",
				"http://i.imgur.com/dkHRzwx.jpg"));
		MOCK_SEARCH_RESULTS.add(new Item("Foxxy", "imgur.com/r/aww",
				"http://i.imgur.com/yw7iAbc.jpg"));

	}

	private ItemQueryConstraints mSearched;

	private SearchResultsAdapter mAdapter;
	private ListView mResultsList;
	private List<Item> mSearchResults;

	private IItemSourceAdapter mItemStorage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results_loading);

		Intent launcher = getIntent();
		mSearched = launcher.getParcelableExtra(KEY_SEARCH_ITEM);
		mItemStorage = new SQLiteAdapter();

		displaySearchToast();

		findViewById(R.id.search_results_quit).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						onResultsLoaded(MOCK_SEARCH_RESULTS);
					}
				});

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

	protected void onResultsLoaded(List<Item> searchResults) {
		setContentView(R.layout.activity_search_results);

		mSearchResults = searchResults;
		mAdapter = new SearchResultsAdapter(this, searchResults);

		mResultsList = (ListView) findViewById(R.id.search_results_list);
		mResultsList.setAdapter(mAdapter);

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
}
