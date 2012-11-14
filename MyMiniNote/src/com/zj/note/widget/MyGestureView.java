package com.zj.note.widget;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.gesture.Gesture;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.zj.note.ConstantValue;
import com.zj.note.R;

/**
 * simple introduction 自定义手势识别类
 * <p>
 * detailed comment
 * 
 * @author zhoujian 2012-6-11
 * @see
 * @since 1.0
 */
public class MyGestureView extends FrameLayout {

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 描绘手势的颜色
     */
    private int mGestureColor;

    /**
     * 手势识别后回调函数等待时间
     */
    private int mFadeOffset;

    /**
     * 笔画宽度
     */
    private float mGestureStrokeWidth;

    /**
     * 当前手势识别类的对象
     */
    private Gesture mCurrentGesture;

    /**
     * 手势轨迹的画笔对象
     */
    private Paint mGesturePaint = new Paint();

    /**
     * 手势轨迹对象
     */
    private Path mPath = new Path();

    /**
     * 手势识别监听器
     */
    private OnGesturePerformedListener mListener;

    /**
     * 上一次touch事件的x坐标
     */
    private float mLastX;

    /**
     * 上一次touch事件的y坐标
     */
    private float mLastY;

    /**
     * 是否画点
     */
    private boolean isPoint;

    /**
     * 手势是否已经淡出
     */
    private boolean mIsFadingOut;

    /**
     * 手势点集合
     */
    private final ArrayList<GesturePoint> mStrokeBuffer = new ArrayList<GesturePoint>();

    /**
     * 手势淡出的线程
     */
    private FadeOutRunnable mFadeOut = new FadeOutRunnable();;

    /**
     * 是否初始化
     */
    private boolean isInit = true;

    /**
     * handler对象
     */
    private Handler hd;

    /**
     * 组件高度
     */
    private int height;



    /**
     * 构造函数
     * 
     * @param context
     * @param attrs
     */
    public MyGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttrs(attrs);
        init();
    }



    public void setHandler(Handler mHandler) {
        hd = mHandler;
    }



    public int getAbsHeight() {
        return height;
    }



    /**
     * 初始化参数
     * 
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
            R.styleable.MyGestureView);

        mGestureColor = a.getColor(R.styleable.MyGestureView_gestureColor,
            Color.BLACK);
        mFadeOffset = a.getInt(R.styleable.MyGestureView_fadeOffset, 420);
        mGestureStrokeWidth = a.getFloat(
            R.styleable.MyGestureView_gestureStrokeWidth, 12.0f);

        a.recycle();
    }



    private void init() {
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setDither(true);
        mGesturePaint.setColor(mGestureColor);
        mGesturePaint.setStyle(Paint.Style.STROKE);
        mGesturePaint.setStrokeJoin(Paint.Join.ROUND);
        mGesturePaint.setStrokeCap(Paint.Cap.ROUND);
        mGesturePaint.setStrokeWidth(mGestureStrokeWidth);

    }



    public Gesture getGesture() {
        return mCurrentGesture;
    }



    public void setGesture(Gesture gesture) {
        mCurrentGesture = gesture;
    }



    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mCurrentGesture != null) {
            canvas.drawPath(mPath, mGesturePaint);
        }
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(ev);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(ev);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp(ev);
                invalidate();
                break;
            default:
                break;
        }

        return true;
    }



    public void cancleGesture() {
        mIsFadingOut = false;
        mPath.rewind();
        mCurrentGesture = null;
    }



    public boolean isFadingHasStarted() {
        return mIsFadingOut;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInit) {
            height = this.getHeight();
            isInit = false;
            Message msg = new Message();
            msg.what = 0;
            hd.sendMessage(msg);
        }
    }



    /**
     * touch down事件回调
     * 
     * @param ev
     */
    private void touchDown(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        mLastX = x;
        mLastY = y;
        isPoint = true;
        if (mIsFadingOut) {//如果现场已经开始 则取消
            mIsFadingOut = false;
            removeCallbacks(mFadeOut);
        }

        if (mCurrentGesture == null) {//新建gesture
            mCurrentGesture = new Gesture();
        }

        mStrokeBuffer.add(new GesturePoint(x, y, ev.getEventTime()));//添加轨迹点
        mPath.moveTo(x, y);//开始轨迹
    }



    private void touchMove(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        float cx = (x + mLastX) / 2;
        float cy = (y + mLastY) / 2;//贝塞尔曲线的控制点

        // if (x == mLastX && y == mLastY) {
        // Log.d("gesture", "isPoint true");
        // isPoint = true;
        // } else {
        // Log.d("gesture", "isPoint false");
        //
        // }
        isPoint = false;
        mPath.quadTo(mLastX, mLastY, cx, cy);//从上一点到控制点画贝塞尔曲线

        mStrokeBuffer.add(new GesturePoint(x, y, ev.getEventTime()));//添加轨迹点

        mLastX = x;
        mLastY = y;
    }



    private void touchUp(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        if (isPoint) {//如果没有move事件 则认为本次手势是画点操作
            mStrokeBuffer.add(new GesturePoint(x
                - ConstantValue.GESTURE_POINT_LENGTH, y
                + ConstantValue.GESTURE_POINT_LENGTH, ev.getEventTime()));
            //为了让点显示在屏幕上 画一短贝塞尔曲线
            mPath.quadTo(x, y,
                (x + x - ConstantValue.GESTURE_POINT_LENGTH) / 2,
                (y + y + ConstantValue.GESTURE_POINT_LENGTH) / 2);
        }
        //up后完成一个笔画 添加笔画
        if (mCurrentGesture != null) {
            mCurrentGesture.addStroke(new GestureStroke(mStrokeBuffer));
        }

        // 清除
        mIsFadingOut = true;
        //将线程放入消息队列 若一定时间内不再有touchdown事件发生 则执行线程完成一次手势 
        //否则线程取消执行 用户继续输入下一个笔画
        postDelayed(mFadeOut, mFadeOffset);
        
        mStrokeBuffer.clear();
    }

    public static interface OnGesturePerformedListener {
        void onGesturePerformed(MyGestureView overlay, Gesture gesture);
    }



    public void addOnGesturePerformedListener(
        OnGesturePerformedListener listener) {
        mListener = listener;
    }

    private class FadeOutRunnable implements Runnable {

        @Override
        public void run() {
            if (mIsFadingOut) {
                mListener.onGesturePerformed(MyGestureView.this,
                    mCurrentGesture);

                mIsFadingOut = false;
                mCurrentGesture = null;
                mPath.rewind();
            } else {

            }
            invalidate();
        }

    }

}
