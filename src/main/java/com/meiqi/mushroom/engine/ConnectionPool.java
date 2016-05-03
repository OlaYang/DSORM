package com.meiqi.mushroom.engine;

import com.meiqi.data.util.LogUtil;
import com.meiqi.mushroom.config.ServicesConfig;
import com.meiqi.mushroom.entity.TMushroomNode;
import com.meiqi.mushroom.util.EnDecryptUtil;
import com.mysql.jdbc.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: 
 * Date: 13-10-7
 * Time: 下午4:10
 */
public class ConnectionPool {
    private static final ConcurrentHashMap<Integer, ArrayBlockingQueue<MushroomConnection>> pool
            = new ConcurrentHashMap<Integer, ArrayBlockingQueue<MushroomConnection>>();

    /**
     * 验证连接是否有效
    * @Title: isValid 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param conn  mysql的连接
    * @param @param currentSecond 时间片段
    * @param @param forceCheck 是否验证
    * @param @return
    * @param @throws SQLException  参数说明 
    * @return boolean    返回类型 
    * @throws
     */
    private static boolean isValid(MushroomConnection conn, long currentSecond
            , boolean forceCheck) throws SQLException {
    	//为false则验证
        if (!forceCheck) {
            if (conn.jdbcConn.isClosed()) {
                conn.lastCheckTime = currentSecond;
                return false;
            }

            long elapsed = currentSecond - conn.lastCheckTime;
            if (elapsed < 19) { // 19s开始检测
                return true;
            }
        }
        //将当前时间片段赋值给连接
        conn.lastCheckTime = currentSecond;
        return checkConnection(conn.jdbcConn);
    }

    private static boolean checkConnection(Connection connection) throws SQLException {
    	//调用java.sql.Connection的isvailid验证方法。设置超时5s,如果该连接有效返回true，如果无效或者超时未响应返回false
        return connection.isValid(5);
    }

    /**
     * 得到一个mushroom连接
    * @Title: poll 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param nid 要获取连接的数据库节点id
    * @param @param currentSecond 调用该方法的时间片段
    * @param @param forceCheck 强制检查
    * @param @return
    * @param @throws SQLException  参数说明 
    * @return MushroomConnection    返回类型 
    * @throws
     */
    public static MushroomConnection poll(Integer nid, long currentSecond, boolean forceCheck)
            throws SQLException {
    	//从配置文件中获取数据库节点实体
        TMushroomNode node = ServicesConfig.getNode(nid);

        if (node == null) {
            throw new SQLException("配置错误: Mysql节点未找到, @nid-" + nid);
        }
        
        return poll(node, currentSecond, forceCheck);
    }

    /**
     * 得到一个数据库连接
    * @Title: poll 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param node  数据库节点实体
    * @param @param currentSecond 执行的时间片段
    * @param @param forceCheck 是否强制检查
    * @param @return
    * @param @throws SQLException  参数说明 
    * @return MushroomConnection    返回类型 
    * @throws
     */
    public static MushroomConnection poll(TMushroomNode node, long currentSecond, boolean forceCheck)
            throws SQLException {
        final Integer nid = node.getNid();
        //从mushroom内存中获取该数据库节点的连接队列
        ArrayBlockingQueue<MushroomConnection> connections = pool.get(nid);
        //如果获取不到队列，先新建
        if (connections == null) {
        	//新建一个长度为128的队列
            connections = new ArrayBlockingQueue<MushroomConnection>(300);
            ArrayBlockingQueue<MushroomConnection> oldConnections = pool.putIfAbsent(nid, connections);
            if (oldConnections != null) {
                connections = oldConnections;
            }
        }
        //从该数据库节点的队列中取出一个连接
        MushroomConnection conn = connections.poll();
        LogUtil.info("nid:"+nid+",connections size:"+connections.size());
        //如果队列中没有取到连接，或者连接已经无效
        if (conn == null || !isValid(conn, currentSecond, forceCheck)) {
        	//新建一个连接
            try {
                Properties p = new Properties();
                p.setProperty("user", node.getUser());
                p.setProperty("password", EnDecryptUtil.decrypt(node.getPassword()));
                p.setProperty("connectTimeout", "100");

                Connection connJDBC = new Driver().connect(node.getUrl(), p);
                connJDBC.prepareCall("SET sql_mode = ''").execute();
                connJDBC.setAutoCommit(false);

                if (conn == null) {
                    conn = new MushroomConnection(connJDBC, nid, currentSecond);
                } else {
                    conn.jdbcConn = connJDBC;
                }
            } catch (Exception e) {
//                LogUtil.error("连接数据库失败, " + node.getUrl());
                throw new SQLException("连接数据库失败, @nid-" + nid);
            }
        }

        return conn;
    }

    /**
     * 关闭连接
    * @Title: offer 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param conn
    * @param @param transaction  参数说明 
    * @return void    返回类型 
    * @throws
     */
    public static void offer(MushroomConnection conn, boolean transaction) {
        try {
        	//如果连接已经关闭不做下一步处理
            if (conn.jdbcConn.isClosed()) {
                return;
            }
            //如果为true进行回滚
            if (transaction) {
                conn.jdbcConn.rollback();
            } else {
            	//否则设置事务为手动提交
                conn.jdbcConn.setAutoCommit(false);
            }
            //从连接池中获取该连接
            ArrayBlockingQueue<MushroomConnection> connections = pool.get(conn.nid);
            if (connections == null || !connections.offer(conn)) {
                conn.jdbcConn.close(); //进行关闭
            }
        } catch (Exception e) {
        }
    }

}
