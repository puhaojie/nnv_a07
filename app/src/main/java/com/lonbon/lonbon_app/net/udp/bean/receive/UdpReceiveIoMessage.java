package com.lonbon.lonbon_app.net.udp.bean.receive;

import java.util.Map;

/**
 * 描述：接收的Udp消息
 * Created by PHJ on 2019/2/18.
 */

public class UdpReceiveIoMessage extends ReceiveIoMessage<Map<String,String>> {

    public UdpReceiveIoMessage(Map<String,String> entity, String remoteIp, int remotePort, int localPort) {
        super(entity, remoteIp, remotePort, localPort);
    }

}
