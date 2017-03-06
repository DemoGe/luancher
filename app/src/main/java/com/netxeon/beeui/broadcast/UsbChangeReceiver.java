package com.netxeon.beeui.broadcast;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.ImageView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.utils.Util;

/**
 * 监听存储插入拔出
 */
public class UsbChangeReceiver extends BroadcastReceiver {

    ImageView imageview;

    public UsbChangeReceiver(ImageView imageview) {
        this.imageview = imageview;
    }

    @Override
    @TargetApi(23)
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            imageview.setImageResource(R.mipmap.usb_on);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageview.setClickable(true);
                imageview.setFocusable(true);
            }

        } else {//卸载
            if (Util.getPublicVolumes(context).size() <= 0) {
                imageview.setImageResource(R.mipmap.usb_off);
                imageview.setFocusable(false);
                imageview.setClickable(false);
            }
        }


    }
}
