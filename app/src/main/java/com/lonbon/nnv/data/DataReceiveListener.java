package com.lonbon.nnv.data;

/**
 * 描述：接收到数据回调通知
 * Created by PHJ on 2019/1/4.
 */

public interface DataReceiveListener<Model> {

    void onDataReceive(Model model);

}
