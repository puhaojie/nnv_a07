package com.example.mytest.manager;

import android.app.Dialog;
import android.content.Context;

import com.example.mytest.R;
import com.example.mytest.dialog.DialogOnclickListener;
import com.example.mytest.dialog.NormalDialog;
import com.example.mytest.dialog.NotifyDialog;
import com.example.mytest.dialog.SimpleDialog;

/**
 * Created by a zhi on 2020/06/17.
 */
public class DialogManager {

    public enum TypeDialog {
        NET_DISCONNECT,       //网络断开
        DEVICE_REBOOT,        //设备重启
        RESUME_FACTORY,       //恢复出厂
        NOTIFY,               //消息通知
        REPEAT_CALL,          //重复呼叫
        WIFI_CONNECTING,      //wifi连接中
        WIFI_CONNECT_ERROR ,  //wifi连接失败
        WIFI_CONNECT_SUCCESS ,//wifi连接成功
        WIFI_PASSWORD_ERROR   //wifi密码输入错误
    }

    private static DialogManager manager;
    private DialogManager() {}
    public static DialogManager getInstance() {
        synchronized (DialogManager.class) {
            if (manager == null) {
                manager = new DialogManager();
            }
        }
        return manager;
    }

    private static final int NORMAL_WIDTH = 403;    //普通弹窗的宽
    private static final int NORMAL_HEIGHT = 224;   //普通弹窗的高
    public Dialog createNormalDialog(Context context, TypeDialog type) {
        NormalDialog dialog = null;
        switch (type) {
            case NET_DISCONNECT:
                dialog = new NormalDialog.DialogBuild(NORMAL_WIDTH, NORMAL_HEIGHT, context)
                        .text("无线网络连接已断开")
                        .textIcon(R.drawable.error)
                        .textGravity(NormalDialog.TypeGravity.CENTER)
                        .title("提示")
                        .titleIcon(R.drawable.remind)
                        .create();
                break;
            case RESUME_FACTORY:
                dialog = new NormalDialog.DialogBuild(NORMAL_WIDTH, NORMAL_HEIGHT, context)
                        .text("出厂设置？\n" +
                                "这是个很危险的操作，它将使所有设\n" +
                                "置恢复到出厂状态，请问是否继续？\n")
                        .textGravity(NormalDialog.TypeGravity.LEFT)
                        .textLineSpaceMul(1.5f)
                        .title("提示")
                        .titleIcon(R.drawable.remind)
                        .addOnclickListener(new DialogOnclickListener() {
                            @Override
                            public void onClickOk() {

                            }

                            @Override
                            public void onClickCancel() {

                            }
                        })
                        .create();
                break;
            case DEVICE_REBOOT:
                dialog = new NormalDialog.DialogBuild(NORMAL_WIDTH, NORMAL_HEIGHT, context)
                        .text("确认重启设备？")
                        .textGravity(NormalDialog.TypeGravity.CENTER)
                        .title("提示")
                        .titleIcon(R.drawable.remind)
                        .addOnclickListener(new DialogOnclickListener() {
                            @Override
                            public void onClickOk() {

                            }

                            @Override
                            public void onClickCancel() {

                            }
                        })
                        .create();
                break;
            case REPEAT_CALL:
                dialog = new NormalDialog.DialogBuild(NORMAL_WIDTH, NORMAL_HEIGHT, context)
                        .text("请勿重复呼叫")
                        .textIcon(R.drawable.error)
                        .textGravity(NormalDialog.TypeGravity.CENTER)
                        .title("提示")
                        .titleIcon(R.drawable.remind)
                        .create();
                break;
            case NOTIFY:

                break;
        }
        return dialog;
    }

    private static final int SIMPLE_WIDTH = 355;    //简易弹窗的宽
    private static final int SIMPLE_HEIGHT = 127;   //简易弹窗的高
    public Dialog createSimpleDialog(Context context, TypeDialog type) {
        SimpleDialog dialog = null;
        switch (type) {
            case WIFI_CONNECTING:
                dialog = new SimpleDialog.DialogBuild(SIMPLE_WIDTH, SIMPLE_HEIGHT, context)
                        .text("wifi连接中，请稍等...")
                        .textIcon(R.drawable.refrash)
                        .create();
                break;
            case WIFI_CONNECT_ERROR:
                dialog = new SimpleDialog.DialogBuild(SIMPLE_WIDTH, SIMPLE_HEIGHT, context)
                        .text("连接失败，请重试！")
                        .textIcon(R.drawable.error)
                        .create();
                break;
            case WIFI_CONNECT_SUCCESS:
                dialog = new SimpleDialog.DialogBuild(SIMPLE_WIDTH, SIMPLE_HEIGHT, context)
                        .text("连接成功！")
                        .create();
                break;
            case WIFI_PASSWORD_ERROR:
                dialog = new SimpleDialog.DialogBuild(SIMPLE_WIDTH, SIMPLE_HEIGHT, context)
                        .text("密码错误，请重新输入！")
                        .textIcon(R.drawable.error)
                        .countdown(3)
                        .create();
                break;
        }
        return dialog;
    }

    public Dialog createNotifyDialog(Context context) {
        NotifyDialog dialog;
        dialog = new NotifyDialog.DialogBuild(420, 344, context)
                .title("消息通知")
                .text("                                         大雪封山\n" +
                        "                           接收时间 2020-6-6 19:30\n" +
                        "   减肥的萨洛克；放假了贷款佛我按实际佛奥违法奇偶偶抗衰老的能力多难看了烦恼事拉丝款付了" +
                        "款萨克的疯狂老师拉萨了电脑房老师的能力开发is哈房间卡萨发挥空间撒风景看撒谎的接口来访哈" +
                        "款萨克的疯狂老师拉萨了电脑房老师的能力开发is哈房间卡萨发挥空间撒风景看撒谎的接口来访哈" +
                        "款萨克的疯狂老师拉萨了电脑房老师的能力开发is哈房间卡萨发挥空间撒风景看撒谎的接口来访哈" +
                        "市电视卡发货快来撒待会付款那速度来看你发来看")
                .textLineSpaceMul(1.5f)
                .textPadding(16, 0, 16, 0)
                .create();
        return dialog;
    }

}
