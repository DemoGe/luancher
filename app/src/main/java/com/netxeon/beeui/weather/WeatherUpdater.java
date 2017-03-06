package com.netxeon.beeui.weather;

import android.os.Handler;
import android.os.Message;

import com.netxeon.beeui.utils.L;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

public class WeatherUpdater implements YahooWeatherInfoListener,
		YahooWeatherExceptionListener {
	private Handler mWeatherHandler;

	public WeatherUpdater(Handler weatherHandler) {
		mWeatherHandler = weatherHandler;
	}

	@Override
	public void onFailConnection(Exception arg0) {
		mWeatherHandler.sendEmptyMessage(WeatherUtils.MSG_WEATHER_NETWORK_DISCONNECTED);
		L.d("WeatherUpdater.gotWeatherInfo() onFailConnection");

	}

	@Override
	public void onFailFindLocation(Exception arg0) {
		mWeatherHandler.sendEmptyMessage(WeatherUtils.MSG_WEATHER_PARSE_CITY_FAILED);
		L.d("WeatherUpdater.gotWeatherInfo() onFailFindLocation");

	}

	@Override
	public void onFailParsing(Exception arg0) {
		mWeatherHandler.sendEmptyMessage(WeatherUtils.MSG_WEATHER_FAILED);
		L.d("WeatherUpdater.gotWeatherInfo() onFailParsing");

	}

	@Override
	public void gotWeatherInfo(WeatherInfo info) {
		Message msg = new Message();
		msg.what = WeatherUtils.MSG_WEATHER_OK;
		msg.obj = info;
		if(mWeatherHandler == null){
			L.d("WeatherUpdater.gotWeatherInfo() mWeatherHandler is null");
		}else{
			L.d("WeatherUpdater.gotWeatherInfo() get weather info");
			mWeatherHandler.sendMessage(msg);
		}

	}

}
