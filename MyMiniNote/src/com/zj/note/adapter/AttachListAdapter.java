package com.zj.note.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.zj.note.R;
import com.zj.note.check.AttachListCheck;
import com.zj.note.manager.BitmapManager;
import com.zj.note.manager.FileManager;

public class AttachListAdapter extends BaseAdapter {

    private static final String TAG = "AttachListAdapter";

    private List<File> mList;

    private Context mContext;


    private SharedPreferences sp;



    public AttachListAdapter(Context context, List<File> list) {
        mList = list;
        mContext = context;


        sp = mContext.getSharedPreferences(ConstantValue.RECORD_FILE_TIME,
            Context.MODE_PRIVATE);
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
            convertView = inflater.inflate(R.layout.attach_item, null);
            ImageView iv = ( ImageView ) convertView.findViewById(R.id.iv);
            TextView length = ( TextView ) convertView
                .findViewById(R.id.length);
            TextView size = ( TextView ) convertView.findViewById(R.id.size);
            ImageButton deleteBtn = ( ImageButton ) convertView
                .findViewById(R.id.delbtn);

            File file = mList.get(position);
            String fileName = file.getName();
            long fileLength = file.length();
            String filePath = file.getAbsolutePath();

            if (FileManager.isPNGFile(fileName)
                || FileManager.isJPEGFile(fileName)) {// 附件为图片时
                Bitmap fileBitmap = BitmapFactory.decodeFile(file
                    .getAbsolutePath());
                // 生成缩略图的bitmap
                Bitmap bm = BitmapManager.compressBitmap(fileBitmap,
                    CommonUtil.dip2px(mContext, ConstantValue.DIP_LIST_IMG_WIDTH),
                    CommonUtil.dip2px(mContext, ConstantValue.DIP_LIST_IMG_HEIGHT));
                iv.setImageBitmap(bm);
                // 显示图片的宽*高
                size.setText(fileBitmap.getWidth() + "*"
                    + fileBitmap.getHeight());
            } else {
                int time = sp.getInt(filePath, 0);
                Log.d(TAG, filePath);
                iv.setImageResource(R.drawable.icon_audio);
                String timeLength = formatSecNum(time);
                size.setText(timeLength);
            }
            length.setText(String.valueOf(fileLength / 1024)
                + ConstantValue.FILE_SIZE);

            deleteBtn.setOnClickListener(new OnListButtonClick(position));
        } catch (Throwable e) {
            Log.e(TAG, "get view exception", e);
        }

        return convertView;
    }



    private void removeItem(int position) {
        mList.get(position).delete();
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

                ((AttachListCheck)mContext).showAlertDialog( -1,
                    MessageValue.TITLE_HINT, MessageValue.CONFIRM,
                    positiveListener, MessageValue.CANCLE, negativeListener,
                    MessageValue.CONFIRM_ATTACH_DEL);

            } catch (Exception e) {
                Log.e(TAG, "onClick exception", e);
            }
        }

    }

}
