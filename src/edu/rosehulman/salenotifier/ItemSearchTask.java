package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.amazon.AmazonPricingSource;
import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.Enumerable.IPredicate;
import edu.rosehulman.salenotifier.ebay.EbayPricingSource;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;
import edu.rosehulman.salenotifier.settings.Setting;
import edu.rosehulman.salenotifier.settings.SettingsManager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ItemSearchTask extends
		AsyncTask<ItemQueryConstraints, List<Item>, List<Item>> {

	public interface ISearchResultsCallback {
		/**
		 * Only called once, after all of the searching has completed. Safe to
		 * assume no results will be returned if there haven't been any after
		 * this call.
		 * 
		 * @param searchResults
		 */
		void onResults(List<Item> searchResults);
	}

	public interface IPartialSearchResultsCallback {
		/**
		 * Any unhandled results should be included in the final
		 * ISearchResultsCallback onResults, otherwise they should be excluded.
		 * 
		 * @param partialResults
		 * @return whether the partialResults were handled
		 */
		boolean onPartialResults(List<Item> partialResults);
	}

	private Context mContext;
	private ISearchResultsCallback mResultsCallback;
	private IPartialSearchResultsCallback mPartialCallback;

	public ItemSearchTask(Context context,
			ISearchResultsCallback finishedCallback,
			IPartialSearchResultsCallback partialCallback) {
		mContext = context;
		mResultsCallback = finishedCallback;
		mPartialCallback = partialCallback;
	}

	@Override
	protected List<Item> doInBackground(ItemQueryConstraints... params) {
		if (params == null || params.length == 0) {
			throw new IllegalArgumentException(
					"Attempt to execute ItemSearchTask without a search item");
		}

		List<Item> results = new ArrayList<Item>();

		List<IPricingSource> priceSources = PricingSourceFactory
				.getValidPriceSources();
		for (IPricingSource priceSource : priceSources) {
			if (params[0].getSearchLimited()
					&& !priceSource.allowsLocalSearches()) {
				continue;
			}

			try {
				List<Item> searchResults = priceSource.search(mContext,
						params[0], new IPartialSearchResultsCallback() {
							// Necessary to get back onto the UI thread before
							// calling in primary callback
							@Override
							public boolean onPartialResults(
									List<Item> partialResults) {
								if (mPartialCallback != null) {
									publishProgress(partialResults);
									return true;
								}
								return false;
							}
						});

				if (searchResults != null) {
					if (mPartialCallback != null) {
						publishProgress(searchResults);
					} else {
						results.addAll(searchResults);
					}
				}
			} catch (ApiException e) {
				Log.e(TrackedItemsActivity.LOG_TAG,
						"ItemSearchTask failed with ApiException", e);
			}
		}
		return results;
	}

	@Override
	protected void onProgressUpdate(List<Item>... values) {
		if (mPartialCallback != null && values != null && values.length > 0) {
			mPartialCallback.onPartialResults(values[0]);
		}
	}

	@Override
	protected void onCancelled() {
		mPartialCallback = null;
		mResultsCallback = null;
	}

	@Override
	protected void onPostExecute(List<Item> result) {
		if (mResultsCallback != null && result != null) {
			mResultsCallback.onResults(result);
		}
	}

}
