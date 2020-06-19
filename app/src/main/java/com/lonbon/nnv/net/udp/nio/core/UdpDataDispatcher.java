package com.lonbon.nnv.net.udp.nio.core;

import android.util.Log;


import com.lonbon.nnv.net.udp.nio.receive.ReceiveUdpData;
import com.lonbon.nnv.utils.CloseUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * 描述：UDP发送和接收逻辑
 * Created by PHJ on 2019/2/28.
 */

public class UdpDataDispatcher implements Closeable {

    private volatile byte[] bytes = new byte[4096];
    private ByteBuffer writeBuffer = ByteBuffer.allocate(4096);
    private ByteBuffer readBuffer = ByteBuffer.wrap(bytes);

    private final DatagramChannel channel;
    private DatagramSocket socket;

    public UdpDataDispatcher(DatagramChannel channel) {
        this.channel = channel;
    }

    /**
     * 发送UDP命令接口
     *
     * @param message       消息
     */
    public void sendMessage(final String message, InetSocketAddress address) {

        try {
            if (socket == null){
                socket = channel.socket();
                socket.setReuseAddress(true);
            }
            // 发送数据
            writeBuffer.clear();
            writeBuffer.put(message.getBytes());
            writeBuffer.flip();
            Log.i("UdpDataDispatcher","sendMessage: address="+address.getAddress().getHostAddress()+":"+address.getPort()+" content="+message );

            int len = channel.send(writeBuffer,address);
//            Log.i("UdpDataDispatcher",len+" ::::sendMessage: address="+address.getAddress().getHostAddress()+":"+address.getPort()+" content="+message );
            if (message.getBytes().length != len)
                throw new IOException("IO数据没有发送完全");
        } catch (IOException e) {
            e.printStackTrace();
            CloseUtils.close(this);
        }
    }

    public ReceiveUdpData receive()  {
        InetSocketAddress address;
        try {
            address = (InetSocketAddress) channel.receive(readBuffer);
        } catch (IOException e) {
            CloseUtils.close(this);
            return null;
        }
        readBuffer.flip();
        int total = 0;
        // 鉴于是UDP网卡接收到的UDP包就是一个完整的报文
        // 读取不了就直接截取了，所以使用if即可，不用使用while
        if (readBuffer.hasRemaining()) {
            total = readBuffer.limit()-readBuffer.position();
            readBuffer.get(bytes,0,total);
        }
        // 清除数据
        readBuffer.clear();
        ReceiveUdpData receiveUdpData = ReceiveUdpData.obtain();
        receiveUdpData.setReceiveUdpData(bytes,total,address, address.getPort());
        return receiveUdpData;
    }

    @Override
    public void close() throws IOException {
    }
}
