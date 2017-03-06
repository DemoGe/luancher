package com.netxeon.beeui.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.activity.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 左右popwindow
 */
public class ListPopupwindow extends PopupWindow {

    public static final int LEFTPOPUP = 0;
    public static final int RIGHTPOPUP = 1;
    private Context mContext;
    private List<ResolveInfo> mRightList;
    private Map<String, String> mLiftMap;
    private List<String> mLeftLiist;
    private ListAdapter mAdapter;
    private PackageManager pm;
    private int direction;
    private View conentView;
    private ListView mListView;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private static final int rows = 3;

    public ListPopupwindow(Context context, int DIRECTION) {
        direction = DIRECTION;
        mContext = context;
        pm = mContext.getPackageManager();
        conentView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popwin_layout, null);
        setList();
        initView();
        initData(mContext);
    }

    private void initView() {
        mListView = (ListView) conentView.findViewById(R.id.pop_listview);
        mAdapter = new ListAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (direction == LEFTPOPUP) {
                    String usbname = mLeftLiist.get(position);
                    String usbid = mLiftMap.get(usbname);
                    final Intent i = new Intent();
                    i.putExtra(mContext.getString(R.string.unmount_sd_volume_id), usbid);
                    i.putExtra(mContext.getString(R.string.unmount_sd_volume_desc), usbname);
                    ComponentName mComp = new ComponentName(mContext.getString(R.string.setting_pkg), mContext.getString(R.string.setting_unmount_sd_activity));
                    i.setComponent(mComp);
                    mContext.startActivity(i);
                } else {
                    String pack = mRightList.get(position).activityInfo.packageName;
                    SPUtils.put(mContext, "default_launcher", pack);
                    ((MainActivity) mContext).doStartApplicationWithPackageName(pack);
                }
                dismiss();
            }
        });
    }


    public void showOrDismissPopupWindow(View parent) {
        if (!this.isShowing()) {
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            if (direction == RIGHTPOPUP) {
                showAtLocation(parent, Gravity.NO_GRAVITY, location[0] - windowWidth, location[1] - windowHeight);
            } else {
                showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth(), location[1] - windowHeight);
            }
        } else {
            this.dismiss();
        }
    }

    private int getHeightOfListView(ListView listView) {
        if (mAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < (mAdapter.getCount() > rows ? rows : mAdapter.getCount()); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }
        totalHeight = totalHeight + ((listView.getDividerHeight()) * ((mAdapter.getCount() > rows ? rows : mAdapter.getCount()) - 1));
        return totalHeight;
    }

    private void initData(Context context) {
        windowWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 6;
        windowHeight = getHeightOfListView(mListView);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(windowWidth);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(windowHeight);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        // 刷新状态
        this.update();
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        backgroundAlpha(0.4F);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new ColorDrawable(0000000000));
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    /**
     * 设置左右list的数据
     */
    private void setList() {
        if (direction == LEFTPOPUP) {
            mLeftLiist = new ArrayList<String>();
            mLiftMap = Util.getPublicVolumes(mContext);
            Iterator<Map.Entry<String, String>> it = mLiftMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                mLeftLiist.add(entry.getKey());
            }
        } else {
            mRightList = Util.getLaunchers(mContext);
            try {
                ActivityInfo ai = pm.getActivityInfo(new ComponentName("com.netxeon.probox","com.netxeon.probox.MainActivity"),0);
                ResolveInfo ri = new ResolveInfo();
                ri.activityInfo = ai;
                mRightList.add(ri);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((MainActivity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((MainActivity) mContext).getWindow().setAttributes(lp);
    }

    private List getList() {
        if (direction == LEFTPOPUP) return mLeftLiist;
        else return mRightList;
    }

    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return getList().size();
        }

        @Override
        public Object getItem(int position) {
            return getList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListHolder listHolder = new ListHolder();
            if (convertView == null) {

                convertView = LayoutInflater.from(mContext).inflate(R.layout.popwin_list_item, null);
                listHolder.image = (ImageView) convertView.findViewById(R.id.item_image);
                listHolder.text = (TextView) convertView.findViewById(R.id.item_text);
                convertView.setTag(listHolder);
            } else {
                listHolder = (ListHolder) convertView.getTag();
            }
            if (direction == RIGHTPOPUP) {
                listHolder.text.setText(((ResolveInfo) getList().get(position)).activityInfo.loadLabel(pm));
                listHolder.image.setImageDrawable(((ResolveInfo) getList().get(position)).activityInfo.loadIcon(pm));
            } else if (direction == LEFTPOPUP) {
                listHolder.text.setText((String) getList().get(position));
                listHolder.image.setImageResource(R.mipmap.usb_on);
            }
            return convertView;
        }

        private class ListHolder {
            public TextView text;
            public ImageView image;
        }
    }

}
