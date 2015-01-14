package edu.rosehulman.salenotifier.db;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DataAdapter<T> {
	
	private SQLiteOpenHelper dbOpenHelper;
	private SQLiteDatabase db;
	
	public DataAdapter() {
		
	}
	
	abstract boolean insert(T item);
	abstract boolean update(T item);
	abstract boolean delete(T item);
	
	abstract T getById(long id);
	abstract List<T> getAll(String where, String groupBy, String order);
	
	
}
