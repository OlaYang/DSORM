package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class GET extends Function {
    static final String NAME = GET.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length == 0) {
            return StringPool.EMPTY;
        }

        if (args.length > 10) {
            throw new RengineException(calInfo.getServiceName(), NAME + "嵌套层次深于10");
        }

        Object parameter = calInfo.getParam();
        List<String> reqKeys = new ArrayList<String>();

        if (parameter != null) {
            for (int i = 0; i < args.length; i++) {
                String reqKey = DataUtil.getStringValue(args[i]);
                reqKeys.add(reqKey);

                if (!(parameter instanceof Map)) {
                   // LogUtil.info("parameter:"+JSON.toJSONString(parameter));
                    throw new RengineException(calInfo.getServiceName(), "上一层数据不是Map结构, 调用层次:" + reqKeys);
                }

                parameter = ((Map) parameter).get(reqKey);
            }

            if (parameter == null) {
                return StringPool.EMPTY;
            }

            if (parameter instanceof Map || parameter instanceof List) {
                return JSON.toJSONString(parameter);
            } else {
                return DataUtil.getStringValue(parameter);
            }
        }

        return StringPool.EMPTY;
    }
}
