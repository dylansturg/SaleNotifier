package edu.rosehulman.salenotifier;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class ItemQueryConstraints implements Parcelable {
	public static final Parcelable.Creator<ItemQueryConstraints> CREATOR = new ItemQueryConstraintsCreator();

	private String mName;
	private String mProductCode;
	private double mSearchRadiusMiles;
	private Location mSearchLocation;

	public ItemQueryConstraints() {
		this("", "", 0.0);
	}

	public ItemQueryConstraints(Parcel source) {
		mName = source.readString();
		mProductCode = source.readString();
		mSearchRadiusMiles = source.readDouble();
		mSearchLocation = source.readParcelable(null);
	}

	public ItemQueryConstraints(String name, String upc, double radius) {
		mName = name;
		mProductCode = upc;
		mSearchRadiusMiles = radius;
	}

	public ItemQueryConstraints(String name, String upc, double radius,
			Location location) {
		this(name, upc, radius);
		mSearchLocation = location;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getProductCode() {
		return mProductCode;
	}

	public void setProductCode(String upc) {
		mProductCode = upc;
	};

	public double getSearchRadius() {
		return mSearchRadiusMiles;
	}

	public void setSearchRadius(double miles) {
		mSearchRadiusMiles = miles;
	}

	public Location getSearchLocation() {
		return mSearchLocation;
	}

	public void setSearchLocation(Location loc) {
		mSearchLocation = loc;
	}

	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeString(mProductCode);
		dest.writeDouble(mSearchRadiusMiles);
		dest.writeParcelable(mSearchLocation, 0);
	}

	private static class ItemQueryConstraintsCreator implements
			Parcelable.Creator<ItemQueryConstraints> {

		@Override
		public ItemQueryConstraints createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new ItemQueryConstraints(source);
		}

		@Override
		public ItemQueryConstraints[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ItemQueryConstraints[size];
		}

	}
}
