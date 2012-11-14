package com.zj.note.check;

import android.content.Context;

import com.zj.note.DialogInterface;
import com.zj.note.DialogUtil;

public class AttachDialogUtil extends DialogUtil {

    public AttachDialogUtil(DialogInterface dialogInterface) {
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

}
