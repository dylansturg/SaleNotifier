package edu.rosehulman.salenotifier.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SaleNotifierBootReceiver extends BroadcastReceiver {

	private SaleNotifierWakefulReceiver alarm = new SaleNotifierWakefulReceiver();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			alarm.setupRegularAlarm(context);
		}
	}
}
