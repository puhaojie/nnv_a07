package com.lonbon.lonbon_app.net.udp.base.command;


import android.os.SystemClock;

import com.lonbon.lonbon_app.Factory;
import com.lonbon.lonbon_app.net.udp.base.callback.IoDataListener;
import com.lonbon.lonbon_app.net.udp.bean.receive.ReceiveIoMessage;
import com.lonbon.lonbon_app.net.udp.bean.send.SendIoMessage;
import com.lonbon.lonbon_app.net.udp.nio.UDPManager;


/**
 * 描述：Udp发送的基类
 * Created by PHJ on 2019/1/9.
 */
public class UdpIoCommand<Type extends ReceiveIoMessage> extends IoCommand<Type> {


    // 用于缓存
    private static UdpIoCommand sPool;
    // 当前线程池的数量
    private static int sPoolSize = 0;
    private static final Object sPoolSync = new Object();
    private static final int MAX_POOL_SIZE = 5;
    private UdpIoCommand next;


    public UdpIoCommand(SendIoMessage entity) {
        super(entity);
    }

    public UdpIoCommand(SendIoMessage entity, IoDataListener<Type> ioDateListener) {
        super(entity, ioDateListener);
    }

    // TODO: 2020/4/23 大败笔，send中不应该是发送真实的数据，而是将该IoCommand类放入到IoManager中，由IoManager来发送。这样的好处可以由程序控制每个消息发送的状态了
    @Override
    public void send() {

        // 过滤不需要回复的
        if (sendIoMessage == null)
            return;
        if (sendIoMessage.isNeedResponse()) {
            sendIoMessage.currentRequestCount ++;
            sendIoMessage.nextInvalidTime = SystemClock.uptimeMillis() + sendIoMessage.resendIntervalTime;
        }


        UDPManager.getInstance().sendUDPCommand(sendIoMessage.getEntity(),
                sendIoMessage.getLocalPort(),
                sendIoMessage.getRemotePort(),
                sendIoMessage.getRemoteIp());

    }

//    @Override
//    public void send() {
//
//        // 过滤不需要回复的
//        if (sendIoMessage == null)
//            return;
//        IoManager.getInstance().enqueue(this);
//
//    }


    // 是否过期 true : 过期
    public synchronized boolean isInvalid(long time) {

        // 多包的过期时间为
        return time >= sendIoMessage.nextInvalidTime;
    }

    /**
     * 多包处理
     * 比较的是收到了第一个包的时候计时
     * @param time 当前比较的时间
     * @return true 超时
     */
    public synchronized boolean isMultiPackageInvalid(long time) {

        // 多包的过期时间为
        if (!sendIoMessage.isMultiPackage()) {
            throw new RuntimeException();
        }

        return time >= sendIoMessage.nextInvalidTime + sendIoMessage.getMultiPackageInvalidTime();
    }

    public void setMultiPackageInvalidTime() {
        sendIoMessage.nextInvalidTime = SystemClock.uptimeMillis() + sendIoMessage.nextInvalidTime;
    }


    public long  getMultiPackageInvalidTime() {
        return sendIoMessage.getMultiPackageInvalidTime();
    }

    // 是否还可以继续的发送
    public synchronized boolean isCanSend() {
        return sendIoMessage.currentRequestCount <= sendIoMessage.resendCount;
    }


    public void notifySendTimeOut() {
        if (mIoDateListener == null)
            return;
        mIoDateListener.onSendTimeOut();
        recycle();
    }

    public boolean notifySendSuccess(final Type model) {
        if (mIoDateListener == null)
            return false;
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                mIoDateListener.onSendSuccess(model);
                recycle();
            }
        });
        return true;
    }



    /**
     * 获取数据的方法
     *
     * @return ReceiveUdpData
     */
    public static UdpIoCommand obtain(SendIoMessage entity, IoDataListener<ReceiveIoMessage> ioDateListener) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                UdpIoCommand m = sPool;
                sPool = m.next;
                sPoolSize--;
                m.next = null;
                m.sendIoMessage = entity;
                m.mIoDateListener = ioDateListener;
                return m;
            }
        }
        return new UdpIoCommand<>(entity,ioDateListener);
    }

    /**
     * 获取数据的方法
     *
     * @return UdpIoCommand
     */
    public static UdpIoCommand obtain(SendIoMessage entity) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                UdpIoCommand m = sPool;
                sPool = m.next;
                sPoolSize--;
                m.next = null;
                m.sendIoMessage = entity;
                return m;
            }
        }
        return new UdpIoCommand(entity);
    }

    /**
     * 清空数据 保存对象
     */
    public void recycle() {
        this.mIoDateListener = null;
        this.sendIoMessage = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }
}
