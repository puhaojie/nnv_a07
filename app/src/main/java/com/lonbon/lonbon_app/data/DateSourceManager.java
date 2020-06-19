package com.lonbon.lonbon_app.data;



import com.lonbon.lonbon_app.net.udp.bean.BaseModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 描述：负责数据分发
 * Created by PHJ on 2019/1/4.
 */

public final class DateSourceManager {

    private static final DateSourceManager instance;

    static {
        instance = new DateSourceManager();
        new ReceiveUdpMessageBus();
    }

    private DateSourceManager() {

    }

    /**
     * 观察者的集合
     * Class<?>： 观察的表
     * Set<ChangedListener>：每一个表对应的观察者有很多
     */
    private final Map<Class<?>, Set<DataReceiveListener>> changedListeners = new HashMap<>();

    /**
     * 从所有的监听者中，获取某一个表的所有监听者
     *
     * @param modelClass 表对应的Class信息
     * @param <Model>    范型
     * @return Set<ChangedListener>
     */
    private <Model extends BaseModel> Set<DataReceiveListener> getListeners(Class<Model> modelClass) {
        if (changedListeners.containsKey(modelClass)) {
            return changedListeners.get(modelClass);
        }
        return null;
    }


    /**
     * 添加一个监听
     *
     * @param tClass   对某个表关注
     * @param listener 监听者
     * @param <Model>  表的范型
     */
    public static <Model extends BaseModel> void addChangedListener(final Class<Model> tClass,
                                                                    DataReceiveListener listener) {
        Set<DataReceiveListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            // 初始化某一类型的容器
            changedListeners = new HashSet<>();
            // 添加到中的Map
            instance.changedListeners.put(tClass, changedListeners);
        }
        changedListeners.add(listener);
    }


    /**
     * 删除某一个表的某一个监听器
     *
     * @param tClass   表
     * @param listener 监听器
     * @param <Model>  表的范型
     */
    public static <Model extends BaseModel> void removeChangedListener(final Class<Model> tClass,
                                                                       DataReceiveListener<Model> listener) {
        Set<DataReceiveListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            // 容器本身为null，代表根本就没有
            return;
        }
        // 从容器中删除你这个监听者
        changedListeners.remove(listener);
    }


    public static <Model extends BaseModel> void receiver(final Class tClass,
                                                          final Model models) {
        if (models == null)
            return;
        instance.notifyDateReceiver(tClass,models);
    }


    /**
     * 进行通知调用
     *
     * @param tClass  通知的类型
     * @param models  通知的Model数组
     * @param <Model> 这个实例的范型，限定条件是BaseModel
     */
    @SuppressWarnings("unchecked")
    private <Model extends BaseModel> void notifyDateReceiver(final Class<Model> tClass,
                                                              final Model models) {
        // 找监听器
        final Set<DataReceiveListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            // 通用的通知
            for (DataReceiveListener<Model> listener : listeners) {
                listener.onDataReceive(models);
            }
        }
    }


}
