package edu.rosehulman.salenotifier.amazon;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

public class AmazonRequest {
	private enum RequestType {
		Keywords, ProductCode
	}

	private ItemQueryConstraints mSearchItem;
	private RequestType mRequestType;
	private int mRequestCount = 1;

	public AmazonRequest(ItemQueryConstraints query) {
		mSearchItem = query;
	}

	public List<AmazonItem> performQuery() {

		List<AmazonItem> searchResults = new ArrayList<AmazonItem>();
		try {
			URI request = buildRequestUri();

			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(request);

			HttpResponse response = client.execute(get);
			String responseData = EntityUtils.toString(response.getEntity());

			Log.d(TrackedItemsActivity.LOG_TAG, "response data: "
					+ responseData);

			AmazonResponse amazonResponse = new AmazonResponse(responseData,
					getOperation());
			List<AmazonItem> results = amazonResponse.getParsedResults();
			if (results != null) {
				searchResults.addAll(results);
			}

		} catch (Exception e) {
			Log.e(TrackedItemsActivity.LOG_TAG, "Querying Amazon failed", e);
		}

		return searchResults;
	}

	private URI buildRequestUri() throws URISyntaxException {
		Map<String, String> queryParams = new HashMap<String, String>();

		appendDefaultParams(queryParams);
		appendOperation(queryParams);
		appendSearchCriteria(queryParams);

		SignedRequestsHelper signer = SignedRequestsHelper.getInstance();
		String apiRequest = signer.sign(queryParams);

		return new URI(apiRequest);
	}

	private String getOperation() {
		switch (mRequestType) {
		case Keywords:
			return "ItemSearch";
		case ProductCode:
			return "ItemLookup";
		}

		return "";
	}

	private void appendOperation(Map<String, String> params) {
		if (mSearchItem == null) {
			return;
		}

		if (mSearchItem.getProductCode() != null
				&& !mSearchItem.getProductCode().isEmpty()) {
			mRequestType = RequestType.ProductCode;
		} else {
			mRequestType = RequestType.Keywords;
		}

		params.put("Operation", getOperation());
	}

	private void appendSearchCriteria(Map<String, String> params) {
		switch (mRequestType) {
		case Keywords:
			params.put("Keywords", mSearchItem.getName());
			break;
		case ProductCode:
			params.put("ItemId", mSearchItem.getProductCode());
			String codeType = mSearchItem.getProductCodeType();
			if (codeType == null || codeType.isEmpty()) {
				codeType = estimateProductCodeType(mSearchItem.getProductCode());
			}
			params.put("IdType", codeType);

			break;
		}
	}

	protected static String estimateProductCodeType(String productCode) {
		if (productCode == null || productCode.isEmpty()) {
			throw new IllegalArgumentException(
					"EbayReqeust attemping to guess a product code type for a null/empty product code");
		}

		int length = productCode.length();
		boolean containsAlpha = !productCode.matches("[0-9]+");

		if (containsAlpha) {
			// Hell if I know what it is...
			// TODO revert to a keyword search
		} else {
			/*
			 * UPC = 12 digits EAN = 12 or 13 digits ISBN = 10 or 13 digits If
			 * we assume no one scans a EAN, then it'll be ok... mostly
			 */
			if (length == 12) {
				return "UPC";
			} else if (length == 10) {
				return "ISBN";
			} else {
				return "EAN";
			}
		}

		// TODO revert to a keyword search
		return "";
	}

	private void appendDefaultParams(Map<String, String> params) {
		params.put("SearchIndex", "All");
		params.put("Availability", "Available");
		params.put("ResponseGroup", "ItemAttributes,Images,OfferSummary");
		params.put("ItemPage", Integer.toString(mRequestCount));
	}
}
