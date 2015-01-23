package edu.rosehulman.salenotifier.notifications;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.gesture.Prediction;
import android.util.Log;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import edu.rosehulman.salenotifier.models.NotificationPredicate;

public class NotificationPredicateFactory {

	private static List<NotificationPredicate> predicates = new ArrayList<NotificationPredicate>();
	static {
		predicates.add(new NotificationPredicate("Price Below", "PriceBelow"));
		predicates.add(new NotificationPredicate("Price Above", "PriceAbove"));
	}

	private static Map<String, INotificationPredicate> cachedPredicates = new HashMap<String, INotificationPredicate>();

	public static List<NotificationPredicate> getAvailablePredicates() {
		return predicates;
	}

	public static INotificationPredicate resolvePredicate(String name) {
		if (cachedPredicates == null) {
			cachedPredicates = new HashMap<String, INotificationPredicate>();
		}

		name = name.toLowerCase(Locale.US);
		if (cachedPredicates.containsKey(name)) {
			return cachedPredicates.get(name);
		}

		INotificationPredicate instance = null;
		try {
			// All Class names are English (who codes in Spanish?)
			String factoryType = name.substring(0, 1).toUpperCase(Locale.US)
					+ name.substring(1);
			Class<?> clazz = Class
					.forName("edu.rosehulman.salenotifier.notifications"
							+ factoryType);
			Constructor<?> ctor = clazz.getConstructor();
			Object object = ctor.newInstance(new Object[] {});
			instance = (INotificationPredicate) object;
			cachedPredicates.put(name, instance);
		} catch (Exception reflectFailure) {
			Log.d(TrackedItemsActivity.LOG_TAG,
					"SettingFactory creating Setting<?> failed due to missing class for type: "
							+ name);
			Log.d(TrackedItemsActivity.LOG_TAG, "Reflection Error: ",
					reflectFailure);
		}
		return instance;
	}
}
