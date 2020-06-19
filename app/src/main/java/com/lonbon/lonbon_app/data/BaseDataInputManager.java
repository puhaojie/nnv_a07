package com.lonbon.lonbon_app.data;


/**
 * 描述：基本的数据输入的Manager
 * Created by PHJ on 2019/1/10.
 */

public abstract class BaseDataInputManager<InputModel> implements DataReceiveListener<InputModel > {

    /**
     * 数据到达
     * @param inputModel 数据
     */
    @Override
    public void onDataReceive(InputModel inputModel) {
        if (inputModel == null)
            return;
        handlerData(inputModel);

    }


    protected abstract void handlerData(InputModel inputModel);


    // 销毁
    public void dispose() {

    }
}
