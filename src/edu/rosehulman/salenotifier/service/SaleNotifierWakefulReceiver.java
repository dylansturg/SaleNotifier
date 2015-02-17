package edu.rosehulman.salenotifier.service;

import java.util.GregorianCalendar;
import edu.rosehulman.salenotifier.TrackedItemsActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class SaleNotifierWakefulReceiver extends WakefulBroadcastReceiver {

	private static final int ALARM_REQUEST_CODE = 9156;

	private AlarmManager alarmManager;
	private PendingIntent alarmIntent;

	public SaleNotifierWakefulReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TrackedItemsActivity.LOG_TAG,
				"Alarm Triggered - starting ItemUpdateBackgroundService");
		Intent itemService = new Intent(context,
				ItemUpdateBackgroundService.class);
		startWakefulService(context, itemService);
	}

	public void setupRegularAlarm(Context context, boolean ignoreIfExists) {
		boolean alarmExists = (PendingIntent.getBroadcast(context,
				ALARM_REQUEST_CODE, new Intent(context,
						SaleNotifierWakefulReceiver.class),
				PendingIntent.FLAG_NO_CREATE) != null);

		if (alarmExists && ignoreIfExists) {
			return;
		}

		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SaleNotifierWakefulReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE,
				intent, 0);

		GregorianCalendar currentTime = new GregorianCalendar();
		currentTime.add(GregorianCalendar.SECOND, 10);

		/*
		 * Updates in 15 mins and then twice a day from then on
		 */
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES,
				AlarmManager.INTERVAL_HALF_DAY, alarmIntent);

		ComponentName receiver = new ComponentName(context,
				SaleNotifierBootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	public void setupRegularAlarm(Context context) {
		setupRegularAlarm(context, true);
	}

	public void cancelAlarm(Context context) {
		if (alarmManager == null) {
			alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
		}

		if (alarmIntent == null) {
			Intent intent = new Intent(context,
					SaleNotifierWakefulReceiver.class);
			alarmIntent = PendingIntent.getBroadcast(context,
					ALARM_REQUEST_CODE, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		}

		alarmManager.cancel(alarmIntent);

		ComponentName receiver = new ComponentName(context,
				SaleNotifierBootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}

}
