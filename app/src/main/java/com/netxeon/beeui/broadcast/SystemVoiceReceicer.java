package com.netxeon.beeui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netxeon.beeui.activity.SystemVoiceActivity;

/**
 * Created by Administrator on 2017/1/3.
 */

public class SystemVoiceReceicer extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentSystemVoice = new Intent(context, SystemVoiceActivity.class);
        intentSystemVoice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentSystemVoice);
    }
}
