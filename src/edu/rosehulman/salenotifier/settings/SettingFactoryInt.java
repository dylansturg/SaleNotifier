package edu.rosehulman.salenotifier.settings;

import java.nio.ByteBuffer;

import android.util.Log;
import edu.rosehulman.salenotifier.TrackedItemsActivity;

public class SettingFactoryInt extends SettingFactory {
	private static final int BYTE_COUNT = 4;

	@Override
	protected Setting<?> buildSettingForValue(byte[] value) {
		Setting<Integer> result = new Setting<Integer>();
		Integer parsed = ByteBuffer.wrap(value).getInt();
		result.setValue(parsed);
		return result;
	}

	@Override
	protected byte[] createBlobForSetting(Object value) {
		try {
			return ByteBuffer.allocate(BYTE_COUNT).putInt((Integer) value)
					.array();
		} catch (Exception badValue) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to parsed value into byte[] for int");
			return new byte[0];
		}
	}


}
