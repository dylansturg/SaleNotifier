package edu.rosehulman.salenotifier.db;

import java.util.GregorianCalendar;
import java.util.List;

import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.models.Seller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

public class ItemPriceDataAdapter extends DataAdapter<ItemPrice> {
	protected static final String TABLE_ITEM_PRICES = "itemPrices";

	/* Column Names */
	protected static final String DB_KEY_ID = "id";
	protected static final String DB_KEY_ITEM_ID = "itemId";
	protected static final String DB_KEY_SELLER_ID = "sellerId";
	/*
	 * Date will be stored in UNIX milis (as a long) and then converted to a
	 * calendar.
	 */
	protected static final String DB_KEY_DATE = "date";
	protected static final String DB_KEY_TYPE = "type";
	protected static final String DB_KEY_BUY_LOCATION = "buyLocation";
	protected static final String DB_KEY_PRICE = "price";

	protected static String CREATE_TABLE;
	static {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_ITEM_PRICES + " (");
		sb.append(DB_KEY_ID + " integer primary key autoincrement, ");
		sb.append(DB_KEY_ITEM_ID + " integer not null, ");
		sb.append(DB_KEY_SELLER_ID + " integer not null, ");
		sb.append(DB_KEY_DATE + " integer not null, ");
		sb.append(DB_KEY_TYPE + " text, ");
		sb.append(DB_KEY_BUY_LOCATION + " text, ");
		sb.append(DB_KEY_PRICE + " real not null);");

		CREATE_TABLE = sb.toString();
	}

	@Override
	String getTableName() {
		return TABLE_ITEM_PRICES;
	}

	@Override
	String getDBKeyColumn() {
		return DB_KEY_ID;
	}

	@Override
	boolean doesItemExist(ItemPrice item) {
		return item.getId() >= 0;
	}

	@Override
	protected ItemPrice getById(long id) {
		ItemPrice result = super.getById(id);
		getItemPriceSeller(result);
		return result;
	}

	@Override
	protected List<ItemPrice> getAll(String where, String groupBy, String order) {
		// TODO Auto-generated method stub
		List<ItemPrice> results = super.getAll(where, groupBy, order);
		for (ItemPrice itemPrice : results) {
			getItemPriceSeller(itemPrice);
		}
		return results;
	}

	private void getItemPriceSeller(ItemPrice item) {
		SellerDataAdapter sellerSource = new SellerDataAdapter();
		Seller expectedSeller = sellerSource.getById(item.getSellerId());
		if (expectedSeller != null) {
			item.setSeller(expectedSeller);
		}
	}

	@Override
	ContentValues toContentValues(ItemPrice item) {
		ContentValues vals = new ContentValues();
		vals.put(DB_KEY_ITEM_ID, item.getItemId());
		vals.put(DB_KEY_SELLER_ID, item.getSellerId());
		vals.put(DB_KEY_TYPE, item.getType());
		vals.put(DB_KEY_BUY_LOCATION, item.getUrlSource());
		vals.put(DB_KEY_DATE, item.getDate() != null ? item.getDate()
				.getTimeInMillis() : new GregorianCalendar().getTimeInMillis());
		vals.put(DB_KEY_PRICE, item.getPrice());
		return vals;
	}

	@Override
	ItemPrice constructItem(Cursor vals) {
		ItemPrice result = new ItemPrice();
		result.setId(vals.getLong(vals.getColumnIndex(DB_KEY_ID)));
		result.setSellerId(vals.getLong(vals.getColumnIndex(DB_KEY_SELLER_ID)));
		result.setItemId(vals.getLong(vals.getColumnIndex(DB_KEY_ITEM_ID)));
		result.setType(vals.getString(vals.getColumnIndex(DB_KEY_TYPE)));
		result.setBuyLocation(vals.getString(vals
				.getColumnIndex(DB_KEY_BUY_LOCATION)));
		result.setDate(vals.getLong(vals.getColumnIndex(DB_KEY_DATE)));

		result.setPrice(vals.getDouble(vals.getColumnIndex(DB_KEY_PRICE)));
		return result;
	}

	@Override
	String[] createUniqueQuery(ItemPrice item) {
		String whereClause = DB_KEY_ID + " = ?" + " OR (" + DB_KEY_ITEM_ID
				+ " = ?" + " AND " + DB_KEY_DATE + " = ?" + " AND "
				+ DB_KEY_SELLER_ID + " = ?" + " AND " + DB_KEY_PRICE + " = ?)";
		String qs = SQLiteQueryBuilder.buildQueryString(false,
				TABLE_ITEM_PRICES, null, whereClause, null, null, null, null);
		return new String[] {
				qs,
				Long.toString(item.getId()),
				Long.toString(item.getItemId()),
				Long.toString(item.getDate() != null ? item.getDate()
						.getTimeInMillis() : 0),
				Long.toString(item.getSellerId()),
				Double.toString(item.getPrice()) };
	}
}
