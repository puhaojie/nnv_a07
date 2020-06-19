package com.lonbon.nnv.net.udp.bean.send;

import android.text.TextUtils;


import com.lonbon.nnv.net.udp.bean.receive.UdpReceiveIoMessage;
import com.lonbon.nnv.utils.FileUtils;

import java.util.Map;

/**
 * 描述：默认的IoMessage，都是有返回的，实现基本的匹配
 *      如发送的InterCmd为BMTransfiniteAlarmCan，返回的为BMTransfiniteAlarmCanOk
 *      规则就是在后面加入Ok
 * Created by PHJ on 2019/2/19.
 */

public class DefaultUdpSendIoMessageByInterCmd extends UdpSendIoMessage {

    private final static String INTERCMD_END = "Ok";
    private final static String INTERCMD_KEY = "InterCmd";
    private String cacheInterCmdKey;

    public DefaultUdpSendIoMessageByInterCmd(String entity, String remoteIp, int remotePort, int localPort) {
        super(entity, remoteIp, remotePort, localPort);
        // 默认是需要回复的
        this.isNeedResponse = true;
    }

    @Override
    public boolean checkMatchReceivedMessage(UdpReceiveIoMessage receiveIoMessage) {
        // 1、如果不需要回复，那么过滤即可
        if (!isNeedResponse)
            return false;
        // 2、检测entity中的 action
        if (TextUtils.isEmpty(cacheInterCmdKey)) {
            Map<String, String> cacheContentMap = FileUtils.parseActionToMap(entity);

            if (!cacheContentMap.containsKey(INTERCMD_KEY)
                    || !receiveIoMessage.getEntity().containsKey(INTERCMD_KEY))
                return false;

            cacheInterCmdKey = cacheContentMap.get(INTERCMD_KEY);
        }

        // 3、比较是否相同
        String interCmdReceiveValue = receiveIoMessage.getEntity().get(INTERCMD_KEY);
        return interCmdReceiveValue != null && (cacheInterCmdKey + INTERCMD_END).equals(interCmdReceiveValue);
    }
}
