package com.netxeon.beeui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netxeon.beeui.activity.AppActicity;

public class KeyReceiver extends BroadcastReceiver {

	private Intent intent2;

	@Override
	public void onReceive(Context context, Intent intent) {
		intent2 = new Intent(context, AppActicity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
	}
	
 

}
