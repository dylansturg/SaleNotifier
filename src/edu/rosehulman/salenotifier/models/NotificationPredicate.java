package edu.rosehulman.salenotifier.models;

public class NotificationPredicate implements IQueryable {

	private long mId = -1;
	private String mDescription;
	private String mPredicate;
	
	public NotificationPredicate() {
		this(-1, "", "");
	}
	
	public NotificationPredicate(String description, String predicate){
		this(-1, description, predicate);
	}
	
	public NotificationPredicate(long id, String description, String predicate){
		mId = id;
		mDescription = description;
		mPredicate = predicate;
	}

	@Override
	public long getId() {
		return mId;
	}

	@Override
	public void setId(long id) {
		mId = id;
	}
	
	public String getDescription(){
		return mDescription;
	}
	
	public void setDescription(String desc){
		mDescription = desc;
	}
	
	public String getPredicate(){
		return mPredicate;
	}
	
	public void setPredicate(String pred){
		mPredicate = pred;
	}
	
	public boolean evaluate(double[] currentPrices, double threshold){
		return false;
	}

	@Override
	public String toString() {
		return mDescription;
	}
}
