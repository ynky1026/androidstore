package com.zj.note.adapter;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.zj.note.DialogInterface;
import com.zj.note.DialogUtil;

public class AdapterDialogUtil extends DialogUtil{

    public AdapterDialogUtil(DialogInterface dialogInterface) {
        super(dialogInterface);
    }
    
    @Override
    protected void showAlertDialog(Context context, int iconId, String title,
        String positiveButtonText, OnClickListener positiveListener,
        String negativeButtonText, OnClickListener negativeListener, String msg) {
        super.showAlertDialog(context, iconId, title, positiveButtonText,
            positiveListener, negativeButtonText, negativeListener, msg);
    }

}
