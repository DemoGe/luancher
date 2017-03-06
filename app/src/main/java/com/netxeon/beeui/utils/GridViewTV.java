package com.netxeon.beeui.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * GridView TV版本.
 *
 */
public class GridViewTV extends GridView {

	public GridViewTV(Context context) {
		super(context);
		init(context, null);
	}

	public GridViewTV(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	/**
	 * .
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			super.dispatchDraw(canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	WidgetTvViewBring mWidgetTvViewBring;
	
	private void init(Context context, AttributeSet attrs) {
		this.setChildrenDrawingOrderEnabled(true);
		mWidgetTvViewBring = new WidgetTvViewBring(this); 
	}

	@Override
	public void bringChildToFront(View child) {
		//Log.d("bo", "bringChildToFront");
		mWidgetTvViewBring.bringChildToFront(this, child);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		
		//Log.d("bo", "getChildDrawingOrder");
		 // position = getSelectedItemPosition() - getFirstVisiblePosition();
		return mWidgetTvViewBring.getChildDrawingOrder(childCount, i);
	}

}
