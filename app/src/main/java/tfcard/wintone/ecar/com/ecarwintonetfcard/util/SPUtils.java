package tfcard.wintone.ecar.com.ecarwintonetfcard.util;

import android.content.Context;
import android.content.SharedPreferences;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import tfcard.wintone.ecar.com.ecarwintonetfcard.App;

/**
 * ===============================================
 *
 * 类描述:
 *
 * 创建人: Eric_Huang
 *
 * 创建时间: 2016/7/25 16:20
 *
 * 修改人:Eric_Huang
 *
 * 修改时间: 2016/7/25 16:20
 *
 * 修改备注:
 *
 * ===============================================
 */
public class SPUtils {

    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param key       键
     * @param object    值
     */
    public static void put(String key, Object object)
    {

        SharedPreferences sp = App.getInstance().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String)
        {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer)
        {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean)
        {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float)
        {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long)
        {
            editor.putLong(key, (Long) object);
        } else
        {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key              键
     * @param defaultObject    默认值
     */
    public static Object get(String key, Object defaultObject)
    {
        SharedPreferences sp = App.getInstance().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String)
        {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer)
        {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean)
        {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float)
        {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long)
        {
            return sp.getLong(key, (Long) defaultObject);
        }

        return defaultObject;
    }

    /**
     * 移除某个key值已经对应的值
     * @param key       键
     */
    public static void remove(String key)
    {
        SharedPreferences sp = App.getInstance().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear()
    {
        SharedPreferences sp = App.getInstance().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     * @param key       键
     * @return          布尔值
     */
    public static boolean contains(String key)
    {
        SharedPreferences sp = App.getInstance().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     * @return              Map
     */
    public static Map<String, ?> getAll()
    {
        SharedPreferences sp = App.getInstance().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     *
     */
    private static class SharedPreferencesCompat
    {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static Method findApplyMethod()
        {
            try
            {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e)
            {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor)
        {
            try
            {
                if (sApplyMethod != null)
                {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignored) {
            }
            editor.commit();
        }
    }


}
