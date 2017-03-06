package com.netxeon.beeui.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.activity.EditorActivity;
import com.netxeon.beeui.adapter.ShortcutsAdapter;
import com.netxeon.beeui.bean.Shortcut;
import com.netxeon.beeui.utils.AnimUtil;
import com.netxeon.beeui.utils.DBHelper;
import com.netxeon.beeui.utils.Data;
import com.netxeon.beeui.utils.GridViewTV;
import com.netxeon.beeui.utils.L;
import com.netxeon.beeui.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 *  分类页
 */
public class CategoryFragment extends Fragment implements View.OnFocusChangeListener,AdapterView.OnItemSelectedListener{

    private String mCurrentCategory = null;
    private PackageManager pm;
    private List<Shortcut> mShortcut = new ArrayList<>();
    private ShortcutsAdapter mAdapter;
    private final String ADDITIONAL = "additional";
    private PackageChangedReceiver mUpdateShortcutsReceiver;
    private IntentFilter mIntentFilter;
    private GridViewTV gridview;
    private int columns = 8;
    private boolean isPaused = false;
    private float scale = new Float(1.1);
    public void setCurrentCategory(String category) {
        mCurrentCategory = category;
    }

    public String getmCurrentCategory() {
        return mCurrentCategory;
    }

    public String getmCurrentCategoryString(){
        String str = "";
        switch (mCurrentCategory){
            case Data.MOVIE :
                str = getActivity().getString(R.string.icon_label_movie);
                break;
            case Data.FAVOURITES :
                str = getActivity().getString(R.string.icon_label_favourites);
                break;
            case Data.MUSIC :
                str = getActivity().getString(R.string.icon_label_music);
                break;
            case Data.STREAM :
                str = getActivity().getString(R.string.icon_label_stream);
                break;
            case Data.INTERNET :
                str = getActivity().getString(R.string.icon_label_internet);
                break;
            case Data.GAME :
                str = getActivity().getString(R.string.icon_label_game);
                break;
            case Data.PHOTO :
                str = getActivity().getString(R.string.icon_label_photo);
                break;
            case Data.SOCIAL :
                str = getActivity().getString(R.string.icon_label_social);
                break;
        }
        return str;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            if (gridview!=null){
                gridview.setFocusable(false);
            }

        } else {
            gridview.setFocusable(true);
            mAdapter = new ShortcutsAdapter(getActivity(), mShortcut, pm, true);
            gridview.setAdapter(mAdapter);
            getData();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_folder, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewInit(view);
        setListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityInit();
    }

    private void viewInit(View view) {
        gridview = (GridViewTV) view.findViewById(R.id.folder_id_gridview);
    }

    private void setListener() {
        gridview.setOnItemClickListener(new ItemClickListener());
        gridview.setOnItemSelectedListener(this);
        gridview.setOnFocusChangeListener(this);
    }

    private void activityInit() {
        L.d("zzy", "cate:" + mCurrentCategory);
        mUpdateShortcutsReceiver = new PackageChangedReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Data.ACTION_UPDATE_SHORTCUTS);
        pm = getActivity().getPackageManager();
        gridview.setNumColumns(columns);
        mAdapter = new ShortcutsAdapter(getActivity(), mShortcut, pm, true);
        gridview.setAdapter(mAdapter);
        getActivity().registerReceiver(mUpdateShortcutsReceiver, mIntentFilter);
    }

    @Override
    public void onResume() {
        if (isPaused) {
            mAdapter.refreshApps();
            isPaused = false;
        }
        getData();
        super.onResume();
    }

    @Override
    public void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mUpdateShortcutsReceiver);
        super.onDestroy();
    }

    private void getData() {
        if (mCurrentCategory == null) {
            return;
        }
        if (mShortcut == null) {
            mShortcut = new ArrayList<>();
        }
        mShortcut.clear();
        mShortcut.addAll(DBHelper.getInstance(getActivity()).queryByCategory(mCurrentCategory));
        Shortcut forAddItem = new Shortcut();
        forAddItem.setComponentName(ADDITIONAL);
        mShortcut.add(forAddItem);
        mAdapter.notifyDataSetChanged(mShortcut);
        gridview.requestFocus();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.folder_id_gridview:
//                    Log.i("bo", "gridview has focus");
                    new Thread(run).start();
                    break;

            }
        } else {
            switch (v.getId()) {
                case R.id.folder_id_gridview:
                    if (mOldView != null) {
                        AnimUtil.setViewScaleDefault(mOldView);
                    }
                    isSelect = false;
                    break;
            }
        }
    }
    Runnable run = new Runnable() {

        @Override
        public void run() {
           // try {
            //    Thread.sleep(50);
                handler.sendEmptyMessage(0);
            //} catch (InterruptedException e)
             //   e.printStackTrace();

        }
    };
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 0:
                    if (isSelect) {
                        isSelect = false;

                    } else {
                        // 如果是第一次进入该gridView，则进入第一个item，如果不是第一次进去，则选择上次出来的item
                        if (mOldView == null) {
                            mOldView = gridview.getChildAt(0);
                            if (mOldView != null){
                                AnimUtil.setViewScale(mOldView, scale);
                            }
                        }else {
                            AnimUtil.setViewScale(mOldView, scale);
                        }


//                Log.i("bo", "heyheyhey");
                    }
                   break;
           }
        }

        ;
    };


    private View mOldView;
    private boolean isSelect = false;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null ) {

            isSelect = true;
            AnimUtil.setViewScale(view, scale);
            if (mOldView != null)
                AnimUtil.setViewScaleDefault(mOldView);
        }
        mOldView = view;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * handle the icon click event
     */
    private class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            ShortcutsAdapter.ShortcutHolder holder = (ShortcutsAdapter.ShortcutHolder) arg1.getTag();
            ComponentName componentName = holder.componentName;
            if (ADDITIONAL.equals(componentName.getPackageName())) {
                Intent editIntent = new Intent(getActivity(), EditorActivity.class);
                editIntent.putExtra(Util.CATEGORY, mCurrentCategory)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(editIntent);
            } else {
                try {
                    Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
                    mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
                    mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    mainintent.setComponent(componentName);
                    startActivity(mainintent);
                } catch (Exception e) {
 //                   L.e("error", "FolderActivity.ItemClickListener.onItemClick() startActivity failed: " + componentName);
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
