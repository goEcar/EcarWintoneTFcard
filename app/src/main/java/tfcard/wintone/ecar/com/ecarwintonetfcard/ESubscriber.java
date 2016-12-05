package tfcard.wintone.ecar.com.ecarwintonetfcard;

import android.content.Context;

import com.ecar.ecarnetwork.base.BaseSubscriber;

/**
 * ===============================================
 * <p>
 * 类描述:
 * <p>
 * 创建人: Eric_Huang
 * <p>
 * 创建时间: 2016/9/22 10:59
 * <p>
 * 修改人:Eric_Huang
 * <p>
 * 修改时间: 2016/9/22 10:59
 * <p>
 * 修改备注:
 * <p>
 * ================================================
 */
public abstract class ESubscriber<T> extends BaseSubscriber<T> {

    private final Context mContext;

    public ESubscriber(Context context) {
        super(context, null);
        mContext = context;
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }

}
