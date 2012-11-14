package com.zj.note.camera;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zj.note.CommonUtil;
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
     * ImageView
     */
    private ImageView iv;

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
     * 照片存放的路径
     */
    private String tmpPicPath;

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
     * 图片处理线程广播接收器
     */
    private BitmapProcessReceiver bitmapReceiver;

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
        btnBar.setVisibility(View.INVISIBLE);

        leftBtn.setOnClickListener(listener);
        rightBtn.setOnClickListener(listener);
        saveBtn.setOnClickListener(listener);
        delBtn.setOnClickListener(listener);

        // saveBtn.setEnabled(false);
        // leftBtn.setEnabled(false);
        // rightBtn.setEnabled(false);

        bitmapReceiver = new BitmapProcessReceiver();
        IntentFilter ift = new IntentFilter(ConstantValue.BITMAP_PROCESS_RESULT);
        registerReceiver(bitmapReceiver, ift);

        imgReceiver = new ImgSaveReceiver();
        IntentFilter imgIft = new IntentFilter(ConstantValue.IMG_SAVE_RESULT);
        registerReceiver(imgReceiver, imgIft);
        iv = ( ImageView ) findViewById(R.id.iv);
        iv.setVisibility(View.VISIBLE);
        siv.setVisibility(View.GONE);
        iv.setImageResource(hintDrawable);
        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isOpen) {
                    openSysActivity();
                }
            }
        });
    }



    protected void openSysActivity() {
        if (dirPath.startsWith(ConstantValue.SD_DIR_PATH)) {
            tmpPicPath = dirPath + System.currentTimeMillis()
                + ConstantValue.FILE_EX_NAME_PNG;
        } else {
            tmpPicPath = ConstantValue.SD_DIR_PATH + dirPath
                + System.currentTimeMillis() + ConstantValue.FILE_EX_NAME_PNG;
        }

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tmpPicPath)));
        startActivityForResult(it, NoteUtil.REQUEST_CODE_CAMERE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 此次没有采用android一般写法 从data中取得uri在取得path 因为三星手机data为null
        if (resultCode == RESULT_OK) {

            Intent it = new Intent(this, NoteService.class);
            it.putExtra(ConstantValue.BITMAP_PROCESS, "");
            it.putExtra(ConstantValue.FILE_PATH, tmpPicPath);
            it.putExtra(ConstantValue.SCREEN_WIDTH, screenWidth);
            it.putExtra(ConstantValue.SCREEN_HEIGHT, screenHeight);
            it.putExtra(ConstantValue.FILE_UTIL, fileUtil);
            it.putExtra(ConstantValue.ACTION_CAMERA, "");
            if (CommonUtil.getAndroidSDKVersion() < 14) {
                Cursor c = this.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, Media.DATE_MODIFIED + " desc");

                if (c.moveToFirst()) {
                    String filePath = c.getString(c.getColumnIndex(Media.DATA));
                    Log.d(TAG, "real path is " + filePath);

                    File file = new File(filePath);
                    file.delete();
                    
                }
            }

            startService(it);
            mDialogUtil.showProgress(this, MessageValue.TITLE_WAIT,
                MessageValue.SAVING_ATTACH, false);

        }
    }



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
        iv.setVisibility(View.VISIBLE);
        isOpen = true;
        siv.setVisibility(View.GONE);
        btnBar.setVisibility(View.GONE);
    }



    @Override
    public void finish() {
        if (bitmapReceiver != null) {
            unregisterReceiver(bitmapReceiver);
            bitmapReceiver = null;
        }
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

    protected class BitmapProcessReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                mDialogUtil.dismissProgress();
                if (intent.hasExtra(ConstantValue.RESULT_ERR)) {
                    Toast.makeText(CameraImgActivity.this,
                        MessageValue.ADD_ATTACH_FAILED, Toast.LENGTH_LONG)
                        .show();
                } else {
                    saveBtn.setEnabled(true);
                    leftBtn.setEnabled(true);
                    rightBtn.setEnabled(true);
                    Toast.makeText(CameraImgActivity.this,
                        MessageValue.ADD_ATTACH_SUC, Toast.LENGTH_LONG).show();
                    String bitmapFilePath = null;
                    if (dirPath.startsWith(ConstantValue.SD_DIR_PATH)) {
                        bitmapFilePath = dirPath
                            + intent
                                .getStringExtra(ConstantValue.BITMAP_FILE_PATH);
                    } else {
                        bitmapFilePath = ConstantValue.SD_DIR_PATH
                            + dirPath
                            + intent
                                .getStringExtra(ConstantValue.BITMAP_FILE_PATH);
                    }

                    filePath = bitmapFilePath;
                    Log.d(TAG, filePath);
                    fileName = filePath.substring(
                        filePath.lastIndexOf("/") + 1, filePath.length());
                    bitmapFile = new File(bitmapFilePath);
                    bitmap = BitmapFactory.decodeFile(bitmapFilePath);
                    siv.setImageBitmap(bitmap);
                    state = RESULT_OK;
                    btnBar.setVisibility(View.VISIBLE);
                    isOpen = false;
                    iv.setVisibility(View.GONE);
                    siv.setVisibility(View.VISIBLE);
                }
            } catch (Throwable e) {
                Log.e(TAG, "on receive exception", e);
            }
        }

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
