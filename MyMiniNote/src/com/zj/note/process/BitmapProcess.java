package com.zj.note.process;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.zj.note.ConstantValue;
import com.zj.note.manager.BitmapManager;
import com.zj.note.manager.FileManager;

public class BitmapProcess implements Runnable {

    /**
     * tag
     */
    private static final String TAG = "BitmapProcess";

    private Intent it;

    private Context c;



    public BitmapProcess(Context context, Intent intent) {
        c = context;
        it = intent;
    }



    @Override
    public void run() {
        Intent intent = new Intent(ConstantValue.BITMAP_PROCESS_RESULT);
        try {
            String path = it.getStringExtra(ConstantValue.FILE_PATH);

            int screenWidth = it.getIntExtra(ConstantValue.SCREEN_WIDTH, 0);
            int screenHeight = it.getIntExtra(ConstantValue.SCREEN_HEIGHT, 0);
            FileManager fileUtil = ( FileManager ) it
                .getSerializableExtra(ConstantValue.FILE_UTIL);
            File picFile = new File(path);
            Log.d(TAG, path);
            //缩放位图到屏幕宽 0.7倍屏幕高
            Bitmap picBitmap = BitmapManager.compressBitmap(picFile,
                screenWidth, ( int ) (screenHeight * 0.7));
            String fileName = null;
            if (it.hasExtra(ConstantValue.ACTION_CAMERA)) {//如果是拍照的图片 删除原始图片文件
                picFile.delete();
                fileName = ConstantValue.CAMERA_NAME
                    + System.currentTimeMillis()
                    + ConstantValue.FILE_EX_NAME_PNG;
            } else {
                fileName = ConstantValue.PIC_NAME + System.currentTimeMillis()
                    + ConstantValue.FILE_EX_NAME_PNG;
            }
            //将bitmap生产png图片保存在磁盘上
            boolean flag = fileUtil.saveBitmap2PNG(picBitmap, fileName);
            if (flag) {
                intent.putExtra(ConstantValue.RESULT_OK, "");
            } else {
                intent.putExtra(ConstantValue.RESULT_ERR, "");
            }
            intent.putExtra(ConstantValue.BITMAP_FILE_PATH, fileName);
        } catch (Throwable e) {
            Log.e(TAG, "bitmap process exception", e);
            intent.putExtra(ConstantValue.RESULT_ERR, "");
        } finally {
            c.sendBroadcast(intent);
        }

    }
}
