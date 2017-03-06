package com.netxeon.beeui.weather;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.netxeon.beeui.utils.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeather.SEARCH_MODE;

public class WeatherUtils {

	public static final String WEATHER_CITY = "locationCity";
	public static final int MSG_WEATHER_OK = 1;
	public static final int MSG_WEATHER_NO_CITY = 0;
	public static final int MSG_WEATHER_FAILED = -1;
	public static final int MSG_WEATHER_PARSE_CITY_FAILED = -2;
	public static final int MSG_WEATHER_NETWORK_DISCONNECTED = -3;
	public static final String WEATHER_CITY2 = "locationCity";
	public static void writeToExternalStoragePublic(Context context, String filename, byte[] content) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();

		if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
			try {
				File file = new File(path, filename);
				file.mkdirs();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Helper Method to Test if external Storage is Available
	 */
	public static boolean isExternalStorageAvailable() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			state = true;
		}
		return state;
	}
	
	public static boolean isExternalStorageReadOnly() {
	    boolean state = false;
	    String extStorageState = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
	        state = true;
	    }
	    return state;
	}
	
	public static void updateWeather(Activity activity, Handler weatherHandler, String city){
		YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, false);
		L.d("stored city name " + city);
		if (!city.equals("empty")) {
			mYahooWeather.setNeedDownloadIcons(true);
			mYahooWeather.setSearchMode(SEARCH_MODE.PLACE_NAME);
			mYahooWeather.queryYahooWeatherByPlaceName(activity,
					city, new WeatherUpdater(weatherHandler));
		} else {
			weatherHandler.sendEmptyMessage(MSG_WEATHER_NO_CITY);
		}
	}
	
}
