package com.lonbon.nnv.net.udp.nio.send;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 描述：发送的逻辑接口
 * Created by PHJ on 2019/3/1.
 */

public interface Sender extends Closeable {

    // 触发异步的发送请求
    boolean postSendAsync() throws IOException;

    void send(String message, InetSocketAddress remoteAddress);
}
