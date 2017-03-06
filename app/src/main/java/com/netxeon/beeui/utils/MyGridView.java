package com.netxeon.beeui.utils;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * 自定义GridView修改变大图片最上端
 * @author Administrator
 *
 */
public class MyGridView extends GridView {
public MyGridView(Context context, AttributeSet attrs) {
super(context, attrs);
// TODO Auto-generated constructor stub
setChildrenDrawingOrderEnabled(true);
}
//设置要设为topView的view
public void setTopView(View v)
{
for(int i = 0; i < getChildCount(); i++)
{
if(getChildAt(i) == v)
{
position = i;
break;
}
}
}

private int position = 0;

//修改绘制顺序
@Override
protected int getChildDrawingOrder(int childCount, int i) {
// TODO Auto-generated method stub
    if (position != -1) {
        if (i == childCount - 1){
            return position;
        }
        if (i == position){

            return childCount - 1;
        }
    }
    return i;
}

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}