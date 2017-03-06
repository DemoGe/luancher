package com.netxeon.beeui.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.netxeon.beeui.R;

/**
 * 监听蓝牙广播
 */
public class BluetoothChangeReceiver extends BroadcastReceiver {

    ImageView imageView;

    public BluetoothChangeReceiver(ImageView imageView) {
        this.imageView = imageView;

    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction() == BluetoothAdapter.ACTION_STATE_CHANGED) {
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra, -1);

            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_ON:
                    imageView.setImageResource(R.mipmap.bt_on);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_OFF:
                    imageView.setImageResource(R.mipmap.bt_off);
                    break;
            }

        }
    }
}
