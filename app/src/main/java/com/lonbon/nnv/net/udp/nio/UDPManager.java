package com.lonbon.nnv.net.udp.nio;


import android.util.Log;
import android.util.SparseArray;


import com.lonbon.nnv.net.udp.base.callback.UDPReceiveCallback;
import com.lonbon.nnv.net.udp.nio.core.UDPConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * UDP 接收和发送的管理者
 *
 * @author phj
 *         created at 2019/1/9 15:05
 */
// TODO: 2019/3/1 两个Port的问题
public class UDPManager {

    private static final String TAG = "UDPManager";

    // 用作端口设置
    private final SparseArray<UDPConnector> udpReceiverSparseArray = new SparseArray<>();


    private static class Instance {
        private static UDPManager instance = new UDPManager();
    }

    private UDPManager() {
    }

    public static UDPManager getInstance() {
        return Instance.instance;
    }


    /**
     * 发送UDP命令接口
     *
     * @param command       命令
     * @param localPort     本地端口
     * @param remotePort    远程端口
     * @param remoteAddress 远程地址
     */
    public void sendUDPCommand(final String command, final int localPort, final int remotePort, final String remoteAddress) {

        if (localPort <= 0 || remotePort <= 0) {
            Log.e(TAG, "sendUDPCommand: localPort = "+localPort+"   remotePort = "+remotePort);
            return;
        }

        UDPConnector connector = udpReceiverSparseArray.get(localPort);
        if (connector == null) {
            Log.e(TAG, "sendUDPCommand: connector == null,port=" + localPort);
            connector = new UDPConnector(localPort);

            udpReceiverSparseArray.put(localPort, connector);
        }
//        Log.e(TAG, "sendUDPCommand: localPort="+localPort+"  remotePort="+remotePort );
        connector.send(command, new InetSocketAddress(remoteAddress, remotePort));
//        System.out.println("Command_Send: " + command + "remoteAddress:" + remoteAddress + "\r\nremotePort:" + remotePort + "\r\nlocalPort:" + localPort);

    }

    /**
     * 开始监听UDP
     *
     * @param port            监听端口
     * @param receiveSize     设置收的包大小
     * @param receiveCallback 监听回调
     */
    public synchronized void startMonitorUDP(int port, int receiveSize, UDPReceiveCallback receiveCallback) {
        UDPConnector connector = udpReceiverSparseArray.get(port);
        if (connector == null) {
            connector = new UDPConnector(port);
            udpReceiverSparseArray.put(port, connector);
//            Log.e(TAG, "startMonitorUDP: port="+port );
        }
        try {
            connector.setReceiveCallback(receiveCallback);
            connector.startReceive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
