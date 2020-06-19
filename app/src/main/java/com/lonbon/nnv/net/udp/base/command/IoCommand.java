package com.lonbon.nnv.net.udp.base.command;


import com.lonbon.nnv.net.udp.base.Sender;
import com.lonbon.nnv.net.udp.base.callback.IoDataListener;
import com.lonbon.nnv.net.udp.bean.receive.ReceiveIoMessage;
import com.lonbon.nnv.net.udp.bean.receive.UdpReceiveIoMessage;
import com.lonbon.nnv.net.udp.bean.send.SendIoMessage;

/**
 * IO模块抽象的消息命令类
 * @author phj
 * created at 2019/1/9 12:16
 */

public abstract class IoCommand<Type extends ReceiveIoMessage> implements Sender {

    // 发送数据的实体
    protected SendIoMessage sendIoMessage;
    protected IoDataListener<Type> mIoDateListener;


    public IoCommand(SendIoMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("SendIoMessage is null.");
        }
        this.sendIoMessage = message;
    }

    public IoCommand(SendIoMessage message, IoDataListener<Type> ioDateListener) {
        this(message);
        this.mIoDateListener = ioDateListener;
    }


    public boolean isMultiPackage() {
        return this.sendIoMessage.isMultiPackage();
    }

    /**
     * 如果MessageId是有效的就需要客户端回复
     */
    public boolean isNeedResponse() {
        return this.sendIoMessage.isNeedResponse();
    }

    @Override
    public boolean cancelSend() {
        return false;
    }

    public void setIoDateListener(IoDataListener<Type> ioDateListener){
        this.mIoDateListener = ioDateListener;
    }

    public boolean checkResponse(UdpReceiveIoMessage receiveIoMessage) {
        return this.sendIoMessage.checkMatchReceivedMessage(receiveIoMessage);
    }

    public SendIoMessage getSendIoMessage() {
        return sendIoMessage;
    }
}
