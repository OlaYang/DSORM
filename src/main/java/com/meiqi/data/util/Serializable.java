package com.meiqi.data.util;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-6-14
 * Time: 下午7:35
 * To change this template use File | Settings | File Templates.
 */
public interface Serializable {
    byte[] serialize();

    void unserialize(byte[] ss);
}
