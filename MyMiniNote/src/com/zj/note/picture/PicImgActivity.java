package com.zj.note.picture;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.zj.note.ConstantValue;
import com.zj.note.MessageValue;
import com.zj.note.NoteUtil;
import com.zj.note.R;
import com.zj.note.camera.CameraImgActivity;
import com.zj.note.service.NoteService;

public class PicImgActivity extends CameraImgActivity {

    private PicImgDialogUtil mDialogUtil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hintDrawable = R.drawable.icon_picture_big;
        super.onCreate(savedInstanceState);
        mDialogUtil = new PicImgDialogUtil(NoteUtil.mDialogInterface);
    }



    protected void openSysActivity() {
        Intent it = new Intent();
        it.setType("image/*");
        it.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(it, NoteUtil.REQUEST_CODE_PICTURESTORE);
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex("_data"));
            Intent it = new Intent(this, NoteService.class);
            it.putExtra(ConstantValue.BITMAP_PROCESS, "");
            it.putExtra(ConstantValue.FILE_PATH, path);
            it.putExtra(ConstantValue.SCREEN_WIDTH, screenWidth);
            it.putExtra(ConstantValue.SCREEN_HEIGHT, screenHeight);
            it.putExtra(ConstantValue.FILE_UTIL, fileUtil);

            startService(it);
            mDialogUtil.showProgress(this, MessageValue.TITLE_WAIT,
                MessageValue.SAVING_ATTACH, false);

        }

    }

}
