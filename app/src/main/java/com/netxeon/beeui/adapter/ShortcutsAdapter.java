package com.netxeon.beeui.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.bean.Shortcut;
import com.netxeon.beeui.utils.Util;

import java.util.ArrayList;
import java.util.List;


public class ShortcutsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Shortcut> mShortcutList;
    private Context mContext;
    private List<ResolveInfo> allApps;
    private List<ResolveInfo> mApps = new ArrayList<>();
    private PackageManager mPm;
    private boolean hasLabel;

    public ShortcutsAdapter(Context context, List<Shortcut> shotcutList,
                            PackageManager pm, boolean hasLabel) {
        this.hasLabel = hasLabel;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mShortcutList = shotcutList;
        allApps = Util.getAllApps(pm);
        mPm = pm;
    }

    @Override
    public int getCount() {
        return mApps.size();
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
        ShortcutHolder holder;
//        L.i("--------apps         position: " + position);
        if (convertView == null) {
            if (hasLabel) {
                convertView = mInflater.inflate(R.layout.item_apps_gridview, null);
                holder = new ShortcutHolder();
                holder.background = (View) convertView.findViewById(R.id.shotcut_item);
                holder.add = (ImageView) convertView.findViewById(R.id.item_category_add);
                holder.icon = (ImageView) convertView.findViewById(R.id.item_apps_gridview_image);
                holder.label = (TextView) convertView.findViewById(R.id.item_apps_gridview_name);
                convertView.setTag(holder);
            } else {
                convertView = mInflater.inflate(R.layout.item_home_shortcut_gridview, null);
                holder = new ShortcutHolder();
                holder.background = (View) convertView.findViewById(R.id.shotcut_item);
                holder.icon = (ImageView) convertView.findViewById(R.id.item_home_shortcut_gridview_image);
                holder.add=(ImageView)convertView.findViewById(R.id.item_category_add_home);
                holder.label =(TextView)convertView.findViewById(R.id.item_home_shortcut_apps);
                convertView.setTag(holder);
            }
        } else {
            holder = (ShortcutHolder) convertView.getTag();
        }
        if (mApps.get(position)==null) {
            if (hasLabel) {
                holder.label.setVisibility(View.GONE);
                holder.add.setVisibility(View.VISIBLE);
                holder.icon.setVisibility(View.GONE);
                holder.add.setBackgroundResource(R.mipmap.app_icon_more);
            } else {
                holder.label.setVisibility(View.GONE);
                holder.add.setVisibility(View.VISIBLE);
                holder.icon.setVisibility(View.GONE);
                holder.add.setBackgroundResource(R.mipmap.app_icon_more);
                //holder.icon.setBackgroundResource(R.mipmap.app_icon_more);
            }
            holder.componentName = new ComponentName("additional", "");
        } else {
            if (hasLabel) {
                holder.label.setVisibility(View.VISIBLE);
                holder.add.setVisibility(View.GONE);
                holder.icon.setVisibility(View.VISIBLE);
            }
            holder.icon.setBackground(mApps.get(position).loadIcon(mPm));
            holder.icon.setVisibility(View.VISIBLE);
            if (hasLabel) {
                holder.label.setText(mApps.get(position).loadLabel(mPm));
            }
            holder.componentName = new ComponentName(mApps.get(position).activityInfo.packageName, mApps.get(position).activityInfo.name);
            holder.label.setText(mApps.get(position).loadLabel(mPm));
            holder.label.setVisibility(View.VISIBLE);
            holder.add.setVisibility(View.GONE);
        }
        return convertView;
    }

    /*
     * the display item
     */
    public class ShortcutHolder {
        public View background;
        public ImageView icon;
        public ComponentName componentName;
        public TextView label;
        public ImageView add;
    }

    public void refreshApps() {
        allApps = Util.getAllApps(mPm);
    }

    public void notifyDataSetChanged(List<Shortcut> list) {
        mApps.clear();
        if (list.size() ==1){
            mApps.add(null);
        }else {
            for (Shortcut shortcut : list) {
                ResolveInfo resolveInfo = null;
                String componentStr = shortcut.getComponentName();
                ComponentName componentName = null;
                for (ResolveInfo info : allApps) {
                    resolveInfo = null;
                    componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                    if (componentName.toString().equals(componentStr)) {
                        resolveInfo = info;
                        break;
                    }
                }
                if (resolveInfo != null) {
                    mApps.add(resolveInfo);
                }
            }
            mApps.add(null);
        }
        notifyDataSetChanged();
    }

}