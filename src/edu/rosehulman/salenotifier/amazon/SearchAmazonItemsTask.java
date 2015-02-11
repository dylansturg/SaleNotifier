package edu.rosehulman.salenotifier.amazon;

import java.util.List;

import edu.rosehulman.salenotifier.ApiException;
import edu.rosehulman.salenotifier.IPricingSource;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SearchAmazonItemsTask extends
		AsyncTask<ItemQueryConstraints, List<Item>, List<Item>> {

	public interface ISearchAmazonCallback {
		void onFinished(List<Item> results);
	}

	private Context mContext;
	private ISearchAmazonCallback mCallback;

	public SearchAmazonItemsTask(Context context, ISearchAmazonCallback callback) {
		mContext = context;
		mCallback = callback;
	}

	@Override
	protected List<Item> doInBackground(ItemQueryConstraints... params) {
		if (params == null || params.length == 0) {
			throw new IllegalArgumentException(
					"SearchAmazonItemTask requires a valid ItemQueryConstraints for searching");
		}

		IPricingSource source = new AmazonPricingSource();
		List<Item> results = null;
		try {
			results = source.search(mContext, params[0], null);
		} catch (ApiException e) {
			Log.e(TrackedItemsActivity.LOG_TAG, "SearchAmazonItemTask failed",
					e);
		}

		return results;
	}

	@Override
	protected void onPostExecute(List<Item> result) {
		if (result != null && mCallback != null) {
			mCallback.onFinished(result);
		}
	}
}
