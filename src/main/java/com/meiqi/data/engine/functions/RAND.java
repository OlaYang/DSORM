package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class RAND extends Function {
    static final String NAME = RAND.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        return Math.random();
    }
}
