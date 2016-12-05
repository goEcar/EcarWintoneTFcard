package tfcard.wintone.ecar.com.ecarwintonetfcard.net;


import java.util.TreeMap;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;
import tfcard.wintone.ecar.com.ecarwintonetfcard.entity.SerialBean;

/**
 * ===============================================
 * <p>
 * 类描述:
 * <p>
 * 创建人: Eric_Huang
 * <p>
 * 创建时间: 2016/9/1 14:09
 * <p>
 * 修改人:Eric_Huang
 * <p>
 * 修改时间: 2016/9/1 14:09
 * <p>
 * 修改备注:
 * <p>
 * ===============================================
 */
public interface PubService {

    @GET("http://tra.parkbees.com/system/data?")
    Observable<SerialBean> getSerialNum(@QueryMap TreeMap<String, String> map);
}
