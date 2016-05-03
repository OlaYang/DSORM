package com.meiqi.liduoo.fastweixin.handle;

import java.util.Observable;

import com.meiqi.liduoo.fastweixin.api.config.ConfigChangeNotice;
import com.meiqi.liduoo.fastweixin.util.BeanUtil;

/**
 * 配置变化监听器抽象类
 *
 * @author peiyu
 */
public abstract class AbstractApiConfigChangeHandle implements ApiConfigChangeHandle {

    @Override
    public void update(Observable o, Object arg) {
        if (BeanUtil.nonNull(arg) && arg instanceof ConfigChangeNotice) {
            configChange((ConfigChangeNotice) arg);
        }
    }

    /**
     * 子类实现，当配置变化时会触发该方法
     *
     * @param notice 通知对象
     */
    public abstract void configChange(ConfigChangeNotice notice);
}
