package edu.rosehulman.salenotifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.util.Log;

public class Item {
	private long mId;
	private String displayName;
	private String productCode;
	private List<ItemPrice> priceData;
	private URL imageUrl;

	Item() {

	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String name) {
		displayName = name;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String upc) {
		productCode = upc;
	}

	public URL getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(URL location) {
		imageUrl = location;
	}

	public void setImageUrl(String location) {
		try {
			imageUrl = new URL(location);
		} catch (MalformedURLException e) {
			Log.d(TrackedItemsActivity.LOG_TAG, "Failed to parse " + location
					+ " into a valid Image URL.", e);
			imageUrl = null;
		}

	}

	public List<ItemPrice> getPrices() {
		return priceData;
	}

	public void update() {
		// TODO Implement db updates
	}
}
