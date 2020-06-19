package com.lonbon.lonbon_app.data;

import com.lonbon.lonbon_app.net.udp.bean.receive.ReceiveIoMessage;

import java.util.Map;

/**
 * 描述：接收到Udp消息的总线，监听UdpReceiveIoMessage类
 * 由该类继续的分发到具体的处理类
 * Created by PHJ on 2019/2/19.
 */

class ReceiveUdpMessageBus extends BaseDataInputManager<ReceiveIoMessage<Map<String, String>>> {

    private static final String TAG = "ReceiveUdpMessageBus";

    ReceiveUdpMessageBus() {
        DateSourceManager.addChangedListener(ReceiveIoMessage.class, this);
    }


    @Override
    protected void handlerData(ReceiveIoMessage<Map<String, String>> udpReceiveIoMessage) {


    }

}
