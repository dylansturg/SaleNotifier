package edu.rosehulman.salenotifier;

public class Seller implements IQueryable {
	private long mId = -1;
	private String mName;

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}
}
