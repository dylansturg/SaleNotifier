package edu.rosehulman.salenotifier.db;

import java.util.List;

public interface IDataAdapter<T> {
	
	boolean insert(T item);
	boolean update(T item);
	boolean delete(T item);
	
	T getById(long id);
	List<T> getAll(String where, String groupBy, String order);
}
