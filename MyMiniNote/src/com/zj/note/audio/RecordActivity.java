package com.zj.note.audio;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.zj.note.ConstantValue;
import com.zj.note.MessageValue;
import com.zj.note.NoteBaseActivity;
import com.zj.note.R;
import com.zj.note.manager.FileManager;

public class RecordActivity extends NoteBaseActivity {

    /**
     * TAG
     */
    private static final String TAG = "RecordActivity";

    /**
     * 当前工作的状态
     */
    private int CURRENT_WORK_STATE = STATE_FREE;

    /**
     * 空闲状态
     */
    private static final int STATE_FREE = 0;

    /**
     * 录音状态
     */
    private static final int STATE_RECORD = 1;

    /**
     * 播放状态
     */
    private static final int STATE_PLAY = 2;

    /**
     * 左轮
     */
    private ImageView leftWheel;

    /**
     * 右轮
     */
    private ImageView rightWheel;

    /**
     * 开始录音按钮
     */
    private ImageButton beginRecord;

    /**
     * 停止录音
     */
    private ImageButton stopRecord;

    /**
     * 播放录音
     */
    private ImageButton play;

    /**
     * 重新播放录音
     */
    private ImageButton resume;

    /**
     * 播放暂停
     */
    private ImageButton pause;

    /**
     * 添加录音按钮
     */
    private ImageButton add;

    /**
     * 删除录音按钮
     */
    private ImageButton del;

    /**
     * 计时结果--个位数秒
     */
    private ImageView sec;

    /**
     * 计时结果--十位数秒
     */
    private ImageView tenSec;

    /**
     * 计时结果--个位数分钟
     */
    private ImageView min;

    /**
     * 计时结果--十位数分钟
     */
    private ImageView tenMin;

    /**
     * 磁带转轮旋转动画
     */
    private Animation anim;

    /**
     * 计时器对象
     */
    private Timer timer;

    /**
     * 时间任务对象
     */
    private MyTimerTask task;

    /**
     * handler对象
     */
    private Handler handler;

    /**
     * 计时秒数
     */
    private int secNum;

    /**
     * 录音类对象
     */
    private MediaRecorder recorder;

    /**
     * 录音保存的路径
     */
    private String dirPath;

    /**
     * 保存的录音文件的路径
     */
    private String filePath;

    /**
     * 保存的录音文件的名称
     */
    private String fileName;

    /**
     * 是否做播放界面
     */
    private boolean isPlayIntent;

    /**
     * 播放器对象
     */
    private MediaPlayer player;

    /**
     * 播放进度条
     */
    private SeekBar progressBar;

    /**
     * SharedPreferences对象
     */
    private SharedPreferences sp;

    /**
     * 本次录音的文件路径集合
     */
    private List<String> currentFilePaths = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.record);

            init();
        } catch (Throwable e) {
            Log.e(TAG, "on CreateException", e);
            Toast.makeText(this, MessageValue.VIEW_INIT_FAILED,
                Toast.LENGTH_LONG).show();
        }
    }



    private void init() {
        dirPath = getIntent().getExtras().getString(ConstantValue.DIR_PATH);
        if (!dirPath.endsWith("/")) {
            dirPath = dirPath + "/";
        }
        isPlayIntent = getIntent().getBooleanExtra(
            ConstantValue.IS_PLAY_INTENT, false);

        recorder = new MediaRecorder();
        player = new MediaPlayer();
        player.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlayer();
            }
        });
        leftWheel = ( ImageView ) findViewById(R.id.wheel_left);
        rightWheel = ( ImageView ) findViewById(R.id.wheel_right);
        tenMin = ( ImageView ) findViewById(R.id.ten_min);
        min = ( ImageView ) findViewById(R.id.min);
        tenSec = ( ImageView ) findViewById(R.id.ten_sec);
        sec = ( ImageView ) findViewById(R.id.sec);

        progressBar = ( SeekBar ) findViewById(R.id.progress_bar);
        progressBar.setOnSeekBarChangeListener(seekListener);
        initAnimation();

        beginRecord = ( ImageButton ) findViewById(R.id.record);
        beginRecord.setOnClickListener(listener);

        stopRecord = ( ImageButton ) findViewById(R.id.stop_record);
        stopRecord.setOnClickListener(listener);

        play = ( ImageButton ) findViewById(R.id.play);
        play.setOnClickListener(listener);

        pause = ( ImageButton ) findViewById(R.id.pause);
        pause.setOnClickListener(listener);

        resume = ( ImageButton ) findViewById(R.id.resume);
        resume.setOnClickListener(listener);

        add = ( ImageButton ) findViewById(R.id.add);
        add.setOnClickListener(listener);

        del = ( ImageButton ) findViewById(R.id.del);
        del.setOnClickListener(listener);

        del.setEnabled(false);
        add.setEnabled(false);
        if (isPlayIntent) {
            String dirID = getIntent().getStringExtra(ConstantValue.DIR_PATH);
            String name = getIntent().getStringExtra(ConstantValue.FILE_NAME);
            File playFile = FileManager.getFileInstance(dirID, name);
            filePath = playFile.getAbsolutePath();
            initPlay();
        } else {
            initRecord();
        }
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                try {
                    switch (msg.what) {
                        case 0:
                            execTimer();
                            break;

                        default:
                            break;
                    }
                } catch (Throwable e) {
                    Log.e(TAG, "handler exception", e);
                }
            };
        };
        sp = getSharedPreferences(ConstantValue.RECORD_FILE_TIME, MODE_PRIVATE);
    }



    /**
     * 初始化播放界面
     */
    private void initPlay() {
        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);
        stopRecord.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        beginRecord.setVisibility(View.GONE);
        del.setEnabled(true);
        add.setEnabled(true);
    }



    /**
     * 初始化录音机界面
     */
    private void initRecord() {
        play.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
        beginRecord.setVisibility(View.VISIBLE);
        del.setEnabled(false);
        add.setEnabled(false);
    }

    private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            try {
                if (CURRENT_WORK_STATE == STATE_PLAY) {
                    if (!player.isPlaying()) {
                        Log.d(TAG, "is not playing");
                        resumePlayer();
                    } else {
                        resumeTimer();
                    }
                } else {
                    stopTimer();
                    resetTimer();
                }
            } catch (Exception e) {
                Log.e(TAG, "onStopTrackingTouch exception", e);
            }

        }



        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            try {
                if (CURRENT_WORK_STATE == STATE_FREE) {
                    startPlayer();
                }
                stopTimer();
            } catch (Exception e) {
                Log.e(TAG, "onStartTrackingTouch exception", e);
            }
        }



        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
            try {
                if (player != null && fromUser
                    && CURRENT_WORK_STATE == STATE_PLAY) {
                    secNum = progress;
                    execTimer();
                    if (progress >= sp.getInt(filePath, 0)) {
                        stopPlayer();
                        return;
                    }

                    player.seekTo(progress * 1000);
                } else {

                }
            } catch (Throwable e) {
                Log.e(TAG, "on progress changed exception", e);
            }
        }
    };



    /**
     * 执行计时任务 更新秒表及进度条
     */
    private void execTimer() {
        if (CURRENT_WORK_STATE != STATE_FREE) {
            Log.d(TAG, "showTime's secNum is " + secNum);
            // 分钟数
            int minNum = secNum / 60;
            // 去掉分钟后余下的秒数
            int leftSec = secNum % 60;
            // 十分钟数
            int tenMinVal = minNum / 10;
            // 去掉十分钟后余下的分钟数
            int minVal = minNum % 10;
            // 十秒数
            int tenSecVal = leftSec / 10;
            // 去掉十秒后余下的秒数
            int secVal = leftSec % 10;
            updateTime(tenMin, tenMinVal);
            updateTime(min, minVal);
            updateTime(tenSec, tenSecVal);
            updateTime(sec, secVal);
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setProgress(secNum);
            }
            Log.d(TAG, "secVal is " + secVal);
        }

    }



    /**
     * 启动计时器
     */
    private void startTimer() {
        secNum = 0;
        timer = new Timer();
        task = new MyTimerTask();
        timer.schedule(task, ConstantValue.TIMER_DELAY,
            ConstantValue.TIMER_PERIOD);
    }



    /**
     * 停止计时器
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }

    }



    /**
     * 重新开启计时器
     */
    private void resumeTimer() {
        timer = new Timer();
        task = new MyTimerTask();
        timer.schedule(task, ConstantValue.TIMER_DELAY,
            ConstantValue.TIMER_PERIOD);
    }



    /**
     * 启动进度条
     */
    private void initProgressBar() {
        progressBar.setMax(sp.getInt(filePath, 0));
        Log.d(TAG, "max is " + sp.getInt(filePath, 0));
        progressBar.setIndeterminate(false);
    }



    /**
     * 重置计时器
     */
    private void resetTimer() {
        secNum = 0;
        updateTime(tenMin, 0);
        updateTime(min, 0);
        updateTime(tenSec, 0);
        updateTime(sec, 0);
        progressBar.setProgress(0);
    }



    /**
     * 更新计时器的值
     * 
     * @param iv
     * @param val
     */
    private void updateTime(ImageView iv, int val) {
        switch (val) {
            case 0:
                iv.setImageResource(R.drawable.number_0);
                break;
            case 1:
                iv.setImageResource(R.drawable.number_1);
                break;
            case 2:
                iv.setImageResource(R.drawable.number_2);
                break;
            case 3:
                iv.setImageResource(R.drawable.number_3);
                break;
            case 4:
                iv.setImageResource(R.drawable.number_4);
                break;
            case 5:
                iv.setImageResource(R.drawable.number_5);
                break;
            case 6:
                iv.setImageResource(R.drawable.number_6);
                break;
            case 7:
                iv.setImageResource(R.drawable.number_7);
                break;
            case 8:
                iv.setImageResource(R.drawable.number_8);
                break;
            case 9:
                iv.setImageResource(R.drawable.number_9);
                break;
            default:
                throw new IllegalArgumentException("time compute error");
        }
        // iv.invalidate();
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                if (v.equals(beginRecord)) {
                    startRecord();
                } else if (v.equals(stopRecord)) {
                    stopRecord();
                } else if (v.equals(play)) {
                    startPlayer();
                } else if (v.equals(pause)) {
                    pausePlayer();
                } else if (v.equals(resume)) {
                    resumePlayer();
                } else if (v.equals(add)) {
                    addRecordFile();
                } else if (v.equals(del)) {
                    delFile();
                }
            } catch (Throwable e) {
                Log.e(TAG, "on click exception", e);
                Toast.makeText(RecordActivity.this,
                    MessageValue.CLICK_EVENT_HANDLE_FAILED, Toast.LENGTH_LONG)
                    .show();
            }
        }
    };



    /**
     * 添加录音
     */
    private void addRecordFile() {
        initRecord();
        secNum = 0;
        stopTimer();
        resetTimer();
    }



    /**
     * 删除录音文件
     */
    private void delFile() {
        File file = new File(filePath);
        if (file != null && file.exists()) {
            file.delete();
        }
        addRecordFile();
        Toast.makeText(this, MessageValue.DEL_SUC, Toast.LENGTH_LONG).show();
    }



    /**
     * 开始录音
     */
    private void startRecord() {
        Log.d(TAG, dirPath);
        CURRENT_WORK_STATE = STATE_RECORD;
        fileName = System.currentTimeMillis() + ConstantValue.FILE_EX_NAME_AMR;
        if (dirPath.startsWith(ConstantValue.SD_DIR_PATH)) {
            filePath = dirPath + ConstantValue.RECORD_NAME + fileName;
        } else {
            filePath = ConstantValue.SD_DIR_PATH + dirPath
                + ConstantValue.RECORD_NAME + fileName;
        }

        // 磁带转轮开始旋转
        leftWheel.setAnimation(anim);
        leftWheel.startAnimation(anim);

        rightWheel.setAnimation(anim);
        rightWheel.startAnimation(anim);
        // 显示停止按钮
        stopRecord.setVisibility(View.VISIBLE);
        beginRecord.setVisibility(View.GONE);
        // 开始录音
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        recorder.setAudioEncoder(AudioEncoder.AMR_NB);
        recorder.setOutputFile(filePath);
        try {
            recorder.prepare();
            recorder.start();

            // 开始计时
            startTimer();

            del.setEnabled(false);
            add.setEnabled(false);
        } catch (Throwable e) {
            CURRENT_WORK_STATE = STATE_FREE;
            Log.e(TAG, "start record exception", e);
        }
    }



    /**
     * 停止录音
     */
    private void stopRecord() {
        try {
            CURRENT_WORK_STATE = STATE_FREE;
            // 停止动画
            leftWheel.clearAnimation();
            rightWheel.clearAnimation();

            // 显示播放录音按钮
            play.setVisibility(View.VISIBLE);
            stopRecord.setVisibility(View.GONE);
            // 停止录音
            recorder.stop();
            // recorder.release();
            // 停止计时
            stopTimer();
            // 保存录音时间
            Editor editor = sp.edit();
            editor.putInt(filePath, secNum);
            // Log.d(TAG, "secNum is " + secNum);
            editor.commit();
            // 重置计时器
            resetTimer();

            progressBar.setVisibility(View.VISIBLE);

            add.setEnabled(true);
            del.setEnabled(true);
            Toast.makeText(this, MessageValue.RECORD_COMPLETE,
                Toast.LENGTH_LONG).show();

            currentFilePaths.add(fileName);
        } catch (Throwable tr) {
            CURRENT_WORK_STATE = STATE_FREE;
            Toast.makeText(this, MessageValue.RECORD_FAILED, Toast.LENGTH_LONG)
                .show();
            Log.e(TAG, "stop record exception", tr);
        }
    }



    /**
     * 播放录音
     */
    private void startPlayer() {
        CURRENT_WORK_STATE = STATE_PLAY;
        try {
            player.reset();
            player.setDataSource(filePath);
            player.prepare();
            player.start();
            // 磁带转轮开始旋转
            leftWheel.setAnimation(anim);
            leftWheel.startAnimation(anim);

            rightWheel.setAnimation(anim);
            rightWheel.startAnimation(anim);

            // 启动进度条
            initProgressBar();
            // 重新启动计时器
            startTimer();
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            del.setEnabled(false);
            add.setEnabled(false);
        } catch (Throwable tr) {
            CURRENT_WORK_STATE = STATE_FREE;
            Log.e(TAG, "play record exception", tr);
        }
    }



    /**
     * 重新播放
     */
    private void resumePlayer() {
        CURRENT_WORK_STATE = STATE_PLAY;
        try {
            player.start();
            // 磁带转轮开始旋转
            leftWheel.setAnimation(anim);
            leftWheel.startAnimation(anim);

            rightWheel.setAnimation(anim);
            rightWheel.startAnimation(anim);

            // 重新启动计时器
            resumeTimer();
            resume.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            del.setEnabled(false);
            add.setEnabled(false);
        } catch (Throwable tr) {
            CURRENT_WORK_STATE = STATE_FREE;
            Log.e(TAG, "play record exception", tr);
        }
    }



    /**
     * 暂停播放器
     */
    private void pausePlayer() {
        try {
            CURRENT_WORK_STATE = STATE_PLAY;
            player.pause();

            // 停止动画
            leftWheel.clearAnimation();
            rightWheel.clearAnimation();
            // 停止计时器
            stopTimer();
            // 暂停进度条
            resume.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
            del.setEnabled(false);
            add.setEnabled(false);
        } catch (Throwable e) {
            Log.e(TAG, "pause play record exception", e);
        }
    }



    /**
     * 停止播放器
     */
    private void stopPlayer() {
        CURRENT_WORK_STATE = STATE_FREE;
        try {
            player.stop();
            // player.release();
            // player = null;
            stopTimer();
            // 停止进度条
            resetTimer();
            // 停止动画
            leftWheel.clearAnimation();
            rightWheel.clearAnimation();
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
            resume.setVisibility(View.GONE);
            del.setEnabled(true);
            add.setEnabled(true);
        } catch (Throwable e) {
            Log.d(TAG, "stop player exception", e);
        }
    }



    /**
     * 动画初始化
     */
    private void initAnimation() {
        anim = AnimationUtils.loadAnimation(this, R.anim.n_wheel_anim);
        anim.setDuration(1500);
        LinearInterpolator lir = new LinearInterpolator();
        anim.setInterpolator(lir);
        anim.setRepeatMode(RotateAnimation.RESTART);
        anim.setRepeatCount(-1);
    }



    @Override
    public void finish() {
        switch (this.CURRENT_WORK_STATE) {
            case STATE_FREE:
                if (player != null) {
                    player.release();
                    player = null;
                }
                if (recorder != null) {
                    recorder.release();
                    recorder = null;
                }
                Intent i = new Intent();

                Bundle b = new Bundle();
                b.putSerializable(ConstantValue.FILE_PATH_COLLECTION,
                    ( Serializable ) currentFilePaths);

                i.putExtras(b);

                this.setResult(RESULT_OK, i);
                super.finish();
                break;
            case STATE_PLAY:
                stopPlayer();
                break;
            case STATE_RECORD:
                stopRecord();
            default:
                break;
        }

    }

    /**
     * 计时器计时任务 simple introduction
     * 
     * <p>
     * detailed comment
     * 
     * @author zhoujian 2012-6-20
     * @see
     * @since 1.0
     */
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            secNum++;
            if (secNum >= 60 * 60) {
                stopRecord();
                Toast.makeText(RecordActivity.this,
                    MessageValue.RECORD_TOOLONG, Toast.LENGTH_LONG);
                return;
            }

            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);

        }

    }
}
