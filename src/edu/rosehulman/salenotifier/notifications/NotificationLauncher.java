package edu.rosehulman.salenotifier.notifications;

import java.util.HashMap;

import edu.rosehulman.salenotifier.ItemCurrentActivity;
import edu.rosehulman.salenotifier.ItemSearchActivity;
import edu.rosehulman.salenotifier.R;
import edu.rosehulman.salenotifier.models.Item;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

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

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		// TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		// // Adds the back stack for the Intent (but not the Intent itself)
		//
		// stackBuilder.addParentStack(ItemSearchActivity.class);
		//
		// // Adds the Intent that starts the Activity to the top of the stack
		// stackBuilder.addNextIntent(resultIntent);
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
