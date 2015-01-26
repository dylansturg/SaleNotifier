package edu.rosehulman.salenotifier.db;

import android.content.ContentValues;
import android.database.Cursor;
import edu.rosehulman.salenotifier.models.ItemNotification;

public class ItemNotificationDataAdapter extends DataAdapter<ItemNotification> {

	protected static final String TABLE_ITEM_NOTIFICATIONS = "itemNotifications";

	/* Column Names */
	protected static final String DB_KEY_ID = "id";
	protected static final String DB_KEY_ITEM_ID = "itemId";
	protected static final String DB_KEY_THRESHOLD = "threshold";
	protected static final String DB_KEY_PREDICATE = "predicate";

	protected static String CREATE_TABLE = "";
	static {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_ITEM_NOTIFICATIONS + " (");
		sb.append(DB_KEY_ID + " integer primary key autoincrement, ");
		sb.append(DB_KEY_ITEM_ID + " integer not null, ");
		sb.append(DB_KEY_THRESHOLD + " real not null, ");
		sb.append(DB_KEY_PREDICATE + " text not null);");

		CREATE_TABLE = sb.toString();
	}

	@Override
	String getTableName() {
		return TABLE_ITEM_NOTIFICATIONS;
	}

	@Override
	String getDBKeyColumn() {
		return DB_KEY_ID;
	}

	@Override
	boolean doesItemExist(ItemNotification item) {
		return item != null && item.getId() >= 0;
	}

	@Override
	ContentValues toContentValues(ItemNotification item) {
		ContentValues vals = new ContentValues();
		vals.put(DB_KEY_PREDICATE, item.getPredicate());
		vals.put(DB_KEY_THRESHOLD, item.getThreshold());
		vals.put(DB_KEY_ITEM_ID, item.getItemId());
		return vals;
	}

	@Override
	ItemNotification constructItem(Cursor vals) {
		ItemNotification result = new ItemNotification();
		result.setId(vals.getLong(vals.getColumnIndex(DB_KEY_ID)));
		result.setPredicate(vals.getString(vals
				.getColumnIndex(DB_KEY_PREDICATE)));
		result.setThreshold(vals.getDouble(vals
				.getColumnIndex(DB_KEY_THRESHOLD)));
		result.setItemId(vals.getLong(vals.getColumnIndex(DB_KEY_ITEM_ID)));
		return result;
	}

	@Override
	String[] createUniqueQuery(ItemNotification item) {
		// TODO Auto-generated method stub
		return null;
	}

}
