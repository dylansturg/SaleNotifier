package edu.rosehulman.salenotifier.settings;

import edu.rosehulman.salenotifier.db.Enumerable.IPredicate;
import edu.rosehulman.salenotifier.models.IQueryable;

public class Setting<T> implements IQueryable {
	public static final String DATA_SOURCE_PREFIX = "DATA_SOURCE_";
	public static final String DATA_SOURCE_NAME_FORMAT = DATA_SOURCE_PREFIX
			+ "%s";

	public static final String SETTING_NAME_NOTIFICATIONS = "NOTIFICATIONS";
	public static final String SETTING_NAME_DELETE_AFTER = "DELETE_AFTER";

	public static IPredicate<Setting<?>> createNamePredicate(final String name) {
		IPredicate<Setting<?>> matchNamePred = new IPredicate<Setting<?>>() {

			@Override
			public boolean match(Setting<?> element) {
				return element.getName().equalsIgnoreCase(name);
			}
		};
		return matchNamePred;
	}

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
