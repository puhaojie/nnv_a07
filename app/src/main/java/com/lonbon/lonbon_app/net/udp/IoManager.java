package com.lonbon.lonbon_app.net.udp;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;


import com.lonbon.lonbon_app.Factory;
import com.lonbon.lonbon_app.data.DateSourceManager;
import com.lonbon.lonbon_app.net.udp.base.command.IoCommand;
import com.lonbon.lonbon_app.net.udp.base.command.UdpIoCommand;
import com.lonbon.lonbon_app.net.udp.bean.receive.ReceiveIoMessage;
import com.lonbon.lonbon_app.net.udp.bean.receive.UdpMultiReceiveIoMessage;
import com.lonbon.lonbon_app.net.udp.bean.receive.UdpReceiveIoMessage;
import com.lonbon.lonbon_app.net.udp.bean.send.SendIoMessage;
import com.lonbon.lonbon_app.net.udp.bean.send.UdpSendIoMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述：IO模块的管理者
 * 1、负责维护命令队列（存储）
 * 2、消息的发送，同一条消息按照顺序发送  发送消息A ->(收到A的回复 / A消息过期) -> 发送消息B
 * 3、消息的重发机制
 * 4、消息的回调（超时和回复）
 * 5、多包处理
 * 6、todo 收到UDP包数据的存储
 * Created by PHJ on 2019/1/9.
 */
public class IoManager {
    private static final String TAG = IoManager.class.getSimpleName();
    private static final int MSG_TIMING_CHECK = 0X7001;

    private static final int MSG_MULTI_PAG_TIMING_CHECK = 0X7002;  // 多包过期检测
    private static final Long INTERVAL_CHECK_TIME = 1000L;

    private final List<IoCommand> waitSendCommandList = new ArrayList<>(); // 待发送的消息队列
    // 发送完了消息 还需要回复
    private final List<IoCommand> needResponseCommandList = new ArrayList<>();

    private final Object waitSendObject = new Object();
    private final Object needResponseObject = new Object();
    // 用于存储正在发送的消息类型集合
    private final Set<String> sendingMessageTypeSet = new HashSet<>();

    /**
     * 多包功能用的少 暂时性使用ConcurrentHashMap
     * 此处保存多包的消息，直到超时的时候才会返回
     * key值同 {@see sendingMessageTypeSet} 相同
     */
    private Map<String, UdpMultiReceiveIoMessage> multiPackMap = new ConcurrentHashMap<>();

    private static IoManager instance = new IoManager();
    private final ThreadPoolExecutor executor;
    private final Handler mainHandler;


    @SuppressWarnings("unchecked")
    private IoManager() {
        executor = new ThreadPoolExecutor(2, 6,
                60L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new Factory.NameableThreadFactory("IoManager-Thread-"));
        // 检查过期的
        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TIMING_CHECK:
                        // 1、定时发送
                        sendEmptyMessageDelayed(MSG_TIMING_CHECK, INTERVAL_CHECK_TIME);
                        // Log.d(TAG, "dispatchMessage: "+executor.getActiveCount()+" -- "+executor.getPoolSize()+" -- "+executor.getCompletedTaskCount() );
                        // 2、再次处理
                        executor.execute(checkExpiredRunnable);
                        break;

                    case MSG_MULTI_PAG_TIMING_CHECK:

                        String keyType = (String) msg.obj;
                        // keyType 过期了
                        synchronized (needResponseObject) {
                            boolean isHave = sendingMessageTypeSet.remove(keyType);
                            if (!isHave) {
                                Log.e(TAG, "dispatchMessage: 没有keyType=" + keyType);
                                return;
                            } else {
                                Log.e(TAG, "dispatchMessage: keyType=" + keyType);
                            }

                            UdpMultiReceiveIoMessage message = multiPackMap.remove(keyType);
                            if (message == null) {
                                Log.e(TAG, "dispatchMessage: message == null");
                                return;
                            }
                            Iterator<IoCommand> iterator = needResponseCommandList.iterator();
                            while (iterator.hasNext()) {
                                IoCommand command = iterator.next();
                                String messageType = command.getSendIoMessage().getMessageType();
                                if (command instanceof UdpIoCommand && messageType.equals(keyType)) {
                                    UdpIoCommand udpIoCommand = (UdpIoCommand) command;
                                    udpIoCommand.getMultiPackageInvalidTime();
                                    // 剔除即可
                                    iterator.remove();
                                    // 这个回调在主线程中
                                    udpIoCommand.notifySendSuccess(message);
                                    return;
                                }
                            }

                            Log.e(TAG, "dispatchMessage: 没有找到对应的");
                        }

                        break;
                }


            }
        };
        mainHandler.sendEmptyMessage(MSG_TIMING_CHECK);
    }

    // 简单的单例实现
    public static IoManager getInstance() {
        return instance;
    }

    /**
     * 对外界调用者的入口
     *
     * @param command 命令消息
     */
    public void sendCommand(IoCommand command) {
        String sendType = command.getSendIoMessage().getMessageType();
        // 加入队列
        synchronized (waitSendObject) {
            waitSendCommandList.add(command);
            handlerSendCommand(sendType);
        }


    }

    /**
     * 发送之前先剔除没有发送的消息
     *
     * @param command 命令消息
     */
    public void sendRemoveAndCommand(IoCommand command) {

        if (command == null) {
            return;
        }
        String sendType = command.getSendIoMessage().getMessageType();
        // 加入队列
        synchronized (waitSendObject) {
            removeNoSendedMsgByType(sendType);
            waitSendCommandList.add(command);
            handlerSendCommand(sendType);
        }


    }


    /**
     * 提供一个发送简单消息的;如下：
     * message : {@link UdpSendIoMessage}
     * command : {@link UdpIoCommand}
     * 发送一个不需要回复回调的消息
     *
     * @param entity     消息实体
     * @param remoteIp   远程IP 未检测合法性
     * @param remotePort 源接收端口
     * @param localPort  本地发送端口
     */
    public static void sendSimpleNoRspMessage(String entity, String remoteIp, int remotePort, int localPort) {
        if (entity == null || entity.equals("") || remotePort <= 0 || localPort <= 0) {
            throw new IllegalArgumentException("IoManager sendNoRspMessage IllegalArgumentException!");
        }
        UdpSendIoMessage message = new UdpSendIoMessage(entity,
                remoteIp,
                remotePort,
                localPort);
        UdpIoCommand command = UdpIoCommand.obtain(message);
        IoManager.getInstance().sendCommand(command);
    }

    private void removeNoSendedMsgByType(String type) {

        if (TextUtils.isEmpty(type)) {
            return;
        }
        Iterator<IoCommand> iterator = waitSendCommandList.iterator();
        while (iterator.hasNext()) {
            IoCommand command = iterator.next();
            SendIoMessage sendIoMessage = command.getSendIoMessage();
            if (type.equals(sendIoMessage.getMessageType())) {
                iterator.remove();
            }
        }
    }

    private void sendCommandFirst(IoCommand command) {
        String messageType = command.getSendIoMessage().getMessageType();
        // 加入队列
        synchronized (waitSendObject) {
            waitSendCommandList.add(0, command);
            handlerSendCommand(messageType);
        }
    }

    private void handlerSendCommand(String messageType) {

        // 没有发送 以及 sendingMessageTypeSet不包括messageType
        if (!sendingMessageTypeSet.contains(messageType)) {
            executor.execute(sendDataToNetworkRunnable);
        }
    }

    /**
     * 收到了一条UDP消息
     *
     * @param receiveIoMessage 消息
     */
    @SuppressWarnings("unchecked")
    void receiveUdpResponse(final UdpReceiveIoMessage receiveIoMessage) {
        Runnable handleReceiveUdpBean = new Runnable() {
            @Override
            public void run() {

                UdpIoCommand udpIoCommand;
                // 是否有接口可以回调
                boolean hasIoDateListener = false;
                synchronized (needResponseObject) {
                    Iterator<IoCommand> iterator = needResponseCommandList.iterator();

                    while (iterator.hasNext()) {
                        IoCommand command = iterator.next();
                        if (command instanceof UdpIoCommand) {
                            udpIoCommand = ((UdpIoCommand) command);
                            //
//                            System.out.println("等待队列1 " + needResponseCommandList.size());
                            if (udpIoCommand.checkResponse(receiveIoMessage)) {

                                String keyType = udpIoCommand.getSendIoMessage().getMessageType();
                                // 非多包情况下
                                if (!udpIoCommand.isMultiPackage()) {
                                    // 代表了回复 需要去除
                                    sendingMessageTypeSet.remove(keyType);

//                                    System.out.println("等待队列2 " + needResponseCommandList.size());
                                    // 剔除即可
                                    iterator.remove();

                                    if (udpIoCommand.notifySendSuccess(receiveIoMessage)) {
                                        hasIoDateListener = true;
                                    }
                                } else { // 多包情况
                                    Log.e(TAG, "run: 多包消息");

                                    UdpMultiReceiveIoMessage message = multiPackMap.get(keyType);
                                    if (message == null) {
                                        List<Map<String, String>> datas = new ArrayList<>();
                                        datas.add(receiveIoMessage.getEntity());
                                        message = new UdpMultiReceiveIoMessage(datas,
                                                receiveIoMessage.getRemoteIp(),
                                                receiveIoMessage.getRemotePort(),
                                                receiveIoMessage.getLocalPort());
                                        multiPackMap.put(keyType, message);
                                        udpIoCommand.setMultiPackageInvalidTime();
                                        Message msg = Message.obtain();
                                        msg.what = MSG_MULTI_PAG_TIMING_CHECK;
                                        msg.obj = keyType;
                                        mainHandler.sendMessageDelayed(msg, udpIoCommand.getMultiPackageInvalidTime());
                                    } else {
                                        message.getEntity().add(receiveIoMessage.getEntity());
                                    }
                                    hasIoDateListener = true;

                                }

                                break;
                            }
                        }
                    }
                }

                // 2、将返回的结果通知上层对没有回调的数据操作
                if (!hasIoDateListener) {
                    notifyUpperProcess(receiveIoMessage);
                }

                // 触发下一次检测(因为可能存在同一类型的回复任务)
                executor.execute(sendDataToNetworkRunnable);
            }
        };
        executor.execute(handleReceiveUdpBean);
    }


    /**
     * @param receiveUdpBean 收到的
     */
    private void notifyUpperProcess(final ReceiveIoMessage receiveUdpBean) {
        System.out.println("通知上层收到的数据 " + receiveUdpBean);
        if (receiveUdpBean == null)
            return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DateSourceManager.receiver(ReceiveIoMessage.class, receiveUdpBean);
            }
        };

        executor.execute(runnable);
    }

    /**
     * 定时检查过期的命令
     */
    private final Runnable checkExpiredRunnable = new Runnable() {
        @Override
        public void run() {
            List<UdpIoCommand> listNeedResponseTemp = new ArrayList<>();
            List<IoCommand> listInvalidTemp = new ArrayList<>();
            synchronized (needResponseObject) {
                long currentTime = SystemClock.uptimeMillis();

                Iterator<IoCommand> iterator = needResponseCommandList.iterator();
                while (iterator.hasNext()) {
                    IoCommand command = iterator.next();
                    if (command instanceof UdpIoCommand) {
                        UdpIoCommand udpIoCommand = ((UdpIoCommand) command);
                        String messageType = command.getSendIoMessage().getMessageType();
                        if (!udpIoCommand.isCanSend() && udpIoCommand.isInvalid(currentTime)) {

                            // 非分包情况 或者分包情况且收到了第一个包了
                            if (!udpIoCommand.isMultiPackage() || !multiPackMap.containsKey(messageType)) {
                                iterator.remove();
                                System.out.println("发送超时" + needResponseCommandList.size());
                                // 重新检测
                                sendingMessageTypeSet.remove(messageType);

                                listNeedResponseTemp.add(udpIoCommand);
                            }

                        } else if (udpIoCommand.isCanSend() && udpIoCommand.isInvalid(currentTime)) { // 还可以继续发送
                            iterator.remove();
                            sendingMessageTypeSet.remove(messageType);

                            listInvalidTemp.add(command);
                            System.out.println(messageType + "重新发送" + needResponseCommandList.size());
                        }
                    }
                }
            }

            for (IoCommand command : listInvalidTemp) {
                // 重新的发送
                sendCommandFirst(command);
            }

            // 再这里执行 防止死锁
            if (listNeedResponseTemp.size() > 0) {
                // 重新触发一次检测
//                executor.execute(sendDataToNetworkRunnable);
                for (UdpIoCommand command : listNeedResponseTemp) {
                    command.notifySendTimeOut();
                }
            }


        }
    };

    // 从队列中取出应发送的消息到网络中
    private final Runnable sendDataToNetworkRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (waitSendObject) {
                if (waitSendCommandList.size() <= 0) {
                    return;
                }

                synchronized (needResponseObject) {
                    for (int i = 0; i < waitSendCommandList.size(); i++) {
                        IoCommand command = waitSendCommandList.get(i);

                        String messageType = command.getSendIoMessage().getMessageType();
                        Log.d(TAG, waitSendCommandList.size() + "run: messageType = " + messageType);
                        if (!sendingMessageTypeSet.contains(messageType)) {
                            waitSendCommandList.remove(command);
                            i --;

                            // 添加那些需要回复的
                            if (command.isNeedResponse()) {
                                sendingMessageTypeSet.add(messageType);
                                needResponseCommandList.add(command);
                            }
                            command.send();
                            // 1、如果是UDP包且需要回复的 需要在等待回复
                            if (command instanceof UdpIoCommand) {
                                UdpIoCommand udpIoCommand = ((UdpIoCommand) command);
                                if (!udpIoCommand.isNeedResponse()) {
                                    // 不需要回复的 在此回收
                                    udpIoCommand.recycle();
                                }
                            }
                        }
                    }
                }


            }

            // 尝试发送另外一个
//            executor.execute(sendDataToNetworkRunnable);
        }
    };
}
