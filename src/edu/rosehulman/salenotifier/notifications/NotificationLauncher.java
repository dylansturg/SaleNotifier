package edu.rosehulman.salenotifier.notifications;

import java.util.HashMap;

import edu.rosehulman.salenotifier.ItemCurrentActivity;
import edu.rosehulman.salenotifier.R;
import edu.rosehulman.salenotifier.models.Item;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationLauncher {

	private static HashMap<String, Integer> mapping = new HashMap<String, Integer>();
	private static int mId = 0;

	private NotificationLauncher() {
	}

	public static void launch(Context ctx, Item item, String message) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				ctx).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(item.getDisplayName()).setContentText(message);

		// TODO: change this to go straight to the items listings
		Intent resultIntent = new Intent(ctx, ItemCurrentActivity.class);
		resultIntent.putExtra(ItemCurrentActivity.KEY_ITEM_ID, item.getId());

		PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0,
				resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.

		int notifId = getOrCreateNotificationId(item.getDisplayName());
		mNotificationManager.notify(notifId, mBuilder.build());
	}

	private static int getOrCreateNotificationId(String itemName) {
		if (!mapping.containsKey(itemName))
			mapping.put(itemName, mId++);
		return mapping.get(itemName);
	}

}
