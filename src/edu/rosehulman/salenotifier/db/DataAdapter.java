package edu.rosehulman.salenotifier.db;

import java.util.List;

import edu.rosehulman.salenotifier.TrackedItemsActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class DataAdapter<T> {
	
	private SQLiteOpenHelper dbOpenHelper;
	private SQLiteDatabase db;
	
	public DataAdapter() {
		dbOpenHelper = SaleNotifierSQLHelper.getInstance();
		db = dbOpenHelper.getWritableDatabase();
	}
	
	abstract boolean insert(T item);
	abstract boolean update(T item);
	abstract boolean delete(T item);
	
	abstract T getById(long id);
	abstract List<T> getAll(String where, String groupBy, String order);
	
	
}
