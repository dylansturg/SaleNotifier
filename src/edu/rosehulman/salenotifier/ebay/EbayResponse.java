package edu.rosehulman.salenotifier.ebay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.rosehulman.salenotifier.TrackedItemsActivity;

import android.content.Context;
import android.util.Log;

public class EbayResponse {

	private Context mContext;
	private List<EbayItem> mResponseItems;

	public EbayResponse(Context context, String jsonResonse,
			String operationName) {
		mContext = context;
		parseResponseJson(jsonResonse, operationName);
		queryItemsStockDetails();
	}

	public EbayResponse(Context context, InputStream jsonStream,
			String operationName) {
		mContext = context;
		parseResponseJson(readStreamToEnd(jsonStream), operationName);
		queryItemsStockDetails();
	}

	public List<EbayItem> getResponseItems() {
		return mResponseItems;
	}

	private void queryItemsStockDetails() {
		if (mResponseItems == null) {
			return;
		}

		int itemCount = mResponseItems.size();
		for (EbayItem item : mResponseItems) {
			if (item.UPC == null || item.UPC.isEmpty()) {
				EbayProductDetailsRequest detailsRequest = new EbayProductDetailsRequest(
						mContext, item);
				detailsRequest.evaluateRequest(); // modifies item
			}
		}
	}

	private void parseResponseJson(String json, String operation) {
		try {
			JSONObject parsed = new JSONObject(json);
			JSONObject response = parsed.getJSONArray(operation + "Response")
					.getJSONObject(0);

			JSONArray searchResults = response.getJSONArray("searchResult");
			JSONObject searchResult = searchResults.getJSONObject(0);

			JSONArray searchResultItems = searchResult.getJSONArray("item");

			List<EbayItem> parsedResultItems = new ArrayList<EbayItem>();
			int itemCount = searchResultItems.length();
			for (int i = 0; i < itemCount; i++) {
				EbayItem parsedItem = parseItem(searchResultItems
						.getJSONObject(i));
				if (parsedItem == null || parsedItem.productIdValue == null
						|| parsedItem.productIdValue.isEmpty()) {
					// Not a useful item we can't find a UPC (later on)
					continue;
				}
				parsedResultItems.add(parsedItem);
			}
			int parsedCount = parsedResultItems.size();

			mResponseItems = parsedResultItems;

		} catch (JSONException jsonFailure) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to parse JSON for EbayResponse");
		}
	}

	private EbayItem parseItem(JSONObject itemJson) {
		EbayItem result = new EbayItem();

		if (itemJson.has("title")) {
			result.title = itemJson.getString("title");
		}
		if (itemJson.has("globalId")) {
			result.globalId = itemJson.getString("globalId");
		}
		if (itemJson.has("galleryURL")) {
			result.galleryURL = itemJson.getString("galleryURL");
		}
		if (itemJson.has("viewItemURL")) {
			result.viewItemURL = itemJson.getString("viewItemURL");
		}
		if (itemJson.has("productId")) {
			JSONObject productDetails = itemJson.getJSONArray("productId")
					.getJSONObject(0);
			if (productDetails.has("@type") && productDetails.has("__value__")) {
				result.productIdType = productDetails.getString("@type");
				result.productIdValue = productDetails.getString("__value__");
			}
		}
		if (itemJson.has("location")) {
			result.location = itemJson.getString("location");
		}
		if (itemJson.has("sellingStatus")) {
			JSONObject sellingStats = itemJson.getJSONArray("sellingStatus")
					.getJSONObject(0);
			if (sellingStats.has("currentPrice")) {
				JSONObject priceStats = sellingStats.getJSONArray(
						"currentPrice").getJSONObject(0);
				if (priceStats.has("@currencyId")
						&& priceStats.has("__value__")) {
					if (priceStats.getString("@currencyId").equalsIgnoreCase(
							"USD")) {
						result.currentPrice = priceStats.getString("__value__");
					}
				}
			}
		}
		if (itemJson.has("listingInfo")) {
			JSONObject listingInfo = itemJson.getJSONArray("listingInfo")
					.getJSONObject(0);
			if (listingInfo.has("startTime")) {
				result.startTime = listingInfo.getString("startTime");
			}
			if (listingInfo.has("endTime")) {
				result.endTime = listingInfo.getString("endTime");
			}
		}

		return result;
	}

	private static String readStreamToEnd(InputStream input) {
		InputStreamReader streamReader = new InputStreamReader(input);
		BufferedReader bufferedReader = new BufferedReader(streamReader);
		StringBuilder result = new StringBuilder();
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"EbayResponse Failed to read input stream into string");
		}

		return result.toString();
	}
}
