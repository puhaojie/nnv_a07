package com.example.mytest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.mytest.R;
import com.example.mytest.utils.LogUtil;

/**
 * 弹窗基类
 * Created by a zhi on 2020/06/19.
 */
public abstract class BaseDialog extends Dialog {
    private int countdown = 0;
    private Handler timerHandler;
    private Runnable timerRunnable;

    public enum TypeGravity {
        CENTER,//居中
        LEFT//左对齐
    }

    //启用倒计时功能
    private static final int SECOND = 1000;
    protected void startTimer(final int countdown) {
        if (timerHandler == null) {
            this.countdown = countdown;
            timerHandler = new Handler(Looper.getMainLooper());
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    if (countdown()) {
                        timerHandler.postDelayed(timerRunnable, SECOND);
                    }
                }
            };
        }
        timerHandler.post(timerRunnable);
    }

    private boolean countdown() {
        handleCountdown(countdown);
        return countdown-- > 0;
    }

    protected void handleCountdown(int countdown) {
    }

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.RoundDialog);
    }

    @Override
    protected void onStop() {
        timerHandler.removeCallbacks(timerRunnable);
        super.onStop();
    }
}
