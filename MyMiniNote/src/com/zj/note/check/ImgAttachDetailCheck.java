package com.zj.note.check;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zj.note.ConstantValue;
import com.zj.note.MessageValue;
import com.zj.note.NoteBaseActivity;
import com.zj.note.R;
import com.zj.note.Session;
import com.zj.note.manager.BitmapManager;
import com.zj.note.manager.FileManager;
import com.zj.note.service.NoteService;
import com.zj.note.widget.ScrollImageView;

/**
 * 图片附件查看界面 simple introduction
 * 
 * <p>
 * detailed comment
 * 
 * @author zhoujian 2012-6-26
 * @see
 * @since 1.0
 */
public class ImgAttachDetailCheck extends NoteBaseActivity {

    /**
     * TAG
     */
    private static final String TAG = "AttachDetailCheck";

    /**
     * ScrollImageView组件
     */
    protected ScrollImageView siv;

    /**
     * 左旋按钮
     */
    protected ImageButton leftBtn;

    /**
     * 右旋按钮
     */
    protected ImageButton rightBtn;

    /**
     * 保存按钮
     */
    protected ImageButton saveBtn;

    /**
     * 删除按钮
     */
    protected ImageButton delBtn;

    /**
     * 按钮栏
     */
    protected LinearLayout btnBar;

    /**
     * 图片的bitmap对象
     */
    protected Bitmap bitmap;

    /**
     * 图片路径
     */
    private String filePath;

    /**
     * 图片所在目录的路径
     */
    private String dirPath;

    /**
     * 图片名称
     */
    private String fileName;


    /**
     * 图片保存广播接收器
     */
    private ImgSaveReceiver receiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.attach_detil);
            init();
        } catch (Throwable e) {
            Log.e(TAG, "onCreate exception", e);
            Toast.makeText(this, MessageValue.VIEW_INIT_FAILED,
                Toast.LENGTH_LONG).show();
        }
    }



    /**
     * 初始化方法
     */
    protected void init() {
        siv = ( ScrollImageView ) findViewById(R.id.scrolliv);
        siv.setVisibility(View.VISIBLE);
        dirPath = getIntent().getStringExtra(ConstantValue.DIR_PATH);
        if (!dirPath.endsWith("/")) {// 如果dirpath结尾没有"/"则添加
            dirPath = dirPath + "/";
        }
        fileName = getIntent().getStringExtra(ConstantValue.FILE_NAME);
        File imgFile = FileManager.getFileInstance(dirPath, fileName);
        filePath = imgFile.getAbsolutePath();
        Log.d(TAG, filePath);
        if (FileManager.isPNGFile(filePath) || FileManager.isJPEGFile(filePath)) {
            bitmap = BitmapFactory.decodeFile(filePath);
            Log.d(TAG,
                filePath.substring(filePath.length() - 4, filePath.length()));
        }

        if (bitmap == null) {
            Log.d(TAG, "bitmap is null");
        }

        if (fileName.substring(0, 1).toLowerCase()
            .equals(ConstantValue.CAMERA_NAME)) {
            siv.setIsScroll(false);
        } else {
            siv.setIsScroll(true);
        }

        leftBtn = ( ImageButton ) findViewById(R.id.toLeft);
        rightBtn = ( ImageButton ) findViewById(R.id.toRight);
        saveBtn = ( ImageButton ) findViewById(R.id.save);
        delBtn = ( ImageButton ) findViewById(R.id.del);

        leftBtn.setOnClickListener(listener);
        rightBtn.setOnClickListener(listener);
        saveBtn.setOnClickListener(listener);
        delBtn.setOnClickListener(listener);

        btnBar = ( LinearLayout ) findViewById(R.id.btnbar);

        if (fileName.substring(0, 1).equals(ConstantValue.GESTURE_NAME)) {
            btnBar.setVisibility(View.GONE);
        }

        siv.setImageBitmap(bitmap);

        receiver = new ImgSaveReceiver();
        IntentFilter ift = new IntentFilter(ConstantValue.IMG_SAVE_RESULT);
        registerReceiver(receiver, ift);

        // saveBtn.setEnabled(false);
        // leftBtn.setEnabled(false);
        // rightBtn.setEnabled(false);
    }

    protected OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                if (v.equals(leftBtn)) {
                    bitmap = BitmapManager.rotateBitmap(bitmap,
                        BitmapManager.ROTATE_TO_LEFT, 90);
                    siv.setImageBitmap(bitmap);
                } else if (v.equals(rightBtn)) {
                    bitmap = BitmapManager.rotateBitmap(bitmap,
                        BitmapManager.ROTATE_TO_RIGHT, 90);
                    siv.setImageBitmap(bitmap);
                } else if (v.equals(saveBtn)) {
                    save();
                } else if (v.equals(delBtn)) {
                    del();
                }
            } catch (Exception e) {
                Log.e(TAG, "on click exception", e);
                Toast.makeText(ImgAttachDetailCheck.this,
                    MessageValue.CLICK_EVENT_HANDLE_FAILED, Toast.LENGTH_LONG)
                    .show();
            }
        }
    };



    /**
     * 保存图片
     */
    protected void save() {
        Intent it = new Intent(this, NoteService.class);
        it.putExtra(ConstantValue.IMG_SAVE, "");
        it.putExtra(ConstantValue.FILE_PATH, filePath);
        it.putExtra(ConstantValue.DIR_PATH, dirPath);
        it.putExtra(ConstantValue.FILE_NAME, fileName);
        Session.put(ConstantValue.IMG_BITMAP, bitmap);
        startService(it);
        showProgressDialog(ImgAttachDetailCheck.this,
            MessageValue.TITLE_WAIT, MessageValue.SAVING, false);
    }



    /**
     * 删除方法
     */
    protected void del() {
        File file = new File(filePath);
        file.delete();
        finish();
    }



    @Override
    public void finish() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.finish();
    }

    private class ImgSaveReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, "receive");
                dismissProgress();
                if (intent.hasExtra("RESULT_ERR")) {
                    Toast.makeText(ImgAttachDetailCheck.this,
                        MessageValue.SAVE_FAILED, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ImgAttachDetailCheck.this,
                        MessageValue.SAVE_SUC, Toast.LENGTH_LONG).show();
                }

            } catch (Throwable e) {
                Log.e(TAG, "on receive exception", e);
            }
        }

    }
}
