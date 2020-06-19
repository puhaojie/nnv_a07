package com.lonbon.lonbon_app.net.udp.nio.core;

import java.io.Closeable;
import java.nio.channels.DatagramChannel;

/**
 * 描述：用于注册/解绑的接口
 * Created by PHJ on 2019/3/1.
 */

public interface IoProvider extends Closeable {

    boolean registerInput(DatagramChannel channel, HandleProviderCallback callback);

    boolean registerOutput(DatagramChannel channel, HandleProviderCallback callback);

    void unRegisterInput(DatagramChannel channel);

    void unRegisterOutput(DatagramChannel channel);


    abstract class HandleProviderCallback implements Runnable {

        @Override
        public final void run() {
            onProviderIo();
        }

        /**
         * 可以进行接收或者发送时的回调
         *
         */
        protected abstract void onProviderIo();

    }

}
