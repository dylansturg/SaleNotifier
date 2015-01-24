package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	/***
	 * Reinitializes the query with our default settings. Should be called after
	 * An api call to make sure filtering does not persist past that request.
	 */
	public void resetQuery() {
		products = new Products(APP_KEY, SECRET);
		products.productsField("activeproductsonly", 1);
		products.productsField("fields", "name", "sitedetails", "upc", "url", "price");
		products.productsField("geo", "usa");
	}
	
	@Override
	public List<ItemPrice> getPrices(Item item) throws ApiException {
		if(!item.getProductCode().isEmpty())
			return getPricesByUpc(item.getProductCode());
		throw new ApiException(new Exception("Unhandled Condition getPrices w/ empty item UPC"));
	}
	
	/***
	 * Returns a list of strings that are the names of the products found that match the
	 * given search term
	 * @param searchString
	 * @return
	 * @throws ApiException
	 */
	public List<String> searchForProduct(String searchString) throws ApiException {
		products.productsField("search", searchString);
		try {
			JSONObject productResults = products.getProducts();
			List<String> l = getProductNamesFromResponse(productResults);
			return l;
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}
	
	/***
	 * Returns a list of Items that match the search string. The have their
	 * prices field populated with the ItemPrice objects found.
	 * @param searchString : term or phrase to search for
	 * @return list of items
	 * @throws ApiException when missing data in ApiResponse or API throws error
	 */
	public List<Item> searchForItems(String searchString) throws ApiException {
		products.productsField("search", searchString);
		try {
			List<Item> prodList = new ArrayList<Item>();
			JSONObject productResults = products.getProducts();
			List<ItemPrice> l = getPricesFromResponse(productResults);
			
			// Map of UPC -> Name
			HashMap<String, String> uniqueUpcs = new HashMap<String, String>();
			for(ItemPrice ip : l)
				uniqueUpcs.put(ip.getProductCode(), ip.getName());
			
			for(String key : uniqueUpcs.keySet()) {
				Item toAdd = new Item(uniqueUpcs.get(key), key, null);
				for(ItemPrice ip : l) {
					toAdd.addPrice(ip);
				}
				prodList.add(toAdd);
			}
			resetQuery();
			return prodList;
		} catch (Exception e) {
			resetQuery();
			throw new ApiException(e);
		}
	}
	
	/***
	 * Find a list of items by their UPC code
	 * @param upc
	 * @return
	 */
	public List<ItemPrice> getItemsByUpc(String upc) throws ApiException {
		return getPrices(upc, false);
	}
	
	/***
	 * Find a list of items by their UPC code
	 * @param upc
	 * @return
	 */
	public List<ItemPrice> getPricesByUpc(String upc) throws ApiException {
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
			resetQuery();
			return getPricesFromResponse(results);
		} catch(Exception e) {
			resetQuery();
			throw new ApiException(e);
		}
	}
	
	/***
	 * Parses the response results into a List of ItemPrice
	 * @param response
	 * @return
	 * @throws ApiException
	 */
	private List<ItemPrice> getPricesFromResponse(JSONObject response) throws ApiException {
		Set<ItemPrice> list = new HashSet<ItemPrice>();
		try {
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
		} catch(JSONException e) {
			throw new ApiException(e);
		}
		return new ArrayList<ItemPrice>(list);
	}
	
	/***
	 * Parses the response into a string list of unique product names
	 * @param response
	 * @return
	 * @throws ApiException
	 */
	private List<String> getProductNamesFromResponse(JSONObject response) throws ApiException {
		Set<String> list = new HashSet<String>();
		try {
			JSONArray results = response.getJSONArray("results");
			for(int i = 0; i < results.length(); i++) {
				JSONObject entry = results.getJSONObject(i);
				try {
					list.add(entry.getString("name"));
				} catch(JSONException e) {
					continue;
				}
			}
		} catch(JSONException e) {
			throw new ApiException(e);
		}
		return new ArrayList<String>(list);
	}
	
}
