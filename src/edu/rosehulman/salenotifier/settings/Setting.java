package edu.rosehulman.salenotifier.settings;

import edu.rosehulman.salenotifier.models.IQueryable;

public class Setting<T> implements IQueryable{
	private long mId = -1;
	private String target;
	private String name;
	private T value;

	public Setting() {

	}

	public Setting(String tar, String name, T val) {
		target = tar;
		this.name = name;
		value = val;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String tar) {
		target = tar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T val) {
		value = val;
	}

	@Override
	public long getId() {
		return mId;
	}

	@Override
	public void setId(long id) {
		mId = id;
	}
}
