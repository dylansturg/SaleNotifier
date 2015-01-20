package edu.rosehulman.salenotifier.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BarcodeResult implements Parcelable {
	
	public static final Parcelable.Creator<BarcodeResult> CREATOR = new BarcodeResultCreator();

	private String mContent;
	private String mFormat;

	public BarcodeResult() {
	}

	public BarcodeResult(Parcel source) {
		mContent = source.readString();
		mFormat = source.readString();
	}

	public BarcodeResult(String content, String format) {
		mContent = content;
		mFormat = format;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String content) {
		mContent = content;
	}

	public String getFormat() {
		return mFormat;
	}

	public void setFormat(String format) {
		mFormat = format;
	}

	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mContent);
		dest.writeString(mFormat);
	}
	
	private static class BarcodeResultCreator implements Parcelable.Creator<BarcodeResult> {

		@Override
		public BarcodeResult createFromParcel(Parcel source) {
			return new BarcodeResult(source);
		}

		@Override
		public BarcodeResult[] newArray(int size) {
			return new BarcodeResult[size];
		}
		
	}

}
