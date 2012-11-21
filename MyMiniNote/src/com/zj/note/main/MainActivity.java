package com.zj.note.main;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.zj.note.NoteUtil;
import com.zj.note.R;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ImageView open = ( ImageView ) findViewById(R.id.opennote);
        // Button check = ( Button ) findViewById(R.id.checknote);

        final NoteUtil noteUtil = new NoteUtil(new MyDialog());

        open.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                noteUtil.openMainNote(MainActivity.this,
                    "testnote/" + System.currentTimeMillis());
            }
        });

        ImageView check = ( ImageView ) findViewById(R.id.checknote);
        check.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                noteUtil.checkNote(MainActivity.this, "testnote");
            }
        });
    }
}
