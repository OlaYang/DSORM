package com.meiqi.data.engine.functions;

import java.util.concurrent.ConcurrentHashMap;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.openservice.commons.util.Base64;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class BASE64_ENCODE extends Function {
    static final String NAME = BASE64_ENCODE.class.getSimpleName();
    private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        String source = StringPool.EMPTY;

        if (args.length > 0) {
            source = DataUtil.getStringValue(args[0]);
        }

        String result = cache.get(source);
        if (result == null) {
            result = Base64.encode(source.getBytes());
            cache.put(source, result);
        }

        return result;
    }
}
