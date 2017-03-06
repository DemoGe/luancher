package com.netxeon.beeui.utils;

import android.text.format.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 */
public class DateUtil {


    public static String getTime() {
        String date = getFormattedDate();
        String sTime = date.substring(11, 16);
        return sTime;

    }

    public static String getDate() {
        String date = getFormattedDate();
        String sDate = date.substring(5, 11);
        return sDate;
    }
    public static String getWeek() {
                Calendar c = Calendar.getInstance();
                String date = getFormattedDate();
                String week = date.substring(23, date.length());
                return week;
            }

    public static String getAmOrPm() {
                Calendar c = Calendar.getInstance();
                String date = getFormattedDate();
                String amorpm = date.substring(20, 22);
                return amorpm;
    }
    private static String getFormattedDate() {

        Time time = new Time();
        time.setToNow();
        DateFormat.getDateInstance();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a  EEEE");
        String formattedDate = df.format(c.getTimeInMillis());
        //String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
