package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.semantics3.api.Products;

import edu.rosehulman.salenotifier.ItemSearchTask.IPartialSearchResultsCallback;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;

public class Semantics3PriceSource extends IPricingSource {
	protected static final String SOURCE_NAME = "SEMANTICS3";

	private static final String APP_KEY = "SEM336FC88457A9E384669666327917180A7";
	private static final String SECRET = "NTFmMDBiODZmNmNhNTUxZTEzNWQxODk2OGE5ODM5Mzg";

	private Products products;

	public Semantics3PriceSource() {
		resetQuery();
	}

	@Override
	public List<Item> search(Context context, ItemQueryConstraints query,
			IPartialSearchResultsCallback partialCallback) throws ApiException {
		String searchString = query.getName();
		products.productsField("search", searchString);
		try {
			List<Item> prodList = new ArrayList<Item>();
			JSONObject productResults = products.getProducts();
			List<ItemPrice> l = getPricesFromResponse(productResults);

			// Map of UPC -> Name
			HashMap<String, String> uniqueUpcs = new HashMap<String, String>();
			for (ItemPrice ip : l)
				uniqueUpcs.put(ip.getProductCode(), ip.getName());

			for (String key : uniqueUpcs.keySet()) {
				Item toAdd = new Item(uniqueUpcs.get(key), key, null);
				for (ItemPrice ip : l) {
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

	@Override
	public List<ItemPrice> searchForPrices(Context context, Item item)
			throws ApiException {
		String upc = item.getProductCode();
		products.productsField("upc", upc);
		try {
			JSONObject results = products.getProducts();
			resetQuery();
			return getPricesFromResponse(results);
		} catch (Exception e) {
			resetQuery();
			throw new ApiException(e);
		}
	}

	@Override
	public String getSourceName() {
		return SOURCE_NAME;
	}

	/***
	 * Reinitializes the query with our default settings. Should be called after
	 * An api call to make sure filtering does not persist past that request.
	 */
	public void resetQuery() {
		products = new Products(APP_KEY, SECRET);
		products.productsField("activeproductsonly", 1);
		products.productsField("fields", "name", "sitedetails", "upc", "url",
				"price");
		products.productsField("geo", "usa");
	}

	/***
	 * Parses the response results into a List of ItemPrice
	 * 
	 * @param response
	 * @return
	 * @throws ApiException
	 */
	private List<ItemPrice> getPricesFromResponse(JSONObject response)
			throws ApiException {
		Set<ItemPrice> list = new HashSet<ItemPrice>();
		try {
			JSONArray results = response.getJSONArray("results");
			// Iterate over each results entry
			for (int i = 0; i < results.length(); i++) {
				JSONObject entry = results.getJSONObject(i);
				try {
					String pName = entry.getString("name");
					double pPrice = entry.getDouble("price");
					String pUpc = entry.getString("upc");
					JSONArray siteDetails = entry.getJSONArray("sitedetails");
					// Iterate over each site (seems to only have 1 ever but
					// they
					// made it an array so...
					for (int sdi = 0; sdi < siteDetails.length(); sdi++) {
						JSONObject sdEntry = siteDetails.getJSONObject(sdi);
						String pSourceName = sdEntry.getString("name");
						String pSourceUrl = sdEntry.getString("url");
						ItemPrice ip = new ItemPrice(pName, pUpc, pPrice,
								pSourceUrl, pSourceName);
						list.add(ip);
					}
				} catch (JSONException e) {
					continue;
				}
			}
		} catch (JSONException e) {
			throw new ApiException(e);
		}
		return new ArrayList<ItemPrice>(list);
	}

}
