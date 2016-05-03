package com.meiqi.mushroom.engine;

import java.sql.Connection;

/**
 * User: 
 * Date: 13-11-22
 * Time: 上午11:39
 */
public class MushroomConnection {
    public Connection jdbcConn;
    long lastCheckTime = 0L;
    final Integer nid;

    public MushroomConnection(Connection jdbcConn, Integer nid, long lastCheckTime) {
        this.jdbcConn = jdbcConn;
        this.nid = nid;
        this.lastCheckTime = lastCheckTime;
    }
}
