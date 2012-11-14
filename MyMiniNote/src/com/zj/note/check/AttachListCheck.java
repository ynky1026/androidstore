package com.zj.note.check;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.zj.note.ConstantValue;
import com.zj.note.FileComparator;
import com.zj.note.MessageValue;
import com.zj.note.R;
import com.zj.note.adapter.AttachListAdapter;
import com.zj.note.audio.RecordActivity;
import com.zj.note.manager.FileManager;

/**
 * 附件查看界面 simple introduction
 * 
 * <p>
 * detailed comment
 * 
 * @author zhoujian 2012-6-14
 * @see
 * @since 1.0
 */
public class AttachListCheck extends Activity {

    /**
     * TAG
     */
    private static final String TAG = "AttachListCheck";

    /**
     * 附件所在目录的路径
     */
    private String dirPath;

    /**
     * 数据适配器
     */
    private AttachListAdapter adapter;

    /**
     * 展示附件的listview对象
     */
    private ListView lv;

    /**
     * 附件文件集合
     */
    private List<File> list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.attach_list);

            // init();
        } catch (Throwable tr) {
            Log.d(TAG, "onCreate exception", tr);
            Toast.makeText(this, MessageValue.VIEW_INIT_FAILED,
                Toast.LENGTH_LONG).show();
        }
    }



    private void init() {
        dirPath = getIntent().getExtras().getString(ConstantValue.DIR_PATH);
        list = new ArrayList<File>();
        lv = ( ListView ) findViewById(R.id.lv);
        //取目录下的附件列表
        File[] attachs = FileManager.queryAttachByPathID(dirPath,
            new NoteFileFilter());
        if (attachs == null || attachs.length == 0) {
            Toast.makeText(this, MessageValue.NO_ATTACH, Toast.LENGTH_LONG)
                .show();
        } else {
            for (File attach : attachs) {
                list.add(attach);
            }
            Collections.sort(list, new FileComparator(
                FileComparator.FILE_CREATE_TIME, FileComparator.ORDER_ASC));
        }

        adapter = new AttachListAdapter(this, list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView< ? > parent, View view,
                int position, long id) {
                File selectFile = list.get(position);
                String fileName = selectFile.getName();
                if (!fileName.substring(0, 1).toLowerCase()
                    .equals(ConstantValue.RECORD_NAME)) {
                    Intent it = new Intent(AttachListCheck.this,
                        ImgAttachDetailCheck.class);
                    it.putExtra(ConstantValue.FILE_NAME, fileName);
                    it.putExtra(ConstantValue.DIR_PATH, dirPath);
                    startActivity(it);
                } else {
                    Intent it = new Intent(AttachListCheck.this,
                        RecordActivity.class);
                    it.putExtra(ConstantValue.IS_PLAY_INTENT, true);
                    it.putExtra(ConstantValue.FILE_NAME, fileName);
                    it.putExtra(ConstantValue.DIR_PATH, dirPath);
                    Log.d(TAG, dirPath);
                    startActivity(it);
                }

            }

        });
    }

    private class NoteFileFilter implements FileFilter {

        @Override
        public boolean accept(File paramFile) {
            return !paramFile.getName()
                .endsWith(ConstantValue.FILE_EX_NAME_TXT);
        }

    }



    @Override
    protected void onResume() {
        try {
            super.onResume();
            init();
        } catch (Throwable tr) {
            Log.d(TAG, "onResume exception", tr);
        }
    }

}
