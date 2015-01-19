package edu.rosehulman.salenotifier;

import java.util.Date;

public class ItemPrice {
	private String name;
	private String productCode;
	private double price;
	private Date date;
	private int sellerId;
	private String seller;
	private IPricingSource source;
	private String urlSource;
	
	public ItemPrice(String name, String productCode, double price, String urlSource, String seller) {
		this.productCode = productCode;
		this.price = price;
		this.urlSource = urlSource;
		this.seller = seller;
		this.name = name;
	}
	
	public Object method() {
		throw new RuntimeException("Don't call this method.");
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public String getSeller() {
		return this.seller;
	}
	
	public String getProductCode() {
		return this.productCode;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getUrlSource() {
		return this.urlSource;
	}
	
}
