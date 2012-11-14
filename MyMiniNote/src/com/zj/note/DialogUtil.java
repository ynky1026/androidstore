package com.zj.note;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class DialogUtil {

    private static ProgressDialog progressDialog;

    private static DialogInterface mDialogInterface;



    public DialogUtil(DialogInterface dialogInterface) {
        mDialogInterface = dialogInterface;
    }



    /**
     * 显示进度条
     * 
     * @param context
     * @param title
     * @param msg
     * @param isCanCancle
     */
    protected void showProgress(Context context, String title, String msg,
        boolean isCanCancle) {
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog = mDialogInterface.setContentLayout(progressDialog);
        mDialogInterface.setIsCanCancle(progressDialog, isCanCancle);
        mDialogInterface.setTitle(progressDialog, title);
        mDialogInterface.setMsg(progressDialog, msg);
        mDialogInterface.setAnimation(progressDialog, context);
    }



    protected void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }



    protected void showAlertDialog(Context context, int iconId, String title,
        String positiveButtonText, OnClickListener positiveListener,
        String negativeButtonText, OnClickListener negativeListener, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog = mDialogInterface.setContentLayout(alertDialog);
        mDialogInterface.setIcon(alertDialog, iconId);
        mDialogInterface.setTitle(alertDialog, title);
        mDialogInterface.setMsg(alertDialog, msg);
        mDialogInterface.setPositiveButton(alertDialog, positiveButtonText,
            positiveListener);
        mDialogInterface.setNegativeButton(alertDialog, negativeButtonText,
            negativeListener);
    }
}
