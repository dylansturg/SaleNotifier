package edu.rosehulman.salenotifier;

import java.net.URL;
import java.util.List;

public class Item {
	private String displayName;
	private String productCode;
	private List<ItemPrice> priceData;
	private URL imageUrl;
	
	Item(){
		
	}
	
	public List<ItemPrice> getPrices(){
		return priceData;
	}
	
	public void update(){
		// TODO Implement db updates
	}
}
