package com.netxeon.beeui.utils;

import com.netxeon.beeui.R;

/**
 * 定义一些常量
 */
public class Data {

    public static final String ACTION_UPDATE_SHORTCUTS = "com.netxeon.probox.UPDATE_SHORTCUTS";
    public static String PRE_DB_VERSION = "db_version";

    private static final int[] mWeatherIcons = {
            R.mipmap.weather0, R.mipmap.weather1, R.mipmap.weather2, R.mipmap.weather3, R.mipmap.weather4,
            R.mipmap.weather5, R.mipmap.weather6, R.mipmap.weather7, R.mipmap.weather8, R.mipmap.weather9,
            R.mipmap.weather10, R.mipmap.weather11, R.mipmap.weather12, R.mipmap.weather13, R.mipmap.weather14,
            R.mipmap.weather15, R.mipmap.weather16, R.mipmap.weather17, R.mipmap.weather18, R.mipmap.weather19,
            R.mipmap.weather20, R.mipmap.weather21, R.mipmap.weather22, R.mipmap.weather23, R.mipmap.weather24,
            R.mipmap.weather25, R.mipmap.weather26, R.mipmap.weather27, R.mipmap.weather28, R.mipmap.weather29,
            R.mipmap.weather30, R.mipmap.weather31, R.mipmap.weather32, R.mipmap.weather33, R.mipmap.weather34,
            R.mipmap.weather35, R.mipmap.weather36, R.mipmap.weather37, R.mipmap.weather38, R.mipmap.weather39,
            R.mipmap.weather40, R.mipmap.weather41, R.mipmap.weather42, R.mipmap.weather43, R.mipmap.weather44,
            R.mipmap.weather45, R.mipmap.weather46, R.mipmap.weather47};


    public final static int getWeatherIcon(int code){
        return mWeatherIcons[code];
    }

    public static final String MOVIE = "movie";
    public static final String FAVOURITES = "favourites";
    public static final String MUSIC = "music";
    public static final String STREAM = "stream";
    public static final String INTERNET = "internet";
    public static final String GAME = "game";
    public static final String PHOTO = "photo";
    public static final String SOCIAL = "social";
    public static final String HOME = "home";

}
