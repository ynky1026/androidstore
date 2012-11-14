package com.zj.note.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zj.note.ConstantValue;
import com.zj.note.process.BitmapProcess;
import com.zj.note.process.GestureIOSave;
import com.zj.note.process.ImgIOProcess;

public class NoteService extends Service {

    /**
     * TAG
     */
    private static final String TAG = "NoteService";

    /**
     * 线程池
     */
    private ExecutorService es = null;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        Log.d(TAG, "service create");
        try {
            super.onCreate();
            init();
        } catch (Exception e) {
            Log.e(TAG, "on create exception", e);
        }
    }



    private void init() {
        es = Executors.newCachedThreadPool();
    }



    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        try {
            if (intent.hasExtra(ConstantValue.GESTURE_SAVE)) {
                es.execute(new GestureIOSave(this, intent));
            } else if (intent.hasExtra(ConstantValue.BITMAP_PROCESS)) {
                es.execute(new BitmapProcess(this, intent));
            } else if (intent.hasExtra(ConstantValue.IMG_SAVE)) {
                es.execute(new ImgIOProcess(this, intent));
            }
        } catch (Throwable e) {
            Log.e(TAG + "onStart", "On start error.", e);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        // 回收资源
        if (es != null) {
            es.shutdown();
            es = null;
        }
    }
}
