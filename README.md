

使用教程
===================================  

   1.进入识别页面之前：
       1）获取序列号
       2）授权
         AuthHelper.getInstance().bindAuthService(Context,序列号);//获取成功后自动保存序列号
         
   2.进入识别页面之后
       1）mSerialNum = SPKeyUtils.getSeriaNum(this); mSerialNum空则不可用文通识别 否则可用
       2）调用RecogniteHelper4WT的api进行识别
       
   3）序列号API
   
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


引用方法
------------------------------------
       compile 'com.github.goEcar:EcarWintoneTFcard:+'
       

       
       
