package com.zj.note.process;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;

import com.zj.note.ConstantValue;
import com.zj.note.Session;
import com.zj.note.manager.BitmapManager;
import com.zj.note.manager.FileManager;

public class GestureIOSave implements Runnable {

    /**
     * TAG
     */
    private static final String TAG = "GestureIOSave";

    private Intent it;

    private Context c;



    public GestureIOSave(Context context, Intent intent) {
        c = context;
        it = intent;
    }



    @Override
    public void run() {
        Log.d(TAG, "process run");
        Intent intent = new Intent(ConstantValue.GESTURE_SAVE_RESULT);
        Bitmap allBitmap = null;
        Bitmap tmpBitmap = null;
        Bitmap background = null;
        String fileName = null;
        try {
            FileManager fileManager = ( FileManager ) it.getExtras()
                .getSerializable(ConstantValue.FILE_MANAGER);
            @SuppressWarnings("unchecked")
            List<Bitmap> list = ( LinkedList<Bitmap> ) Session
                .get(ConstantValue.GESTURE_LIST);
            int screenWidth = it.getIntExtra(ConstantValue.SCREEN_WIDTH, 0);
            int bitmapHeight = it.getIntExtra(ConstantValue.BITMAP_HEIGHT, 0);
            int saveBitmapNum = it.getIntExtra(ConstantValue.SAVE_BM_NUM, 0);
            int currentPage = it.getIntExtra(ConstantValue.CURRENT_PAGE, 0);
            int numBitmapRow = it.getIntExtra(ConstantValue.NUM_ROW, 0);
            fileName = it.getStringExtra(ConstantValue.FILE_NAME);
            // 行背景
            background = ( Bitmap ) Session.get(ConstantValue.BACKGROUD_BITMAP);
            // 取第一行的图片内容 为空的话生成空白图片
            Bitmap bm = list.get(0);
            if (bm == null) {
                bm = Bitmap.createBitmap(screenWidth, bitmapHeight,
                    Config.ARGB_4444);
            }
            // 生成第一行的位图
            Bitmap oItemBm = BitmapManager.mergeBitmapFrame(background, bm);
            // 初始化合并所有图片的总位图对象
            allBitmap = Bitmap.createBitmap(screenWidth, bm.getHeight(),
                Config.ARGB_4444);
            // 将第一行位图画到总的位图上
            Canvas canvas = new Canvas(allBitmap);
            canvas.drawBitmap(oItemBm, 0, 0, null);
            // 开始循环 从第二行起
            for (int i = saveBitmapNum + 1; i < currentPage * numBitmapRow; i++) {
                // 取第i行的内容位图对象 为空的话生成空白图片
                Bitmap rowBitmap = list.get(i);
                if (rowBitmap == null) {
                    rowBitmap = Bitmap.createBitmap(screenWidth, bitmapHeight,
                        Config.ARGB_4444);
                }
                // 生成第i行的位图 并将背景和内容画到此位图中
                Bitmap itemBitmap = BitmapManager.mergeBitmapFrame(background,
                    rowBitmap);
                // 生成一个临时位图对象 用以合并第i行的位图与之前所有行的总位图
                tmpBitmap = BitmapManager.mergeBitmapVertical(allBitmap,
                    itemBitmap);
                // 将合并后的临时位图引用赋值给总位图对象 完成一次合并

                allBitmap = tmpBitmap;
            }
            boolean flag = fileManager.saveBitmap2PNG(allBitmap, fileName);
            if (flag) {
                intent.putExtra(ConstantValue.RESULT_OK, "");
            } else {
                intent.putExtra(ConstantValue.RESULT_ERR, "");
            }

        } catch (Throwable e) {
            Log.e(TAG, "gesture save exception", e);
            intent.putExtra(ConstantValue.RESULT_ERR, "");
        } finally {
            c.sendBroadcast(intent);
            if (allBitmap != null) {
                allBitmap.recycle();
            }
            if (tmpBitmap != null) {
                tmpBitmap.recycle();
            }
        }
    }

}
