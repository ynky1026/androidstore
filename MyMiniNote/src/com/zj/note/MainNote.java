package com.zj.note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.zj.note.camera.CameraImgActivity;
import com.zj.note.manager.FileManager;
import com.zj.note.picture.PicImgActivity;
import com.zj.note.service.NoteService;

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
	 * 图片处理线程广播接收器
	 */
	private BitmapProcessReceiver bitmapReceiver;

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

	// /**
	// * 图片处理线程广播接收器
	// */
	// private BitmapProcessReceiver receiver;

	/**
	 * 用于保存本次添加的附件的集合
	 */
	private List<File> currentFiles = new ArrayList<File>();

	/**
	 * 照片的临时路径
	 */
	private String tmpPicPath = "";

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
		titleView = (EditText) findViewById(R.id.title);
		contentView = (EditText) findViewById(R.id.content);
		audio = (ImageButton) findViewById(R.id.audio);
		camera = (ImageButton) findViewById(R.id.camera);
		picture = (ImageButton) findViewById(R.id.picture);
		gesture = (ImageButton) findViewById(R.id.gesture);
		check = (ImageButton) findViewById(R.id.check);
		save = (ImageButton) findViewById(R.id.save);
		audio.setOnClickListener(listener);
		camera.setOnClickListener(listener);
		picture.setOnClickListener(listener);
		gesture.setOnClickListener(listener);
		check.setOnClickListener(listener);
		save.setOnClickListener(listener);

		dirPath = getIntent().getExtras().getString(ConstantValue.DIR_PATH);
		if (!dirPath.endsWith("/")) {
			dirPath = dirPath + "/";
		}
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

		bitmapReceiver = new BitmapProcessReceiver();
		IntentFilter ift = new IntentFilter(ConstantValue.BITMAP_PROCESS_RESULT);
		registerReceiver(bitmapReceiver, ift);
		// receiver = new BitmapProcessReceiver();
		// IntentFilter ift = new
		// IntentFilter(ConstantValue.BITMAP_PROCESS_RESULT);
		// registerReceiver(receiver, ift);
	}

	/**
	 * 初始化笔记本
	 */
	private void initNote() {
		FileReader fr = null;
		BufferedReader reader = null;
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
			fr = new FileReader(txtFile);
			reader = new BufferedReader(fr);
			String s = null;
			while ((s = reader.readLine()) != null) {
				str.append(s);
			}
			Log.d(TAG, "content is " + str.toString());
			contentView.setText(str);
		} catch (Throwable e) {
			Log.e(TAG, "init note exception", e);
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e2) {
				Log.e(TAG, "init note error", e2);
			}

		}

	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				if (v.equals(audio)) {
					noteUtil.openRecord(MainNote.this, dirPath);
				} else if (v.equals(camera)) {
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					tmpPicPath = dirPath + System.currentTimeMillis()
							+ ConstantValue.FILE_EX_NAME_PNG;
					noteUtil.openCamere(MainNote.this, tmpPicPath);
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
						Intent it = new Intent(
								ConstantValue.BROADCAST_NOTE_CHANGE);
						sendBroadcast(it);
					} catch (Throwable e) {
						Toast.makeText(MainNote.this, MessageValue.SAVE_FAILED,
								Toast.LENGTH_LONG).show();
						Log.e(TAG, "save note exception", e);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "on Click exception", e);
				Toast.makeText(MainNote.this,
						MessageValue.CLICK_EVENT_HANDLE_FAILED,
						Toast.LENGTH_LONG).show();
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
					Intent it = new Intent(this, NoteService.class);
					it.putExtra(ConstantValue.BITMAP_PROCESS, "");
					it.putExtra(ConstantValue.FILE_PATH, tmpPicPath);
					it.putExtra(ConstantValue.SCREEN_WIDTH, screenWidth);
					it.putExtra(ConstantValue.SCREEN_HEIGHT, screenHeight);
					it.putExtra(ConstantValue.FILE_UTIL, fileUtil);
					it.putExtra(ConstantValue.ACTION_CAMERA, "");
					if (CommonUtil.getAndroidSDKVersion() < 14) {
						Cursor c = this
								.getContentResolver()
								.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
										null, null, null,
										Media.DATE_MODIFIED + " desc");

						if (c.moveToFirst()) {
							String filePath = c.getString(c
									.getColumnIndex(Media.DATA));
							Log.d(TAG, "real path is " + filePath);

							File file = new File(filePath);
							file.delete();

						}
					}

					startService(it);
					showProgressDialog(this, MessageValue.TITLE_WAIT,
							MessageValue.SAVING_ATTACH, false);
				}

				break;
			case NoteUtil.REQUEST_CODE_PICTURESTORE:
				String fileName = data
						.getStringExtra("ConstantValue.CURRENT_SAVE_FILE");
				File picFile = FileManager.getFileInstance(dirPath, fileName);

				currentFiles.add(picFile);

				break;
			case NoteUtil.REQUST_CODE_AUDIO:
				List<String> audioNames = (List<String>) data
						.getSerializableExtra(ConstantValue.FILE_PATH_COLLECTION);
				if (audioNames != null) {
					for (String name : audioNames) {
						File audioFile = FileManager.getFileInstance(dirPath,
								name);
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
			case NoteUtil.REQUEST_CODE_PIC_EDIT:
				String picFileName = data
						.getStringExtra("ConstantValue.CURRENT_SAVE_FILE");
				File camFile = FileManager
						.getFileInstance(dirPath, picFileName);
				currentFiles.add(camFile);
				break;
			default:
				break;
			}
		} catch (Throwable e) {
			Log.e(TAG, "onActivityResult exception", e);
		}

	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		Log.d(TAG, "finish");
		try {
			if (bitmapReceiver != null) {
				unregisterReceiver(bitmapReceiver);
				bitmapReceiver = null;
			}
			if (!hasSaved) {
				showAlertDialog(0, MessageValue.TITLE_HINT,
						MessageValue.CONFIRM,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									dialog.dismiss();
									MainNote.super.finish();
									File curFile = new File(dirPath);
									curFile.delete();

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
							public void onClick(DialogInterface dialog,
									int which) {
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
			if (bitmapReceiver != null) {
				unregisterReceiver(bitmapReceiver);
				bitmapReceiver = null;
			}
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

	protected class BitmapProcessReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				dismissProgress();
				if (intent.hasExtra(ConstantValue.RESULT_ERR)) {
					Toast.makeText(MainNote.this,
							MessageValue.ADD_ATTACH_FAILED, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(MainNote.this, MessageValue.ADD_ATTACH_SUC,
							Toast.LENGTH_LONG).show();
					String bitmapFilePath = null;
					bitmapFilePath = dirPath
							+ intent.getStringExtra(ConstantValue.BITMAP_FILE_PATH);

					Intent it = new Intent();
					it.setClass(MainNote.this, CameraImgActivity.class);
					it.putExtra(ConstantValue.CAMERA_BIT_TMP_PATH,
							bitmapFilePath);
					it.putExtra(ConstantValue.DIR_PATH, dirPath);
					startActivityForResult(it, NoteUtil.REQUEST_CODE_PIC_EDIT);
					// filePath = bitmapFilePath;
					// Log.d(TAG, filePath);
					// fileName = filePath.substring(
					// filePath.lastIndexOf("/") + 1, filePath.length());
					// bitmapFile = new File(bitmapFilePath);
					// bitmap = BitmapFactory.decodeFile(bitmapFilePath);
					// siv.setImageBitmap(bitmap);
					// state = RESULT_OK;
					// btnBar.setVisibility(View.VISIBLE);
					// isOpen = false;

				}
			} catch (Throwable e) {
				Log.e(TAG, "on receive exception", e);
			}
		}

	}

}
