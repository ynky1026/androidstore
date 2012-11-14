package com.zj.note.manager;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

/**
 * bitmap处理类 simple introduction
 * 
 * <p>
 * detailed comment
 * 
 * @author zhoujian 2012-6-12
 * @see
 * @since 1.0
 */
public class BitmapManager {

    private static final String TAG = "BitmapManager";

    /**
     * 图片向左旋转
     */
    public static final int ROTATE_TO_LEFT = 0;

    /**
     * 图片向右旋转
     */
    public static final int ROTATE_TO_RIGHT = 1;



    /**
     * 水平方向合并2个等高的位图 并生成新位图
     * 
     * @param bm1
     * @param bm2
     * @return 合并后的新bitmap
     */
    public static Bitmap mergeBitmapHorizontal(Bitmap bm1, Bitmap bm2) {
        Bitmap bitmap = null;
        if (bm1 == null && bm2 == null) {
            throw new IllegalArgumentException(
                "both of the two params are null is not allowed");
        } else if (bm1.getHeight() != bm2.getHeight()) {
            throw new IllegalArgumentException(
                "the two bitmap's height should be equals");
        } else if (bm1 == null || bm2 == null) {
            bitmap = bm1 == null ? bm2 : bm1;
        } else {
            bitmap = Bitmap.createBitmap(bm1.getWidth() + bm2.getWidth(),
                bm1.getHeight(), Config.ARGB_4444);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bm1, 0, 0, null);
            canvas.drawBitmap(bm2, bm1.getWidth(), 0, null);
        }

        if (bm1 != null) {
            bm1.recycle();
            bm1 = null;
        }
        if (bm2 != null) {
            bm2.recycle();
            bm2 = null;
        }
        return bitmap;

    }



    /**
     * 垂直方向合并2个等宽的位图 并生成新位图
     * 
     * @param bm1
     * @param bm2
     * @return 合并后的新bitmap
     */
    public static Bitmap mergeBitmapVertical(Bitmap bm1, Bitmap bm2) {
        Bitmap bitmap = null;
        if (bm1 == null && bm2 == null) {
            throw new IllegalArgumentException(
                "both of the two params are null is not allowed");
        } else if (bm1.getWidth() != bm2.getWidth()) {
            throw new IllegalArgumentException(
                "the two bitmap's width should be equals");
        } else if (bm1 == null || bm2 == null) {
            bitmap = bm1 == null ? bm2 : bm1;
        } else {
            bitmap = Bitmap.createBitmap(bm1.getWidth(),
                bm1.getHeight() + bm2.getHeight(), Config.ARGB_4444);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bm1, 0, 0, null);
            canvas.drawBitmap(bm2, 0, bm1.getHeight(), null);
        }
        if (bm1 != null) {
            bm1.recycle();
            bm1 = null;
        }
        if (bm2 != null) {
            bm2.recycle();
            bm2 = null;
        }

        return bitmap;

    }



    /**
     * 为foreground位图添加背景
     * 
     * @param background
     *            背景位图
     * @param foreground
     *            前景位图
     * @return
     */
    public static Bitmap mergeBitmapFrame(Bitmap background, Bitmap foreground) {
        if (background == null) {
            throw new IllegalArgumentException("background can not be null");
        }
        if (foreground == null) {
            foreground = Bitmap.createBitmap(background.getWidth(),
                background.getHeight(), Config.ARGB_4444);
        }
        Bitmap bitmap = Bitmap.createBitmap(background.getWidth(),
            background.getHeight(), Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, 0, 0, null);

        return bitmap;
    }



    /**
     * 把bitmap缩放到指定大小
     * 
     * @param resource
     *            要缩放的bitmap
     * @param width
     *            缩放后的宽
     * @param height
     *            缩放后的高
     * @return 缩放后的bitmap
     */
    public static Bitmap compressBitmap(Bitmap resource, int width, int height) {
        if (resource == null) {
            throw new IllegalArgumentException(
                "the param bitmap can not be null");
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);
        Matrix matrix = new Matrix();
        matrix.setScale(( float ) width / resource.getWidth(), ( float ) height
            / resource.getHeight());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(resource, matrix, null);

        return bitmap;
    }



    public static Bitmap compressBitmap(Bitmap resource, float scaleX,
        float scaleY) {
        if (resource == null) {
            throw new IllegalArgumentException(
                "the param bitmap can not be null");
        }

        Bitmap bitmap = Bitmap.createBitmap(
            ( int ) (resource.getWidth() * scaleX),
            ( int ) (resource.getHeight() * scaleY), Config.ARGB_4444);

        Log.d(TAG, "width is " + bitmap.getWidth());
        Log.d(TAG, "height is " + bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(resource, matrix, null);

        return bitmap;
    }



    /**
     * 压缩位图
     * 
     * @param bmFile
     * @param width
     * @param height
     * @return
     */
    public static Bitmap compressBitmap(File bmFile, int width, int height) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        String path = bmFile.getAbsolutePath();
        BitmapFactory.decodeFile(path, opts);
        Bitmap fileBm = BitmapFactory.decodeFile(path);
        if (fileBm.getWidth() * fileBm.getHeight() <= width * height) {
            return fileBm;
        }
        int inSampleSize = ( int ) Math.ceil(Math.sqrt(fileBm.getWidth()
            * fileBm.getHeight() / (width * height)));

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = computeSampleSize(inSampleSize);
        Log.d(TAG, "inSampleSize is" + inSampleSize);
        Bitmap comBm = BitmapFactory.decodeFile(path, opts);
        fileBm.recycle();
        fileBm = null;
        return comBm;
    }



    // /**
    // * 压缩位图
    // *
    // * @param bmFile
    // * @param width
    // * @param height
    // * @return
    // */
    // public static Bitmap compressBitmap(File bmFile, int width, int height) {
    // Options opts = new Options();
    // opts.inJustDecodeBounds = true;
    // String path = bmFile.getAbsolutePath();
    // BitmapFactory.decodeFile(path, opts);
    // Bitmap fileBm = BitmapFactory.decodeFile(path);
    // int inSampleSize = ( int ) Math.ceil(Math.sqrt(fileBm.getWidth()
    // * fileBm.getHeight() / (width * height)));
    //
    // opts.inJustDecodeBounds = false;
    // opts.inSampleSize = computeSampleSize(inSampleSize);
    // Log.d(TAG, "inSampleSize is" + inSampleSize);
    // Bitmap comBm = BitmapFactory.decodeFile(path, opts);
    // fileBm.recycle();
    // fileBm = null;
    // return comBm;
    // }

    /**
     * 旋转图片
     * 
     * @param bitmap
     * @param leftOrRight
     * @param degrees
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int leftOrRight,
        int degrees) {
        if (bitmap == null) {
            throw new IllegalArgumentException(
                "the param bitmap can not be null");
        }
        int mDegrees = 0;
        if (leftOrRight == ROTATE_TO_LEFT) {
            mDegrees = -degrees;
        } else if (leftOrRight == ROTATE_TO_RIGHT) {
            mDegrees = degrees;
        }
        Matrix matrix = new Matrix();

        matrix.setRotate(mDegrees);
        Bitmap tmpBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
            bitmap.getHeight(), matrix, false);

        bitmap.recycle();

        return tmpBm;
    }



    /**
     * 取最接近参数的2的整数次幂
     * 
     * @param inSampleSize
     * @return
     */
    private static int computeSampleSize(int inSampleSize) {
        // 每次除2的商
        int result = inSampleSize;
        // 每次除以2的余数
        int remainder = 0;
        // 计数器
        int i = 0;
        // 参数是否就是2的整数次幂
        boolean flag = true;
        // 循环除以2 直到商为1时
        while (result != 1) {
            remainder = inSampleSize % 2;
            if (remainder != 0) {
                flag = false;
            }
            result = result / 2;
            i++;
        }
        if (flag) {// 所有余数都为0 则参数即是2的整数次幂
            return inSampleSize;
        }

        int outSampleSize = ( int ) (Math.abs(Math.pow(2, i) - inSampleSize) > Math
            .abs(Math.pow(2, i + 1) - inSampleSize) ? Math.pow(2, i + 1) : Math
            .pow(2, i));
        // int outSampleSize = ( int ) Math.pow(2, i + 1);

        return outSampleSize;
    }
}
