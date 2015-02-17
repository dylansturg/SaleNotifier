package edu.rosehulman.salenotifier.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class UpdateResultReceiver extends ResultReceiver {

	public static final int ITEMS_UPDATED_RESULT = 0x12345;
	
	public interface IOnItemsUpdated{
		void onUpdateFinished();
	}
	
	private IOnItemsUpdated mCallback;
	public UpdateResultReceiver(Handler handler) {
		super(handler);
	}
	
	public void setOnItemsUpdated(IOnItemsUpdated callback){
		mCallback = callback;
	}
	
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if(resultCode == ITEMS_UPDATED_RESULT && mCallback != null){
			mCallback.onUpdateFinished();
		}
	}

}
