package edu.rosehulman.salenotifier.db;

import java.util.List;

import edu.rosehulman.salenotifier.IQueryable;
import edu.rosehulman.salenotifier.Item;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DataAdapter<T extends IQueryable> {

	protected SQLiteOpenHelper dbOpenHelper;
	protected SQLiteDatabase db;
	
	abstract String getTableName();
	abstract String getDBKeyColumn();
	abstract boolean doesItemExist(T item);
	abstract ContentValues toContentValues(T item);
	

	public DataAdapter() {
		dbOpenHelper = SaleNotifierSQLHelper.getInstance();
		db = dbOpenHelper.getWritableDatabase();
	}

	boolean insert(T item) {
		if (doesItemExist(item)) {
			return update(item);
		}

		ContentValues vals = toContentValues(item);
		long id = db.insert(getTableName(), null, vals);
		item.setId(id);

		return id >= 0;
	}

	boolean update(T item) {
		if (!doesItemExist(item)) {
			return insert(item);
		}
		ContentValues vals = toContentValues(item);
		return db.update(getTableName(), vals, getDBKeyColumn() + "=" + item.getId(),
				null) == 1;
	}

	boolean delete(T item) {
		return delete(item.getId());
	}

	boolean delete(long id) {
		return db.delete(getTableName(), getDBKeyColumn() + "=" + id, null) == 1;
	}

	T getById(long id) {
		return null;
	}

	List<T> getAll(String where, String groupBy, String order){
		return null;
	}

}
