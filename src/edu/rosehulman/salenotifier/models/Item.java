package edu.rosehulman.salenotifier.models;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.rosehulman.salenotifier.TrackedItemsActivity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Item implements IQueryable, Parcelable {
	private long mId = -1;
	private String displayName = "";
	private String productCode = "";
	private List<ItemPrice> priceData;
	private URL imageUrl;

	public static final Parcelable.Creator<Item> CREATOR = new ItemCreator();

	public Item() {
	}

	public Item(String name, String upc, String imageUrl) {
		displayName = name;
		productCode = upc;
		setImageUrl(imageUrl);
		priceData = new ArrayList<ItemPrice>();
	}

	public Item(Parcel source) {
		mId = source.readLong();
		displayName = source.readString();
		productCode = source.readString();
		priceData = new ArrayList<ItemPrice>();
		source.readTypedList(priceData, ItemPrice.CREATOR);
	}

	public String getLowestPriceAsString() {
		if (priceData.isEmpty())
			return "N/A";
		Collections.sort(priceData);
		return "$" + String.format("%1$,.2f", priceData.get(0).getPrice());
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
					+ " into a valid Image URL.");
			imageUrl = null;
		}
	}

	public List<ItemPrice> getPrices() {
		return priceData;
	}

	public List<ItemPrice> getCurrentPrices() {
		List<ItemPrice> currents = new ArrayList<ItemPrice>();
		List<Long> seenIds = new ArrayList<Long>();

		if (priceData == null) {
			return currents;
		}

		Collections.sort(priceData, new ItemPriceDateSort());

		for (ItemPrice price : priceData) {
			if (!seenIds.contains(price.getId())) {
				currents.add(price);
				seenIds.add(price.getId());
			}
		}
		return currents;
	}

	public void addPrice(ItemPrice ip) {
		if (priceData == null) {
			priceData = new ArrayList<ItemPrice>();
		}
		priceData.add(ip);
	}

	@Override
	public String toString() {
		return "" + mId + " " + displayName + " image: "
				+ imageUrl.toExternalForm() + " upc: " + productCode;
	}

	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(displayName);
		dest.writeString(productCode);
		dest.writeTypedList(priceData != null ? priceData
				: new ArrayList<ItemPrice>());
		dest.writeString(imageUrl != null ? imageUrl.toExternalForm() : "");
	}

	private static class ItemCreator implements Parcelable.Creator<Item> {

		@Override
		public Item createFromParcel(Parcel source) {
			return new Item(source);
		}

		@Override
		public Item[] newArray(int size) {
			return new Item[size];
		}
	}

	private class ItemPriceDateSort implements Comparator<ItemPrice> {
		@Override
		public int compare(ItemPrice lhs, ItemPrice rhs) {
			int sellerCompare = Double.compare(lhs.getSellerId(),
					rhs.getSellerId());
			if (sellerCompare == 0) {
				return lhs.getDate().compareTo(rhs.getDate());
			}
			return sellerCompare;
		}
	}
}
