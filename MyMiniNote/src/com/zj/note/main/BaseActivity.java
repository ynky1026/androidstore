package com.zj.note.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.note.R;

/**
 * @author zj 公共activity
 * 
 */
public abstract class BaseActivity extends Activity {

	/**
	 * 等待提示框
	 */
	private ProgressDialog progressDialog = null;

	/**
	 * 提示框左侧按钮事件监听器
	 */
	private DialogInterface.OnClickListener mPositiveListener = null;

	/**
	 * 提示框右侧按钮事件监听器
	 */
	private DialogInterface.OnClickListener mNegativeListener = null;

	/**
	 * 显示等待提示框
	 * 
	 * @param title
	 *            标题内容
	 * @param msg
	 *            消息内容
	 * @param cancelabel
	 *            是否可取消
	 */
	public void showProgressDialog(Context context, String title, String msg,
			boolean cancelabel) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(cancelabel);
		progressDialog.show();
		progressDialog.getWindow().setContentView(R.layout.n_process_dialog);

		// 标题
		TextView dialogTitle = (TextView) progressDialog
				.findViewById(R.id.title);
		dialogTitle.setText(title);

		// 内容
		TextView dialogBody = (TextView) progressDialog
				.findViewById(R.id.text_body);
		dialogBody.setText(msg);
		// 图片旋转动画
		Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.n_progressbar_anim);
		anim.setDuration(1000);
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		anim.setInterpolator(linearInterpolator);
		anim.setRepeatMode(RotateAnimation.RESTART);
		anim.setRepeatCount(-1);

		// 等待的图片
		ImageView image = (ImageView) progressDialog
				.findViewById(R.id.progress_icon);
		image.setImageResource(R.drawable.n_load);
		image.setAnimation(anim);
	}

	public void dismissProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	
	public void showAlertDialog(int iconId, String title,
			String positiveButtonText,
			DialogInterface.OnClickListener positiveListener,
			String negativeButtonText,
			DialogInterface.OnClickListener negativeListener, String msg) {

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.show();
		alertDialog.setContentView(R.layout.n_alertdialog);

		mPositiveListener = positiveListener;
		mNegativeListener = negativeListener;

		// 标题
		TextView titleView = (TextView) alertDialog.findViewById(R.id.title);
		// 内容
		TextView body = (TextView) alertDialog.findViewById(R.id.text_body);
		// 左按钮
		Button leftButton = (Button) alertDialog.findViewById(R.id.left_button);
		leftButton.setVisibility(View.GONE);
		// 右按钮
		Button rightButton = (Button) alertDialog
				.findViewById(R.id.right_button);
		rightButton.setVisibility(View.GONE);
		// 设置标题
		titleView.setText(title);
		// 设置内容
		if(msg!=null&&msg!=""){
			body.setText(msg);
		}

		// 设置左按钮文字
		leftButton.setText(positiveButtonText);
		if (negativeButtonText != null && negativeButtonText != "") {
			// 设置右按钮文字
			rightButton.setText(negativeButtonText);
		}

		if (positiveListener != null) {
			leftButton.setVisibility(View.VISIBLE);
			leftButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPositiveListener != null) {
						mPositiveListener.onClick(alertDialog, 0);
					}
				}
			});

			if (negativeListener != null) {
				rightButton.setVisibility(View.VISIBLE);
				rightButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mNegativeListener != null) {
							mNegativeListener.onClick(alertDialog, 1);
						}
					}
				});
			}
		}
	}

	/**
	 * 显示提示框
	 * 
	 * @param iconId
	 *            图标ID
	 * @param titleId
	 *            标题ID
	 * @param positiveButtonTextId
	 *            第一按钮的文字ID
	 * @param positiveListener
	 *            第一按钮的点击事件
	 * @param msgId
	 *            消息ID
	 * @param formatArgs
	 *            消息参数
	 */
	public void showAlertDialog(int iconId, int titleId,
			int positiveButtonTextId,
			DialogInterface.OnClickListener positiveListener, int msgId,
			Object... formatArgs) {

		showAlertDialog(iconId, titleId, positiveButtonTextId,
				positiveListener, -1, null, msgId, formatArgs);
	}

}
