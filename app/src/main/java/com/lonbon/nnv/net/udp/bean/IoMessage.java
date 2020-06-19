package com.lonbon.nnv.net.udp.bean;


/**
 * 描述：公共的接收和发送的Io消息类
 * Created by PHJ on 2019/1/15.
 */

public class IoMessage<Model> extends BaseModel {

    // 远程的IP
    protected String remoteIp;
    // 远程的端口
    protected int remotePort;
    // 本地监听的端口
    protected int localPort;
    // 1.作为发送IoSendMessage，该字段为发送的实体
    // 2.作为接收IoReceiveMessage，该字段为接收的实体
    protected Model entity;

    public String getRemoteIp() {
        return remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public Model getEntity() {
        return entity;
    }


    @Override
    public String toString() {
        return "IoMessage{" +
                "remoteIp='" + remoteIp + '\'' +
                ", remotePort=" + remotePort +
                ", localPort=" + localPort +
                ", entity=" + entity +
                '}';
    }
}
