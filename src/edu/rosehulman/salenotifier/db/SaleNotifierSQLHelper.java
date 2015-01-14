package edu.rosehulman.salenotifier.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SaleNotifierSQLHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "sale_notifier.db";
	
	private static SaleNotifierSQLHelper instance = null;
	
	public static synchronized void init(Context context){
		instance = new SaleNotifierSQLHelper(context);
	}
	
	public static synchronized boolean isInit(){
		return instance != null;
	}
	
	public static synchronized SaleNotifierSQLHelper getInstance(){
		if(instance == null){
			throw new IllegalStateException("Tried to access SaleNotifierSQLHelper.getInstance with calling init first");
		}
	}

	private SaleNotifierSQLHelper(Context context){
		super(context, "database.name", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
