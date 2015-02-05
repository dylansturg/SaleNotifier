package edu.rosehulman.salenotifier;

import java.util.HashMap;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.NotificationPredicate;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationFactory {

	private static HashMap<String, Integer> mapping = new HashMap<String, Integer>();
	private static int mId = 0;
	private Context ctx;
	
	public NotificationFactory(Context ctx) {
		this.ctx = ctx;
	}
	
	public int create(double amount, Item item, NotificationPredicate pred) {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(ctx)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(item.getDisplayName())
		        .setContentText(pred.getDescription() + " " + amount);
		
		// TODO: change this to go straight to the items listings
		Intent resultIntent = new Intent(ctx, TrackedItemsActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(TrackedItemsActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		
		
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		String[] deals = new String[2];
		
		deals[0] = "Tea Kettle for 15.95";
		deals[1] = "Tea Kettle for 19.75";
		
		// Sets a title for the Inbox in expanded layout
		inboxStyle.setBigContentTitle("Found Deals");
		// Moves events into the expanded layout
		for (int i=0; i < deals.length; i++) {
			inboxStyle.addLine(deals[i]);
		}
		// Moves the expanded layout object into the notification object.
		mBuilder.setStyle(inboxStyle);

		
		int notifId = getOrCreateNotificationId(item.getDisplayName());
		mNotificationManager.notify(notifId, mBuilder.build());
		return notifId;
	}
	
	private int getOrCreateNotificationId(String itemName) {
		if(!mapping.containsKey(itemName))
			mapping.put(itemName, mId++);
		return mapping.get(itemName);
	}
	
}
