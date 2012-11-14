package com.zj.note.test;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zj.note.NoteUtil;
import com.zj.note.R;

public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        Button open = ( Button ) findViewById(R.id.opennote);
        // Button check = ( Button ) findViewById(R.id.checknote);

        final NoteUtil noteUtil = new NoteUtil(new MyDialog());

        open.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                noteUtil.openMainNote(TestActivity.this,
                    "testnote/" + System.currentTimeMillis());
            }
        });

        Button check = ( Button ) findViewById(R.id.checknote);
        check.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                noteUtil.checkNote(TestActivity.this, "testnote");
            }
        });
    }
}
