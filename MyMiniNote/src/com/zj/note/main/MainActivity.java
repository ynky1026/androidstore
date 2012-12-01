package com.zj.note.main;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
		dirPath = Environment.getExternalStorageDirectory().getPath()
				+ "/note_data/";
		// File directory = new File(dirPath);
		// if (!directory.exists()) {
		// directory.mkdir();
		// }

		String parentDir = dirPath + System.currentTimeMillis();
		// File noteDir = new File(parentDir);
		// if (!noteDir.exists()) {
		// noteDir.mkdir();
		// }
		newnote = (TextView) findViewById(R.id.newnote);
		checknote = (TextView) findViewById(R.id.checknote);
		newnote.setOnClickListener(listener);
		checknote.setOnClickListener(listener);
		vf = (MyViewFlipper) findViewById(R.id.vf);
		LocalActivityManager manager = getLocalActivityManager();
		Intent it1 = new Intent(this, MainNote.class);

		it1.putExtra(ConstantValue.DIR_PATH, parentDir);
		Log.d(TAG, "parentDir is " + parentDir);
		View v1 = manager.startActivity("newnote", it1).getDecorView();
		vf.addView(v1, 0);
		Intent it2 = new Intent(this, NoteListCheck.class);
		it2.putExtra(ConstantValue.DIR_PATH, dirPath);
		View v2 = manager.startActivity("checknote", it2).getDecorView();
		vf.addView(v2, 1);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "finish");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainNote a1 = (MainNote) getLocalActivityManager().getActivity(
					"newnote");
			a1.finish();
			NoteListCheck a2 = (NoteListCheck) getLocalActivityManager()
					.getActivity("checknote");
			a2.finish();
		}

		return true;
	};
}
