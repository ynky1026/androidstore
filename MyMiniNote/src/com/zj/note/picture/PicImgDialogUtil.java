package com.zj.note.picture;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.zj.note.DialogInterface;
import com.zj.note.DialogUtil;

public class PicImgDialogUtil extends DialogUtil {

    public PicImgDialogUtil(DialogInterface dialogInterface) {
        super(dialogInterface);
    }



    @Override
    protected void showProgress(Context context, String title, String msg,
        boolean isCanCancle) {
        super.showProgress(context, title, msg, isCanCancle);
    }



    @Override
    protected void dismissProgress() {
        super.dismissProgress();
    }



    @Override
    protected void showAlertDialog(Context context, int iconId, String title,
        String positiveButtonText, OnClickListener positiveListener,
        String negativeButtonText, OnClickListener negativeListener, String msg) {
        super.showAlertDialog(context, iconId, title, positiveButtonText,
            positiveListener, negativeButtonText, negativeListener, msg);
    }
}
