package com.example.mytest.dialog;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.example.mytest.R;
import com.example.mytest.utils.LogUtil;

/**
 * Created by a zhi on 2020/06/17.
 */
public class SimpleDialog extends BaseDialog {
    private TextView textViewText;
    private Context context;

    private SimpleDialog(DialogBuild dialogBuild) {
        super(dialogBuild.context);
        this.context = dialogBuild.context;
        setContentView(R.layout.dialog_simple);
        initView(dialogBuild);
    }

    private void initView(DialogBuild dialogBuild) {
        //设置窗口尺寸
        LinearLayout dialogCanvas = findViewById(R.id.dialog_canvas);
        dialogCanvas.getLayoutParams().height = dialogBuild.height;
        dialogCanvas.getLayoutParams().width = dialogBuild.width;
        //内容格式
        textViewText = findViewById(R.id.text_view);
        textViewText.setText(dialogBuild.text);
        textViewText.setLineSpacing(0, dialogBuild.textLineSpaceMul);
        textViewText.setPadding(dialogBuild.textLeft, dialogBuild.textTop, dialogBuild.textRight, dialogBuild.textBottom);
        if (dialogBuild.textIcon != 0) {
            textViewText.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(dialogBuild.textIcon), null, null, null);
        }
        setTextViewGravity(dialogBuild.textGravity);
        //定时器
        if (dialogBuild.countdown > 0) {
            startTimer(dialogBuild.countdown);
        }
        //禁止外触
//        setCanceledOnTouchOutside(false);
    }

    private void setTextViewGravity(TypeGravity gravity) {
        if (gravity == TypeGravity.CENTER) {
            textViewText.setGravity(Gravity.CENTER);
        } else if (gravity == TypeGravity.LEFT) {
            textViewText.setGravity(Gravity.LEFT);
        }
    }

    @Override
    protected void handleCountdown(int countdown) {
        super.handleCountdown(countdown);
        if (countdown == 0) {
            this.dismiss();
        }
    }

    public static class DialogBuild {
        private int width;
        private int height;
        private int textLeft;
        private int textRight;
        private int textTop;
        private int textBottom;
        private String text;
        private @DrawableRes
        int textIcon;
        private TypeGravity textGravity;
        private float textLineSpaceMul;
        private Context context;
        private int countdown;
        public DialogBuild(int width, int height, Context context) {
            this.width = width;
            this.height = height;
            this.context = context;
        }

        public DialogBuild text(String text) {
            this.text = text;
            return this;
        }

        public DialogBuild textGravity(TypeGravity textGravity) {
            this.textGravity = textGravity;
            return this;
        }

        public DialogBuild textLineSpaceMul(float textLineSpaceMul) {
            this.textLineSpaceMul = textLineSpaceMul;
            return this;
        }

        public DialogBuild textPadding(int left, int top, int right, int bottom) {
            this.textLeft = left;
            this.textRight = right;
            this.textTop = top;
            this.textBottom = bottom;
            return this;
        }

        public DialogBuild textIcon(@DrawableRes int textIcon) {
            this.textIcon = textIcon;
            return this;
        }

        public DialogBuild countdown(int countdown) {
            this.countdown = countdown;
            return this;
        }

        public SimpleDialog create() {
            return new SimpleDialog(this);
        }

    }
}
