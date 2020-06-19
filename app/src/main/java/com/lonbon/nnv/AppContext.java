package com.lonbon.nnv;


import com.lonbon.nnv.net.udp.nio.core.IoProvider;

/**
 * 描述：DC项目的自定义上下文
 *      用于保存一些项目中所需要的资源
 * Created by PHJ on 2019/2/25.
 */

public class AppContext {

    private static AppContext INSTANCE;

    private IoProvider ioProvider;

    static {
        INSTANCE = new AppContext();
    }

    private AppContext() {
    }


    public AppContext setIoProvider(IoProvider ioProvider) {
        this.ioProvider = ioProvider;
        return this;
    }

    public IoProvider getIoProvider() {
        return ioProvider;
    }

    public static AppContext get() {
        return INSTANCE;
    }

}
