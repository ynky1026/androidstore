package com.zj.note;

import java.io.File;
import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.zj.note.audio.RecordActivity;
import com.zj.note.check.AttachListCheck;
import com.zj.note.check.ImgAttachDetailCheck;
import com.zj.note.check.NoteListCheck;
import com.zj.note.gesture.GestureActivity;
import com.zj.note.manager.FileManager;
import com.zj.note.picture.PicImgActivity;

/**
 * 笔记组件工具类 simple introduction
 * 
 * <p>
 * detailed comment
 * 
 * @author zhoujian 2012-6-26
 * @see
 * @since 1.0
 */
public class NoteUtil implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 录音requestcode
     */
    public static final int REQUST_CODE_AUDIO = 0;

    /**
     * 拍照requestcode
     */
    public static final int REQUEST_CODE_CAMERE = 1;

    /**
     * 图库上传requestcode
     */
    public static final int REQUEST_CODE_PICTURESTORE = 2;

    /**
     * 手写输入requestcode
     */
    public static final int REQUEST_CODE_GESTURE = 3;

    /**
     * 图片编辑requestcode
     */
    public static final int REQUEST_CODE_PIC_EDIT = 4;

    /**
     * 弹出窗接口
     */
    public static DialogInterface mDialogInterface;

    /**
     * 状态sd卡已插入 并且有足够剩余空间
     */
    public static final int FINE_SDCARD = 0;

    /**
     * 状态sd卡未插入
     */
    public static final int NO_SDCARD = 1;

    /**
     * 状态sd卡没有足够的剩余空间
     */
    public static final int NOT_ENOUGHT_SPACE = 2;


    NoteUtil() {

    }



    /**
     * 打开新建笔记界面
     * 
     * @param activity
     *            调用次方法的activity对象
     * @param dirPath
     *            笔记要保存的路径
     */
    public void openMainNote(Activity activity, String dirPath) {
        Intent it = new Intent(activity, MainNote.class);
        it.putExtra(ConstantValue.DIR_PATH, dirPath);
        activity.startActivity(it);
    }



    /**
     * 打开录音界面
     * 
     * @param activity
     *            调用次方法的activity对象
     * @param dirPath
     *            笔记要保存的路径
     */
    public void openRecord(Activity activity, String dirPath) {
        Intent it = new Intent(activity, RecordActivity.class);
        it.putExtra(ConstantValue.DIR_PATH, dirPath);
        activity.startActivityForResult(it, REQUST_CODE_AUDIO);
    }



    /**
     * 打开拍照界面
     * 
     * @param activity
     * @param filePath
     */
    public void openCamere(Activity activity, String dirPath) {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(dirPath)));
        activity.startActivityForResult(it, NoteUtil.REQUEST_CODE_CAMERE);

    }



    /**
     * 打开图库上传界面
     * 
     * @param activity
     * @param dirPath
     */
    public void openPictureStore(Activity activity, String dirPath) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        FileManager fileUtil = new FileManager(dirPath);
        Intent picIntent = new Intent(activity, PicImgActivity.class);
        picIntent.putExtra(ConstantValue.DIR_PATH, dirPath);
        picIntent.putExtra(ConstantValue.NOTE_UTIL, this);
        picIntent.putExtra(ConstantValue.SCREEN_WIDTH, screenWidth);
        picIntent.putExtra(ConstantValue.SCREEN_HEIGHT, screenHeight);
        picIntent.putExtra(ConstantValue.FILE_UTIL, fileUtil);
        activity.startActivityForResult(picIntent,
            NoteUtil.REQUEST_CODE_PICTURESTORE);
    }



    /**
     * 打开手写输入界面
     * 
     * @param activity
     * @param dirPath
     */
    public void openGestureInput(Activity activity, String dirPath) {
        // int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        // Log.d("TAG", "h is " + h);
        Intent it = new Intent(activity, GestureActivity.class);
        it.putExtra(ConstantValue.DIR_PATH, dirPath);
        activity.startActivityForResult(it, REQUEST_CODE_GESTURE);
    }



    /**
     * 打开查看附件界面
     * 
     * @param activity
     * @param dirPath
     */
    public void openAttachList(Activity activity, String dirPath) {
        Intent it = new Intent(activity, AttachListCheck.class);
        it.putExtra(ConstantValue.DIR_PATH, dirPath);
        activity.startActivity(it);
    }



    /**
     * 打开图片查看界面
     * 
     * @param activity
     * @param dirPath
     * @param fileName
     */
    public void openImgCheck(Activity activity, String dirPath, String fileName) {
        Intent it = new Intent(activity, ImgAttachDetailCheck.class);
        it.putExtra(ConstantValue.FILE_NAME, fileName);
        it.putExtra(ConstantValue.DIR_PATH, dirPath);
        activity.startActivity(it);
    }



    /**
     * 打开查看笔记界面
     * 
     * @param activity
     * @param dirPath
     */
    public void checkNote(Activity activity, String dirPath) {
        Intent it = new Intent(activity, NoteListCheck.class);
        it.putExtra(ConstantValue.DIR_PATH, dirPath);
        activity.startActivity(it);
    }



    /**
     * 检测sd卡
     * 
     * @return
     */
    public static int checkSdcard() {
        String status = Environment.getExternalStorageState();
        int result = 0;
        if (Environment.MEDIA_MOUNTED.equals(status)) {
            long availableSpare = getSdcardFreeSize();
            Log.d("NOTEUTIL", "availableSpare is " + availableSpare);
            if (availableSpare >= ConstantValue.MIN_SDCARD_SPACE) {
                result = FINE_SDCARD;
            } else {
                result = NOT_ENOUGHT_SPACE;
            }

        } else {
            result = NO_SDCARD;
        }

        return result;
    }



    /**
     * 取得SD卡的剩余大小
     * 
     * @return
     */
    public static long getSdcardFreeSize() {
        String sdcard = Environment.getExternalStorageDirectory().getPath();
        if (sdcard.equals("") || sdcard == null) {
            return 0;
        }

        File file = new File(sdcard);
        StatFs statFs = new StatFs(file.getPath());
        long availableSpare = (statFs.getBlockSize() * (( long ) statFs
            .getAvailableBlocks() - 4));

        return availableSpare;
    }
}
