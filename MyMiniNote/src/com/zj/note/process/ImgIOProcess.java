package com.zj.note.process;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.zj.note.ConstantValue;
import com.zj.note.Session;
import com.zj.note.manager.FileManager;

public class ImgIOProcess implements Runnable {

    /**
     * TAG
     */
    private static final String TAG = "ImgIOProcess";

    /**
     * Context对象
     */
    private Context mContext;

    /**
     * Intent对象
     */
    private Intent mIntent;



    public ImgIOProcess(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }



    @Override
    public void run() {
        Intent it = new Intent(ConstantValue.IMG_SAVE_RESULT);
        try {
            String filePath = mIntent.getStringExtra(ConstantValue.FILE_PATH);
            String dirPath = mIntent.getStringExtra(ConstantValue.DIR_PATH);
            String fileName = mIntent.getStringExtra(ConstantValue.FILE_NAME);
            Bitmap bitmap = ( Bitmap ) Session.get(ConstantValue.IMG_BITMAP);
            File file = new File(filePath);
            if (file == null || !file.exists()) {
                return;
            }
            file.delete();

            FileManager fm = new FileManager(dirPath);
            boolean flag = true;
            if (FileManager.isPNGFile(fileName)) {
                flag = fm.saveBitmap2PNG(bitmap, fileName);
            } else if (FileManager.isJPEGFile(fileName)) {
                flag = fm.saveBitmap2JPEG(bitmap, fileName);
            }
            if (flag) {
                it.putExtra(ConstantValue.RESULT_OK, "");
            } else {
                it.putExtra(ConstantValue.RESULT_ERR, "");
            }

        } catch (Throwable e) {
            Log.e(TAG, "save exception", e);
            it.putExtra(ConstantValue.RESULT_ERR, "");
        } finally {
            mContext.sendBroadcast(it);
        }
    }

}
