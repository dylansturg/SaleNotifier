package edu.rosehulman.salenotifier.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class ItemQueryConstraints implements Parcelable {
	public static final Parcelable.Creator<ItemQueryConstraints> CREATOR = new ItemQueryConstraintsCreator();

	private String mName;
	private String mProductCode;
	private String mProductCodeType;
	private double mSearchRadiusMiles;
	private Location mSearchLocation;

	public ItemQueryConstraints() {
		this("", "", "", 0.0);
	}

	public ItemQueryConstraints(Parcel source) {
		mName = source.readString();
		mProductCode = source.readString();
		mProductCodeType = source.readString();
		mSearchRadiusMiles = source.readDouble();
		mSearchLocation = source.readParcelable(null);
	}

	public ItemQueryConstraints(String name, String upc, String type, double radius) {
		mName = name;
		mProductCode = upc;
		mProductCodeType = type;
		mSearchRadiusMiles = radius;
	}

	public ItemQueryConstraints(String name, String upc, String type, double radius,
			Location location) {
		this(name, upc, type, radius);
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
	
	public String getProductCodeType(){
		return mProductCodeType;
	}
	
	public void setProductCodeType(String type){
		mProductCodeType = type;
	}

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
		dest.writeString(mProductCodeType);
		dest.writeDouble(mSearchRadiusMiles);
		dest.writeParcelable(mSearchLocation, 0);
	}

	private static class ItemQueryConstraintsCreator implements
			Parcelable.Creator<ItemQueryConstraints> {

		@Override
		public ItemQueryConstraints createFromParcel(Parcel source) {
			return new ItemQueryConstraints(source);
		}

		@Override
		public ItemQueryConstraints[] newArray(int size) {
			return new ItemQueryConstraints[size];
		}

	}
}
