package com.lonbon.lonbon_app.net.udp.bean.receive;


import com.lonbon.lonbon_app.net.udp.bean.IoMessage;

/**
 * 描述：接收的消息
 * Created by PHJ on 2019/2/18.
 */

public class ReceiveIoMessage<Entity> extends IoMessage<Entity> {

    public ReceiveIoMessage(Entity entity, String remoteIp, int remotePort, int localPort) {
        this.entity = entity;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }
}
