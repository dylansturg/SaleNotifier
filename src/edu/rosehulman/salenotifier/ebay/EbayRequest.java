package edu.rosehulman.salenotifier.ebay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import edu.rosehulman.salenotifier.ApiException;
import edu.rosehulman.salenotifier.R;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.ebay.SearchEbayItemsTask.ISearchEbayIncrementalResultNotifier;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.CancellationSignal;
import android.util.Log;
import android.util.LruCache;

public class EbayRequest {

	private enum RequestType {
		Keywords, Product
	}

	private static final Map<RequestType, String> API_OPERATIONS = new HashMap<RequestType, String>();
	static {
		API_OPERATIONS.put(RequestType.Keywords, "findItemsByKeywords");
		API_OPERATIONS.put(RequestType.Product, "findItemsByProduct");
	}

	private static final int PAGE_SIZE = 100;
	private static final int DESIRED_RESULT_COUNT = 10;
	private static final int MAXIMUM_REQUEST_COUNT = 5;

	String APIKey;
	private ItemQueryConstraints mQuery;
	private Context mContext;

	private RequestType mRequestType;
	private int mRequestCount = 0;

	private ISearchEbayIncrementalResultNotifier mResultNotifier;

	private static final int CacheSize = 8 * 1024 * 1024;
	private LruCache<String, String> mProductRequestCache = new LruCache<String, String>(
			CacheSize);

	public EbayRequest(Context context, ItemQueryConstraints query) {
		this(context, query, null);
	}

	public EbayRequest(Context context, ItemQueryConstraints query,
			ISearchEbayIncrementalResultNotifier resultCallback) {
		APIKey = context.getString(R.string.EbayAPIKey);
		mQuery = query;
		mContext = context;
		mResultNotifier = resultCallback;
	}

	protected String getOperationName() {
		if (mQuery.getProductCode() != null
				&& !mQuery.getProductCode().isEmpty()) {
			mRequestType = RequestType.Product;
		} else {
			mRequestType = RequestType.Keywords;
		}

		return API_OPERATIONS.get(mRequestType);
	}

	public List<EbayItem> evaluateRequest() {

		HttpClient client = new DefaultHttpClient();
		List<EbayItem> searchResults = new ArrayList<EbayItem>();

		int totalSearchResults = 0;
		try {

			Uri serviceRequest = buildRequestUri();
			URI javaUri = new URI(serviceRequest.getScheme(),
					serviceRequest.getAuthority(), serviceRequest.getPath(),
					serviceRequest.getQuery(), serviceRequest.getFragment());

			String postBody = createRequestBody();

			Log.d(TrackedItemsActivity.LOG_TAG,
					String.format(
							"Querying for eBay items, currently have %d results after %d requests.",
							totalSearchResults, mRequestCount));
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Endpoing: " + serviceRequest.toString());

			HttpPost post = new HttpPost(javaUri);
			post.setEntity(new StringEntity(postBody));

			HttpResponse response = client.execute(post);
			String responseContent = EntityUtils.toString(response.getEntity(),
					"UTF-8");

			EbayResponse parsedResponse = new EbayResponse(mContext,
					responseContent, getOperationName(), mProductRequestCache);
			List<EbayItem> results = parsedResponse.getResponseItems();
			if (results != null) {
				totalSearchResults += results.size();
				if (mResultNotifier != null) {
					boolean resultsHandled = mResultNotifier
							.publishPartialResults(results);
					if (!resultsHandled) {
						searchResults.addAll(results);
					}
				} else {
					searchResults.addAll(results);
				}
			}
			mRequestCount++;

		} catch (URISyntaxException e) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to parse eBay Product Details URI");
		} catch (UnsupportedEncodingException encodingException) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to encode POST XML data for EbayProductDetailsRequest");
		} catch (ClientProtocolException httpException) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to execute HTTP POST for EbayProductDetailsRequest");
		} catch (IOException networkException) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"IO Failed when executing HTTP POST for EbayProductDetailsRequest");
		} catch (ApiException apiFailed) {
			return searchResults;
		}

		int cacheHits = mProductRequestCache.hitCount();
		Log.d(TrackedItemsActivity.LOG_TAG, "eBay cache got " + cacheHits
				+ " hits");

		return searchResults;
	}

	private Uri buildRequestUri() {
		String serviceAuthority = mContext.getString(R.string.EbayAPIService);
		String[] servicePaths = mContext.getResources().getStringArray(
				R.array.EbayAPIPaths);

		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http").authority(serviceAuthority);
		for (String path : servicePaths) {
			builder.appendPath(path);
		}

		String operation = getOperationName();
		builder.appendQueryParameter("OPERATION-NAME", operation);
		builder.appendQueryParameter("SECURITY-APPNAME", APIKey);
		builder.appendQueryParameter("REQUEST-DATA-FORMAT", "JSON");
		builder.appendQueryParameter("RESPONSE-DATA-FORMAT", "JSON");

		// End of Request Headers
		builder.appendQueryParameter("REST-PAYLOAD", "");

		Uri serviceRequest = builder.build();

		return serviceRequest;
	}

	private String createRequestBody() {
		JSONObject body = new JSONObject();
		body.put("jsonns.xsi", "http://www.w3.org/2001/XMLSchema-instance");
		body.put("jsonns.xs", "http://www.w3.org/2001/XMLSchema");
		body.put("jsonns.tns",
				"http://www.ebay.com/marketplace/search/v1/services");

		JSONObject encodedRequest = new JSONObject();
		body.put("tns." + getOperationName() + "Request", encodedRequest);

		appendSearchCriteria(encodedRequest);
		appendPaginationCriteria(encodedRequest);
		appendLocalSearchParameters(encodedRequest);

		return body.toString();
	}

	private void appendPaginationCriteria(JSONObject request) {
		String pagination = "paginationInput";

		JSONObject paginationObj = new JSONObject();
		paginationObj.put("pageNumber", Integer.toString(mRequestCount + 1));
		paginationObj.put("entriesPerPage", Integer.toString(PAGE_SIZE));

		request.put("paginationInput", paginationObj);

	}

	private void appendSearchCriteria(JSONObject request) {

		switch (mRequestType) {
		case Keywords:
			appendKeywordSearchCriteria(request);
			break;
		case Product:
			appendProductSearchCriteria(request);
			break;
		}

		request.put("sortOrder", "BestMatch");

		JSONArray itemFilter = new JSONArray();
		JSONObject nonAuctionFilter = new JSONObject();
		nonAuctionFilter.put("name", "ListingType");
		nonAuctionFilter.put("value", "FixedPrice");
		itemFilter.put(nonAuctionFilter);

		request.put("itemFilter", itemFilter);

	}

	private void appendKeywordSearchCriteria(JSONObject request) {
		request.put("keywords", mQuery.getName());
	}

	private void appendProductSearchCriteria(JSONObject request) {
		String codeType = "";
		if (mQuery.getProductCodeType() != null
				&& !mQuery.getProductCodeType().isEmpty()) {
			codeType = mQuery.getProductCodeType();
		} else {
			codeType = estimateProductCodeType(mQuery.getProductCode());
		}

		JSONObject productFilter = new JSONObject();
		productFilter.put("@type", codeType);
		productFilter.put("__value__", mQuery.getProductCode());

		request.put("productId", productFilter);
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
			} else {
				return "ISBN";
			}
		}

		// TODO revert to a keyword search
		return "";
	}

	private void appendLocalSearchParameters(JSONObject request) {
		if (mQuery == null || !mQuery.getSearchLimited()) {
			return;
		}

		Location searchFrom = mQuery.getSearchLocation();
		double searchDistance = mQuery.getSearchRadius();

		if (searchFrom == null) {
			return; // can't do anything
		}

		Geocoder geocoder = new Geocoder(mContext);
		List<Address> address;
		try {
			address = geocoder.getFromLocation(searchFrom.getLatitude(),
					searchFrom.getLongitude(), 10);

		} catch (IOException e) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Geocoder failed to convert location to address for zip");
			return;
		}

		if (address == null || address.size() == 0) {
			return; // no results
		}

		String postalCode = null;
		for (Address addr : address) {
			if (addr.getPostalCode() != null && !addr.getPostalCode().isEmpty()) {
				postalCode = addr.getPostalCode();
				break;
			}
		}

		if (postalCode != null) {

			JSONObject localSearchFilter = new JSONObject();
			localSearchFilter.put("name", "MaxDistance");
			localSearchFilter.put("value",
					Integer.toString((int) searchDistance));

			request.put("buyerPostalCode", postalCode);
			JSONArray itemFilters = request.has("itemFilter") ? request
					.getJSONArray("itemFilter") : new JSONArray();

			itemFilters.put(localSearchFilter);
			request.put("itemFilter", itemFilters);
		}
	}
}
