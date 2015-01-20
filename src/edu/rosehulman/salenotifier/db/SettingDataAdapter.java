package edu.rosehulman.salenotifier.db;

import edu.rosehulman.salenotifier.settings.Setting;
import edu.rosehulman.salenotifier.settings.SettingFactory;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Pair;

public class SettingDataAdapter extends DataAdapter<Setting<?>> {
	protected static final String TABLE_SETTINGS = "settings";

	/* Column Names */
	protected static final String DB_KEY_ID = "id";
	protected static final String DB_KEY_TARGET = "target";
	protected static final String DB_KEY_NAME = "name";
	protected static final String DB_KEY_VALUE_TYPE = "valueType";
	protected static final String DB_KEY_VALUE = "value";

	protected static String CREATE_TABLE = "";
	static {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_SETTINGS + " (");
		sb.append(DB_KEY_ID + " integer primary key autoincrement, ");
		sb.append(DB_KEY_TARGET + " text not null, ");
		sb.append(DB_KEY_NAME + " text not null, ");
		sb.append(DB_KEY_VALUE_TYPE + " text, ");
		sb.append(DB_KEY_VALUE + " blob);");

		CREATE_TABLE = sb.toString();
	}

	@Override
	String getTableName() {
		return TABLE_SETTINGS;
	}

	@Override
	String getDBKeyColumn() {
		return DB_KEY_ID;
	}

	@Override
	boolean doesItemExist(Setting<?> item) {
		return item != null && item.getId() >= 0;
	}

	@Override
	ContentValues toContentValues(Setting<?> item) {
		ContentValues result = new ContentValues();
		result.put(DB_KEY_NAME, item.getName());
		result.put(DB_KEY_TARGET, item.getTarget());
		Pair<String, byte[]> blob = SettingFactory.blobify(item.getValue());
		result.put(DB_KEY_VALUE, blob.second);
		result.put(DB_KEY_VALUE_TYPE, blob.first);
		return result;
	}

	@Override
	Setting<?> constructItem(Cursor vals) {
		long id = vals.getLong(vals.getColumnIndex(DB_KEY_ID));
		String name = vals.getString(vals.getColumnIndex(DB_KEY_NAME));
		String target = vals.getString(vals.getColumnIndex(DB_KEY_TARGET));
		byte[] blobValue = vals.getBlob(vals.getColumnIndex(DB_KEY_VALUE));
		String blobType = vals.getString(vals.getColumnIndex(DB_KEY_VALUE_TYPE));
		
		return SettingFactory.constructSetting(id, target, name, blobType, blobValue);
	}

}
