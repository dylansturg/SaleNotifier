package edu.rosehulman.salenotifier.db;

import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.database.Cursor;
import edu.rosehulman.salenotifier.models.ItemPrice;

public class ItemPriceDataAdapter extends DataAdapter<ItemPrice>{
	protected static final String TABLE_ITEM_PRICES = "itemPrices";
	
	/* Column Names */
	protected static final String DB_KEY_ID = "id";
	protected static final String DB_KEY_PRODUCT_CODE = "productCode";
	protected static final String DB_KEY_PRICE = "price";
	/*
	 * Date will be stored in UNIX milis (as a long) and then converted to a calendar.
	 */
	protected static final String DB_KEY_DATE = "date";
	protected static final String DB_KEY_SELLER_ID = "sellerId";
	
	protected static String CREATE_TABLE;
	static {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_ITEM_PRICES + " (");
		sb.append(DB_KEY_ID + " integer primary key autoincrement, ");
		sb.append(DB_KEY_PRODUCT_CODE + " text not null, ");
		sb.append(DB_KEY_PRICE + " real not null, ");
		sb.append(DB_KEY_DATE + " integer not null, ");
		sb.append(DB_KEY_SELLER_ID + " integer not null);");
		
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
	ContentValues toContentValues(ItemPrice item) {
		ContentValues vals = new ContentValues();
		vals.put(DB_KEY_DATE, item.getDate() != null ? item.getDate().getTimeInMillis() : new GregorianCalendar().getTimeInMillis());
		vals.put(DB_KEY_PRICE, item.getPrice());
		vals.put(DB_KEY_PRODUCT_CODE, item.getProductCode() != null ? item.getProductCode() : "");
		return vals;
	}
	@Override
	ItemPrice constructItem(Cursor vals) {
		ItemPrice result = new ItemPrice();
		result.setId(vals.getLong(vals.getColumnIndex(DB_KEY_ID)));
		result.setPrice(vals.getDouble(vals.getColumnIndex(DB_KEY_PRICE)));
		result.setProductCode(vals.getString(vals.getColumnIndex(DB_KEY_PRODUCT_CODE)));
		result.setSellerId(vals.getLong(vals.getColumnIndex(DB_KEY_SELLER_ID)));
		return result;
	}
}
