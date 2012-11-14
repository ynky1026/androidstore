package com.zj.note.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * simple introduction
 * 
 * <p>
 * detailed comment 当使用setImageBitmap方法设置图片源时 如果位图高于组件高度 本组件可以实现拖动效果
 * note:暂时不支持水平拖动 只有在位图宽度铺满整个组件宽度时本组件放可正常工作
 * 
 * @author zhoujian 2012-6-6
 * @see
 * @since 1.0
 */
public class ScrollImageView extends ImageView {

    /**
     * 组件显示的位图
     */
    private Bitmap mBitmap;

    /**
     * 图片是否可滚动
     */
    private boolean isCanScroll;

    /**
     * 当前显示在组件上的位图
     */
    private Bitmap currentDrawBm;

    /**
     * 上次触摸的y坐标
     */
    private float mLastY;

    /**
     * 最小拖动的距离
     */
    private float minOffset = 4;

    /**
     * 当前正在显示的位图的左上角相对于mBitmap的y坐标
     */
    private int currentBmY = 0;

    /**
     * 当前正在显示的位图的左上角相对于mBitmap的y坐标的最小值
     */
    private int minCurrentBmY = 0;

    /**
     * 当前正在显示的位图的左上角相对于mBitmap的y坐标的最大值
     */
    private int maxCurrentBmY;

    /**
     * 是否滚动 false的话进行缩放 note：缩放可能回带来图片质量的损失
     */
    private boolean isScoll;



    public ScrollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    public void setIsScroll(boolean flag) {
        isScoll = flag;
    }



    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null)
            return;
        mBitmap = bm;
        super.setImageBitmap(bm);

        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            if (this.getHeight() < mBitmap.getHeight() && isScoll) {
                isCanScroll = true;
                maxCurrentBmY = mBitmap.getHeight() - this.getHeight();
                Log.d("scrollImageView", "maxCurrentBmY is " + maxCurrentBmY);
                currentDrawBm = Bitmap.createBitmap(mBitmap, 0, currentBmY,
                    mBitmap.getWidth(), this.getHeight());
                canvas.drawBitmap(currentDrawBm, 0, 0, null);
            } else {
                isCanScroll = false;
                super.onDraw(canvas);
            }
        } else {
            super.onDraw(canvas);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isCanScroll)
                    return true;
                // mLastX = event.getX();
                mLastY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                // float x = event.getX();
                float y = event.getY();

                // 此次偏移量
                // float offsetX = x - mLastX;
                float offsetY = y - mLastY;

                if (Math.abs(offsetY) >= minOffset) {
                    currentBmY -= offsetY;
                    Log.d("scrollImageView", "currentBmY is " + currentBmY);
                    if (currentBmY < minCurrentBmY) {
                        currentBmY = minCurrentBmY;
                    }

                    if (currentBmY > maxCurrentBmY) {
                        currentBmY = maxCurrentBmY;
                    }

                    if (isCanScroll) {
                        currentDrawBm = Bitmap.createBitmap(mBitmap, 0,
                            currentBmY, mBitmap.getWidth(), this.getHeight());

                    }
                    // setImageBitmap(currentDrawBm);
                    requestLayout();

                    invalidate();
                    mLastY = y;
                }

                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }



    @Override
    protected int computeVerticalScrollRange() {
        return isCanScroll ? mBitmap.getHeight() : this.getHeight();
    }



    @Override
    public void computeScroll() {
        super.computeScroll();
    }



    @Override
    protected int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

}
