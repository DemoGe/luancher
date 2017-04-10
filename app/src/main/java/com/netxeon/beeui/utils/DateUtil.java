package com.netxeon.beeui.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 */
public class DateUtil {


    public static String getTime(Context context) {
        String date = getFormattedDate(context);
        String sTime = date.substring(11, 16);
        return sTime;

    }

    public static String getDate(Context context) {
        String date = getFormattedDate(context);
        String sDate = date.substring(5, 11);
        return sDate;
    }
    public static String getWeek(Context context) {
                Calendar c = Calendar.getInstance();
                String date = getFormattedDate(context);
                String week = date.substring(23, date.length());
                return week;
            }

    public static String getAmOrPm(Context context) {
                Calendar c = Calendar.getInstance();
                String date = getFormattedDate(context);
                String amorpm = date.substring(20, 22);
                return amorpm;
    }

    private static String getFormattedDate(Context context) {

        Time time = new Time();
        time.setToNow();
        DateFormat.getDateInstance();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a  EEEE");
        String formattedDate = df.format(c.getTimeInMillis());
        //String formattedDate = df.format(c.getTime());
        //加入是否是12小时判断
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if(strTimeFormat!=null && strTimeFormat.equals("12"))
        {
            DateFormat dateFormat12=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a  EEEE");

            formattedDate = dateFormat12.format(c.getTimeInMillis());
        }
        return formattedDate;
    }

}
