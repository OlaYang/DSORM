package com.meiqi.data.engine;

import com.meiqi.data.engine.excel.NumberPool;

import java.sql.Connection;

/**
 * User: 
 * Date: 13-11-14
 * Time: 下午4:04
 */
public class RengineConnection {
    Connection jdbcConn;
    long lastCheckTime = NumberPool.LONG_0;
    long createTime = NumberPool.LONG_0;
    public RengineConnection(Connection jdbcConn, long lastCheckTime, long createTime) {
        this.jdbcConn = jdbcConn;
        this.lastCheckTime = lastCheckTime;
        this.createTime=createTime;
    }
}
