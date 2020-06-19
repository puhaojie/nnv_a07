package com.lonbon.lonbon_app.net.udp.base.callback;

import java.net.InetAddress;

/**
 * 描述：
 * Created by PHJ on 2020/6/19.
 */

public interface UDPReceiveCallback {
    void onReceiveData(byte[] var1, int var2, InetAddress var3, int var4);
}
