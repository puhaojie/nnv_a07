package com.lonbon.nnv.net.udp.bean.receive;

import java.util.List;
import java.util.Map;

/**
 * 描述：接收的多包Udp消息
 * Created by PHJ on 2019/2/18.
 */

public class UdpMultiReceiveIoMessage extends ReceiveIoMessage<List<Map<String,String>>> {

    public UdpMultiReceiveIoMessage(List<Map<String,String>> entity, String remoteIp, int remotePort, int localPort) {
        super(entity, remoteIp, remotePort, localPort);
    }
    
}
