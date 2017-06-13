package com.netxeon.beeui.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.widget.ImageView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.utils.Util;
import com.netxeon.beeui.weather.WeatherUtils;

import java.util.Timer;
import java.util.TimerTask;


public class NetworkChangedReceiver extends BroadcastReceiver {
    // update weather info per 3 hours
    private static final long UPDATE_WEATHER_PER_TIME = 1000 * 60 * 60 * 3;
    int count = 0;
    private Activity mContext;
    private Handler mUpdateHandler;
    private long mLastWeatherUpdate = 0;

    public NetworkChangedReceiver(Activity context, Handler updateHandler) {
        mContext = context;
        mUpdateHandler = updateHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION) || intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean updateWeather = intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION);//网络状态改变
            initNetwork(updateWeather);
        }

        //Util.updateDateDisplay(mContext);

    }

    /*
     * update networkIcon while start app or network changed
     */
    private void initNetwork(boolean updateWeather) {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        boolean ethernet = false;
        boolean available = false;
        if (info != null&&connManager!=null) {
            ethernet = info.getType() == ConnectivityManager.TYPE_ETHERNET;
            available = info.isConnected();//网络是否可用
        }
        if (updateWeather && available) {
            if (System.currentTimeMillis() - mLastWeatherUpdate > UPDATE_WEATHER_PER_TIME) {
                mLastWeatherUpdate = System.currentTimeMillis();
                 initWeather();
 //               L.i("NetworkChangedReceiver.initNetwork() update weather now ");
            } else {
   //             L.i("NetworkChangedReceiver.initNetwork() no need update weather yet ");
            }
        }
        ImageView wifi_image = (ImageView) mContext.findViewById(R.id.main_foot_wifi_states);
        ImageView ethernat = (ImageView)mContext.findViewById(R.id.main_foot_ethernet);

        if (available == false) {
            ethernat.setImageResource(R.mipmap.ethernet_off);
            wifi_image.setImageResource(R.mipmap.icon_wifi_0);
   //         L.i("NetworkChangedReceiver:networkstate_off");
        } else {
            if (ethernet) {
                ethernat.setImageResource(available ? R.mipmap.ethernet_on : R.mipmap.ethernet_off);
                wifi_image.setImageResource(R.mipmap.icon_wifi_0);
            } else {
                ethernat.setImageResource(R.mipmap.ethernet_off);
                WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                int level = WifiManager.calculateSignalLevel(manager.getConnectionInfo().getRssi(), 5);
    //            L.i(level+"level");
                wifi_image.setImageResource(R.drawable.wifi_signl);
                switch (level) {
                    case 1:
                        wifi_image.setImageLevel(level);
                        break;
                    case 2:
                        wifi_image.setImageLevel(level);
                        break;
                    case 3:
                        wifi_image.setImageLevel(level);
                        break;
                    case 4:
                    case 5:
                        wifi_image.setImageLevel(4);
                        break;
                    default:
                        wifi_image.setImageLevel(1);
                }
            }
        }
    }

    private void initWeather() {
        count++;
        Timer mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                WeatherUtils.updateWeather(mContext, mUpdateHandler, Util.getString(mContext, WeatherUtils.WEATHER_CITY));
 //               L.d("MainActivity.initWeather() start Timer weatherUpdater. count:" + count);
            }
        };
        try {
            mTimer.schedule(task, 100, UPDATE_WEATHER_PER_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
