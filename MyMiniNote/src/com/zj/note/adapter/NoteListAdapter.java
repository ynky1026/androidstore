package com.zj.note.adapter;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.note.CommonUtil;
import com.zj.note.ConstantValue;
import com.zj.note.MessageValue;
import com.zj.note.NoteUtil;
import com.zj.note.R;
import com.zj.note.Session;
import com.zj.note.manager.BitmapManager;
import com.zj.note.manager.FileManager;

public class NoteListAdapter extends BaseAdapter {

    private static final String TAG = "NoteListAdapter";

    private List<File> mList;

    private Context mContext;

    private AdapterDialogUtil mDialogUtil;



    public NoteListAdapter(Context context, List<File> list) {
        mList = list;
        mContext = context;

        mDialogUtil = new AdapterDialogUtil(NoteUtil.mDialogInterface);
    }



    @Override
    public int getCount() {
        return mList.size();
    }



    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }



    @Override
    public long getItemId(int position) {
        return position;
    }



    /**
     * 将计时器的秒数格式化为分钟：秒数格式的字符串
     * 
     * @param secNum
     * @return
     */
    private String formatSecNum(int secNum) {
        // 分钟数
        int minNum = secNum / 60;
        // 去掉分钟后余下的秒数
        int leftSec = secNum % 60;

        int tenMinVal = minNum / 10;
        int minVal = minNum % 10;

        int tenSecVal = leftSec / 10;
        int secVal = leftSec % 10;

        StringBuffer str = new StringBuffer(String.valueOf(tenMinVal));
        str.append(minVal).append(":").append(tenSecVal).append(secVal);
        return str.toString();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            LayoutInflater inflater = ( LayoutInflater ) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.note_list_item, null);
            ImageView iv = ( ImageView ) convertView.findViewById(R.id.iv);
            iv.setImageResource(R.drawable.list_icon_folder);

            File currentFile = mList.get(position);
            TextView nameText = ( TextView ) convertView
                .findViewById(R.id.name);
            TextView dateText = ( TextView ) convertView
                .findViewById(R.id.date);
            if (currentFile.isDirectory()) {
                File[] txtFiles = currentFile.listFiles(new NoteFileFilter());
                if (txtFiles != null && txtFiles.length != 0) {
                    File noteFile = txtFiles[0];
                    String noteName = noteFile.getName();

                    nameText.setText(noteName.substring(0,
                        noteName.indexOf(ConstantValue.FILE_EX_NAME_TXT)));

                    long time = noteFile.lastModified();
                    Date date = new Date(time);
                    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
                        Locale.CHINA);

                    String dateStr = df.format(date);
                    dateText.setText(dateStr);
                }

            } else {
                String fileName = currentFile.getName();
                long fileLength = currentFile.length();
                String filePath = currentFile.getAbsolutePath();

                if (FileManager.isPNGFile(fileName)
                    || FileManager.isJPEGFile(fileName)) {// 附件为图片时
                    Bitmap fileBitmap = BitmapFactory.decodeFile(currentFile
                        .getAbsolutePath());
                    // 生成缩略图的bitmap
                    Bitmap bm = BitmapManager.compressBitmap(currentFile,
                        CommonUtil.dip2px(mContext,
                            ConstantValue.DIP_LIST_IMG_WIDTH), CommonUtil.dip2px(
                            mContext, ConstantValue.DIP_LIST_IMG_HEIGHT));
                    iv.setImageBitmap(bm);

                    nameText.setText(fileBitmap.getWidth() + "*"
                        + fileBitmap.getHeight());
                } else {
                    int time = ( Integer ) Session.get(filePath);
                    iv.setImageResource(R.drawable.icon_audio);
                    String timeLength = formatSecNum(time);
                    nameText.setText(timeLength);
                }
                dateText.setText(String.valueOf(fileLength / 1024)
                    + ConstantValue.FILE_SIZE);
            }

            ImageButton deleteBtn = ( ImageButton ) convertView
                .findViewById(R.id.delbtn);
            deleteBtn.setOnClickListener(new OnListButtonClick(position));

        } catch (Throwable e) {
            Log.e(TAG, "get view exception", e);
        }
        return convertView;
    }

    private class NoteFileFilter implements FileFilter {

        @Override
        public boolean accept(File paramFile) {
            return paramFile.getName().endsWith(ConstantValue.FILE_EX_NAME_TXT);
        }

    }



    private void removeItem(int position) {
        File delFile = mList.get(position);
        if (delFile.isDirectory()) {
            FileManager.deleteDir(delFile);
        } else {
            delFile.delete();
        }

        mList.remove(position);
        this.notifyDataSetChanged();
    }

    private class OnListButtonClick implements OnClickListener {

        private int mPosition;



        public OnListButtonClick(int position) {
            this.mPosition = position;
        }



        @Override
        public void onClick(View v) {
            try {
                DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(mPosition);
                        dialog.dismiss();
                    }

                };
                DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                };

                mDialogUtil.showAlertDialog(mContext, -1,
                    MessageValue.TITLE_HINT, MessageValue.CONFIRM,
                    positiveListener, MessageValue.CANCLE, negativeListener,
                    MessageValue.CONFIRM_ATTACH_DEL);

            } catch (Exception e) {
                Log.e(TAG, "onClick exception", e);
            }
        }

    }

}
