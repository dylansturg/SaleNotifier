package edu.rosehulman.salenotifier.db;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import edu.rosehulman.salenotifier.models.Item;

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
}
