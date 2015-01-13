package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.semantics3.api.Products;

public class Semantics3PriceSource implements IPricingSource {

	private static final String APP_KEY = "SEM336FC88457A9E384669666327917180A7";
	private static final String SECRET = "NTFmMDBiODZmNmNhNTUxZTEzNWQxODk2OGE5ODM5Mzg";
	
	private Products products;
	
	public Semantics3PriceSource() {
		products = new Products(APP_KEY, SECRET);
	}
	
	@Override
	public List<ItemPrice> getPrices(Item item) throws ApiException {
		products.productsField("search", item.getDisplayName());
		try {
			JSONObject results = products.getProducts();
		} catch (Exception e) {
			throw new ApiException(e);
		}
		return null;
	}
	
	/***
	 * Find a list of items by their UPC code
	 * @param upc
	 * @return
	 */
	public List<ItemPrice> getPrices(String upc) {
		return null;	
	}
	
	/**
	 * Find prices for items with a given upper boundary on the price
	 * @param upc : UPC code of the product
	 * @param lessThan : price to use as an upper boundary
	 * @return
	 */
	public List<ItemPrice> getPricesLessThan(String upc, double lessThan) throws ApiException {
		try {
			products.productsField("upc", upc).productsField("price", "lt", lessThan);
			JSONObject results = products.getProducts();
			JSONArray matches = results.getJSONArray("results");
			// TODO: populate the matches passed to the exception
			if(matches.length() > 1) throw new MultipleMatchesExeption(null);
			
			ArrayList<ItemPrice> list = new ArrayList<ItemPrice>();
			JSONObject item = matches.getJSONObject(0);
			JSONArray sellers = item.getJSONArray("sitedetails");
			for(int i = 0; i < sellers.length(); i++) {
				// TODO: pass the seller name along
				String sellerName = sellers.getJSONObject(i).getString("name");
				// TODO: pass the URL location along
				String url = sellers.getJSONObject(i).getString("url");
				JSONArray prices = sellers.getJSONObject(i).getJSONArray("latestoffers");
				for(int j = 0; j < prices.length(); j++) {
					double price = prices.getJSONObject(j).getDouble("price");
					list.add(new ItemPrice(upc, price, url));
				}
			}
			return list;
		} catch(Exception e) {
			throw new ApiException(e);
		}
	}
}
