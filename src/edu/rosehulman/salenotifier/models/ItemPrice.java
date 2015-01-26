package edu.rosehulman.salenotifier.models;

import java.util.GregorianCalendar;

import edu.rosehulman.salenotifier.IPricingSource;
import edu.rosehulman.salenotifier.db.SQLiteAdapter;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemPrice implements IQueryable, Parcelable, Comparable<ItemPrice> {
	public static final String TYPE_ONLINE = "Online";
	public static final String TYPE_STORE = "Store";

	private long mId = -1;

	private Item mItem;
	private long mItemId = -1;

	private Seller mSeller;
	private long mSellerId = -1;

	private GregorianCalendar mFoundDate;
	private String mType;
	private String mBuyLocation;

	private double mPrice;

	private IPricingSource mPriceSource;

	private SQLiteAdapter mDataSource;

	public static final Parcelable.Creator<ItemPrice> CREATOR = new ItemPriceCreator();

	public ItemPrice() {
		// Default setup
	}

	public ItemPrice(Item item, Seller seller, double price,
			GregorianCalendar foundDate, String type, String location,
			IPricingSource source) {
		mItem = item;
		mItemId = item.getId();

		mSeller = seller;
		mSellerId = seller.getId();

		mPrice = price;
		mFoundDate = foundDate;
		mType = type;
		mBuyLocation = location;

		mPriceSource = source;
	}

	public ItemPrice(String name, String productCode, double price,
			String urlSource, String seller) {
		mItem = new Item();
		mItem.setProductCode(productCode);
		mItem.setDisplayName(name);

		mPrice = price;
		mBuyLocation = urlSource;
		mType = TYPE_ONLINE;

		mSeller = new Seller();
		mSeller.setName(seller);
	}

	public ItemPrice(double price, String urlSource) {
		mPrice = price;

		mType = TYPE_ONLINE;
		mBuyLocation = urlSource;
	}

	public ItemPrice(Parcel source) {
		mId = source.readLong();
		mItem = source.readParcelable(null);
		mItemId = source.readLong();

		mSeller = source.readParcelable(null);
		mSellerId = source.readLong();

		long timeMilis = source.readLong();
		mFoundDate = new GregorianCalendar();
		mFoundDate.setTimeInMillis(timeMilis);

		mType = source.readString();
		mBuyLocation = source.readString();
		mPrice = source.readDouble();
	}

	// Section: Seller

	public Seller getSeller() {
		if (mSeller == null) {
			mSeller = getDataSource().getSeller(mSellerId);
		}
		return mSeller;
	}

	public void setSeller(Seller seller) {
		mSeller = seller;
		mSellerId = seller.getId();
	}

	public long getSellerId() {
		return mSellerId;
	}

	public void setSellerId(long id) {
		mSellerId = id;
		mSeller = null;
	}

	public String getSellerName() {
		Seller seller = getSeller();
		if (seller == null) {
			return null;
		}
		return seller.getName();
	}

	// Section: Item

	public Item getItem() {
		if (mItem == null) {
			mItem = getDataSource().getItemById(mItemId);
		}
		return mItem;
	}

	public void setItem(Item item) {
		mItem = item;
		mItemId = item.getId();
	}

	public long getItemId() {
		return mItemId;
	}

	public void setItemId(long id) {
		mItemId = id;
		mItem = null;
	}

	public String getName() {
		Item item = getItem();
		if (item == null) {
			return null;
		}
		return item.getDisplayName();
	}

	public String getProductCode() {
		Item item = getItem();
		if (item == null) {
			return null;
		}
		return item.getProductCode();
	}

	public void setProductCode(String upc) {
		Item item = getItem();
		if (item == null) {
			mItem = new Item();
			mItem.setProductCode(upc);
		} else {
			item.setProductCode(upc);
		}
	}

	// Section: Local Fields

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getUrlSource() {
		return mBuyLocation;
	}

	public double getPrice() {
		return mPrice;
	}

	public void setPrice(double p) {
		mPrice = p;
	}

	public GregorianCalendar getDate() {
		return mFoundDate;
	}

	public void setDate(GregorianCalendar date) {
		mFoundDate = date;
	}

	public void setDate(long timeInMilis) {
		mFoundDate = new GregorianCalendar();
		mFoundDate.setTimeInMillis(timeInMilis);
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

	public String getBuyLocation() {
		return mBuyLocation;
	}

	public void setBuyLocation(String location) {
		mBuyLocation = location;
	}

	public IPricingSource getPricingSource() {
		return mPriceSource;
	}

	public void setPricingSource(IPricingSource ps) {
		mPriceSource = ps;
	}

	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeParcelable(mItem, flags);
		dest.writeLong(mItemId);

		dest.writeParcelable(mSeller, flags);
		dest.writeLong(mSellerId);

		long timeMilis = mFoundDate != null ? mFoundDate.getTimeInMillis() : 0;
		dest.writeLong(timeMilis);

		dest.writeString(mType);
		dest.writeString(mBuyLocation);
		dest.writeDouble(mPrice);
	}

	private static class ItemPriceCreator implements
			Parcelable.Creator<ItemPrice> {

		@Override
		public ItemPrice createFromParcel(Parcel source) {
			return new ItemPrice(source);
		}

		@Override
		public ItemPrice[] newArray(int size) {
			return new ItemPrice[size];
		}

	}

	private SQLiteAdapter getDataSource() {
		if (mDataSource == null) {
			mDataSource = new SQLiteAdapter();
		}
		return mDataSource;
	}

	@Override
	public int compareTo(ItemPrice another) {
		return Double.compare(getPrice(), another.getPrice());
	}
}
