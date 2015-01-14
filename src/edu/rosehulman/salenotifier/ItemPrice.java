package edu.rosehulman.salenotifier;

import java.util.GregorianCalendar;

public class ItemPrice implements IQueryable{
	private long mId = -1;
	private String productCode;
	private double price;
	private GregorianCalendar date;
	private long sellerId;
	private IPricingSource source;

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

}
