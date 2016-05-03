package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.util.EnDecryptUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class MD5 extends Function {
    static final String NAME = MD5.class.getSimpleName();
    private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        String source = StringPool.EMPTY;

        if (args.length > 0) {
            source = DataUtil.getStringValue(args[0]);
        }

        String result = cache.get(source);
        if (result == null) {
            result = EnDecryptUtil.digest(source, false);
            cache.put(source, result);
        }

        return result;
    }
}
