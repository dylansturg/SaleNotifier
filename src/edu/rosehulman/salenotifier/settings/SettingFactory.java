package edu.rosehulman.salenotifier.settings;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.rosehulman.salenotifier.TrackedItemsActivity;

import android.util.Log;
import android.util.Pair;

public abstract class SettingFactory {
	protected abstract Setting<?> buildSettingForValue(byte[] value);
	protected abstract byte[] createBlobForSetting(Object value);

	private static Map<String, SettingFactory> cachedFactories = new HashMap<String, SettingFactory>();

	public static Pair<String, byte[]> blobify(Object value) {
		String type = value.getClass().getSimpleName();
		SettingFactory factory = resolveFactory(type);
		if(factory == null){
			return null;
		}
		byte[] val = factory.createBlobForSetting(value);
		return new Pair<String, byte[]>(type, val);
	}

	public static Setting<?> constructSetting(long id, String target,
			String name, String type, byte[] value) {
		SettingFactory factory = resolveFactory(type);
		if (factory == null) {
			return null;
		}
		Setting<?> result = factory.buildSettingForValue(value);
		if (result != null) {
			result.setId(id);
			result.setTarget(target);
			result.setName(name);
		}
		return result;
	}

	private static SettingFactory resolveFactory(String type) {
		if (cachedFactories == null) {
			cachedFactories = new HashMap<String, SettingFactory>();
		}
		// All Class names are English (who codes in Spanish?)
		type = type.toLowerCase(Locale.US); // ensure all cached types are
											// entirely lowercase
		// Ensures all types resolve to same factory
		SettingFactory instance = null;
		if (cachedFactories.containsKey(type)) {
			instance = cachedFactories.get(type);
		}

		if (instance == null) {
			try {
				// All Class names are English (who codes in Spanish?)
				String factoryType = type.substring(0, 1)
						.toUpperCase(Locale.US) + type.substring(1);
				Class<?> clazz = Class
						.forName("edu.rosehulman.salenotifier.settings.SettingFactory"
								+ factoryType);
				Constructor<?> ctor = clazz.getConstructor();
				Object object = ctor.newInstance(new Object[] {});
				instance = (SettingFactory) object;
			} catch (Exception reflectFailure) {
				Log.d(TrackedItemsActivity.LOG_TAG,
						"SettingFactory creating Setting<?> failed due to missing class for type: "
								+ type);
				Log.d(TrackedItemsActivity.LOG_TAG, "Reflection Error: ",
						reflectFailure);
			}
		}

		if (instance == null) {
			// We tried pretty hard...
			return null;
		}

		return instance;
	}
}
