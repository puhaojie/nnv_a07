package com.lonbon.nnv.net.udp.nio.core;


import com.lonbon.nnv.AppContext;
import com.lonbon.nnv.net.udp.nio.receive.Receiver;
import com.lonbon.nnv.net.udp.nio.send.Sender;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.UUID;

/**
 * 描述：代表一个UDP的虚拟连接
 *       管理UDP的发送和接收
 * Created by PHJ on 2019/3/1.
 */

public abstract class Connector implements Closeable, DatagramChannelAdapter.ReceiveUdpListener {

    protected UUID key = UUID.randomUUID();
    protected DatagramChannel channel;
    private Sender sender;
    private Receiver receiver;


    public void setup() throws IOException {

        AppContext context = AppContext.get();
        DatagramChannelAdapter adapter = new DatagramChannelAdapter(channel, context.getIoProvider(),this);

        this.sender = adapter;
        this.receiver = adapter;

    }


    public void send(String msg, InetSocketAddress remoteAddress) {
        sender.send(msg,remoteAddress);
    }

    public void startReceive() throws IOException {
        // 启动接收
        receiver.start();
    }

}
