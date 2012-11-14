package com.zj.note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.zj.note.manager.FileManager;
import com.zj.note.picture.PicImgActivity;

/**
 * 新建笔记界面 simple introduction
 * 
 * <p>
 * detailed comment
 * 
 * @author zhoujian 2012-6-26
 * @see
 * @since 1.0
 */
public class MainNote extends NoteBaseActivity {

    /**
     * TAG
     */
    private static final String TAG = "MainNote";

    /**
     * 录音按钮
     */
    private ImageButton audio;

    /**
     * 照相按钮
     */
    private ImageButton camera;

    /**
     * 图片按钮
     */
    private ImageButton picture;

    /**
     * 手势按钮
     */
    private ImageButton gesture;

    /**
     * 查看附件按钮
     */
    private ImageButton check;

    /**
     * 保存按钮
     */
    private ImageButton save;

    /**
     * 标题输入框
     */
    private EditText titleView;

    /**
     * 笔记内容输入框
     */
    private EditText contentView;

    // /**
    // * 相机返回的bitmap
    // */
    // private Bitmap cameraBitmap;

    // /**
    // * 图库返回的bitmap
    // */
    // private Bitmap picStoreBitmap;

    /**
     * 本次笔记的文件夹名
     */
    private String dirPath;

    /**
     * io处理类对象
     */
    private FileManager fileUtil;

    /**
     * 是否保存
     */
    private boolean hasSaved;

    /**
     * 屏幕宽
     */
    private int screenWidth;

    /**
     * 屏幕高
     */
    private int screenHeight;

    // /**
    // * 相机照片的路径
    // */
    // private String tmpCameraPicPath;

    /**
     * 是否为查看标识位
     */
    private boolean isNoteCheck;

    /**
     * 当前笔记名字
     */
    private String noteName;

    /**
     * NoteUtil对象
     */
    private NoteUtil noteUtil;

    /**
     * 弹出窗工具类
     */
    private DialogUtil mDialogUtil;

    // /**
    // * 图片处理线程广播接收器
    // */
    // private BitmapProcessReceiver receiver;

    /**
     * 用于保存本次添加的附件的集合
     */
    private List<File> currentFiles = new ArrayList<File>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.mainnote);
            init();
        } catch (Throwable e) {
            Log.e(TAG, "on Create exception", e);
            Toast.makeText(this, MessageValue.VIEW_INIT_FAILED,
                Toast.LENGTH_LONG).show();
        }
    }



    /**
     * 初始化方法
     */
    private void init() {
        noteUtil = new NoteUtil();
        titleView = ( EditText ) findViewById(R.id.title);
        contentView = ( EditText ) findViewById(R.id.content);
        audio = ( ImageButton ) findViewById(R.id.audio);
        camera = ( ImageButton ) findViewById(R.id.camera);
        picture = ( ImageButton ) findViewById(R.id.picture);
        gesture = ( ImageButton ) findViewById(R.id.gesture);
        check = ( ImageButton ) findViewById(R.id.check);
        save = ( ImageButton ) findViewById(R.id.save);
        audio.setOnClickListener(listener);
        camera.setOnClickListener(listener);
        picture.setOnClickListener(listener);
        gesture.setOnClickListener(listener);
        check.setOnClickListener(listener);
        save.setOnClickListener(listener);

        dirPath = getIntent().getExtras().getString(ConstantValue.DIR_PATH);
        isNoteCheck = getIntent()
            .getBooleanExtra(ConstantValue.IS_CHECK, false);
        if (isNoteCheck) {
            initNote();
        }

        Log.d(TAG, dirPath);

        fileUtil = new FileManager(dirPath);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        mDialogUtil = new DialogUtil(NoteUtil.mDialogInterface);

        // receiver = new BitmapProcessReceiver();
        // IntentFilter ift = new
        // IntentFilter(ConstantValue.BITMAP_PROCESS_RESULT);
        // registerReceiver(receiver, ift);
    }



    /**
     * 初始化笔记本
     */
    private void initNote() {
        try {
            File dirFile = new File(dirPath);
            File[] txtFiles = dirFile.listFiles(new NoteFileFilter());
            // File[] txtFiles = FileManager.queryAttachByPathID(dirPath,
            // new NoteFileFilter());
            File txtFile = txtFiles[0];
            noteName = txtFile.getName().substring(0,
                txtFile.getName().indexOf(ConstantValue.FILE_EX_NAME_TXT));
            titleView.setText(noteName);

            StringBuffer str = new StringBuffer();
            FileReader fr = new FileReader(txtFile);
            BufferedReader reader = new BufferedReader(fr);
            String s = null;
            while ((s = reader.readLine()) != null) {
                str.append(s);
            }
            Log.d(TAG, "content is " + str.toString());
            contentView.setText(str);
        } catch (Throwable e) {
            Log.e(TAG, "init note exception", e);
        }

    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                if (v.equals(audio)) {
                    noteUtil.openRecord(MainNote.this, dirPath);
                } else if (v.equals(camera)) {
                    (( InputMethodManager ) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    noteUtil.openCamere(MainNote.this, dirPath);
                } else if (v.equals(picture)) {
                    Intent picIntent = new Intent(MainNote.this,
                        PicImgActivity.class);
                    picIntent.putExtra(ConstantValue.DIR_PATH, dirPath);
                    picIntent.putExtra(ConstantValue.NOTE_UTIL, noteUtil);
                    picIntent.putExtra(ConstantValue.SCREEN_WIDTH, screenWidth);
                    picIntent.putExtra(ConstantValue.SCREEN_HEIGHT,
                        screenHeight);
                    picIntent.putExtra(ConstantValue.FILE_UTIL, fileUtil);
                    startActivityForResult(picIntent,
                        NoteUtil.REQUEST_CODE_PICTURESTORE);
                } else if (v.equals(gesture)) {
                    noteUtil.openGestureInput(MainNote.this, dirPath);
                } else if (v.equals(check)) {
                    noteUtil.openAttachList(MainNote.this, dirPath);
                } else if (v.equals(save)) {
                    try {
                        hasSaved = true;
                        if (isNoteCheck) {// 如果已经保存过 删除原文本文件重新保存
                            Log.d(TAG, noteName);
                            fileUtil.deleteFile(noteName
                                + ConstantValue.FILE_EX_NAME_TXT);
                        }
                        String titleViewText = titleView.getText().toString();
                        String title = titleViewText == null
                            || titleViewText.equals("") ? MessageValue.NO_TITLE_NOTE
                            : titleViewText;// 未输入标题则存为无标题笔记

                        String contentViewText = contentView.getText()
                            .toString();
                        String content = contentViewText == null
                            || contentViewText.equals("") ? ""
                            : contentViewText;
                        fileUtil.saveTxtFile(title, content);

                        Toast.makeText(MainNote.this, MessageValue.SAVE_SUC,
                            Toast.LENGTH_LONG).show();
                    } catch (Throwable e) {
                        Toast.makeText(MainNote.this, MessageValue.SAVE_FAILED,
                            Toast.LENGTH_LONG).show();
                        Log.e(TAG, "save note exception", e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "on Click exception", e);
                Toast.makeText(MainNote.this,
                    MessageValue.CLICK_EVENT_HANDLE_FAILED, Toast.LENGTH_LONG)
                    .show();
            }
        }
    };



    @SuppressWarnings("unchecked")
    protected void onActivityResult(int requestCode, int resultCode,
        android.content.Intent data) {
        try {
            switch (requestCode) {
                case NoteUtil.REQUEST_CODE_CAMERE:
                    if (resultCode == RESULT_OK) {
                        String fileName = data
                            .getStringExtra("ConstantValue.CURRENT_SAVE_FILE");
                        File camFile = FileManager.getFileInstance(dirPath,
                            fileName);
                        currentFiles.add(camFile);
                    }
                    break;
                case NoteUtil.REQUEST_CODE_PICTURESTORE:
                    String fileName = data
                        .getStringExtra("ConstantValue.CURRENT_SAVE_FILE");
                    File picFile = FileManager.getFileInstance(dirPath,
                        fileName);

                    currentFiles.add(picFile);

                    break;
                case NoteUtil.REQUST_CODE_AUDIO:
                    List<String> audioNames = ( List<String> ) data
                        .getSerializableExtra(ConstantValue.FILE_PATH_COLLECTION);
                    if (audioNames != null) {
                        for (String name : audioNames) {
                            File audioFile = FileManager.getFileInstance(
                                dirPath, name);
                            if (audioFile != null && audioFile.exists()) {
                                currentFiles.add(audioFile);
                            }
                        }
                    }
                    break;
                case NoteUtil.REQUEST_CODE_GESTURE:
                    String gestureName = data
                        .getStringExtra(ConstantValue.GESTURE_FILE_PATH);
                    File gestureFile = FileManager.getFileInstance(dirPath,
                        gestureName);
                    if (gestureFile != null && gestureFile.exists()) {
                        currentFiles.add(gestureFile);
                    }
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            Log.e(TAG, "onActivityResult exception", e);
        }

    };



    @Override
    public void finish() {
        try {
            if (!hasSaved) {
                mDialogUtil.showAlertDialog(this, 0, MessageValue.TITLE_HINT,
                    MessageValue.CONFIRM,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                dialog.dismiss();
                                MainNote.super.finish();
                                for (File file : currentFiles) {
                                    if (file != null && file.exists()) {
                                        if (file.isDirectory()) {
                                            FileManager.deleteDir(file);
                                        } else {
                                            file.delete();
                                        }
                                    }
                                }

                                // if (receiver != null) {
                                // unregisterReceiver(receiver);
                                // receiver = null;
                                // }
                            } catch (Throwable e) {
                                Log.e(TAG, "on Click exception", e);
                            }
                        }
                    }, MessageValue.CANCLE,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                Log.e(TAG, "on Click exception", e);
                            }
                        }
                    }, MessageValue.UNSAVE_NOTE_DEL);

            } else {
                // if (receiver != null) {
                // unregisterReceiver(receiver);
                // receiver = null;
                // }
                super.finish();
            }
        } catch (Throwable e) {
            Log.e(TAG, "finish exception", e);
        }
    }



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        try {
            super.onDestroy();
            // if (receiver != null) {
            // unregisterReceiver(receiver);
            // receiver = null;
            // }
            if (!hasSaved && !isNoteCheck) {
                for (File file : currentFiles) {
                    if (file != null && file.exists()) {
                        if (file.isDirectory()) {
                            FileManager.deleteDir(file);
                        } else {
                            file.delete();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, "on destroy exception", e);
        }
    }

    private class NoteFileFilter implements FileFilter {

        @Override
        public boolean accept(File paramFile) {
            return paramFile.getName().endsWith(ConstantValue.FILE_EX_NAME_TXT);
        }

    }

    // private class BitmapProcessReceiver extends BroadcastReceiver {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // try {
    // mDialogUtil.dismissProgress();
    // if (intent.hasExtra(ConstantValue.RESULT_ERR)) {
    // Toast.makeText(MainNote.this,
    // MessageValue.ADD_ATTACH_FAILED, Toast.LENGTH_LONG)
    // .show();
    // } else {
    // Toast.makeText(MainNote.this, MessageValue.ADD_ATTACH_SUC,
    // Toast.LENGTH_LONG).show();
    //
    // String bitmapFilePath = dirPath
    // + intent.getStringExtra(ConstantValue.BITMAP_FILE_PATH);
    // File bitmapFile = new File(bitmapFilePath);
    // if (bitmapFile != null && bitmapFile.exists()) {
    // currentFiles.add(bitmapFile);
    // }
    // }
    // } catch (Throwable e) {
    // Log.e(TAG, "on receive exception", e);
    // }
    // }
    //
    // }

}
