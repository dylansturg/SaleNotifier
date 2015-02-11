package edu.rosehulman.salenotifier.amazon;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import edu.rosehulman.salenotifier.ApiException;
import edu.rosehulman.salenotifier.IPricingSource;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.ItemSearchTask.IPartialSearchResultsCallback;
import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.Enumerable.IPredicate;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;
import edu.rosehulman.salenotifier.models.Seller;

public class AmazonPricingSource extends IPricingSource {
	protected static final String AMAZON_SOURCE_NAME = "AMAZON";

	private static final String[] PREFERRED_IMAGE_SIZES = { "Medium", "Small",
			"Large" };
	private static final Seller AMAZON_SELLER = new Seller("Amazon");

	public AmazonPricingSource() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSourceName() {
		return AMAZON_SOURCE_NAME;
	}

	@Override
	public List<Item> search(Context context, ItemQueryConstraints query,
			IPartialSearchResultsCallback partialCallback) throws ApiException {
		try {
			AmazonRequest request = new AmazonRequest(query);
			List<AmazonItem> results = request.performQuery();
			List<Item> searchResults = consolidateSearchResults(results);
			return searchResults;
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}

	@Override
	public List<ItemPrice> searchForPrices(Context context, Item item)
			throws ApiException {
		List<Item> searchResults = search(context, new ItemQueryConstraints(
				item.getDisplayName(), item.getProductCode(), null, 0), null);
		if (searchResults.size() > 0) {
			if (searchResults.size() > 1) {
				Log.d(TrackedItemsActivity.LOG_TAG,
						"AmazonPricingSource received multiple Items after searching on product code for "
								+ item);
			}

			return searchResults.get(0).getPrices();
		}

		return new ArrayList<ItemPrice>();
	}

	private List<Item> consolidateSearchResults(List<AmazonItem> items) {
		Enumerable<Item> results = new Enumerable<Item>();

		for (AmazonItem item : items) {
			if (item.UPCs.size() == 0 && item.EANs.size() == 0) {
				continue; // no key to save with
			}
			Set<String> productCodes = new HashSet<String>(item.UPCs);
			productCodes.addAll(item.EANs);

			Item existing = results
					.firstOrDefault(matchingProductCode(productCodes));
			if (existing == null) {
				existing = convertToItem(item);
				results.add(existing);
			}

			addAmazonItemAsPrice(item, existing);
		}

		return results;
	}

	private void addAmazonItemAsPrice(AmazonItem amazon, Item item) {
		ItemPrice result = new ItemPrice();
		result.setItem(item);
		result.setDate(new GregorianCalendar());
		result.setBuyLocation(amazon.DetailsUrl);
		result.setPrice(amazon.price);
		result.setSeller(AMAZON_SELLER);
		result.setType(ItemPrice.TYPE_ONLINE);

		item.addPrice(result);
	}

	private Item convertToItem(AmazonItem item) {
		Item result = new Item();
		result.setDisplayName(item.title);
		String productCode = null;

		Iterator<String> codeIter = item.UPCs.iterator();
		if (item.UPCs.size() > 0) {
			codeIter = item.UPCs.iterator();
		} else {
			codeIter = item.EANs.iterator();
		}

		while (codeIter.hasNext()) {
			productCode = codeIter.next();
			break;
		}
		result.setProductCode(productCode);

		String image = null;
		for (String size : PREFERRED_IMAGE_SIZES) {
			if (item.ImageUrls.containsKey(size)) {
				image = item.ImageUrls.get(size);
				break;
			}
		}

		if (image != null) {
			result.setImageUrl(image);
		}
		return result;
	}

	private IPredicate<Item> matchingProductCode(final Set<String> codes) {
		return new IPredicate<Item>() {

			@Override
			public boolean match(Item element) {
				return codes.contains(element.getProductCode());
			}
		};
	}

}
