package com.netxeon.beeui.adapter;

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

import java.util.List;

/**
 * appsfragment的adapter
 */
public class AppsAdapter extends BaseAdapter {

    private List<ResolveInfo> list;
    private LayoutInflater layoutInflater;
    private Context mContext;
    private PackageManager pm;

    public AppsAdapter(Context context, List<ResolveInfo> list) {
        this.list = list;
        this.mContext = context;
        pm = context.getPackageManager();
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_apps_gridview, null);
            viewHolder = new ViewHolder((TextView) convertView.findViewById(R.id.item_apps_gridview_name),
                    (ImageView) convertView.findViewById(R.id.item_apps_gridview_image));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.apk_ico.setImageDrawable(list.get(position).activityInfo
                .loadIcon(pm));
        viewHolder.apk_name.setText(list.get(position).activityInfo
                .loadLabel(pm));
        return convertView;
    }

    public class ViewHolder {
        TextView apk_name;
        ImageView apk_ico;

        public ViewHolder(TextView textView, ImageView imageView) {
            this.apk_name = textView;
            this.apk_ico = imageView;
        }
    }

    public void notifyDataSetChanged(List<ResolveInfo> list) {
        this.list = list;
        super.notifyDataSetChanged();
    }
}
