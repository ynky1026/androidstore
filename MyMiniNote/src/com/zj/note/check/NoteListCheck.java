package com.zj.note.check;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.zj.note.ConstantValue;
import com.zj.note.FileComparator;
import com.zj.note.MainNote;
import com.zj.note.MessageValue;
import com.zj.note.R;
import com.zj.note.adapter.NoteListAdapter;
import com.zj.note.audio.RecordActivity;
import com.zj.note.main.BaseActivity;
import com.zj.note.manager.FileManager;

public class NoteListCheck extends BaseActivity {

	/**
	 * TAG
	 */
	private static final String TAG = "NoteListCheck";

	/**
	 * 目录路径
	 */
	private String dirPath;

	// /**
	// * 附件所在目录的file对象
	// */
	// private File dirFile;

	/**
	 * 数据适配器
	 */
	private NoteListAdapter adapter;

	/**
	 * 展示笔记的listview对象
	 */
	private ListView lv;

	/**
	 * 附件文件集合
	 */
	private List<File> list;

	private NoteListChangeReciever receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.note_list);

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
		dirPath = getIntent().getStringExtra(ConstantValue.DIR_PATH);

		list = new ArrayList<File>();
		lv = (ListView) findViewById(R.id.lv);

		showData();
		receiver = new NoteListChangeReciever();
		IntentFilter itf = new IntentFilter(ConstantValue.BROADCAST_NOTE_CHANGE);
		registerReceiver(receiver, itf);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					File selectFile = list.get(position);
					String dirPath = selectFile.getAbsolutePath() + "/";
					if (selectFile.isDirectory()) {
						Intent it = new Intent(NoteListCheck.this,
								MainNote.class);
						it.putExtra(ConstantValue.DIR_PATH, dirPath);
						it.putExtra(ConstantValue.IS_CHECK, true);
						startActivity(it);
					} else {
						String fileName = selectFile.getName();
						String filePath = selectFile.getAbsolutePath();
						if (!fileName.substring(0, 1).toLowerCase()
								.equals(ConstantValue.RECORD_NAME)) {
							Intent it = new Intent(NoteListCheck.this,
									ImgAttachDetailCheck.class);
							it.putExtra(ConstantValue.FILE_PATH, filePath);
							startActivity(it);
						} else {
							Intent it = new Intent(NoteListCheck.this,
									RecordActivity.class);
							it.putExtra(ConstantValue.IS_PLAY_INTENT, true);
							it.putExtra(ConstantValue.FILE_PATH, filePath);
							it.putExtra(ConstantValue.DIR_PATH, dirPath);
							startActivity(it);
						}
					}
				} catch (Exception e) {
					Log.e(TAG, "on item click exception", e);
					Toast.makeText(NoteListCheck.this,
							MessageValue.CLICK_EVENT_HANDLE_FAILED,
							Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	public void showData() {
		list.clear();
		File[] files = FileManager.queryAttachByPathID(dirPath);
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					File[] childFiles = file.listFiles(new NoteFileFilter());
					if (childFiles == null || childFiles.length == 0) {
						continue;
					}
				}
				list.add(file);
			}
		}

		if (list == null || list.size() == 0) {

			// Toast.makeText(this, MessageValue.NO_NOTE, Toast.LENGTH_LONG)
			// .show();
		} else {
			Collections.sort(list, new FileComparator(
					FileComparator.FILE_LAST_MODIFY_TIME,
					FileComparator.ORDER_DESC));
		}

		adapter = new NoteListAdapter(this, list);
		lv.setAdapter(adapter);
	}

	private class NoteFileFilter implements FileFilter {

		@Override
		public boolean accept(File paramFile) {
			return paramFile.getName().endsWith(ConstantValue.FILE_EX_NAME_TXT);
		}

	}

	private class NoteListChangeReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			showData();
		}

	}

	@Override
	public void finish() {
		Log.d(TAG, "finish");
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	}

}
