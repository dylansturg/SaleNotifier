package edu.rosehulman.salenotifier.amazon;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AmazonItem {

	protected String ASIN;
	protected String DetailsUrl;
	protected Map<String, String> ImageUrls = new HashMap<String, String>();
	protected double price = -1;
	protected String title;
	protected Set<String> UPCs = new TreeSet<String>();
	protected Set<String> EANs = new TreeSet<String>();

	/*
	 * It's a POJO.
	 */
	public AmazonItem() {
	}

}
