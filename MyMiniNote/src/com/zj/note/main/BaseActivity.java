package com.zj.note.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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
//        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(cancelabel);
        progressDialog.show();
        progressDialog.getWindow().setContentView(R.layout.n_process_dialog);

        // 标题
        TextView dialogTitle = ( TextView ) progressDialog
            .findViewById(R.id.title);
        dialogTitle.setText(title);

        // 内容
        TextView dialogBody = ( TextView ) progressDialog
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
        ImageView image = ( ImageView ) progressDialog
            .findViewById(R.id.progress_icon);
        image.setImageResource(R.drawable.n_load);
        image.setAnimation(anim);
    }

}
