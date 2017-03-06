package com.netxeon.beeui.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.netxeon.beeui.bean.Shortcut;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "shortcut";
    private static DBHelper mHelper;

    public static DBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new DBHelper(context);
        }
        return mHelper;
    }

    private DBHelper(Context context) {
        // CursorFactory设置为null,使用默认值
        super(context, Util.DB_FILE, null, DATABASE_VERSION);
    }

    // 数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, category VARCHAR, componentName VARCHAR, persistent INTEGER)");
    }

    // 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(Shortcut shortcut) {
        ContentValues values = new ContentValues();
        values.put("category", shortcut.getCategory());
        values.put("componentName", shortcut.getComponentName());
        values.put("persistent", shortcut.getPersistent() ? 1 : 0);
        SQLiteDatabase db = getWritableDatabase();
        Log.d("zzy", db.replace(TABLE_NAME, null, values) + "");
        db.close();
    }

    public List<Shortcut> queryByCategory(String category) {
        List<Shortcut> shortcutList = new ArrayList<Shortcut>();
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = {category};
        if (db == null) {
            Log.e("db", "----db is null !!!!!!!");
        }
        Cursor cursor = db.query(TABLE_NAME, null, "category=?", selectionArgs, null, null, null);
        if (!cursor.moveToFirst()) {
            Log.e("db", "----there is no data !!!!!!!");
        } else {
            do {
                Shortcut shortcut = new Shortcut();
                shortcut.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                shortcut.setComponentName((cursor.getString(cursor.getColumnIndex("componentName"))));
                shortcut.setPersistent(cursor.getInt(cursor.getColumnIndex("persistent")) == 1);
                shortcutList.add(shortcut);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return shortcutList;
    }

    /**
     * Persistents相当于系统守护进程，系统开机自动启动的一些系统进程等的标志位，卸载xml中（永久性应用）
     *
     * @return
     */
    public List<Shortcut> queryAllPersistents() {
        List<Shortcut> shortcutList = new ArrayList<Shortcut>();
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = {String.valueOf(1)};
        Cursor cursor = db.query(TABLE_NAME, null, "persistent=?", selectionArgs, null, null, null);
        if (!cursor.moveToFirst()) {
//            Log.v("temp", "----there is no data !!!!!!!");
        } else {
            do {
                Shortcut shortcut = new Shortcut();
                shortcut.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                shortcut.setComponentName((cursor.getString(cursor.getColumnIndex("componentName"))));
                shortcut.setPersistent(cursor.getInt(cursor.getColumnIndex("persistent")) == 1);
                shortcutList.add(shortcut);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return shortcutList;
    }

    public void deleteShortcut(String componentName, String category) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "componentName=? and category=?", new String[]{componentName, category});
        db.close();
    }

    //delete the shortcut while the package has removed
    public void deleteByPackageName(String packageName) {
        SQLiteDatabase db = getWritableDatabase();
        //db.delete(TABLE_NAME, "componentName =?", new String[] { packageName});
        //Logger.log(Logger.TAG_PACKAGE, "DBhelper.deleteByPackageName() : " + packageName);
        db.execSQL("DELETE FROM shortcut WHERE componentName LIKE '%" + packageName + "%'");
        db.close();
    }

}