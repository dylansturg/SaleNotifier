package edu.rosehulman.salenotifier.db;

import android.content.ContentValues;
import android.database.Cursor;
import edu.rosehulman.salenotifier.Seller;

public class SellerDataAdapter extends DataAdapter<Seller> {
	protected static final String TABLE_SELLERS = "sellers";
	
	/* Column Names */
	protected static final String DB_KEY_ID = "id";
	protected static final String DB_KEY_NAME = "name";
	
	protected static String CREATE_TABLE;
	static {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_SELLERS + " (");
		sb.append(DB_KEY_ID + " integer primary key autoincrement, ");
		sb.append(DB_KEY_NAME + " text not null);");
		CREATE_TABLE = sb.toString();
	}
	@Override
	String getTableName() {
		return TABLE_SELLERS;
	}
	@Override
	String getDBKeyColumn() {
		return DB_KEY_ID;
	}
	@Override
	boolean doesItemExist(Seller item) {
		return item.getId() >= 0;
	}
	@Override
	ContentValues toContentValues(Seller item) {
		ContentValues vals = new ContentValues();
		vals.put(DB_KEY_NAME, item.getName());
		return vals;
	}
	@Override
	Seller constructItem(Cursor vals) {
		Seller result = new Seller();
		result.setId(vals.getLong(vals.getColumnIndex(DB_KEY_ID)));
		result.setName(vals.getString(vals.getColumnIndex(DB_KEY_NAME)));
		return result;
	}
	
}
