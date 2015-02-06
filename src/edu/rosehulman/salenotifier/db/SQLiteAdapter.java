package edu.rosehulman.salenotifier.db;

import java.util.List;

import edu.rosehulman.salenotifier.IItemSourceAdapter;
import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.Seller;

public class SQLiteAdapter implements IItemSourceAdapter {

	private static SQLiteAdapter instance = new SQLiteAdapter();

	public static synchronized SQLiteAdapter getInstance() {
		return instance;
	}

	@Override
	public Item getItem(String productCode) {
		ItemDataAdapter itemDb = new ItemDataAdapter();
		List<Item> results = itemDb.getAll(ItemDataAdapter.DB_KEY_PRODUCT_CODE
				+ "=" + productCode, null, null);
		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	public Item getItemById(long id) {
		ItemDataAdapter itemDb = new ItemDataAdapter();
		return itemDb.getById(id);
	}

	@Override
	public void saveItem(Item item) {
		ItemDataAdapter itemDb = new ItemDataAdapter();
		itemDb.update(item);
	}

	@Override
	public List<Item> getAllItems() {
		ItemDataAdapter itemDb = new ItemDataAdapter();
		return itemDb.getAll(null, null, null);
	}

	@Override
	public void deleteItem(Item item) {
		deleteItem(item.getId());
	}

	@Override
	public void deleteItem(long id) {
		// TODO Auto-generated method stub
		ItemDataAdapter itemDb = new ItemDataAdapter();
		itemDb.delete(id);
	}

	public Seller getSeller(long id) {
		SellerDataAdapter sellerDb = new SellerDataAdapter();
		return sellerDb.getById(id);
	}

	@Override
	public void deleteItemPrice(ItemPrice price) {
		if (price != null) {
			deleteItem(price.getId());
		}
	}

	@Override
	public void deleteItemPrice(long id) {
		ItemPriceDataAdapter priceDb = new ItemPriceDataAdapter();
		priceDb.delete(id);
	}

}
