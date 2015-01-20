package edu.rosehulman.salenotifier.models;

import java.util.GregorianCalendar;

import edu.rosehulman.salenotifier.IPricingSource;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemPrice implements IQueryable, Parcelable{
	private long mId = -1;
	private String productCode;
	private double price;
	private GregorianCalendar date;
	private long sellerId;
	private IPricingSource source;
	
	public static final Parcelable.Creator<ItemPrice> CREATOR = new ItemPriceCreator();
	
	public ItemPrice(){
		
	}
	
	public ItemPrice(Parcel source){
		mId = source.readLong();
		productCode = source.readString();
		price = source.readDouble();
		date = new GregorianCalendar();
		date.setTimeInMillis(source.readLong());
		sellerId = source.readLong();
	}

	public Object method() {
		throw new RuntimeException("Don't call this method.");
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String upc) {
		productCode = upc;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double p) {
		price = p;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public long getSellerId() {
		return sellerId;
	}

	public void setSellerId(long id) {
		sellerId = id;
	}

	public IPricingSource getPricingSource() {
		return source;
	}

	public void setPricingSource(IPricingSource ps) {
		source = ps;
	}

	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(productCode);
		dest.writeDouble(price);
		dest.writeLong(date != null ? date.getTimeInMillis() : 0);
		dest.writeLong(sellerId);
	}

	private static class ItemPriceCreator implements Parcelable.Creator<ItemPrice>{

		@Override
		public ItemPrice createFromParcel(Parcel source) {
			return new ItemPrice(source);
		}

		@Override
		public ItemPrice[] newArray(int size) {
			return new ItemPrice[size];
		}
		
	}
}
