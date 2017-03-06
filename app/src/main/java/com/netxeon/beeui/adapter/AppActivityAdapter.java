package com.netxeon.beeui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.bean.App;

import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */

public class AppActivityAdapter extends BaseAdapter {

    private Context mContext;
    public List<App> mData;
    LayoutInflater mInflater;
    private int layoutId;
   private  int selected =-1;

    public AppActivityAdapter(Context mContext, List<App> datas, int layoutId) {
        this.mContext = mContext;
        this.mData = datas;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged(int id) {
        selected = id;
        super.notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        App app = mData.get(position);
        holder.shortCut_img.setImageDrawable(app.getIcon());
        holder.item_tv.setText(app.getAppName());

        ScaleAnimation animation= new ScaleAnimation(0.95f, 1.1f, 0.95f, 1.1f
                ,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        animation.setDuration(90);
        animation.setStartTime(0);
        animation.setFillAfter(true);


        ScaleAnimation animation2= new ScaleAnimation(1f, 1f, 1f, 1f
                ,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        animation.setDuration(90);
        animation.setStartTime(0);
        animation.setFillAfter(true);

        if (selected == position) {
            //convertView.setBackgroundColor(0xffff00ff);
             try {
                 convertView.startAnimation(animation);
                 convertView.setBackgroundResource(R.drawable.img_grid_item_bg);
             }catch (Exception e){}

        } else {
            try {
                //convertView.setBackgroundColor(0xff0099cc);
                convertView.startAnimation(animation2);
//        	convertView.setScaleType(ImageView.ScaleType.CENTER);
                convertView.setBackgroundResource(R.drawable.img_bottom_item_bg);
            }catch (Exception e){}

        }



        return convertView;
    }

    public class ScaleAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }
        @Override
        public void onAnimationEnd(Animation animation) {

        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }






    public   class ViewHolder{
        ImageView shortCut_img;
        TextView item_tv;

      public ViewHolder(View view){
            shortCut_img = (ImageView) view.findViewById(R.id.shortcut_img);
            item_tv = (TextView) view.findViewById(R.id.item_tv);
        }
    }
}
