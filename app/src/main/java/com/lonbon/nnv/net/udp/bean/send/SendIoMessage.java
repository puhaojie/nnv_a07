package com.lonbon.nnv.net.udp.bean.send;

import android.util.Log;

import com.lonbon.nnv.net.udp.bean.IoMessage;
import com.lonbon.nnv.net.udp.bean.receive.UdpReceiveIoMessage;


/**
 * 描述：发送的消息实体
 * 增加多包回复处理
 * Created by PHJ on 2019/2/18.
 */

public abstract class SendIoMessage extends IoMessage<String> {

    // 发送次数
    public int resendCount = 3;
    // 重发的间隔时间
    public int resendIntervalTime = 5 * 1000;
    // 是否需要重新发送
    public boolean isNeedResponse = false;

    public int currentRequestCount = 1; // 当前的请求次数
    public long nextInvalidTime;        // 下一次过期时间，便于检测


    /**
     * 是否是多包；存在这种情况 发送的一条命令 但是返回多个UDP包，多个包是一条消息
     * 策略：收到第一条消息后 间隔一定的时间再接收到的相同的数据放在同一个包下面
     */
    private boolean isMultiPackage = false;
    // 多包间隔时间 默认间隔1000毫秒
    private long multiPackageInvalidTime = 1000;


    public SendIoMessage setMultiPackage() {
        return setMultiPackageInvalidTime(1000);
    }

    public SendIoMessage setMultiPackageInvalidTime(long invalidTime) {
        if (invalidTime > 2000 || invalidTime < 50) {
            invalidTime = 1000;
        }
        multiPackageInvalidTime = invalidTime;
        isMultiPackage = true;
        setNeedResponse(true);
        setResendCount(1);
        Log.e("SendIoMessage", this + "此条UDP需要多包返回。multiPackageInvalidTime=" + multiPackageInvalidTime);
        return this;
    }

    public long getMultiPackageInvalidTime() {
        return multiPackageInvalidTime;
    }

    public boolean isMultiPackage() {
        return isMultiPackage;
    }

    public SendIoMessage setResendCount(int resendCount) {
        // 为了简单处理 多包情况下不能启动重发机制
        if (isMultiPackage) {
            resendCount = 1;
        }
        if (resendCount <= 0)
            throw new IllegalArgumentException();
        this.resendCount = resendCount;
        return this;
    }

    public SendIoMessage setResendIntervalTime(int resendIntervalTime) {
        if (resendCount <= 0)
            throw new IllegalArgumentException();
        this.resendIntervalTime = resendIntervalTime;
        return this;
    }

    public SendIoMessage setNeedResponse(boolean needResponse) {
        isNeedResponse = needResponse;
        return this;
    }

    public int getResendCount() {
        return resendCount;
    }

    public int getResendIntervalTime() {
        return resendIntervalTime;
    }

    public boolean isNeedResponse() {
        return isNeedResponse;
    }

    /**
     * 判断该消息是不是对于该消息的回复
     *
     * @param receiveIoMessage 收到的消息
     * @return 匹配一致：true(为回复)  匹配不一致：false(不是回复)
     */
    public boolean checkMatchReceivedMessage(UdpReceiveIoMessage receiveIoMessage) {
        return false;
    }

    /**
     * 用于保证检测是否是同一个类型的消息
     * 需要保证每个类型的消息返回的值是一致的
     *
     * @return String 消息的类型
     */
    public abstract String getMessageType();

    @Override
    public String toString() {
        return "SendIoMessage{" +
                "resendCount=" + resendCount +
                ", resendIntervalTime=" + resendIntervalTime +
                ", isNeedResponse=" + isNeedResponse +
                ", currentRequestCount=" + currentRequestCount +
                ", nextInvalidTime=" + nextInvalidTime +
                '}';
    }
}
