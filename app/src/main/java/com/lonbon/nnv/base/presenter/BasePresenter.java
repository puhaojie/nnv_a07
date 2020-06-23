package com.lonbon.nnv.base.presenter;

/**
 * BasePresenter (复写基础方法)
 * Created by Administrator on 2018/10/23.
 */

public class BasePresenter<View extends BaseContract.View>
         implements BaseContract.Presenter{


    private View mView;

    public BasePresenter(View view) {
        setView(view);
    }

    /**
     * 设置一个View，子类可以复写
     */
    @SuppressWarnings("unchecked")
    protected void setView(View view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }


    /**
     * 给子类使用的获取View的操作
     * 不允许复写
     *
     * @return View
     */
    protected final View getView() {
        return mView;
    }


    @Override
    public void start() {
        View view = mView;
        if (view != null)
            view.showLoading();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        View view = mView;
        if (view != null)
            view.setPresenter(null);
        mView = null;
    }
}
