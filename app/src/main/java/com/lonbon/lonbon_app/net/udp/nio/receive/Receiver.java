package com.lonbon.lonbon_app.net.udp.nio.receive;

import java.io.Closeable;
import java.io.IOException;

/**
 * 描述：接收的逻辑接口
 * Created by PHJ on 2019/3/1.
 */

public interface Receiver extends Closeable {

    // 触发异步的接收请求
    boolean postReceiveAsync() throws IOException;

    // 开始监听
    void start();
}
