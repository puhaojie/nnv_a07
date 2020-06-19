package com.lonbon.nnv.net.udp.nio.core;


import com.lonbon.nnv.net.udp.base.callback.UDPReceiveCallback;
import com.lonbon.nnv.utils.CloseUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * 描述：
 * Created by PHJ on 2019/3/1.
 */
public class UDPConnector extends Connector {

    private  int localPort;

    private UDPReceiveCallback receiveCallback; // 接收回调




    public UDPConnector(int localPort) {
        this.localPort = localPort;
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setReceiveCallback(UDPReceiveCallback receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    private void init() throws IOException {
        if (channel == null) {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.socket().setReuseAddress(true);
            // 接收绑定端口
            channel.socket().bind(new InetSocketAddress(localPort));
            setup();
        }

    }



    @Override
    public void startReceive() throws IOException {
        if (channel == null) {
            channel = DatagramChannel.open();
        }

        super.startReceive();
    }

    @Override
    public void close() throws IOException {
        if (channel != null && channel.isOpen()) {
            CloseUtils.close(channel);
        }
    }

    @Override
    public void onReceiveUdpListener(byte[] data, int length, InetSocketAddress address, int port) {
        if (receiveCallback != null && length > 0) {
            receiveCallback.onReceiveData(data, length, address.getAddress(), port);
        }
    }
}
