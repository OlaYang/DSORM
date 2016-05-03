package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.StringPool;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-8-27
 * Time: 上午9:13
 * To change this template use File | Settings | File Templates.
 */
public class GETBYJSON extends Function {
    static final String NAME = GETBYJSON.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {

        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }

        if (args.length > 11) {
            throw new RengineException(calInfo.getServiceName(), NAME + "嵌套层次深于10");
        }


        String json = DataUtil.getStringValue(args[0]);
        Map<Object, Object> cache = calInfo.getCache(NAME);

        Object obj = cache.get(json);
        if (obj == null) {
            try {
                obj = JSON.parseObject(json, Map.class);
            } catch (JSONException e) {
                try {
                    obj = JSON.parseObject(json, List.class);
                } catch (JSONException e1) {
                    // throw new RengineException(calInfo.getServiceName(), NAME + "传入的json不是Map结构或者List结构");
                    return StringPool.EMPTY;
                }

            }
            cache.put(json, obj);
        }


        Object result = obj;
        for (int i = 1; i < args.length; i++) {
            String key = DataUtil.getStringValue(args[i]);
            if (result instanceof Map) {
                result = ((Map) result).get(key);
                if (i == args.length - 1) {
                    if(result instanceof Map || result instanceof List){
                        return JSON.toJSONString(result);
                    } else {
                        return result;
                    }
                } else {
                    if (result instanceof List) {
                        if (((List) result).size() == 1) {
                            result = ((List) result).get(0);
                        } else {
                            return StringPool.EMPTY;
                        }
                    }
                }
            } else if (result instanceof List) {
                if (((List) result).size() == 1) {
                    result = JSON.toJSONString(((List) result).get(0));
                } else {
                    result = JSON.toJSONString(result);
                }
                return result;
            } else {
                return StringPool.EMPTY;
            }
        }


        return json;


    }

}
