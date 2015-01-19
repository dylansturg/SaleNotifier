package edu.rosehulman.salenotifier;

import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemPrice implements IQueryable, Parcelable{
	private long mId = -1;
	private String productCode;
	private double price;
	private GregorianCalendar date;
	private long sellerId;
	private String seller;
	private String name;
	private IPricingSource source;
	private String urlSource;
	
	public static final Parcelable.Creator<ItemPrice> CREATOR = new ItemPriceCreator();
	
	public ItemPrice(String name, String productCode, double price, String urlSource, String seller) {
		this.productCode = productCode;
		this.price = price;
		this.urlSource = urlSource;
		this.seller = seller;
		this.name = name;
	}
	
	public ItemPrice() { }
	
	public ItemPrice(Parcel source) {
		mId = source.readLong();
		productCode = source.readString();
		price = source.readDouble();
		date = new GregorianCalendar();
		date.setTimeInMillis(source.readLong());
		sellerId = source.readLong();
		seller = source.readString();
		name = source.readString();
		// TODO:
		urlSource = source.readString();
	}
	
	public String getSeller() {
		return this.seller;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getUrlSource() {
		return this.urlSource;
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
		dest.writeString(seller);
		dest.writeString(name);
		dest.writeString(urlSource);
	}

	private static class ItemPriceCreator implements Parcelable.Creator<ItemPrice> {

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
