package edu.rosehulman.salenotifier.db;

import java.util.List;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.Seller;
import android.content.ContentValues;
import android.database.Cursor;

public class ItemDataAdapter extends DataAdapter<Item> {
	protected static final String TABLE_ITEMS = "items";

	/* Column Names */
	protected static final String DB_KEY_ID = "id";
	protected static final String DB_KEY_DISPLAY_NAME = "displayName";
	protected static final String DB_KEY_PRODUCT_CODE = "productCode";
	protected static final String DB_KEY_IMAGE = "image";

	protected static String CREATE_TABLE = "";
	static {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_ITEMS + " (");
		sb.append(DB_KEY_ID + " integer primary key autoincrement, ");
		sb.append(DB_KEY_DISPLAY_NAME + " text not null, ");
		sb.append(DB_KEY_PRODUCT_CODE + " text not null, ");
		sb.append(DB_KEY_IMAGE + " text not null);");

		CREATE_TABLE = sb.toString();
	}

	@Override
	String getTableName() {
		return TABLE_ITEMS;
	}

	@Override
	String getDBKeyColumn() {
		return DB_KEY_ID;
	}

	@Override
	boolean doesItemExist(Item item) {
		return item.getId() >= 0;
	}

	@Override
	protected Item getById(long id) {
		Item result = super.getById(id);
		addPricesToItem(result);
		return result;
	}

	@Override
	protected List<Item> getAll(String where, String groupBy, String order) {
		List<Item> results = super.getAll(where, groupBy, order);
		for (Item item : results) {
			addPricesToItem(item);
		}
		return results;
	}

	private void addPricesToItem(Item item) {
		ItemPriceDataAdapter priceSource = new ItemPriceDataAdapter();
		List<ItemPrice> prices = priceSource.getAll(
				ItemPriceDataAdapter.DB_KEY_ITEM_ID + " = " + item.getId(),
				null, null);
		for (ItemPrice itemPrice : prices) {
			item.addPrice(itemPrice);
		}
	}

	@Override
	protected boolean insert(Item item) {
		boolean inserted = super.insert(item);
		updateItemPrices(item);
		return inserted;
	}

	@Override
	protected boolean update(Item item) {
		boolean updated = super.update(item);
		updateItemPrices(item);
		return updated;
	}

	private void updateItemPrices(Item item) {
		ItemPriceDataAdapter priceSource = new ItemPriceDataAdapter();
		SellerDataAdapter sellerSource = new SellerDataAdapter();
		List<ItemPrice> prices = item.getPrices();
		if (prices != null) {
			for (ItemPrice itemPrice : prices) {
				Seller itemSeller = itemPrice.getSeller();
				if (itemSeller != null) {
					sellerSource.getOrCreate(itemSeller);
					itemPrice.setSellerId(itemSeller.getId());
				}

				itemPrice.setItemId(item.getId());
				priceSource.update(itemPrice);
			}
		}
	}

	@Override
	protected boolean delete(long id) {
		ItemPriceDataAdapter priceSource = new ItemPriceDataAdapter();
		List<ItemPrice> associatedPrices = priceSource.getAll(
				ItemPriceDataAdapter.DB_KEY_ITEM_ID + " = " + id, null, null);
		if (associatedPrices != null && associatedPrices.size() > 0) {
			for (ItemPrice itemPrice : associatedPrices) {
				priceSource.delete(itemPrice.getId());
			}
		}

		return super.delete(id);
	}

	@Override
	ContentValues toContentValues(Item item) {
		ContentValues values = new ContentValues();
		values.put(DB_KEY_DISPLAY_NAME,
				item.getDisplayName() != null ? item.getDisplayName() : "");
		values.put(DB_KEY_IMAGE, item.getImageUrl() != null ? item
				.getImageUrl().toExternalForm() : "");
		values.put(DB_KEY_PRODUCT_CODE,
				item.getProductCode() != null ? item.getProductCode() : "");
		return values;
	}

	@Override
	Item constructItem(Cursor vals) {
		Item result = new Item();
		result.setId(vals.getLong(vals.getColumnIndex(DB_KEY_ID)));
		result.setDisplayName(vals.getString(vals
				.getColumnIndex(DB_KEY_DISPLAY_NAME)));
		result.setImageUrl(vals.getString(vals.getColumnIndex(DB_KEY_IMAGE)));
		result.setProductCode(vals.getString(vals
				.getColumnIndex(DB_KEY_PRODUCT_CODE)));
		return result;
	}

	@Override
	String[] createUniqueQuery(Item item) {
		// TODO Auto-generated method stub
		return null;
	}
}
