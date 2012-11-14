package com.zj.note.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zj.note.ConstantValue;

/**
 * simple introduction 有光标闪烁的imageview
 * <p>
 * detailed comment
 * 
 * @author zhoujian 2012-5-28
 * @see
 * @since 1.0
 */
public class MyImageView extends ImageView {

    @SuppressWarnings("unused")
    private Context context;

    private boolean drawCursor;

    private boolean flag = true;

    private Bitmap mBitmap;

    private Paint mPaint;

    private Handler hd;



    // /**
    // * 光标的x坐标
    // */
    // private int cusorX;

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);

        hd = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
                        invalidate();
                        break;

                    default:
                        break;
                }
            };
        };

        this.context = context;

    }



    /**
     * 设置是否开启光标闪烁功能
     * 
     * @param flag
     */
    public void setCusorStatus(boolean flag) {
        this.flag = flag;
        if (flag) {
            MyThread th = new MyThread();
            th.start();
        }

    }



    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.mBitmap = bm;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) {
            // cusorX = this.getPaddingLeft();
            if (drawCursor) {
                canvas.drawLine(0.0f, 0.0f, 0.0f, this.getHeight(), mPaint);
            }

            return;
        }
        // 当前显示的bitmap的高度 及光标的高度
        int height = mBitmap.getHeight();
        // 当前显示的bitmap的宽度 及光标起始点和重点的x坐标
        int width = mBitmap.getWidth();

        if (drawCursor) {
            canvas.drawLine(width, 0.0f, width, height, mPaint);
        }
    }

    // 光标闪烁的线程
    class MyThread extends Thread {
        @Override
        public void run() {
            while (flag) {
                try {
                    Thread.sleep(ConstantValue.CURSOR_TIME);
                    if (drawCursor) {
                        drawCursor = false;
                    } else {
                        drawCursor = true;
                    }

                    Message msg = new Message();
                    msg.what = 0;
                    hd.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
