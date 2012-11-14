package com.zj.note.gesture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.Gesture;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.zj.note.CommonUtil;
import com.zj.note.ConstantValue;
import com.zj.note.MessageValue;
import com.zj.note.NoteBaseActivity;
import com.zj.note.NoteUtil;
import com.zj.note.R;
import com.zj.note.Session;
import com.zj.note.adapter.GestureInputAdapter;
import com.zj.note.manager.BitmapManager;
import com.zj.note.manager.FileManager;
import com.zj.note.service.NoteService;
import com.zj.note.widget.MyGesture;
import com.zj.note.widget.MyGestureView;
import com.zj.note.widget.MyGestureView.OnGesturePerformedListener;

public class GestureActivity extends NoteBaseActivity implements
    OnGesturePerformedListener {

    /**
     * TAG
     */
    private static final String TAG = "GestureActivity";

    /**
     * 自定义手写控件
     */
    private MyGestureView gv;

    /**
     * 手写笔记列表
     */
    private ListView lv;

    /**
     * 屏幕宽
     */
    private int screenWidth;

    /**
     * 屏幕高
     */
    private int screenHeight;

    /**
     * 每个文字的bitmap的宽
     */
    private int bitmapWidth;

    /**
     * 每个文字bitmap的高
     */
    private int bitmapHeight;

    /**
     * 一行显示多少个bitmap
     */
    private int numBitmapCol = ConstantValue.GESTURE_COL_NUM;

    /**
     * 默认显示多少行列表
     */
    private int numBitmapRow = ConstantValue.GESTURE_ROW_NUM;

    /**
     * 当前正在编辑哪一列
     */
    private int currentEditCol = 0;

    /**
     * 当前正在编辑哪一行
     */
    private int currentEditRow;

    private GestureInputAdapter adapter;

    private List<Bitmap> list;

    private MyGesture mg;

    /**
     * 当前页数
     */
    private int currentPage = 1;

    /**
     * 是否执行翻页
     */
    private boolean flag;

    /**
     * 回车按钮
     */
    private ImageButton enterBtn;

    /**
     * 空格按钮
     */
    private ImageButton spaceBtn;

    /**
     * 删除按钮
     */
    private ImageButton deleteBtn;

    /**
     * 保存按钮
     */
    private ImageButton saveBtn;

    /**
     * 共保存了多少个bitmap
     */
    private int saveBitmapNum;

    // /**
    // * 合并后的总位图
    // */
    // private Bitmap allBitmap;

    /**
     * 附近保存目录
     */
    private String dirPath;

    /**
     * IO处理类对象
     */
    private FileManager fileManager;

    /**
     * 保存的文件名
     */
    private String fileName;

    /**
     * 弹出窗工具
     */
    private GestureDialogUtil mDialogUtil;

    /**
     * 保存手写笔记广播接收器
     */
    private GestureSaveBroadcastReceiver receiver;

    /**
     * 手写输入的字数
     */
    private int count = 0;

    /**
     * Handler对象
     */
    private Handler hd;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.gesture_input);
            init();
        } catch (Exception e) {
            Log.e(TAG, "onCreate exception", e);
            Toast.makeText(this, "画面初始化失败", Toast.LENGTH_LONG).show();
        }
    }



    /**
     * 初始化方法
     */
    private void init() {
        dirPath = getIntent().getStringExtra(ConstantValue.DIR_PATH);
        fileManager = new FileManager(dirPath);
        lv = ( ListView ) findViewById(R.id.lv);
        // lv.setFocusable(false);
        // lv.setFocusableInTouchMode(false);
        lv.setEnabled(false);
        lv.setDividerHeight(0);
        // lv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 660));
        gv = ( MyGestureView ) findViewById(R.id.gestures);
        gv.addOnGesturePerformedListener(this);

        enterBtn = ( ImageButton ) findViewById(R.id.enter);
        spaceBtn = ( ImageButton ) findViewById(R.id.space);
        deleteBtn = ( ImageButton ) findViewById(R.id.delete);
        saveBtn = ( ImageButton ) findViewById(R.id.save);

        enterBtn.setOnClickListener(listener);
        spaceBtn.setOnClickListener(listener);
        deleteBtn.setOnClickListener(listener);
        saveBtn.setOnClickListener(listener);

        mg = new MyGesture();
        gv.setGesture(mg);
        // 取屏幕宽高
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        Log.d(TAG, "screenHeight is " + screenHeight);

        fileName = ConstantValue.GESTURE_NAME + System.currentTimeMillis()
            + ConstantValue.FILE_EX_NAME_PNG;

        mDialogUtil = new GestureDialogUtil(NoteUtil.mDialogInterface);

        receiver = new GestureSaveBroadcastReceiver();
        IntentFilter ift = new IntentFilter(ConstantValue.GESTURE_SAVE_RESULT);
        registerReceiver(receiver, ift);

        hd = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // onDraw执行后可以正确取出列表的高
                bitmapWidth = (screenWidth - CommonUtil.dip2px(
                    GestureActivity.this, ConstantValue.PAD_COL))
                    / numBitmapCol;
                bitmapHeight = gv.getAbsHeight() / numBitmapRow;
                list = new LinkedList<Bitmap>();
                for (int i = 0; i < numBitmapRow; i++) {
                    list.add(null);
                }
                Log.d(TAG, "bitmapHeight is " + bitmapHeight);
                adapter = new GestureInputAdapter(GestureActivity.this, list,
                    currentEditRow, bitmapHeight);
                lv.setAdapter(adapter);
                Log.d(TAG, "listview's height is " + lv.getHeight());

            };
        };
        gv.setHandler(hd);
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                if (v.equals(enterBtn)) {
                    if (gv.isFadingHasStarted()) {
                        return;
                    }
                    currentEditRow++;
                    currentEditCol = 0;
                    adapter.setCusorRow(currentEditRow);
                    if (currentEditRow > (numBitmapRow * (currentPage) - 1)) {
                        setPageInitData();
                        lv.setSelection(numBitmapRow * (currentPage - 1));
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else if (v.equals(spaceBtn)) {
                    if (gv.isFadingHasStarted()) {
                        return;
                    }
                    Bitmap newBitmap = mergeBitmap(null);
                    list.remove(currentEditRow);
                    list.add(currentEditRow, newBitmap);
                    // adapter.setCusorRow(currentEditRow);
                    adapter.notifyDataSetChanged();
                } else if (v.equals(deleteBtn)) {
                    delete();
                } else if (v.equals(saveBtn)) {
                    saveBitmap();
                }
            } catch (Throwable e) {
                Log.e(TAG, "on click exception", e);
                Toast.makeText(GestureActivity.this,
                    MessageValue.CLICK_EVENT_HANDLE_FAILED, Toast.LENGTH_LONG)
                    .show();
            }
        }
    };



    /**
     * 保存bitmap 合并所有bitmap 列表适配器中保存每行文字的位图 将每行背景和文字的位图画在一起是每行的位图
     * 合并所有行的位图生成总的位图保存
     */
    private void saveBitmap() {
        if (list != null && list.size() > 0) {
            // 行背景
            Bitmap background = Bitmap.createBitmap(screenWidth, bitmapHeight,
                Config.ARGB_4444);
            // 取背景的.9图片
            NinePatchDrawable backNinePatch = ( NinePatchDrawable ) getResources()
                .getDrawable(R.drawable.bg);
            // 设置矩形区域为屏幕长 每行位图高（即一行的区域）
            Rect rect = new Rect();
            rect.left = 0;
            rect.right = screenWidth;
            rect.top = 0;
            rect.bottom = bitmapHeight;
            // 拉伸.9图片到一行的宽高
            backNinePatch.setBounds(rect);
            // 将.9图片画到背景上
            Canvas backCanvas = new Canvas(background);
            backNinePatch.draw(backCanvas);
            Intent it = new Intent(this, NoteService.class);
            Session.put(ConstantValue.GESTURE_LIST, list);
            Session.put(ConstantValue.BACKGROUD_BITMAP, background);

            it.putExtra(ConstantValue.FILE_MANAGER, fileManager);
            it.putExtra(ConstantValue.SCREEN_WIDTH, screenWidth);
            it.putExtra(ConstantValue.BITMAP_HEIGHT, bitmapHeight);
            it.putExtra(ConstantValue.SAVE_BM_NUM, saveBitmapNum);
            it.putExtra(ConstantValue.CURRENT_PAGE, currentPage);
            it.putExtra(ConstantValue.NUM_ROW, numBitmapRow);
            it.putExtra(ConstantValue.FILE_NAME, fileName);

            it.putExtra(ConstantValue.GESTURE_SAVE, "");
            startService(it);
            // fileManager.saveBitmap2PNG(allBitmap, fileName);

            mDialogUtil.showProgress(GestureActivity.this,
                MessageValue.TITLE_WAIT, MessageValue.SAVING, false);
        } else {
            Toast.makeText(this, MessageValue.NO_GESTURE, 1000).show();
        }
    }



    /**
     * 翻页是初始化适配器中新添加的数据
     */
    private void setPageInitData() {
        for (int i = 0; i < numBitmapRow; i++) {
            list.add(null);
        }
        adapter.notifyDataSetChanged();
        currentPage++;
    }



    /**
     * 删除方法
     */
    private void delete() {
        // 如果在手势线程未执行时点击删除 则取消线程执行 及是删除了刚写这个字
        if (gv.isFadingHasStarted()) {
            gv.cancleGesture();
            return;
        }
        // 取当前编辑行的位图对象 如果是空且不是第一行时应删除上一行 第一行的话则直接中止
        Bitmap oldBm = list.get(currentEditRow);
        if (oldBm == null) {
            if (currentEditRow == 0) {
                return;
            }
            currentEditRow--;

            // 当前编辑行非第一行且位图为空时 取上一行位图对象
            Bitmap preRowBitmap = list.get(currentEditRow);
            // 如果上一行对象也为空 将当期编辑行置为此行 切光标移动到行首
            if (preRowBitmap == null) {
                currentEditCol = 0;
                adapter.setCusorRow(currentEditRow);
                adapter.notifyDataSetChanged();
                // 当前行为页首时翻向上一页
                // Note setSelection()方法放在notifyDataSetChanged后才能得到正常结果
                if (currentEditRow + 1 == (currentPage - 1) * numBitmapRow) {
                    currentPage--;
                    lv.setSelection((currentPage - 1) * numBitmapRow);
                }
                return;
            }
            // 如上一行非空 递归删除此行最后一个字
            delete();
            // 当前行为页首时翻向上一页
            // Note setSelection()方法放在notifyDataSetChanged后才能得到正常结果
            if (currentEditRow + 1 == (currentPage - 1) * numBitmapRow) {
                currentPage--;
                lv.setSelection((currentPage - 1) * numBitmapRow);
            }
            return;
        }
        // 开始删除
        // 删除后新位图对象引用
        Bitmap newBitmap;
        // 老位图的宽度除以单个位图宽为当前编辑的列
        int oldWidtt = oldBm.getWidth();
        currentEditCol = oldWidtt / bitmapWidth;
        // 新位图宽为老位图宽减掉单个位图宽
        int newWidth = oldWidtt - bitmapWidth;
        // 如果计算得新位图宽为0则此方法删除了本行最后一个字 本行将显示空位图
        if (newWidth == 0) {
            newBitmap = null;
        } else {
            // 建立新位图 他是老位图从0,0像素开始 水平方向截取新位图长 垂直方向截取单个位图高
            newBitmap = Bitmap
                .createBitmap(oldBm, 0, 0, newWidth, bitmapHeight);
        }
        currentEditCol--;
        count--;
        Log.d("gesturedemo", "currentEditCol is " + currentEditCol);
        // 更新适配器数据 并刷新列表 完成删除操作
        list.remove(currentEditRow);
        list.add(currentEditRow, newBitmap);
        adapter.setCusorRow(currentEditRow);
        adapter.notifyDataSetChanged();
    }



    @Override
    public void onGesturePerformed(MyGestureView overlay, Gesture gesture) {
        try {
            if (count > 150) {
                Toast.makeText(this, MessageValue.GESTURE_TOOLONG, 1000).show();
                return;
            }
            ArrayList<GestureStroke> strokes = gesture.getStrokes();
            Log.d("gesture", "strokes's size is " + strokes.size());
            Bitmap mp;
            // 将手写转成位图
            mp = mg.toBitmap(bitmapWidth, bitmapHeight, gv.getWidth(),
                gv.getHeight(), 0, Color.BLACK, strokes);
            Bitmap newBitmap = mergeBitmap(mp);
            // 编辑列表
            list.remove(currentEditRow);
            list.add(currentEditRow, newBitmap);
            // adapter.setCusorRow(currentEditRow);
            adapter.notifyDataSetChanged();
            if (flag) {
                lv.setSelection(numBitmapRow * (currentPage - 1));
            }
            count++;
        } catch (Exception e) {
            Log.e(TAG, "onGesturePerformed exception", e);
        }
    }



    /**
     * 将新生成的位图添加一行的位图中
     * 
     * @param bm
     * @return
     */
    private Bitmap mergeBitmap(Bitmap bm) {
        // 如果当前编辑行已经写满则从下一行第一列开始
        if (currentEditCol + 1 > numBitmapCol) {
            currentEditRow++;
            currentEditCol = 0;
        }
        // 如果当前编辑行已经大于一页最多显示的行数 则翻页
        if (currentEditRow > (numBitmapRow * (currentPage) - 1)) {
            setPageInitData();
            // lv.setSelection(numBitmapRow * (currentPage - 1) - 1);
            flag = true;
        } else {
            flag = false;
        }

        if (bm == null) {
            bm = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
                Config.ARGB_4444);
        }

        // 取当前编辑行的位图对象
        Bitmap b1 = list.get(currentEditRow);
        Bitmap newBitmap;
        // 当前行位图对象为空时 本行位图将是bm
        if (b1 == null) {
            newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
                Config.ARGB_4444);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(bm, 0, 0, null);
        } else {
            newBitmap = BitmapManager.mergeBitmapHorizontal(b1, bm);
        }

        currentEditCol++;

        if (currentEditCol == numBitmapCol) {
            adapter.setCusorRow(currentEditRow + 1);
        } else {
            adapter.setCusorRow(currentEditRow);
        }

        Log.d("gesturedemo", "currentEditCol is " + currentEditCol);
        return newBitmap;
    }

    private class GestureSaveBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, "receive");
                mDialogUtil.dismissProgress();
                if (intent.hasExtra("RESULT_ERR")) {
                    Toast.makeText(GestureActivity.this,
                        MessageValue.SAVE_FAILED, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GestureActivity.this, MessageValue.SAVE_SUC,
                        Toast.LENGTH_LONG).show();
                }

            } catch (Throwable e) {
                Log.e(TAG, "on receive exception", e);
            }
        }

    }



    @Override
    public void finish() {
        try {
            mDialogUtil.showAlertDialog(this, 0, MessageValue.TITLE_HINT,
                MessageValue.YES, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dialog.dismiss();
                            onFinish();
                        } catch (Exception e) {
                            Log.e(TAG, "on click exception", e);
                            Toast.makeText(GestureActivity.this,
                                MessageValue.CLICK_EVENT_HANDLE_FAILED,
                                Toast.LENGTH_LONG).show();
                        }
                    }
                }, MessageValue.NO, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dialog.dismiss();
                        } catch (Throwable e) {
                            Log.e(TAG, "on Click exception", e);
                            Toast.makeText(GestureActivity.this,
                                MessageValue.CLICK_EVENT_HANDLE_FAILED,
                                Toast.LENGTH_LONG).show();
                        }
                    }
                }, MessageValue.EXIT_GESTURE);
        } catch (Throwable e) {
            Log.e(TAG, "finish exception", e);
        }

    }



    private void onFinish() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        Intent i = new Intent();

        Bundle b = new Bundle();
        b.putString(ConstantValue.GESTURE_FILE_PATH, fileName);
//        if (dirPath.startsWith(ConstantValue.SD_DIR_PATH)) {
//            b.putString(ConstantValue.GESTURE_FILE_PATH, dirPath + fileName);
//        } else {
//            b.putString(ConstantValue.GESTURE_FILE_PATH,
//                ConstantValue.SD_DIR_PATH + dirPath + fileName);
//        }

        Log.d(TAG, ConstantValue.SD_DIR_PATH + dirPath + fileName);
        i.putExtras(b);

        this.setResult(RESULT_OK, i);
        super.finish();
    }
}