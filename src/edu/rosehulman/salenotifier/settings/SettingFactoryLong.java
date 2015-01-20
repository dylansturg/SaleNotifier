package edu.rosehulman.salenotifier.settings;

import java.nio.ByteBuffer;

import edu.rosehulman.salenotifier.TrackedItemsActivity;

import android.util.Log;

public class SettingFactoryLong extends SettingFactory {
	private static final int BYTE_COUNT = 8;

	@Override
	protected Setting<?> buildSettingForValue(byte[] value) {
		Setting<Long> result = new Setting<Long>();
		Long parsed = ByteBuffer.wrap(value).getLong();
		result.setValue(parsed);
		return result;
	}

	@Override
	protected byte[] createBlobForSetting(Object value) {
		try {
			return ByteBuffer.allocate(BYTE_COUNT).putLong((Long) value)
					.array();
		} catch (Exception badValue) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"Failed to parsed value into byte[] for long");
			return new byte[0];
		}
	}

}
