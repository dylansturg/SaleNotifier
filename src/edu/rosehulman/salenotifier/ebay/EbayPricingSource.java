package edu.rosehulman.salenotifier.ebay;

import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.util.Log;

import edu.rosehulman.salenotifier.ApiException;
import edu.rosehulman.salenotifier.IPricingSource;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.Enumerable.IPredicate;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.ItemQueryConstraints;
import edu.rosehulman.salenotifier.models.Seller;

public class EbayPricingSource implements IPricingSource {
	private static final String EBAY_SELLER_NAME = "eBay";

	public EbayPricingSource() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Item> searchForItems(String searchString) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemPrice> getPrices(Item item) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemPrice> getItemsByUpc(String upc) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemPrice> getPricesByUpc(String upc) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemPrice> getPricesLessThan(String upc, double lessThan)
			throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> search(Context context, ItemQueryConstraints query)
			throws ApiException {
		if (query == null) {
			return null;
		}

		try {
			EbayRequest apiRequest = new EbayRequest(context, query);
			List<EbayItem> apiResult = apiRequest.evaluateRequest();
			List<Item> results = consolidateEbayResponse(apiResult);
			return results;
		} catch (Exception e) {
			throw new ApiException(e);
		}

	}

	@Override
	public List<ItemPrice> searchForPrices(Context context, Item item)
			throws ApiException {
		if (item == null) {
			return null;
		}

		try {
			ItemQueryConstraints query = new ItemQueryConstraints();
			query.setProductCode(item.getProductCode());
			query.setName(item.getDisplayName());

			EbayRequest apiRequest = new EbayRequest(context, query);
			List<EbayItem> apiResult = apiRequest.evaluateRequest();
			List<Item> results = consolidateEbayResponse(apiResult);

			if (results == null || results.size() == 0) {
				return null;
			}

			if (results.size() > 1) {
				// Not likely - let's see if it happens
				Log.d(TrackedItemsActivity.LOG_TAG,
						"EbayPricingSource received multiple Item when performing searchForPrices");
			}

			return results.get(0).getPrices();
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}

	private List<Item> consolidateEbayResponse(List<EbayItem> response) {
		if(response == null){
			return null;
		}
		
		Enumerable<Item> items = new Enumerable<Item>();

		for (EbayItem ebayItem : response) {
			if (ebayItem.UPC == null || ebayItem.UPC.isEmpty()) {
				continue; // useless item (because we can't do anything with it,
							// no matter how awesome it might be)
			}
			Item existing = items
					.firstOrDefault(createItemPredicate(ebayItem.UPC));
			if (existing == null) {
				existing = createItemFromEbayItem(ebayItem);
				items.add(existing);
			}
			appendEbayItemAsItemPrice(existing, ebayItem);
		}

		return items;
	}

	private static void appendEbayItemAsItemPrice(Item target,
			EbayItem priceData) {
		ItemPrice result = new ItemPrice();
		result.setBuyLocation(priceData.viewItemURL);
		result.setDate(new GregorianCalendar()); // just use now
		result.setItem(target);
		Seller seller = new Seller();
		seller.setName(EBAY_SELLER_NAME);
		result.setSeller(seller);
		result.setType(ItemPrice.TYPE_ONLINE);

		try {
			double price = Double.parseDouble(priceData.currentPrice);
			result.setPrice(price);
			// Purposefully in the try/catch: the ItemPrice isn't meaningful
			// without a price
			target.addPrice(result);
		} catch (Exception e) {
			// fail parse
		}
	}

	private static Item createItemFromEbayItem(EbayItem item) {
		Item result = new Item();
		result.setDisplayName(item.title);
		String image = null;
		if (item.stockThumbnailURL != null && !item.stockThumbnailURL.isEmpty()) {
			image = item.stockThumbnailURL;
		} else if (item.galleryURL != null && !item.galleryURL.isEmpty()) {
			image = item.galleryURL;
		}

		if (image != null) {
			result.setImageUrl(image);
		}

		result.setProductCode(item.UPC);
		return result;
	}

	private static IPredicate<Item> createItemPredicate(final String upc) {
		return new IPredicate<Item>() {
			@Override
			public boolean match(Item element) {
				return element.getProductCode().equalsIgnoreCase(upc);
			}
		};
	}
}
