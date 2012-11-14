package com.zj.note;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public interface DialogInterface {

    public ProgressDialog setContentLayout(ProgressDialog dialog);



    public void setTitle(ProgressDialog dialog, String msg);



    public void setMsg(ProgressDialog dialog, String msg);



    public void setIsCanCancle(ProgressDialog dialog, boolean isCanCancle);



    public void setAnimation(ProgressDialog dialog, Context context);



    public AlertDialog setContentLayout(AlertDialog dialog);



    public void setIcon(AlertDialog dialog, int iconId);



    public void setTitle(AlertDialog dialog, String title);



    public void setPositiveButton(AlertDialog dialog, String text,
        OnClickListener positiveListener);



    public void setNegativeButton(AlertDialog dialog, String text,
        OnClickListener negativeListener);



    public void setMsg(AlertDialog dialog, String msg);
}
