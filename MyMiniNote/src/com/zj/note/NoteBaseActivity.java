package com.zj.note;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class NoteBaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int status = NoteUtil.checkSdcard();
        if (status == NoteUtil.NO_SDCARD) {
            Toast.makeText(this, MessageValue.NO_SDCARD, Toast.LENGTH_LONG)
                .show();
            return;
        } else if (status == NoteUtil.NOT_ENOUGHT_SPACE) {
            Toast.makeText(this, MessageValue.NOT_ENOUGHT_SPACE,
                Toast.LENGTH_LONG).show();
            return;
        }
    }
}
