package tfcard.wintone.ecar.com.ecarwintonetfcard;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;

/**
 * ===============================================
 * <p/>
 * 类描述:
 * <p/>
 * 创建人: Eric_Huang
 * <p/>
 * 创建时间: 2016/8/26 11:16
 * <p/>
 * 修改人:Eric_Huang
 * <p/>
 * 修改时间: 2016/8/26 11:16
 * <p/>
 * 修改备注:
 * <p/>
 * ===============================================
 */
public abstract class BaseActivity extends AppCompatActivity {

    public String TAG;

    protected Activity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView();

        TAG = this.getClass().getSimpleName();

        ButterKnife.bind(this);
        mContext = this;

        this.initData();
        this.initView();
        this.setOnInteractListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 设置布局
     */
    public void setContentView() {
        View layoutView = getLayoutView();
        if (null != layoutView) {
            super.setContentView(layoutView);
        } else {
            super.setContentView(getLayoutId());
        }
    }

    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        //是否要一个activity 栈，移除当前activity
    }


    /*************************抽象方法*******************************/
    /**
     * 布局layoutId
     *
     */
    public abstract int getLayoutId();

    public View getLayoutView() {
        return null;
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 数据展示在view上
     */
    public abstract void initView();

    /**
     * 监听事件
     */
    public abstract void setOnInteractListener();

    /*************************抽象方法*******************************/
}
