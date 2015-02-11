package edu.rosehulman.salenotifier.ebay;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.ApiException;
import edu.rosehulman.salenotifier.IPricingSource;
import edu.rosehulman.salenotifier.ItemSearchTask.IPartialSearchResultsCallback;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

import android.content.Context;
import android.os.AsyncTask;
import android.os.CancellationSignal;
import android.util.Log;

public class SearchEbayItemsTask extends
		AsyncTask<ItemQueryConstraints, List<Item>, List<Item>> {

	public interface ISearchEbayCallback {
		void onFinished(List<Item> results);
	}

	public interface ISearchEbayIncrementalResultListener {
		boolean publishPartialResults(List<Item> results);
	}

	protected interface ISearchEbayIncrementalResultNotifier {
		boolean publishPartialResults(List<EbayItem> results);
	}

	private ISearchEbayCallback mCallback;
	private ISearchEbayIncrementalResultListener mPartialResultCallback;
	private Context mContext;
	private CancellationSignal mCancelToken;

	private List<Item> mUnhandledPartialResults = new ArrayList<Item>();

	public SearchEbayItemsTask(Context context, ISearchEbayCallback callback) {
		mContext = context;
		mCallback = callback;

		mCancelToken = new CancellationSignal();
	}

	public SearchEbayItemsTask(Context context,
			ISearchEbayCallback finishedCallback,
			ISearchEbayIncrementalResultListener partialCallback) {
		this(context, finishedCallback);
		mPartialResultCallback = partialCallback;
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
					params[0], new IPartialSearchResultsCallback() {

						@Override
						public boolean onPartialResults(
								List<Item> partialResults) {
							if (mPartialResultCallback != null) {
								publishProgress(partialResults);
								return true;
							}
							return false;
						}
					});
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
	protected void onProgressUpdate(List<Item>... values) {
		if (values != null) {
			boolean resultsHandled = false;
			if (mPartialResultCallback != null) {
				resultsHandled = mPartialResultCallback
						.publishPartialResults(values[0]);
			}
			if (!resultsHandled) {
				mUnhandledPartialResults.addAll(values[0]);
			}
		}
	}

	@Override
	protected void onCancelled(List<Item> result) {
		mCallback = null;
		mPartialResultCallback = null;
		mCancelToken.cancel();
	}

	@Override
	protected void onPostExecute(List<Item> result) {
		if (result != null && mCallback != null) {
			if (mUnhandledPartialResults != null) {
				result.addAll(mUnhandledPartialResults);
			}
			mCallback.onFinished(result);
		}
	}
}
