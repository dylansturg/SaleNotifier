package edu.rosehulman.salenotifier.models;

public class ItemNotification implements IQueryable {

	private long mId = -1;
	private long mItemId = -1;
	private double mThreshold = 0;
	private String mPredicate;

	public ItemNotification() {
		this(0, "", -1);
	}

	public ItemNotification(double threshold, String predicate, long itemId) {
		this(-1, threshold, predicate, itemId);
	}

	public ItemNotification(long id, double threshold, String predicate,
			long itemId) {
		mId = id;
		mThreshold = threshold;
		mPredicate = predicate;
		mItemId = itemId;
	}

	@Override
	public long getId() {
		return mId;
	}

	@Override
	public void setId(long id) {
		mId = id;
	}

	public long getItemId() {
		return mItemId;
	}

	public void setItemId(long id) {
		mItemId = id;
	}

	public double getThreshold() {
		return mThreshold;
	}

	public void setThreshold(double threshold) {
		mThreshold = threshold;
	}

	public String getPredicate() {
		return mPredicate;
	}

	public void setPredicate(String predicate) {
		mPredicate = predicate;
	}
}
