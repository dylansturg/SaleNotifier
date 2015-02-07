package edu.rosehulman.salenotifier.ebay;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.ApiException;
import edu.rosehulman.salenotifier.IPricingSource;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SearchEbayItemsTask extends
		AsyncTask<ItemQueryConstraints, Void, List<Item>> {

	public interface ISearchEbayCallback {
		void onFinished(List<Item> results);
	}

	private ISearchEbayCallback mCallback;
	private Context mContext;

	public SearchEbayItemsTask(Context context, ISearchEbayCallback callback) {
		mContext = context;
		mCallback = callback;
	}

	@Override
	protected List<Item> doInBackground(ItemQueryConstraints... params) {
		if (mContext == null) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"SearchEbayItemTask failed due to null Context");
			return null;
		}

		if (params == null || params.length == 0) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"SearchEbayItemTask failed due to null ItemQueryConstraints");
			return null;
		}

		IPricingSource ebayPriceSource = new EbayPricingSource();
		try {
			List<Item> searchResults = ebayPriceSource.search(mContext,
					params[0]);
			if (searchResults == null) {
				return new ArrayList<Item>();
			}
			return searchResults;
		} catch (ApiException e) {
			Log.e(TrackedItemsActivity.LOG_TAG, "SearchEbayItemTask failed", e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(List<Item> result) {
		if (result != null && mCallback != null) {
			mCallback.onFinished(result);
		}
	}
}
