package com.lonbon.nnv.base.presenter;

import android.support.annotation.StringRes;

/**
 * 公共的协定
 * Created by PHJ on 2018/10/21.
 */

public interface BaseContract {

    // 基本的界面职责
    interface View<T extends Presenter> {
        // 公共的：显示一个字符串错误
        void showError(@StringRes int str);

        // 公共的：显示进度条
        void showLoading();

        // 支持设置一个Presenter
        void setPresenter(T presenter);
    }

    // 基本的Presenter职责
    interface Presenter {
        // 共用的开始触发
        void start();

        // 共用的销毁触发
        void destroy();
    }

}
