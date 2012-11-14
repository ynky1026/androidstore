package com.zj.note;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {

    /**
     * 排序类型
     */
    private int mCompareType;

    /**
     * 升序还是降序
     */
    private int mOrderType;

    /**
     * 排序类型 文件最后一次修改的时间
     */
    public static final int FILE_LAST_MODIFY_TIME = 0;

    /**
     * 排序类型 文件创建时间
     */
    public static final int FILE_CREATE_TIME = 1;

    /**
     * 升序
     */
    public static final int ORDER_ASC = 0;

    /**
     * 降序
     */
    public static final int ORDER_DESC = 1;

    /**
     * 返回结果
     */
    private int result;



    public FileComparator(int compareType, int orderType) {
        this.mCompareType = compareType;
        this.mOrderType = orderType;
    }



    @Override
    public int compare(File f1, File f2) {
        switch (mCompareType) {
            case FILE_LAST_MODIFY_TIME:
                if (f1.lastModified() >= f2.lastModified()) {
                    if (mOrderType == ORDER_ASC) {
                        result = 1;
                    } else {
                        result = -1;
                    }
                } else {
                    if (mOrderType == ORDER_ASC) {
                        result = -1;
                    } else {
                        result = 1;
                    }
                }
                break;
            case FILE_CREATE_TIME:
                String name1 = f1.getName();
                String name2 = f2.getName();

                long time1 = Long.parseLong(name1.substring(1,
                    name1.indexOf(".")));
                long time2 = Long.parseLong(name2.substring(1,
                    name1.indexOf(".")));

                if (time1 >= time2) {
                    if (mOrderType == ORDER_ASC) {
                        result = 1;
                    } else {
                        result = -1;
                    }
                } else {
                    if (mOrderType == ORDER_ASC) {
                        result = -1;
                    } else {
                        result = 1;
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}
