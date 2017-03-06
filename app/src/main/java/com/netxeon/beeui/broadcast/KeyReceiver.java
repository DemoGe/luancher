package com.netxeon.beeui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.netxeon.beeui.activity.ShutdownActivity;

public class KeyReceiver extends BroadcastReceiver {

	

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("test", "接收到了");

		    Intent intent2 = new Intent(context,ShutdownActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
	}
	
 

}
