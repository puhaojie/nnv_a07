package com.lonbon.nnv.net.udp.base.callback;

/**
 * IO模块数据回调接口
 * @author phj
 * created at 2019/1/9 12:17
 */
public interface IoDataListener<Model> {

    // 发送超时，对方无回复
    void onSendTimeOut();

    // 发送成功后 给上层的调用
    void onSendSuccess(Model model);

}
