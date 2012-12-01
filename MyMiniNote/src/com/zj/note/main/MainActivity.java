package com.zj.note.main;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.zj.note.ConstantValue;
import com.zj.note.MainNote;
import com.zj.note.R;
import com.zj.note.check.NoteListCheck;
import com.zj.note.widget.MyViewFlipper;

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
		View v1 = manager.startActivity("newnote", it1).getDecorView();
		vf.addView(v1,0);
		Intent it2 = new Intent(this, NoteListCheck.class);
		it2.putExtra(ConstantValue.DIR_PATH, dirPath);
		View v2 = manager.startActivity("checknote", it2).getDecorView();
		vf.addView(v2,1);
		v1.setOnTouchListener(touchListener);
		v2.setOnTouchListener(touchListener);
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
	
	private OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.d(TAG, "touch is run");
			vf.dispatchTouchEvent(event);
			return true;
		}
	};
}
