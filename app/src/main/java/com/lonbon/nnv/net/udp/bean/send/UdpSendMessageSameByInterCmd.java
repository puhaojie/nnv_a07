package com.lonbon.nnv.net.udp.bean.send;

import android.text.TextUtils;

import com.lonbon.nnv.net.udp.bean.receive.UdpReceiveIoMessage;
import com.lonbon.nnv.utils.FileUtils;

import java.util.Map;

/**
 * 描述：InterCmd相同 如AA 返回也是 AA
 * Created by PHJ on 2019/2/25.
 */

public class UdpSendMessageSameByInterCmd extends UdpSendIoMessage {

    private final static String INTERCMD_KEY = "InterCmd";
    private String cacheInterCmdKey;

    public UdpSendMessageSameByInterCmd(String entity, String remoteIp, int remotePort, int localPort) {
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
        return interCmdReceiveValue != null && cacheInterCmdKey.equals(interCmdReceiveValue);
    }
}
