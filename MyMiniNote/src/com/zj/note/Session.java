package com.zj.note;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

/**
 * 全局变量容器类
 */
public final class Session {
    /**
     * Log用TAG
     */
    private static final String TAG = "Session.";

    /**
     * 保存全局变量的容器
     */
    private static final Map<String, Object> session = new ConcurrentHashMap<String, Object>();

    /**
     * 取得数据
     * 
     * @param key 键值
     * @return 数据
     */
    public static Object get(String key) {
        if (CommonUtil.isEmpty(key) || CommonUtil.isEmpty(session)) {
            return null;
        }

        return session.get(key);
    }

    /**
     * 保存数据<br>
     * 如果指定的key已经存在，则旧值将被替换
     * 
     * @param key 键值
     * @param data 数据
     */
    public static void put(String key, Object data) {
        try {
            if (CommonUtil.isEmpty(key) || data == null) {
                return;
            }

            session.put(key, data);
        } catch (Exception ex) {
            Log.e(TAG + "put", "Put key exception.", ex);
        }
    }

    /**
     * 移除指定键值的数据
     * 
     * @param key 键值
     */
    public static void remove(String key) {
        try {
            if (!CommonUtil.isEmpty(key)) {
                session.remove(key);
            }
        } catch (Exception ex) {
            Log.e(TAG + "remove", "Remove key exception.", ex);
        }
    }

    /**
     * 取得数据个数
     * 
     * @return 数据个数
     */
    public static int getCount() {
        if (session == null) {
            return 0;
        }
        return session.size();
    }

    /**
     * 是否存在指定的键值
     * 
     * @param key 键值
     * @return 存在返回true、否则返回false
     */
    public static boolean hasKey(String key) {
        if (CommonUtil.isEmpty(key)) {
            return false;
        }

        try {
            Set<String> keys = session.keySet();
            if (CommonUtil.isEmpty(keys)) {
                return false;
            }

            Iterator<String> itr = keys.iterator();
            String _key = "";
            while (itr.hasNext()) {
                _key = itr.next();
                if (CommonUtil.isEmpty(_key)) {
                    continue;
                } else if (_key.indexOf(key) >= 0) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG + "hasKey", "Has key exception.", ex);
        }

        return false;
    }

    /**
     * 移除指定前缀的数据
     */
    public static void removeByPrefix(String prefix) {
        try {
            if (CommonUtil.isEmpty(prefix)) {
                return;
            }

            Set<String> keys = session.keySet();
            if (CommonUtil.isEmpty(keys)) {
                return;
            }

            Iterator<String> itr = keys.iterator();
            String key = "";
            while (itr.hasNext()) {
                key = itr.next();
                if (CommonUtil.isEmpty(key)) {
                    continue;
                } else if (key.indexOf(prefix) == 0) {
                    session.remove(key);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG + "removeByPrefix", "Remove keys by prefix exception.", ex);
        }
    }

    /**
     * 清空所有数据
     */
    public static void clear() {
        if (!CommonUtil.isEmpty(session)) {
            session.clear();
        }
    }
}
