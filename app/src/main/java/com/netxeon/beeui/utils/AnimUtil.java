package com.netxeon.beeui.utils;

import android.view.View;

public class AnimUtil {

    /**
     *  动画放大view
     * @param view 要改变的view
     */
    public static void setViewScale(View view,float scale) {
        view.animate().scaleY(scale).scaleX(scale).start();
    }
    /**
     *  动画把放大view变回默认大小
     * @param view 要改变的view
     */
    public static void setViewScaleDefault(View view) {
        view.animate().scaleY(1).scaleX(1).start();
    }
}
