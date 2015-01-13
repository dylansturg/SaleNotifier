package edu.rosehulman.salenotifier;

import java.util.Date;

public class ItemPrice {
	private String productCode;
	private double price;
	private Date date;
	private int sellerId;
	private IPricingSource source;
	
	public Object method(){
		throw new RuntimeException("Don't call this method.");
	}
}
