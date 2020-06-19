package com.lonbon.nnv.net.udp.nio.send;

import java.net.InetSocketAddress;

/**
 * 描述：UDP发送快照
 * Created by PHJ on 2019/3/4.
 */

public class UDPSendSnapshot {
    private String message;
    private InetSocketAddress remoteAddress;

    // 用于缓存
    private static UDPSendSnapshot sPool;
    // 当前线程池的数量
    private static int sPoolSize = 0;
    private static final Object sPoolSync = new Object();
    private static final int MAX_POOL_SIZE = 5;
    private UDPSendSnapshot next;


    /**
     * 获取数据的方法
     *
     * @return ReceiveUdpData
     */
    public static UDPSendSnapshot obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                UDPSendSnapshot m = sPool;
                sPool = m.next;
                sPoolSize--;
                m.next = null;
                return m;
            }
        }
        return new UDPSendSnapshot();
    }

    /**
     * 清空数据 保存对象
     */
    public void recycle() {
        this.message = null;
        this.remoteAddress = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    private UDPSendSnapshot() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
