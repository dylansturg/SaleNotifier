package edu.rosehulman.salenotifier.ebay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import edu.rosehulman.salenotifier.R;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

public class EbayRequest {

	String APIKey;
	private ItemQueryConstraints mQuery;
	private Context mContext;

	public EbayRequest(Context context, ItemQueryConstraints query) {
		APIKey = context.getString(R.string.EbayAPIKey);
		mQuery = query;
		mContext = context;
	}

	protected String getOperationName() {
		return "findItemsByKeywords";
	}

	public List<EbayItem> evaluateRequest() {
		Uri serviceRequest = buildRequestUri();

		HttpClient client = new DefaultHttpClient();
		try {
			URI javaUri = new URI(serviceRequest.getScheme(),
					serviceRequest.getAuthority(), serviceRequest.getPath(),
					serviceRequest.getQuery(), serviceRequest.getFragment());

			HttpGet get = new HttpGet(javaUri);

			HttpResponse response = client.execute(get);
			String responseContent = EntityUtils.toString(response.getEntity(),
					"UTF-8");

			EbayResponse parsedResponse = new EbayResponse(mContext,
					responseContent, getOperationName());

			return parsedResponse.getResponseItems();

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

		return null;
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
		builder.appendQueryParameter("RESPONSE-DATA-FORMAT", "JSON");

		// End of Request Headers
		builder.appendQueryParameter("REST-PAYLOAD", "");

		int filterIndex = 0;
		filterIndex = appendSearchCriteria(builder, filterIndex);
		// filterIndex = appendLocalSearchParameters(builder, filterIndex);

		Uri serviceRequest = builder.build();

		return serviceRequest;
	}

	private int appendSearchCriteria(Uri.Builder builder, int filterIndex) {

		builder.appendQueryParameter("keywords", mQuery.getName());
		String itemFilter = String.format("itemFilter(%d)", filterIndex);
		builder.appendQueryParameter(itemFilter + ".name", "ListingType");
		builder.appendQueryParameter(itemFilter + ".value", "FixedPrice");
		builder.appendQueryParameter("sortOrder", "BestMatch");

		filterIndex++;
		return filterIndex;
	}

	private int appendLocalSearchParameters(Uri.Builder builder, int filterIndex) {
		assert mQuery != null;

		Location searchFrom = mQuery.getSearchLocation();
		double searchDistance = mQuery.getSearchRadius();

		if (searchFrom == null) {
			return filterIndex; // can't do anything
		}

		Geocoder geocoder = new Geocoder(mContext);

		List<Address> address;
		try {
			address = geocoder.getFromLocation(searchFrom.getLatitude(),
					searchFrom.getLongitude(), 1);

		} catch (IOException e) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Geocoder failed to convert location to address for zip");
			return filterIndex;
		}

		if (address == null || address.size() == 0) {
			return filterIndex; // no results
		}

		String itemFilter = String.format("itemFilter(%d)", filterIndex);
		String postalCode = address.get(0).getPostalCode();

		builder.appendQueryParameter("buyerPostalCode", postalCode);
		builder.appendQueryParameter(itemFilter + ".name", "MaxDistance");
		builder.appendQueryParameter(itemFilter + ".value",
				Double.toString(searchDistance));

		return filterIndex++;
	}
}
