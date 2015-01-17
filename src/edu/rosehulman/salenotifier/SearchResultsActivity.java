package edu.rosehulman.salenotifier;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SearchResultsActivity extends Activity {
	
	public static final String KEY_SEARCH_ITEM = "KEY_SEARCH_ITEM";
	public static final String KEY_SEARCH_RADIUS = "KEY_SEARCH_RADIUS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
	}
}
