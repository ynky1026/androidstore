package com.zj.note.main;

import com.zj.note.ConstantValue;
import com.zj.note.MainNote;
import com.zj.note.R;
import com.zj.note.check.NoteListCheck;
import com.zj.note.widget.MyViewFlipper;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends ActivityGroup {

	/**
	 * TAG
	 */
	private static final String TAG = "MainActivity";

	private TextView newnote;

	private TextView checknote;

	private MyViewFlipper vf;

	private String dirPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			init();
		} catch (Exception e) {
			Log.e(TAG, "on Create error", e);
		}
	}

	private void init() {
		dirPath = getApplicationContext().getFilesDir().getAbsolutePath()
				+ "/note_data/";
		newnote = (TextView) findViewById(R.id.newnote);
		checknote = (TextView) findViewById(R.id.checknote);
		newnote.setOnClickListener(listener);
		checknote.setOnClickListener(listener);
		vf = (MyViewFlipper) findViewById(R.id.vf);
		LocalActivityManager manager = getLocalActivityManager();
		Intent it1 = new Intent(this, MainNote.class);
		it1.putExtra(ConstantValue.DIR_PATH, dirPath);
		vf.addView(manager.startActivity("newnote", it1).getDecorView(),0);
		Intent it2 = new Intent(this, NoteListCheck.class);
		it2.putExtra(ConstantValue.DIR_PATH, dirPath);
		vf.addView(manager.startActivity("checknote", it2).getDecorView(),1);
		
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.newnote:
				vf.moveToView(0);
				break;
			case R.id.checknote:
				vf.moveToView(1);
				break;
			default:
				break;
			}
		}
	};
}
