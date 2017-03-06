package com.netxeon.beeui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.adapter.ShortcutsAdapter;
import com.netxeon.beeui.bean.Shortcut;
import com.netxeon.beeui.utils.DBHelper;
import com.netxeon.beeui.utils.Data;
import com.netxeon.beeui.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类页(disposed)
 */
public class CategoryActivity extends Activity {
    private String mCurrentCategory;
    private PackageManager pm;
    private List<Shortcut> mShortcut;
    private ShortcutsAdapter mAdapter;
    private final String ADDITIONAL = "additional";
    private PackageChangedReceiver mUpdateShortcutsReceiver;
    private IntentFilter mIntentFilter;
    private GridView gridview;
    private int columns = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        String category = getIntent().getStringExtra(Util.CATEGORY);
//        L.d("zzy", getIntent().getStringExtra(Util.CATEGORY));
        if (category != null && !category.isEmpty()) {
            mCurrentCategory = category;
        } else {
//            L.e("probox2", "------FolderActivity.onCreate()--category is null !!!!!!");
        }
        //监听程序变化
        mUpdateShortcutsReceiver = new PackageChangedReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Data.ACTION_UPDATE_SHORTCUTS);
        pm = getPackageManager();

        gridview = (GridView) findViewById(R.id.folder_id_gridview);
        gridview.setOnItemClickListener(new ItemClickListener());
        gridview.setNumColumns(columns);
    }

    @Override
    public void onResume() {
        getData();
        registerReceiver(mUpdateShortcutsReceiver, mIntentFilter);
        super.onResume();
    }

    @Override
    public void onPause() {
        unregisterReceiver(mUpdateShortcutsReceiver);
        super.onPause();
    }

    private void getData() {
        if (mShortcut == null) {
            mShortcut = new ArrayList<>();
        }
        mShortcut = DBHelper.getInstance(this).queryByCategory(mCurrentCategory);
        Shortcut forAddItem = new Shortcut();
        forAddItem.setComponentName(ADDITIONAL);
        mShortcut.add(forAddItem);
        mAdapter = new ShortcutsAdapter(this, mShortcut, pm, true);
        gridview.setAdapter(mAdapter);
        gridview.requestFocus();
    }

    /*
     * handle the icon click event
     */
    private class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            ShortcutsAdapter.ShortcutHolder holder = (ShortcutsAdapter.ShortcutHolder) arg1.getTag();
            ComponentName componentName = holder.componentName;
            if (ADDITIONAL.equals(componentName.getPackageName())) {
                Intent editIntent = new Intent(CategoryActivity.this, EditorActivity.class);
                editIntent.putExtra(Util.CATEGORY, mCurrentCategory);
                startActivity(editIntent);
            } else {
                try {
                    Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
                    mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
                    mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    mainintent.setComponent(componentName);
                    startActivity(mainintent);
                } catch (Exception e) {
//                    L.e("error", "FolderActivity.ItemClickListener.onItemClick() startActivity failed: " + componentName);
                }
            }

        }
    }

    private class PackageChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }

    }
}
