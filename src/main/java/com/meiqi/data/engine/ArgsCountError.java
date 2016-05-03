package com.meiqi.data.engine;

/**
 * User: 
 * Date: 13-11-5
 * Time: 上午10:11
 */
public class ArgsCountError extends RengineException {
    public ArgsCountError(String function) {
        super(null, function + "参数个数不匹配");
    }
}
