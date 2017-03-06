
package com.netxeon.beeui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.netxeon.beeui.R;

/**
 * 水波浪球形进度View
 *
 *
 */
public class MySinkingView extends FrameLayout {
    private static final int DEFAULT_TEXTCOLOT = 0xFFFFFFFF;

    private static final int DEFAULT_TEXTSIZE = 240;

    private float mPercent;

    private Paint mPaint = new Paint();

    private Bitmap mBitmap;

    private Bitmap mScaledBitmap;

    private float mLeft;

    private int mSpeed = 15;

    private int mRepeatCount = 0;

    private Status mFlag = Status.NONE;

    private int mTextColor = DEFAULT_TEXTCOLOT;

    private int mTextSize = DEFAULT_TEXTSIZE;

    public MySinkingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }

    public void setTextSize(int size) {
        mTextSize = size;
    }

    public void setPercent(float percent) {
        mFlag = Status.RUNNING;
        mPercent = percent;
        postInvalidate();

    }

    public void setStatus(Status status) {
        mFlag = status;
    }

    /**
     * 释放内存
     */
    public void clear() {
        mFlag = Status.NONE;
        if (mScaledBitmap != null) {
            mScaledBitmap.recycle();
            mScaledBitmap = null;
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    // dispatchDraw()方法内部会遍历每个子视图，调用drawChild()去重新回调每个子视图的draw()方法(注意，这个 地方“需要重绘”的视图才会调用draw()方法)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        
        //裁剪成圆区域
        Path path = new Path();
        canvas.save();
        path.reset();
        canvas.clipPath(path);
        path.addCircle(width / 2, height / 2, width / 2, Direction.CCW);//Direction.CCW内部
        canvas.clipPath(path, Op.REPLACE);

        if (mFlag == Status.RUNNING) {
            if (mScaledBitmap == null) {
                mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.wave2);
                mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth(), getHeight(), false);
                mBitmap.recycle();
                mBitmap = null;
                mRepeatCount = (int) Math.ceil(getWidth() / mScaledBitmap.getWidth() + 0.5) + 1;
            }
            for (int idx = 0; idx < mRepeatCount; idx++) {
                canvas.drawBitmap(mScaledBitmap, mLeft + (idx - 1) * mScaledBitmap.getWidth(), (1-mPercent) * getHeight(), null);
            }
            String str = (int) (mPercent * 100) + "%";
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTextSize);
            mPaint.setStyle(Style.FILL);
            canvas.drawText(str, (getWidth() - mPaint.measureText(str)) / 2, getHeight() / 2 + mTextSize / 2, mPaint);

            mLeft += mSpeed;
            if (mLeft >= mScaledBitmap.getWidth())
                mLeft = 0;
            // 绘制外圆环
            mPaint.setStyle(Style.STROKE);
            mPaint.setStrokeWidth(4);
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.rgb(33, 211, 39));
            canvas.drawCircle(width / 2, height / 2, width / 2 - 2, mPaint);

            postInvalidateDelayed(20);
        }
        canvas.restore();

    }

    public enum Status {
        RUNNING, NONE
    }

}
