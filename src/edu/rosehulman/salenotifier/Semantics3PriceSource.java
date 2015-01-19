package edu.rosehulman.salenotifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.semantics3.api.Products;

public class Semantics3PriceSource implements IPricingSource {

	private static final String APP_KEY = "SEM336FC88457A9E384669666327917180A7";
	private static final String SECRET = "NTFmMDBiODZmNmNhNTUxZTEzNWQxODk2OGE5ODM5Mzg";
	
	private Products products;
	
	public Semantics3PriceSource() {
		resetQuery();
	}
	
	public void resetQuery() {
		products = new Products(APP_KEY, SECRET);
		products.productsField("activeproductsonly", 1);
		products.productsField("fields", "name", "sitedetails", "upc", "url", "price");
		products.productsField("geo", "usa");
	}
	
	@Override
	public List<ItemPrice> getPrices(Item item) throws ApiException {
		return search(item.getDisplayName());
	}
	
	public List<ItemPrice> search(String searchString) throws ApiException {
		products.productsField("search", searchString);
		try {
			JSONObject results = products.getProducts();
//			System.out.println(results.toString());
		} catch (Exception e) {
			throw new ApiException(e);
		}
		return null;
	}
	
	public List<String> searchForProduct(String searchString) throws ApiException {
		products.productsField("search", searchString);
		try {
			JSONObject productResults = products.getProducts();
			List<String> l = getProductNamesFromResponse(productResults);
//			for(String s : l) {
//				System.out.println(s);
//			}
			return l;
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}
	
	public List<ItemPrice> searchForPrices(String searchString) throws ApiException {
		products.productsField("search", searchString);
		try {
			JSONObject productResults = products.getProducts();
			List<ItemPrice> l = getPricesFromResponse(productResults);
//			for(ItemPrice ip : l) {
//				System.out.println(ip.getName() + " == " + ip.getProductCode() + " == " + ip.getSeller() + " == " +ip.getPrice());
//			}
			return l;
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}
	
	/***
	 * Find a list of items by their UPC code
	 * @param upc
	 * @return
	 */
	public List<ItemPrice> getPrices(String upc) throws ApiException {
		return getPrices(upc, false);
	}
	
	/**
	 * Find prices for items with a given upper boundary on the price
	 * @param upc : UPC code of the product
	 * @param lessThan : price to use as an upper boundary
	 * @return
	 */
	public List<ItemPrice> getPricesLessThan(String upc, double lessThan) throws ApiException {
		products.productsField("upc", upc).productsField("price", "lt", lessThan);
		return getPrices(upc, true);
	}
	
	protected List<ItemPrice> getPrices(String upc, boolean conditioned) throws ApiException {
		if(!conditioned)
			products.productsField("upc", upc);
		try {
			JSONObject results = products.getProducts();
			return getPricesFromResponse(results);
		} catch(Exception e) {
			throw new ApiException(e);
		}
	}
	
	private List<ItemPrice> getPricesFromResponse(JSONObject response) throws ApiException {
		Set<ItemPrice> list = new HashSet<ItemPrice>();
		JSONArray results = response.getJSONArray("results");
		// Iterate over each results entry
		for(int i = 0; i < results.length(); i++) {
			JSONObject entry = results.getJSONObject(i);
			try {
				String pName = entry.getString("name");
				double pPrice = entry.getDouble("price");
				String pUpc = entry.getString("upc");
				JSONArray siteDetails = entry.getJSONArray("sitedetails");
				// Iterate over each site (seems to only have 1 ever but they
				// made it an array so...
				for(int sdi = 0; sdi < siteDetails.length(); sdi++) {
					JSONObject sdEntry = siteDetails.getJSONObject(sdi);
					String pSourceName = sdEntry.getString("name");
					String pSourceUrl = sdEntry.getString("url");
					ItemPrice ip = new ItemPrice(pName, pUpc, pPrice, pSourceUrl, pSourceName);
					list.add(ip);
				}
			} catch(JSONException e) {
				continue;
			}
		}
		return new ArrayList<ItemPrice>(list);
	}
	
	private List<String> getProductNamesFromResponse(JSONObject response) throws ApiException {
		Set<String> list = new HashSet<String>();
		JSONArray results = response.getJSONArray("results");
		for(int i = 0; i < results.length(); i++) {
			JSONObject entry = results.getJSONObject(i);
			try {
				list.add(entry.getString("name"));
			} catch(JSONException e) {
				continue;
			}
		}
		return new ArrayList<String>(list);
	}
	
}
