package com.zj.note.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyViewFlipper extends ViewGroup {

	/**
	 * TAG
	 */
	private static final String TAG = "MyViewFlipper";

	/**
	 * 上一次的x坐标
	 */
	private float lastX;

	/**
	 * 当前的view索引
	 */
	private int curPosition;

	// /**
	// * move触发的阀值
	// */
	// private float move_offsetX = 30.0f;

	/**
	 * scroller
	 */
	private Scroller scroller;

	/**
	 * tracker
	 */
	private VelocityTracker tracker;

	/**
	 * 滑动速度的阀值
	 */
	private int maxVeloctiy = 1000;

	/**
	 * view状态
	 */
	private int state;

	/**
	 * 状态 正常
	 */
	private final int STATE_NORMAL = 0;

	/**
	 * 状态 到达边界
	 */
	private final int STATE_EDGO = 1;

	public MyViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		scroller = new Scroller(context);
		tracker = VelocityTracker.obtain();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int left = 0;
			int childCount = this.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = this.getChildAt(i);
				child.layout(left, 0, left + this.getMeasuredWidth(),
						this.getMeasuredHeight());
				left += this.getMeasuredWidth();
				Log.d(TAG, "left is " + left);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void moveToView(int index) {
		if (index < 0) {
			moveToView(0);
			return;
		}
		if (index > (this.getChildCount() - 1)) {
			moveToView(this.getChildCount() - 1);
			return;
		}
		if (getScrollX() != (index * this.getWidth())) {
			scroller.startScroll(getScrollX(), getScrollY(), index * getWidth()
					- getScrollX(), getScrollY(), 500);
			invalidate();
			curPosition = index;
			Log.d(TAG, "curPosition is " + curPosition);
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = event.getX();
			tracker.addMovement(event);
			break;
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			int offsetX = (int) (lastX - x);
			lastX = x;
			if (checkMove(offsetX)) {
				tracker.addMovement(event);
				scrollBy(offsetX, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (state != STATE_EDGO) {
				tracker.computeCurrentVelocity(1000);
				if (tracker.getXVelocity() > maxVeloctiy) {
					moveToView(curPosition - 1);
				} else if (tracker.getXVelocity() < -maxVeloctiy) {
					moveToView(curPosition + 1);
				} else {
					if (getScrollX() >= ((curPosition + 1) * getWidth()) / 2) {
						moveToView(curPosition + 1);
					} else {
						moveToView(curPosition - 1);
					}
				}

			} else {
				moveToView(curPosition);
			}
			tracker.clear();
			tracker.recycle();
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 移动校验
	 * 
	 * @param offset
	 * @return
	 */
	private boolean checkMove(int offset) {
		if (getScrollX() <= -(this.getWidth() / 5) && offset < 0) {
			state = STATE_EDGO;
			return false;
		}
		if (getScrollX() >= ((this.getChildCount() - 1) * this.getWidth() + this
				.getWidth() / 5) && offset > 0) {
			state = STATE_EDGO;
			return false;
		}
		state = STATE_NORMAL;
		return true;
	}

}
