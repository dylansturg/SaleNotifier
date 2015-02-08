package edu.rosehulman.salenotifier.ebay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.rosehulman.salenotifier.R;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;

public class EbayProductDetailsRequest {

	private static final String XML_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<getProductDetailsRequest xmlns=\"http://www.ebay.com/marketplace/marketplacecatalog/v1/services\">"
			+ "<productDetailsRequest>"
			+ "<productIdentifier>"
			+ "<ePID>%s</ePID>"
			+ "</productIdentifier>"
			+ "<datasetPropertyName>UPC</datasetPropertyName>"
			+ "<datasetPropertyName>ISBN</datasetPropertyName>"
			+ "<datasetPropertyName>EAN</datasetPropertyName>"
			+ "</productDetailsRequest>" + "</getProductDetailsRequest>";

	private static final ArrayList<String> ALLOWED_PRODUCT_CODE_TYPES = new ArrayList<String>();
	static {
		ALLOWED_PRODUCT_CODE_TYPES.add("UPC");
		ALLOWED_PRODUCT_CODE_TYPES.add("ISBN");
		ALLOWED_PRODUCT_CODE_TYPES.add("EAN");
	}

	private Context mContext;
	private EbayItem mSearchItem;
	private LruCache<String, String> mCache;

	public EbayProductDetailsRequest(Context context, EbayItem item,
			LruCache<String, String> requestCache) {
		mContext = context;
		mSearchItem = item;
		mCache = requestCache;
	}

	public EbayItem evaluateRequest() {
		String cachedCode = mCache != null ? mCache
				.get(mSearchItem.productIdValue) : null;
		;
		if (cachedCode != null) {
			mSearchItem.UPC = cachedCode;
			return mSearchItem;
		}

		Uri serviceRequest = buildRequestUri();
		String payload = buildRequestPayload();

		HttpClient client = new DefaultHttpClient();
		try {
			URI javaUri = new URI(serviceRequest.getScheme(),
					serviceRequest.getAuthority(), serviceRequest.getPath(),
					serviceRequest.getQuery(), serviceRequest.getFragment());

			HttpPost post = new HttpPost(javaUri);
			post.setEntity(new StringEntity(payload, "UTF-8"));

			HttpResponse response = client.execute(post);
			String responseContent = EntityUtils.toString(response.getEntity(),
					"UTF-8");
			parseProductDetailsResponse(responseContent);

			if (mCache != null) {
				mCache.put(mSearchItem.productIdType,
						mSearchItem.UPC != null ? mSearchItem.UPC : "");
			}

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
		}

		return mSearchItem;
	}

	private Uri buildRequestUri() {
		String endpoint = mContext.getString(R.string.EbayAPIService);
		String apiKey = mContext.getString(R.string.EbayAPIKey);
		String[] servicePaths = mContext.getResources().getStringArray(
				R.array.EbayProductAPIPaths);

		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http").authority(endpoint);
		for (String path : servicePaths) {
			builder.appendPath(path);
		}

		String operation = "getProductDetails";
		builder.appendQueryParameter("OPERATION-NAME", operation);
		builder.appendQueryParameter("SECURITY-APPNAME", apiKey);
		builder.appendQueryParameter("RESPONSE-DATA-FORMAT", "JSON");

		return builder.build();
	}

	private String buildRequestPayload() {
		String xmlPayload = String.format(XML_FORMAT,
				mSearchItem.productIdValue);
		return xmlPayload;
	}

	private void parseProductDetailsResponse(String response) {
		try {
			JSONObject parsed = new JSONObject(response);
			JSONObject result = parsed
					.getJSONArray("getProductDetailsResponse").getJSONObject(0);

			JSONObject product = result.getJSONArray("product")
					.getJSONObject(0);

			if (product.has("stockPhotoURL")) {
				JSONObject stockPhoto = product.getJSONArray("stockPhotoURL")
						.getJSONObject(0);
				if (stockPhoto.has("thumbnail")) {
					JSONObject thumbnail = stockPhoto.getJSONArray("thumbnail")
							.getJSONObject(0);
					if (thumbnail.has("value")) {
						mSearchItem.stockThumbnailURL = thumbnail.getJSONArray(
								"value").getString(0);
					}
				}
			}

			// I'm really sorry if someone has to look at it.
			// eBay formats their stuff really verbosely, and this JSON
			// framework doesn't have strongly typed models... :(
			if (product.has("productDetails")) {
				JSONArray details = product.getJSONArray("productDetails");

				Map<String, String> availableProductCodes = new HashMap<String, String>();
				int detailsCount = details.length();
				for (int i = 0; i < detailsCount; i++) {
					JSONObject propertyDetails = details.getJSONObject(i);
					if (propertyDetails.has("propertyName")) {
						String propertyName = propertyDetails.getJSONArray(
								"propertyName").getString(0);
						if (ALLOWED_PRODUCT_CODE_TYPES.contains(propertyName)) {
							if (propertyDetails.has("value")) {
								JSONObject propertyValue = propertyDetails
										.getJSONArray("value").getJSONObject(0);
								if (propertyValue.has("text")) {
									JSONObject upcText = propertyValue
											.getJSONArray("text")
											.getJSONObject(0);
									if (upcText.has("value")) {
										availableProductCodes.put(propertyName,
												upcText.getJSONArray("value")
														.getString(0));
									}
								}
							}
						}
					}
				}
				if (availableProductCodes.size() > 0) {
					for (String key : ALLOWED_PRODUCT_CODE_TYPES) {
						if (availableProductCodes.containsKey(key)) {
							mSearchItem.UPC = availableProductCodes.get(key);
							break;
						}
					}
				}
			}

		} catch (JSONException e) {

		}

	}
}
