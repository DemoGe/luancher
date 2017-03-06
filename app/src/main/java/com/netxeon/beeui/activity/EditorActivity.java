package com.netxeon.beeui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netxeon.beeui.R;
import com.netxeon.beeui.bean.Shortcut;
import com.netxeon.beeui.fragment.HomeFragment;
import com.netxeon.beeui.utils.AnimUtil;
import com.netxeon.beeui.utils.DBHelper;
import com.netxeon.beeui.utils.Data;
import com.netxeon.beeui.utils.GridViewTV;
import com.netxeon.beeui.utils.PackagesComparator;
import com.netxeon.beeui.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 加号点进去逻辑
 */
public class EditorActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private PackageManager pm;
    private List<ResolveInfo> mPackages;
    private AppsSelectsAdapter mAdapter;
    private LayoutInflater mInflater;
    // fitter mode means we are in selection mode,
    // that when the user click the icon, the icon will become selected
    private boolean mFitterMode;
    private String mCurrentCategory;
    private DBHelper mDB;
    private static final int MSG_DELETE = -1;
    private static final int MSG_INSERT = 1;
    private PackageChangedReceiver mUpdateShortcutsReceiver;
    private IntentFilter mIntentFilter;
    private GridViewTV gridview;
    private Context mContext;
    private static final int columns = 6;
    private static final float window_width = 0.7F;
    private static final float window_height = 0.7F;

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mContext = this;
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * window_height);
        p.width = (int) (d.getWidth() * window_width);
        getWindow().setAttributes(p);
        getWindow().setGravity(Gravity.CENTER);

        mDB = DBHelper.getInstance(this);
        Intent argIntent = getIntent();
        mCurrentCategory = argIntent.getStringExtra(Util.CATEGORY);
        if (mCurrentCategory != null && !mCurrentCategory.isEmpty()) {
            mFitterMode = true;
        }
        mUpdateShortcutsReceiver = new PackageChangedReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        mIntentFilter.addDataScheme("package");
        pm = getPackageManager();

        gridview = (GridViewTV) findViewById(R.id.all_apps_gridview);
        gridview.setNumColumns(columns);
        gridview.setOnItemClickListener(new ItemClickListener());
        gridview.setOnItemSelectedListener(this);
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
        mPackages = Util.getAllApps(pm);
        if (mFitterMode) {
            fitterThePersistentPackages();
            initSelections();
        }
        Collections.sort(mPackages, new PackagesComparator(pm));
        mAdapter = new AppsSelectsAdapter(this);
        gridview.setAdapter(mAdapter);
        gridview.requestFocus();
    }

    // do not display the persistent packages
    private void fitterThePersistentPackages() {
        List<ResolveInfo> persistentPackages = new ArrayList<ResolveInfo>();
        List<Shortcut> persistentShortcut = new ArrayList<Shortcut>();
        persistentShortcut = mDB.queryAllPersistents();
        for (int i = 0; i < persistentShortcut.size(); i++) {
            for (ResolveInfo info : mPackages) {
                info.isDefault = false;
                if (persistentShortcut.get(i).getComponentName().equals(new ComponentName(info.activityInfo.packageName, info.activityInfo.name).toString())) {
                    persistentPackages.add(info);
                }
            }
        }
        mPackages.removeAll(persistentPackages);//去掉所有persistent字段为1的应用
    }

    private void initSelections() {
        List<Shortcut> selectedList = new ArrayList<Shortcut>();
        selectedList = mDB.queryByCategory(mCurrentCategory);
        for (Shortcut shortcut : selectedList) {
            for (ResolveInfo info : mPackages) {
                if (shortcut.getComponentName().equals(new ComponentName(info.activityInfo.packageName, info.activityInfo.name).toString())) {
                    info.isDefault = true;
                }
            }
        }
    }


    private float scale = new Float(1.1);
    private View mOldView;
    private boolean isSelect = false;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null && gridview.hasFocus()) {
           // Log.d("bo", view.toString() + " bringToFront");

            isSelect = true;
            AnimUtil.setViewScale(view, scale);
            if (mOldView != null){
                AnimUtil.setViewScaleDefault(mOldView);
            }
            mOldView = view;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 加号adapter
     */
    private class AppsSelectsAdapter extends BaseAdapter {

        public AppsSelectsAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mPackages.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_apps_gridview, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.item_apps_gridview_name);
                holder.icon = (ImageView) convertView.findViewById(R.id.item_apps_gridview_image);
                holder.selectView = (ImageView) convertView.findViewById(R.id.apps_icon_selected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(mPackages.get(position).activityInfo.loadLabel(pm));
            holder.icon.setBackground(mPackages.get(position).activityInfo.loadIcon(pm));
            holder.selectView.setVisibility(mPackages.get(position).isDefault ? View.VISIBLE : View.INVISIBLE);
            holder.componentName = new ComponentName(mPackages.get(position).activityInfo.packageName, mPackages.get(position).activityInfo.name);
            return convertView;
        }
    }

    /*
     * the display item
     */
    private class ViewHolder {
        private TextView text;
        private ImageView icon;
        private ImageView selectView;
        private ComponentName componentName;
    }

    /*
     * handle the icon click event
     */
    private class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            ViewHolder holder = (ViewHolder) arg1.getTag();
            if (mFitterMode) {//选中模式
                boolean visible = holder.selectView.getVisibility() == View.VISIBLE;
                if (mCurrentCategory.equals(Data.HOME) && !visible && DBHelper.getInstance(mContext).queryByCategory(Data.HOME).size() >= HomeFragment.columns -1) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.shortcut_out_of_range), Toast.LENGTH_LONG).show();
                    return;
                }
                mPackages.get(position).isDefault = !visible;
                mAdapter.notifyDataSetChanged();
                Message msg = dbHandler.obtainMessage();
                msg.obj = holder;
                msg.arg1 = visible ? MSG_DELETE : MSG_INSERT;
                msg.sendToTarget();
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setComponent(holder.componentName);
                    startActivity(intent);
                } catch (Exception e) {
//                    Log.e("error",
//                            "AllAppsActivity.ItemClickListener.onItemClick() startActivity failed: "
//                                    + holder.componentName);
                }
            }
        }
    }

    private Handler dbHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == MSG_INSERT) {
                ViewHolder holder = (ViewHolder) msg.obj;
                Util.insertShortcut(EditorActivity.this, holder.componentName.toString(), mCurrentCategory);
            } else if (msg.arg1 == MSG_DELETE) {
                ViewHolder holder = (ViewHolder) msg.obj;
                mDB.deleteShortcut(holder.componentName.toString(), mCurrentCategory);
            }
        }
    };


    private class PackageChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }

    }
}
