package edu.rosehulman.salenotifier.db;

import edu.rosehulman.salenotifier.TrackedItemsActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SaleNotifierSQLHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "sale_notifier.db";
	
	private static final String[] CREATE_TABLE_STATEMENTS = {
		ItemDataAdapter.CREATE_TABLE,
		ItemPriceDataAdapter.CREATE_TABLE,
		SellerDataAdapter.CREATE_TABLE,
		SettingDataAdapter.CREATE_TABLE,
	};
	
	private static final String[] TABLE_NAMES = {
		ItemDataAdapter.TABLE_ITEMS,
		ItemPriceDataAdapter.TABLE_ITEM_PRICES,
		SellerDataAdapter.TABLE_SELLERS,
		SettingDataAdapter.TABLE_SETTINGS,
	};
	
	private static SaleNotifierSQLHelper instance = null;
	
	public static synchronized void init(Context context){
		instance = new SaleNotifierSQLHelper(context);
	}
	
	public static synchronized boolean isInit(){
		return instance != null;
	}
	
	public static synchronized SaleNotifierSQLHelper getInstance(){
		if(instance == null){
			Log.d(TrackedItemsActivity.LOG_TAG, "Tried to access SaleNotifierSQLHelper.getInstance with calling init first");
			throw new IllegalStateException("Tried to access SaleNotifierSQLHelper.getInstance with calling init first");
		}
		return instance;
	}

	private SaleNotifierSQLHelper(Context context){
		super(context,  DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String createTable : CREATE_TABLE_STATEMENTS) {
			db.execSQL(createTable);
		}
	}

	/* For now, drop and recreate entire set of tables.  We aren't worried about data persistence between upgrades at this moment.
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (String tableName : TABLE_NAMES) {
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
		}
		onCreate(db);
	}

}
