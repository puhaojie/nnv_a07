package com.lonbon.lonbon_app.net.udp.bean.send;


import android.text.TextUtils;



import com.lonbon.lonbon_app.utils.FileUtils;

import java.util.Map;

/**
 * 描述：UDP协议的发送消息集合
 *      该类只是一个基类，因为每个类的匹配关系模糊，只能在子类中单独的确定
 * Created by PHJ on 2019/2/18.
 */

public class UdpSendIoMessage extends SendIoMessage {

    private final static String ACTION = "Action";
    private final static String INTERCMD = "InterCmd";

    private String mCacheType;

    // 默认的发送
    public UdpSendIoMessage(String entity, String remoteIp, int remotePort, int localPort) {
        this.entity = entity;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }


    /**
     * 所有的UDP包下面的唯一标志都是 Action + InterCmd
     * @return Action + InterCmd
     */
    @Override
    public String getMessageType() {

        if (!TextUtils.isEmpty(mCacheType)) {
            return mCacheType;
        }
        String entity = this.entity;
        if (TextUtils.isEmpty(entity)) {
            throw new IllegalArgumentException("UdpSendIoMessage's entity is null.");
        }
        Map<String, String> cacheContentMap = FileUtils.parseActionToMap(entity);
        if (!cacheContentMap.containsKey(ACTION) && !cacheContentMap.containsKey(INTERCMD)) {
            throw new IllegalArgumentException("entity doesn't have Action or InterCmd.");
        }
        String type = null;
        if (cacheContentMap.containsKey(ACTION)) {
            type = cacheContentMap.get(ACTION);
        }
        if (cacheContentMap.containsKey(INTERCMD)) {
            if (type == null) {
                type = "";
            }
            type += cacheContentMap.get(INTERCMD);
        }
        mCacheType = type;
        return mCacheType;
    }
}
