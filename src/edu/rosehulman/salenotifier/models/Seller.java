package edu.rosehulman.salenotifier.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Seller implements IQueryable, Parcelable {

	public static final Parcelable.Creator<Seller> CREATOR = new SellerCreator();

	private long mId = -1;
	private String mName;

	public Seller(Parcel source) {
		mId = source.readLong();
		mName = source.readString();
	}

	public Seller() {
		this("", -1);
	}

	public Seller(String name) {
		this(name, -1);
	}

	public Seller(String name, long id) {
		mId = id;
		mName = name;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mName);
	}

	private static class SellerCreator implements Parcelable.Creator<Seller> {

		@Override
		public Seller createFromParcel(Parcel source) {
			return new Seller(source);
		}

		@Override
		public Seller[] newArray(int size) {
			return new Seller[size];
		}

	}
}
