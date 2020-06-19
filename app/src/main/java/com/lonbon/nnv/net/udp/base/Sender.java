package com.lonbon.nnv.net.udp.base;


/**
 * 描述：最上层的发送接口
 * Created by PHJ on 2019/1/9.
 */

public interface Sender {
    /**
     * 发送一条消息
     */
    void send();

    /**
     * 取消发送该消息
     * @return 成功标志
     */
    boolean cancelSend();
}
