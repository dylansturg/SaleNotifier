package edu.rosehulman.salenotifier.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Enumerable<T> extends ArrayList<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3411334056105642282L;

	public Enumerable(List<T> settings) {
		super(settings);
	}
	
	public Enumerable(){
		super();
	}
	
	public Enumerable(int capacity){
		super(capacity);
	}

	public Enumerable<T> where(IPredicate<T> condition){
		Enumerable<T> results = new Enumerable<T>();
		Iterator<T> contents = iterator();
		while(contents.hasNext()){
			T item = contents.next();
			if(condition.match(item)){
				results.add(item);
			}
		}
		return results;
	}
	
	public T firstOrDefault(IPredicate<T> condition){
		Iterator<T> contents = iterator();
		while(contents.hasNext()){
			T item = contents.next();
			if(condition.match(item)){
				return item;
			}
		}
		return null;
	}
	
	public interface IPredicate<T>{
		boolean match(T element);
	}
}
