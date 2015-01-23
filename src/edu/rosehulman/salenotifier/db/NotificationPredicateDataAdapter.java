package edu.rosehulman.salenotifier.db;

import edu.rosehulman.salenotifier.models.NotificationPredicate;
import android.content.ContentValues;
import android.database.Cursor;

public class NotificationPredicateDataAdapter extends
		DataAdapter<NotificationPredicate> {

	protected static final String TABLE_NOTIFICATIONS = "notificationPredicates";

	/* Column Names */
	protected static final String DB_KEY_ID = "id";
	protected static final String DB_KEY_DESCRIPTION = "description";
	protected static final String DB_KEY_PREDICATE = "predicate";

	protected static String CREATE_TABLE = "";
	static {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_NOTIFICATIONS + " (");
		sb.append(DB_KEY_ID + " integer primary key autoincrement, ");
		sb.append(DB_KEY_DESCRIPTION + " text, ");
		sb.append(DB_KEY_PREDICATE + " text);");

		CREATE_TABLE = sb.toString();
	}

	@Override
	String getTableName() {
		return TABLE_NOTIFICATIONS;
	}

	@Override
	String getDBKeyColumn() {
		return DB_KEY_ID;
	}

	@Override
	boolean doesItemExist(NotificationPredicate item) {
		return item != null && item.getId() > -1;
	}

	@Override
	ContentValues toContentValues(NotificationPredicate item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	NotificationPredicate constructItem(Cursor vals) {
		// TODO Auto-generated method stub
		return null;
	}

}
