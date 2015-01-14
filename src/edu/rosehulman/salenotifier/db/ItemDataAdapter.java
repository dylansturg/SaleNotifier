package edu.rosehulman.salenotifier.db;

import java.util.List;

import android.content.ContentValues;

import edu.rosehulman.salenotifier.Item;

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

	private ContentValues toContentValues(Item item) {
		ContentValues values = new ContentValues();
		values.put(DB_KEY_DISPLAY_NAME, item.getDisplayName());
		values.put(DB_KEY_IMAGE, item.getImageUrl().toExternalForm());
		values.put(DB_KEY_PRODUCT_CODE, item.getProductCode());
		return values;
	}

	private boolean doesItemExist(Item item) {
		return item.getId() >= 0;
	}

	@Override
	public boolean insert(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Item getById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> getAll(String where, String groupBy, String order) {
		// TODO Auto-generated method stub
		return null;
	}

}
