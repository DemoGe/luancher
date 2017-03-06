package com.netxeon.beeui.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.adapter.AppsAdapter;
import com.netxeon.beeui.utils.PackagesComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 应用页
 */
public class AppsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private AppsAdapter mAdapter;
    private GridView gridView;
    private PackageManager pm;
    private List<ResolveInfo> mPackages;
    private PackageChangedReceiver mUpdateShortcutsReceiver;
    private IntentFilter mIntentFilter;
    //apps列数限制
    private int columns = 8;

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        if (hidden) {
//        } else {
//             getData();
//        }
//        super.onHiddenChanged(hidden);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPackages = new ArrayList<>();
        mUpdateShortcutsReceiver = new PackageChangedReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        mIntentFilter.addDataScheme("package");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewInit(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityInit();
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mUpdateShortcutsReceiver);
        super.onDestroy();
    }

    private void activityInit() {
        pm = getActivity().getPackageManager();
        mAdapter = new AppsAdapter(getActivity(), mPackages);
//        gridView.setFocusable(false);
        gridView.setNumColumns(columns);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(this);
        getActivity().registerReceiver(mUpdateShortcutsReceiver, mIntentFilter);
    }

    private void viewInit(View view) {
        gridView = (GridView) view.findViewById(R.id.fragment_apps_gridview);
    }

    private void getData() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        mPackages = pm.queryIntentActivities(intent, 0);
        Collections.sort(mPackages, new PackagesComparator(pm));
        mAdapter.notifyDataSetChanged(mPackages);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
 //获取点击的包名和act名
//            L.d("pn:"+mPackages.get(position).activityInfo.packageName);
//            L.d("an:"+mPackages.get(position).activityInfo.name);
            startActivity(pm.getLaunchIntentForPackage(mPackages.get(position).activityInfo.packageName));
        } catch (Exception e) {
        }
    }

    private class PackageChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }

    }
}
