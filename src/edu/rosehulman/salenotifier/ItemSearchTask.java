package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class ItemSearchTask extends AsyncTask<Void, Void, List<Item>> {

	private SearchResultsActivity context;
	private String searchString;
	
	public ItemSearchTask(SearchResultsActivity context, String searchString) {
		this.context = context;
		this.searchString = searchString;
	}
	
	@Override
	protected List<Item> doInBackground(Void... params) {
		
		List<Item> results = new ArrayList<Item>();
		IPricingSource source = new Semantics3PriceSource();
		try {
			results.addAll(source.searchForItems(searchString));
		} catch (ApiException e) {
			e.printStackTrace();
			return results;
		}
		return results;
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	
	@Override
	protected void onPostExecute(List<Item> result) {
		this.context.onResultsLoaded(result);
	}
	
}
