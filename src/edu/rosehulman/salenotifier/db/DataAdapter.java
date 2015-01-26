package edu.rosehulman.salenotifier.db;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.models.IQueryable;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DataAdapter<T extends IQueryable> {

	protected SQLiteOpenHelper dbOpenHelper;
	protected SQLiteDatabase db;

	abstract String getTableName();

	abstract String getDBKeyColumn();

	/**
	 * Quickly determine if the item could be in the database (no queries
	 * allowed).
	 * 
	 * @param item
	 * @return item in database plausible
	 */
	abstract boolean doesItemExist(T item);

	abstract ContentValues toContentValues(T item);

	abstract T constructItem(Cursor vals);

	/**
	 * 
	 * @param item
	 * @return array of query string (index 0) along with selection args (indices 1+)
	 */
	abstract String[] createUniqueQuery(T item);

	protected DataAdapter() {
		dbOpenHelper = SaleNotifierSQLHelper.getInstance();
		db = dbOpenHelper.getWritableDatabase();
	}

	protected T getOrCreate(T item) {
		String[] findExists = createUniqueQuery(item);
		if (findExists != null && findExists.length > 0) {
			String[] selectionArgs = new String[findExists.length - 1];
			if (findExists.length > 1) {
				System.arraycopy(findExists, 1, selectionArgs, 0,
						findExists.length - 1);
				Cursor existingItem = db.rawQuery(findExists[0], selectionArgs);
				if (existingItem.moveToFirst()) {
					return constructItem(existingItem);
				}
			}
		}

		// Fall through means the item couldn't be found in the database
		insert(item);
		return item;
	}

	/**
	 * Inserts the item into the database. Only call if the item is NOT already
	 * in the database. If the item might be in the database, call update
	 * instead.
	 * 
	 * @param item
	 * @return
	 */
	protected boolean insert(T item) {
		ContentValues vals = toContentValues(item);
		long id = db.insert(getTableName(), null, vals);
		item.setId(id);

		return id >= 0;
	}

	/**
	 * Updates the item in the database. Guaranteed to result in the modified
	 * item existing in database. Will insert if necessary. Should almost always
	 * be used in place of insert.
	 * 
	 * @param item
	 * @return
	 */
	protected boolean update(T item) {
		if (!doesItemExist(item)) {
			return insert(item);
		}
		ContentValues vals = toContentValues(item);
		int updated = db.update(getTableName(), vals, getDBKeyColumn() + "="
				+ item.getId(), null);
		if (updated > 0) {
			return true;
		} else {
			return insert(item);
		}

	}

	protected boolean delete(T item) {
		return delete(item.getId());
	}

	protected boolean delete(long id) {
		return db.delete(getTableName(), getDBKeyColumn() + "=" + id, null) == 1;
	}

	protected T getById(long id) {
		Cursor results = db.query(getTableName(), null, getDBKeyColumn() + "="
				+ id, null, null, null, null);
		if (results.getCount() <= 0) {
			return null;
		}
		results.moveToFirst();
		T result = constructItem(results);
		return result;
	}

	protected List<T> getAll(String where, String groupBy, String order) {
		Cursor results = db.query(getTableName(), null, where, null, groupBy,
				null, order);

		ArrayList<T> items = new ArrayList<T>();
		if (results.moveToFirst()) {
			do {
				items.add(constructItem(results));
			} while (results.moveToNext());
		}
		return items;
	}

}
