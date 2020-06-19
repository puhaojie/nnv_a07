package com.example.mytest.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.example.mytest.R;

/**
 * Created by a zhi on 2020/06/17.
 */
public class NotifyDialog extends BaseDialog implements View.OnClickListener {
    private TextView textViewText;
    private Context context;
    private DialogOnclickListener listener;

    private NotifyDialog(DialogBuild dialogBuild) {
        super(dialogBuild.context);
        this.context = dialogBuild.context;
        setContentView(R.layout.dialog_notify);
        initView(dialogBuild);
    }

    private void initView(DialogBuild dialogBuild) {
        //设置窗口尺寸
        RelativeLayout dialogCanvas = findViewById(R.id.dialog_canvas);
        dialogCanvas.getLayoutParams().height = dialogBuild.height;
        dialogCanvas.getLayoutParams().width = dialogBuild.width;
        //内容
        textViewText = findViewById(R.id.text_view);
        textViewText.setText(dialogBuild.text);
        textViewText.setLineSpacing(0, dialogBuild.textLineSpaceMul);
        textViewText.setPadding(dialogBuild.textLeft, dialogBuild.textTop, dialogBuild.textRight, dialogBuild.textBottom);
        if (dialogBuild.textIcon != 0) {
            textViewText.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(dialogBuild.textIcon), null, null, null);
        }
        setTextViewGravity(dialogBuild.textGravity);
        //标题
        TextView textViewTitle = findViewById(R.id.title_dialog);
        textViewTitle.setText(dialogBuild.title);
        if (dialogBuild.titleIcon != 0) {
            textViewTitle.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(dialogBuild.titleIcon), null, null, null);
        }
        listener = dialogBuild.listener;
//        Debug.startMethodTracing();
        setCanceledOnTouchOutside(false);
        Button buttonOk = findViewById(R.id.ok);
        buttonOk.setOnClickListener(this);
        Button buttonCancel = findViewById(R.id.cancel);
        buttonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok:
                if (listener != null) {
                    listener.onClickOk();
                }
                dismiss();
                break;
            case R.id.cancel:
                if (listener != null) {
                    listener.onClickCancel();
                }
                dismiss();
                break;
        }
    }

    private void setTextViewGravity(TypeGravity gravity) {
        if (gravity == TypeGravity.CENTER) {
            textViewText.setGravity(Gravity.CENTER);
        } else if (gravity == TypeGravity.LEFT) {
            textViewText.setGravity(Gravity.LEFT);
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
        private String title;
        private @DrawableRes int titleIcon;
        private @DrawableRes int textIcon;
        private TypeGravity textGravity;
        private float textLineSpaceMul;
        private Context context;
        private DialogOnclickListener listener;
        public DialogBuild(int width, int height, Context context) {
            this.width = width;
            this.height = height;
            this.context = context;
        }

        public DialogBuild text(String text) {
            this.text = text;
            return this;
        }

        public DialogBuild title(String title) {
            this.title = title;
            return this;
        }

        public DialogBuild titleIcon(@DrawableRes int titleIcon) {
            this.titleIcon = titleIcon;
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

        public DialogBuild addOnclickListener(DialogOnclickListener listener) {
            this.listener = listener;
            return this;
        }

        public NotifyDialog create() {
            return new NotifyDialog(this);
        }

    }
}
