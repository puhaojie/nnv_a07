package com.lonbon.nnv.net.udp.nio.receive;


import java.net.InetSocketAddress;

/**
 * 描述：保存接收数据的类
 * Created by PHJ on 2019/3/1.
 */

public class ReceiveUdpData {
    private byte[] bytes;
    private int total;
    private InetSocketAddress address;
    private int port;

    // 用于缓存
    private static ReceiveUdpData sPool;
    // 当前线程池的数量
    private static int sPoolSize = 0;
    private static final Object sPoolSync = new Object();
    private static final int MAX_POOL_SIZE = 3;
    private ReceiveUdpData next;


    /**
     * 获取数据的方法
     *
     * @return ReceiveUdpData
     */
    public static ReceiveUdpData obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                ReceiveUdpData m = sPool;
                sPool = m.next;
                sPoolSize--;
                m.next = null;
                return m;
            }
        }
        return new ReceiveUdpData();
    }

    private ReceiveUdpData() {

    }

    public void setReceiveUdpData(byte[] bytes, int total, InetSocketAddress address, int port) {
        this.bytes = bytes;
        this.total = total;
        this.address = address;
        this.port = port;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getTotal() {
        return total;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    /**
     * 清空数据 保存对象
     */
    public void recycle() {
        this.bytes = null;
        this.total = -1;
        this.address = null;
        this.port = -1;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

}
