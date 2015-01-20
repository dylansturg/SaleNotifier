package edu.rosehulman.salenotifier.settings;

import java.io.UnsupportedEncodingException;

import edu.rosehulman.salenotifier.TrackedItemsActivity;
import android.util.Log;

public class SettingFactoryString extends SettingFactory {
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	public SettingFactoryString(){
		
	}

	@Override
	protected Setting<?> buildSettingForValue(byte[] value) {
		Setting<String> result = new Setting<String>();
		try {
			String realVal = new String(value, DEFAULT_CHARSET);
			result.setValue(realVal);
		} catch (UnsupportedEncodingException e) {
			Log.d(TrackedItemsActivity.LOG_TAG, "Failed to parse value into string");
		}
		return result;
	}

	@Override
	protected byte[] createBlobForSetting(Object value) {
		try{
			String realVal = (String)value;
			return realVal.getBytes(DEFAULT_CHARSET);
		} catch(Exception blobFail){
			Log.d(TrackedItemsActivity.LOG_TAG, "Failed to cast blob type", blobFail);
		}
		return new byte[0];
	}

}
