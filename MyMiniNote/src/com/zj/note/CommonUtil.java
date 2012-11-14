package com.zj.note;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 共通方法
 */
public class CommonUtil {

    /**
     * 判断字符串是否为空或null
     * 
     * @param str
     *            待判断字符串
     * @return 判断结果
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * 判断List是否为空
     * 
     * @param list
     *            待判断List
     * @return 判断结果
     */
    public static boolean isEmpty(List< ? > list) {
        if (list == null || list.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * 判断Set是否为空
     * 
     * @param set
     *            待判断Set
     * @return 判断结果
     */
    public static boolean isEmpty(Set< ? > set) {
        if (set == null || set.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * 判断字符串数组是否为空
     * 
     * @param strs
     *            待判断字符串数组
     * @return 判断结果
     */
    public static boolean isEmpty(Object[] strs) {
        if (strs == null || strs.length <= 0) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * 判断Map是否为空
     * 
     * @param map
     *            待判断Map对象
     * @return 判断结果
     */
    public static boolean isEmpty(Map< ? , ? > map) {
        if (map == null || map.size() <= 0) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * 判断字符串是否为空或trim后是否为空串
     * 
     * @param str
     *            待判断字符串对象
     * @return 判断结果
     */
    public static boolean isEmptyTrim(String str) {
        if (str == null || "".equals(str) || "".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * dip单位转换为px单位
     * 
     * @param displayMetrics
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue, DisplayMetrics displayMetrics) {
        final float scale = displayMetrics.density;
        return ( int ) (dipValue * scale + 0.5f);
    }



    /**
     * px单位转换为dip单位
     * 
     * @param displayMetrics
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue, DisplayMetrics displayMetrics) {
        final float scale = displayMetrics.density;
        return ( int ) (pxValue / scale + 0.5f);
    }



    /**
     * dip转px
     * 
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return ( int ) (dipValue * scale + 0.5f);
    }
    
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }
}
