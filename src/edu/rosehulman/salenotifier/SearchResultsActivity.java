package edu.rosehulman.salenotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SearchResultsActivity extends Activity {
	
	public static final String KEY_SEARCH_ITEM = "KEY_SEARCH_ITEM";
	
	private ItemQueryConstraints mSearched;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		
		Intent launcher = getIntent();
		mSearched = launcher.getParcelableExtra(KEY_SEARCH_ITEM);
		
		if(mSearched == null){
			Toast.makeText(this, "Please provide something to search for!", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, "Searching for " + mSearched.getName() + "(" + mSearched.getProductCode() + ") within " + mSearched.getSearchRadius() + " miles.", Toast.LENGTH_LONG).show();
		}
	}
}
