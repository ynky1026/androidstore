package com.zj.note.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.note.DialogInterface;
import com.zj.note.R;

public class MyDialog implements DialogInterface {

    @Override
    public ProgressDialog setContentLayout(ProgressDialog progressDialog) {
        progressDialog.getWindow().setContentView(R.layout.n_process_dialog);
        return progressDialog;
    }



    @Override
    public void setTitle(ProgressDialog progressDialog, String msg) {
        // 标题
        TextView dialogTitle = ( TextView ) progressDialog
            .findViewById(R.id.title);
        dialogTitle.setText(msg);
    }



    @Override
    public void setMsg(ProgressDialog progressDialog, String msg) {
        // 内容
        TextView dialogBody = ( TextView ) progressDialog
            .findViewById(R.id.text_body);
        dialogBody.setText(msg);
    }



    @Override
    public void setIsCanCancle(ProgressDialog progressDialog,
        boolean isCanCancle) {
        progressDialog.setCancelable(isCanCancle);
    }



    @Override
    public void setAnimation(ProgressDialog progressDialog, Context context) {
        // 图片旋转动画
        Animation anim = AnimationUtils.loadAnimation(context,
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



    @Override
    public AlertDialog setContentLayout(AlertDialog alertDialog) {
        alertDialog.setContentView(R.layout.n_alertdialog);
        return alertDialog;
    }



    @Override
    public void setIcon(AlertDialog dialog, int iconId) {
        
    }



    @Override
    public void setTitle(AlertDialog alertDialog, String title) {
        // 标题
        TextView titleView = ( TextView ) alertDialog.findViewById(R.id.title);
        titleView.setText(title);
    }



    @Override
    public void setPositiveButton(AlertDialog alertDialog, String text,
        OnClickListener positiveListener) {
        // 左按钮
        Button leftButton = ( Button ) alertDialog
            .findViewById(R.id.left_button);
        leftButton.setVisibility(View.GONE);
        final OnClickListener mPositiveListener = positiveListener;
        final AlertDialog mAlertDialog = alertDialog;
        // 设置左按钮文字
        leftButton.setText(text);

        if (positiveListener != null) {
            leftButton.setVisibility(View.VISIBLE);
            leftButton
                .setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPositiveListener != null) {
                            mPositiveListener.onClick(mAlertDialog, 0);
                        }
                    }
                });
        }
    }



    @Override
    public void setNegativeButton(AlertDialog alertDialog, String text,
        OnClickListener negativeListener) {
        // 左按钮
        Button rightButton = ( Button ) alertDialog
            .findViewById(R.id.right_button);
        rightButton.setVisibility(View.GONE);
        final OnClickListener mNegativeListener = negativeListener;
        final AlertDialog mAlertDialog = alertDialog;
        // 设置左按钮文字
        rightButton.setText(text);

        if (negativeListener != null) {
            rightButton.setVisibility(View.VISIBLE);
            rightButton
                .setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mNegativeListener != null) {
                            mNegativeListener.onClick(mAlertDialog, 0);
                        }
                    }
                });
        }
    }



    @Override
    public void setMsg(AlertDialog alertDialog, String msg) {
        // 内容
        TextView body = ( TextView ) alertDialog.findViewById(R.id.text_body);
        body.setText(msg);
    }

}
