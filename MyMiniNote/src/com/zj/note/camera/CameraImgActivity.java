package com.zj.note.camera;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zj.note.ConstantValue;
import com.zj.note.MessageValue;
import com.zj.note.NoteUtil;
import com.zj.note.R;
import com.zj.note.Session;
import com.zj.note.check.ImgAttachDetailCheck;
import com.zj.note.manager.FileManager;
import com.zj.note.service.NoteService;
import com.zj.note.widget.ScrollImageView;

public class CameraImgActivity extends ImgAttachDetailCheck {

    /**
     * TAG
     */
    private static final String TAG = "CameraImgActivity";

    /**
     * 图片路径
     */
    protected String filePath;

    /**
     * 图片所在目录的路径
     */
    protected String dirPath;

    /**
     * 图片名称
     */
    protected String fileName;

    /**
     * 笔记工具类
     */
    protected NoteUtil noteUtil;

    /**
     * 弹出窗工具类
     */
    private CameraImgDialogUtil mDialogUtil;

    /**
     * 屏幕宽
     */
    protected int screenWidth;

    /**
     * 屏幕高
     */
    protected int screenHeight;

    /**
     * io处理类对象
     */
    protected FileManager fileUtil;


    /**
     * 图片保存广播接收器
     */
    private ImgSaveReceiver imgReceiver;

    /**
     * 拍照后保存的文件
     */
    private File bitmapFile;

    /**
     * 成功失败的状态
     */
    private int state;

    /**
     * 是否打开照相机
     */
    protected boolean isOpen = true;

    /**
     * 提示图片
     */
    protected int hintDrawable = R.drawable.icon_photo_big;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Throwable e) {
            Log.e(TAG, "on Create exception", e);
            Toast.makeText(this, MessageValue.VIEW_INIT_FAILED,
                Toast.LENGTH_LONG);
        }
    }



    @Override
    protected void init() {
        dirPath = getIntent().getStringExtra(ConstantValue.DIR_PATH);
        if (!dirPath.endsWith("/")) {
            dirPath = dirPath + "/";
        }
        noteUtil = ( NoteUtil ) getIntent().getSerializableExtra(
            ConstantValue.NOTE_UTIL);
        screenWidth = getIntent().getIntExtra(ConstantValue.SCREEN_WIDTH, 0);
        screenHeight = getIntent().getIntExtra(ConstantValue.SCREEN_HEIGHT, 0);
        fileUtil = ( FileManager ) getIntent().getSerializableExtra(
            ConstantValue.FILE_UTIL);
        mDialogUtil = new CameraImgDialogUtil(NoteUtil.mDialogInterface);

        siv = ( ScrollImageView ) findViewById(R.id.scrolliv);
        leftBtn = ( ImageButton ) findViewById(R.id.toLeft);
        rightBtn = ( ImageButton ) findViewById(R.id.toRight);
        saveBtn = ( ImageButton ) findViewById(R.id.save);
        delBtn = ( ImageButton ) findViewById(R.id.del);

        btnBar = ( LinearLayout ) findViewById(R.id.btnbar);

        leftBtn.setOnClickListener(listener);
        rightBtn.setOnClickListener(listener);
        saveBtn.setOnClickListener(listener);
        delBtn.setOnClickListener(listener);

        // saveBtn.setEnabled(false);
        // leftBtn.setEnabled(false);
        // rightBtn.setEnabled(false);

        imgReceiver = new ImgSaveReceiver();
        IntentFilter imgIft = new IntentFilter(ConstantValue.IMG_SAVE_RESULT);
        registerReceiver(imgReceiver, imgIft);
    }




    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // super.onActivityResult(requestCode, resultCode, data);
    // // 此次没有采用android一般写法 从data中取得uri在取得path 因为三星手机data为null
    // if (resultCode == RESULT_OK) {
    // Intent it = new Intent(this, NoteService.class);
    // it.putExtra(ConstantValue.BITMAP_PROCESS, "");
    // it.putExtra(ConstantValue.FILE_PATH, tmpPicPath);
    // it.putExtra(ConstantValue.SCREEN_WIDTH, screenWidth);
    // it.putExtra(ConstantValue.SCREEN_HEIGHT, screenHeight);
    // it.putExtra(ConstantValue.FILE_UTIL, fileUtil);
    // it.putExtra(ConstantValue.ACTION_CAMERA, "");
    // if (CommonUtil.getAndroidSDKVersion() < 14) {
    // Cursor c = this.getContentResolver().query(
    // MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
    // null, Media.DATE_MODIFIED + " desc");
    //
    // if (c.moveToFirst()) {
    // String filePath = c.getString(c.getColumnIndex(Media.DATA));
    // Log.d(TAG, "real path is " + filePath);
    //
    // File file = new File(filePath);
    // file.delete();
    //
    // }
    // }
    //
    // startService(it);
    // mDialogUtil.showProgress(this, MessageValue.TITLE_WAIT,
    // MessageValue.SAVING_ATTACH, false);
    //
    // }
    // }



    @Override
    protected void save() {
        Intent it = new Intent(this, NoteService.class);
        it.putExtra(ConstantValue.IMG_SAVE, "");
        it.putExtra(ConstantValue.FILE_PATH, filePath);
        it.putExtra(ConstantValue.DIR_PATH, dirPath);
        it.putExtra(ConstantValue.FILE_NAME, fileName);
        Session.put(ConstantValue.IMG_BITMAP, bitmap);
        startService(it);
        mDialogUtil.showProgress(CameraImgActivity.this,
            MessageValue.TITLE_WAIT, MessageValue.SAVING, false);
    }



    @Override
    protected void del() {
        File file = new File(filePath);
        file.delete();
        finish();
    }



    @Override
    public void finish() {
        if (imgReceiver != null) {
            unregisterReceiver(imgReceiver);
            imgReceiver = null;
        }
        if (!isOpen) {
            Intent it = new Intent();
            it.putExtra(ConstantValue.CURRENT_SAVE_FILE, bitmapFile.getName());
            this.setResult(state, it);
        }
        super.finish();
    }

    protected class ImgSaveReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, "receive");
                mDialogUtil.dismissProgress();
                if (intent.hasExtra("RESULT_ERR")) {
                    Toast.makeText(CameraImgActivity.this,
                        MessageValue.SAVE_FAILED, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CameraImgActivity.this,
                        MessageValue.SAVE_SUC, Toast.LENGTH_LONG).show();

                }

            } catch (Throwable e) {
                Log.e(TAG, "on receive exception", e);
            }
        }

    }
}
