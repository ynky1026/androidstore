package com.zj.note.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.zj.note.R;
import com.zj.note.widget.MyImageView;

public class GestureInputAdapter extends BaseAdapter {

    private List<Bitmap> list;

    private Context context;

    /**
     * 需要开启光标闪烁的行
     */
    private int row;

    private int imgHeight;



    public GestureInputAdapter(Context context, List<Bitmap> list, int row,
        int imgHeight) {
        this.context = context;
        this.list = list;
        this.row = row;
        this.imgHeight = imgHeight;

    }



    @Override
    public int getCount() {
        return list.size();
    }



    @Override
    public Object getItem(int position) {
        return list.get(position);
    }



    @Override
    public long getItemId(int position) {
        return position;
    }



    public void setCusorRow(int row) {
        this.row = row;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            LayoutInflater inflater = ( LayoutInflater ) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gesture_cell, null);

            MyImageView iv = ( MyImageView ) convertView.findViewById(R.id.iv);
            LinearLayout item = ( LinearLayout ) convertView
                .findViewById(R.id.item);
            item.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                imgHeight));
            Bitmap bm = list.get(position);

            if (position == row) {
                iv.setCusorStatus(true);
            } else {
                iv.setCusorStatus(false);
            }
            if (bm == null) {
                bm = Bitmap.createBitmap(1, imgHeight, Config.ARGB_4444);
            }
            iv.setImageBitmap(bm);
        } catch (Exception e) {
            Log.e("GestureInputAdapter", "get view exception", e);
        }
        Log.d("TAG", "convertView's height is " + convertView.getHeight());
        return convertView;
    }
}
