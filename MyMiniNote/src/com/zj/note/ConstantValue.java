package com.zj.note;

public class ConstantValue {

    /**
     * 录音文件时长
     */
    public static final String RECORD_FILE_TIME = "FILE_TIME";

    /**
     * jpeg文件扩展名
     */
    public static final String FILE_EX_NAME_JPEG = ".jpeg";
    
    /**
     * jpg文件扩展名
     */
    public static final String FILE_EX_NAME_JPG = ".jpg";

    /**
     * png文件扩展名
     */
    public static final String FILE_EX_NAME_PNG = ".png";

    /**
     * txt文件扩展名
     */
    public static final String FILE_EX_NAME_TXT = ".txt";

    /**
     * amr文件扩展名
     */
    public static final String FILE_EX_NAME_AMR = ".amr";

    /**
     * 列表imageview的宽
     */
    public static final int DIP_LIST_IMG_WIDTH = 50;

    /**
     * 列表imageview的高
     */
    public static final int DIP_LIST_IMG_HEIGHT = 50;

    /**
     * 附件文件大小单位
     */
    public static final String FILE_SIZE = "KB";

    /**
     * INTENT--目录路径
     */
    public static final String DIR_PATH = "DIR_PATH";

    /**
     * INTENT--文件路径
     */
    public static final String FILE_PATH = "FILE_PATH";
    
    /**
     * INTENT--笔记工具类
     */
    public static final String NOTE_UTIL = "NOTE_UTIL";

    /**
     * INTENT--是否为播放界面
     */
    public static final String IS_PLAY_INTENT = "IS_PLAY_INTENT";

    /**
     * INTENT--文件路径集合
     */
    public static final String FILE_PATH_COLLECTION = "FILE_PATH_COLLECTION";

    /**
     * INTENT--保存图像的结果
     */
    public static final String IMG_SAVE_RESULT = "IMG_SAVE_RESULT";

    /**
     * INTENT--启动线程保存图像
     */
    public static final String IMG_SAVE = "IMG_SAVE";

    /**
     * INTENT--文件名
     */
    public static final String FILE_NAME = "FILE_NAME";

    /**
     * INTENT--是否查看
     */
    public static final String IS_CHECK = "IS_CHECK";

    /**
     * INTENT--笔记保存结果
     */
    public static final String GESTURE_SAVE_RESULT = "GESTURE_SAVE_RESULT";

    /**
     * INTENT--保存笔记
     */
    public static final String GESTURE_SAVE = "GESTURE_SAVE";

    /**
     * INTENT--FILEMANAGER对象
     */
    public static final String FILE_MANAGER = "FILE_MANAGER";

    /**
     * INTENT--屏幕宽
     */
    public static final String SCREEN_WIDTH = "SCREEN_WIDTH";

    /**
     * INTENT--屏幕高
     */
    public static final String SCREEN_HEIGHT = "SCREEN_HEIGHT";

    /**
     * 手写笔记路径
     */
    public static final String GESTURE_FILE_PATH = "GESTURE_FILE_PATH";

    /**
     * INTENT--位图高
     */
    public static final String BITMAP_HEIGHT = "BITMAP_HEIGHT";

    /**
     * INTENT--总共保存多少个bitmap
     */
    public static final String SAVE_BM_NUM = "SAVE_BM_NUM";

    /**
     * INTENT--当前页
     */
    public static final String CURRENT_PAGE = "CURRENT_PAGE";

    /**
     * INTENT--行数
     */
    public static final String NUM_ROW = "NUM_ROW";

    /**
     * INTENT--bitmap处理线程结果
     */
    public static final String BITMAP_PROCESS_RESULT = "BITMAP_PROCESS_RESULT";

    /**
     * INTENT--启动线程处理bitmap
     */
    public static final String BITMAP_PROCESS = "BITMAP_PROCESS";

    /**
     * INTENT--FILEMANAGER对象
     */
    public static final String FILE_UTIL = "FILE_UTIL";

    /**
     * INTENT--照相机
     */
    public static final String ACTION_CAMERA = "CAMERA";

    /**
     * INTENT--保存的bitmap路径
     */
    public static final String BITMAP_FILE_PATH = "BITMAP_FILE_PATH";

    /**
     * INTENT--线程处理结果 成功
     */
    public static final String RESULT_OK = "RESULT_OK";

    /**
     * INTENT--线程处理结果 失败
     */
    public static final String RESULT_ERR = "RESULT_ERR";

    /**
     * Session key bitmap
     */
    public static final String IMG_BITMAP = "IMG_BITMAP";

    /**
     * Session key 手写笔记集合
     */
    public static final String GESTURE_LIST = "GESTURE_LIST";

    /**
     * Session key 背景图片bitmap
     */
    public static final String BACKGROUD_BITMAP = "BACKGROUD_BITMAP";

    /**
     * 定时任务等待时间
     */
    public static final long TIMER_DELAY = 1000;

    /**
     * 定时任务间隔时间
     */
    public static final long TIMER_PERIOD = 1000;

    /**
     * 区分录音文件的文件名
     */
    public static final String RECORD_NAME = "a";

    /**
     * 区分录音文件的文件名
     */
    public static final String CAMERA_NAME = "c";

    /**
     * 区分图库文件的文件名
     */
    public static final String PIC_NAME = "p";

    /**
     * 区分手写文件的文件名
     */
    public static final String GESTURE_NAME = "g";

    /**
     * 手写笔记的列数
     */
    public static final int GESTURE_COL_NUM = 10;

    /**
     * 手写笔记的行数
     */
    public static final int GESTURE_ROW_NUM = 12;

    /**
     * 手写列表边距
     */
    public static final int PAD_COL = 10;

    /**
     * 手写列表上下边距
     */
    public static final int PAD_ROW = 50;

    /**
     * 手写列表高度的系数
     */
    public static final float GESTURE_HEIGHT_FACTOR = 0.9f;

    /**
     * 手写画点时生成的轨迹距离
     */
    public static final int GESTURE_POINT_LENGTH = 30;

    /**
     * 光标间隔
     */
    public static final int CURSOR_TIME = 500;

    /**
     * 当前保存的文件
     */
    public static final String CURRENT_SAVE_FILE = "CURRENT_SAVE_FILE";

    /**
     * 目录路径
     */
    public static final String SD_DIR_PATH = "/sdcard/notewidget/";
    
    /**
     * sd卡最小空间
     */
    public static final long MIN_SDCARD_SPACE = 3145728;
}
