package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResultsActivity extends Activity {
	
	public static final String KEY_SEARCH_ITEM = "KEY_SEARCH_ITEM";
	
	private static ArrayList<Item> MOCK_SEARCH_RESULTS = new ArrayList<Item>();
	static {
		MOCK_SEARCH_RESULTS.add(new Item("Puppies", "imgur.com/r/aww", "http://i.imgur.com/N6g331A.jpg"));
		MOCK_SEARCH_RESULTS.add(new Item("Kittens", "imgur.com/r/aww", "http://i.imgur.com/1NW6KMM.jpg"));
		MOCK_SEARCH_RESULTS.add(new Item("Red Panda", "imgur.com/r/aww", "http://i.imgur.com/dkHRzwx.jpg"));
		MOCK_SEARCH_RESULTS.add(new Item("Foxxy", "imgur.com/r/aww", "http://i.imgur.com/yw7iAbc.jpg"));
		
	}
	
	private ItemQueryConstraints mSearched;
	
	private SearchResultsAdapter mAdapter;
	private ListView mResultsList;
	private List<Item> mSearchResults;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results_loading);
		
		Intent launcher = getIntent();
		mSearched = launcher.getParcelableExtra(KEY_SEARCH_ITEM);
		
		if(mSearched == null){
			Toast.makeText(this, "Please provide something to search for!", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, "Searching for " + mSearched.getName() + "(" + mSearched.getProductCode() + ") within " + mSearched.getSearchRadius() + " miles.", Toast.LENGTH_LONG).show();
		}
		
		findViewById(R.id.search_results_quit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onResultsLoaded(MOCK_SEARCH_RESULTS);
			}
		});
	}
	
	protected void onResultsLoaded(List<Item> searchResults){
		setContentView(R.layout.activity_search_results);
		
		mSearchResults = searchResults;
		mAdapter = new SearchResultsAdapter(this, searchResults);
		
		mResultsList = (ListView)findViewById(R.id.search_results_list);
		mResultsList.setAdapter(mAdapter);
	}
}
