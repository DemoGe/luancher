package com.netxeon.beeui.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;

import com.netxeon.beeui.bean.Shortcut;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用util
 */
public class Util {
    public static final String DB_FILE = "probox.db";
    public static final String WALLPAPER_FILE = "wallpaper.png";
    public static final String PRE_FILE = "prefile";
    public static final String PRE_DATA_INITIALIZED = "data_initialized";
    public final static String CATEGORY = "CATEGORY";
    private static String PIC_DIR;
    private static boolean mSynchronized = false;
    private static List<ResolveInfo> mApps;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    public static List<ResolveInfo> getLaunchers(Context context) {
        Map<String, String> map = new HashMap<String, String>();
        PackageManager pm = context.getPackageManager();
        Intent i = new Intent("android.intent.action.MAIN");
        i.addCategory("android.intent.category.HOME");
        return pm.queryIntentActivities(i, 0);
    }

    public static void insertShortcut(Context context, String componentName, String category) {
        Shortcut shortcut = new Shortcut();
        shortcut.setCategory(category);
        shortcut.setComponentName(componentName);
        shortcut.setPersistent(false);
        DBHelper db = DBHelper.getInstance(context);
        db.insert(shortcut);
    }

    public static int getNewDatabaseVersion(Context context, String metaKey) {
        Bundle metaData = null;
        int newVersion = 0;
        if (context == null || metaKey == null) {
            return 0;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                newVersion = metaData.getInt(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return newVersion;
    }

    public static void copyDatabaseFromAssert(Context context, int newDatabaseVersion) {
//        L.d("-------Util.copyDatabaseFromAssert() start");
        try {
            InputStream is = context.getAssets().open(DB_FILE);
            byte[] buffer = new byte[1024];
            int count;
            FileOutputStream out = new FileOutputStream(context.openOrCreateDatabase(DB_FILE, Context.MODE_PRIVATE, null).getPath());
            while ((count = is.read(buffer)) > 0) {
                out.write(buffer, 0, count);
                out.flush();
            }
            out.close();
            is.close();
            Util.setInt(context, Data.PRE_DB_VERSION, newDatabaseVersion);
        } catch (Exception e) {
            //L.i("-------Util.copyDatabaseFromAssert() error:" + e.toString());
        }
        //       L.i("-------Util.copyDatabaseFromAssert() completed");
    }

    public static void copyWallpaperFromAssert(final Context context) {
//        L.i("-------Util.copyWallpaperFromAssert() start");
        try {
            InputStream is = context.getAssets().open(WALLPAPER_FILE);
            byte[] buffer = new byte[1024];
            int count = 0;
            PIC_DIR = context.getFilesDir().getAbsolutePath();
            File picDir = new File(PIC_DIR);
            if (!picDir.exists()) picDir.mkdirs();
            FileOutputStream out = new FileOutputStream(PIC_DIR + File.separator + WALLPAPER_FILE);
            while ((count = is.read(buffer)) > 0) {
                out.write(buffer, 0, count);
                out.flush();
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        MediaScannerConnection.scanFile(context, new String[]{PIC_DIR + File.separator + WALLPAPER_FILE}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                //            L.i("Scanned " + path);

                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        //       L.i("-------Util.copyWallpaperFromAssert() completed");
    }

   /* -------------------------sp操作------------------------------*/

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Util.PRE_FILE, Context.MODE_PRIVATE);
        return sp.getString(key, "empty");
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Util.PRE_FILE, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Util.PRE_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(key, value).apply();
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Util.PRE_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value).apply();
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Util.PRE_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Util.PRE_FILE, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }


    public static List<ResolveInfo> getAllApps(PackageManager pm) {
        Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
        mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> mApps = pm.queryIntentActivities(mainintent, 0);
        Collections.sort(mApps, new PackagesComparator(pm));
        return mApps;
    }


    public static Map<String, String> getPublicVolumes(Context context) {

        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Map<String, String> map = new HashMap<String, String>();
        try {
            //获取volumeInfo
            List<Object> volumeInfos = (List<Object>) sm.getClass().getMethod("getVolumes", null).invoke(sm, null);
            L.d("usb", "start");
            for (final Object info : volumeInfos) {
                L.d("usb", "type is : " + info.getClass().getMethod("getType", null).invoke(info, null));
                //获取设备状态
                if ((Integer) info.getClass().getMethod("getType", null).invoke(info, null) == 0
                        && (Integer) info.getClass().getMethod("getState", null).invoke(info, null) == 2) {
                    String name = (String) sm.getClass().getMethod("getBestVolumeDescription", info.getClass()).invoke(sm, info);
                    String id = (String) info.getClass().getField("id").get(info);
                    map.put(name, id);
                } else {
                    L.d("usb", "Skipping volume " + info.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.d("usbor during Sync: 您的主机中的软件中止了一个已建立的连接。\n" +
                    "12:53:10 Session 'app': Error Installing APK\n" +
                    "12:53:14 ADB rejected shell command (logcat -v long): device offline (x)\n" +
                    "12:53:17 ADB rejected shell command (cat /proc/net/xt_qtaguid/stats | grep 1000): device offline (x)\n" +
                    "12:53:17 ADB rejected shell command (cat /proc/stat): device offline (x)\n", e.toString());
        }
        return map;
    }
}
