package com.meiqi.data.engine;

/**
 * User: 
 * Date: 13-11-5
 * Time: 上午10:11
 */
public class ArgColumnNotFound extends RengineException {
    public ArgColumnNotFound(String function, String colCal) {
        super(null, function + "参数列未找到, " + colCal);
    }
}
