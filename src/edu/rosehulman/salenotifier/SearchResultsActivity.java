package edu.rosehulman.salenotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SearchResultsActivity extends Activity {
	
	public static final String KEY_SEARCH_ITEM = "KEY_SEARCH_ITEM";
	public static final String KEY_SEARCH_RADIUS = "KEY_SEARCH_RADIUS";
	
	private Item mSearched;
	private double mSearchRadius;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		
		Intent launcher = getIntent();
		mSearched = launcher.getParcelableExtra(KEY_SEARCH_ITEM);
		mSearchRadius = launcher.getDoubleExtra(KEY_SEARCH_RADIUS, -1);
		
		if(mSearched == null){
			Toast.makeText(this, "Please provide something to search for!", Toast.LENGTH_LONG).show();
			finish();
		}
	}
}
