package com.zj.note.widget;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

public class MyGesture extends Gesture {

    /**
     * 轨迹对象
     */
    private Path path;



    /**
     * 将手势轨迹转成指定大小的bitmap
     * 
     * @param width
     * @param height
     * @param mGestureHeight
     * @param mGestureWidth
     * @param inset
     * @param color
     * @param mStrokes
     * @return
     */
    public Bitmap toBitmap(int width, int height, int mGestureHeight,
        int mGestureWidth, int inset, int color,
        ArrayList<GestureStroke> mStrokes) {
        Path path = toPath(mStrokes);
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(20);

        float mGestureScale = mGestureWidth > mGestureHeight ? mGestureWidth
            : mGestureHeight;
        float mBitmapScale = width > height ? width : height;

        float scale = mBitmapScale / mGestureScale;// 缩放倍率
        // //水平方向缩放倍率
        // float wScale = new BigDecimal(width).divide(
        // new BigDecimal(mGestureWidth), 3, BigDecimal.ROUND_HALF_EVEN)
        // .floatValue();
        // //垂直方向缩放倍率
        // float hScale = new BigDecimal(height).divide(
        // new BigDecimal(mGestureHeight), 3, BigDecimal.ROUND_HALF_EVEN)
        // .floatValue();
        canvas.translate(inset, inset);
        canvas.scale(scale, scale);
        // canvas.scale(wScale, hScale);
        canvas.drawPath(path, paint);
        // canvas.restore();
        return bitmap;
    }



    // @Override
    // public void addStroke(GestureStroke stroke) {
    // Log.d("GestureDemo", "my addStroke run");
    // if (stroke != null) {
    // mStrokes.add(stroke);
    // }
    // }

    /**
     * 将笔划转成path
     * 
     * @param mStrokes
     * @return
     */
    public Path toPath(ArrayList<GestureStroke> mStrokes) {
        Log.d("GestureDemo", "my toPath run");
        path = new Path();
        int len = mStrokes.size();
        for (int i = 0; i < len; i++) {
            path.addPath(mStrokes.get(i).getPath());
        }
        return path;
    }
}
