package tfcard.wintone.ecar.com.ecarwintonetfcard.net;

import com.ecar.ecarnetwork.http.api.ApiBox;
import com.ecar.ecarnetwork.http.util.InvalidUtil;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import rx.Observable;
import tfcard.wintone.ecar.com.ecarwintonetfcard.entity.SerialBean;
import tfcard.wintone.ecar.com.ecarwintonetfcard.util.SysServiceUtils;
import urils.ecaray.com.ecarutils.Utils.DataFormatUtil;

/**
 * ===============================================
 * <p>
 * 类描述:
 * <p>
 * 创建人: Eric_Huang
 * <p>
 * 创建时间: 2016/9/1 14:10
 * <p>
 * 修改人:Eric_Huang
 * <p>
 * 修改时间: 2016/9/1 14:10
 * <p>
 * 修改备注:
 * <p>
 * ===============================================
 */
public class PubDataCenter {

    private static final String PARAMS_MODULE = "module";
    private static final String PARAMS_METHOD = "method";
    private static final String PARAMS_SERVICE = "service";

    public static PubDataCenter sPubDataCenter;
    private static PubService sPubService;
    private PubDataCenter() {
    }

    public static synchronized PubDataCenter getInstance() {
        if (sPubDataCenter == null) {
            sPubDataCenter = new PubDataCenter();
        }
        if(sPubService==null){
            sPubService = ApiBox.getInstance().createService(PubService.class, "http://192.168.8.30:6080/std/data/");
        }
        return sPubDataCenter;
    }

    public static TreeMap<String, String> getSerialParamsMap() {
        TreeMap<String, String> lMap = new TreeMap<>();
        lMap.put(PARAMS_MODULE, "plo");
        lMap.put(PARAMS_METHOD, "getSerialCode");
        lMap.put(PARAMS_SERVICE, "SerialCode");
        return lMap;
    }

    /**
     * 获取文通序列号，因为是蜜蜂的接口，加密方式不同
     */
    public static TreeMap<String, String> getSecurityMapKeys4Serial(TreeMap<String, String> tMap) {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = tMap.keySet();
        Iterator<String> iterator = keys.iterator();
        String parmas = "";
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = tMap.get(key);
            parmas = DataFormatUtil.addText(sb, parmas, key, "=", value, "&");
        }
        parmas = InvalidUtil.addText(sb, parmas, "requestKey=",
                InvalidUtil.BinstrToStr("1000100 110011 110000 110010 111001 1000011 110111 110011 110100 110000 110110 110010 110010 110001 1000010 110000 110010 110000 110010 110110 1000010 110110 111000 110100 1000010 1000010 110000 110000 110101 110111 111001 1000011"));
        String md5 = InvalidUtil.md5(parmas).toString();
        tMap.remove("requestKey");
        tMap.put("sign", md5);

        return tMap;
    }

    /**
     * 获取文通识别序列号
     * @return  Observable
     */
    public Observable<SerialBean> getSerialNum(){
        TreeMap<String, String> lTreeMap = getSerialParamsMap();

        //获取设备唯一标识有两种情况：移动设备获取的是15位的IMEI号，电信设备获取的是14位MEID号，双卡双待就会出现这种情况
        //因此绑定文通识别的唯一标识，在获取到14位的时候，默认添加0在最后一位
        String phoneIdentifier = SysServiceUtils.getIMEI();
        if(phoneIdentifier.length() == 15){
            lTreeMap.put("devicecode", SysServiceUtils.getIMEI());
        }else if(phoneIdentifier.length() == 14){
            lTreeMap.put("devicecode", SysServiceUtils.getIMEI()+"0");
        }

        TreeMap<String , String> lEncTreeMap = getSecurityMapKeys4Serial(lTreeMap);
        return sPubService.getSerialNum(lEncTreeMap);
    }

}
