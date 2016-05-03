package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class PI extends Function {
    static final String NAME = PI.class.getSimpleName();
    static final double pi = Math.PI;

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        return pi;
    }
}
