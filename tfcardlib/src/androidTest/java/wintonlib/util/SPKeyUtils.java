package wintonlib.util;

import android.content.Context;

/**
 * ===============================================
 * <p>
 * 类描述:
 * <p>
 * 创建人: Eric_Huang
 * <p>
 * 创建时间: 2016/9/8 10:03
 * <p>
 * 修改人:Eric_Huang
 * <p>
 * 修改时间: 2016/9/8 10:03
 * <p>
 * 修改备注:
 * <p>
 * ===============================================
 */
public class SPKeyUtils {

    /**
     * 文通识别
     */
    //获取文通序列号
    private static final String s_SERIAL_NUM = "serialNum";

    //获取序列号
    public static String getSeriaNum(Context mContext){
       return (String) SPUtils.get(mContext,s_SERIAL_NUM, "");
    }
    //保存序列号
    public static void  saveSeriaNum(Context mContext, String seriaNum){
        SPUtils.put(mContext,s_SERIAL_NUM, seriaNum);

    }

    //清除序列号
    public static void  clearSeriaNum(Context mContext){
        SPUtils.put(mContext,s_SERIAL_NUM, "");
    }
}
