package com.meiqi.mushroom.engine;

import com.meiqi.mushroom.util.Utils;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: 
 * Date: 13-11-28
 * Time: 下午5:17
 */
public class Transactions {
    private static final ConcurrentHashMap<String, MushroomTransaction> pool
            = new ConcurrentHashMap<String, MushroomTransaction>();

    private static final Timer timer = new Timer("check_transaction_state");

    public static class CheckTransactionTask extends TimerTask {
        final String transactionNum;

        CheckTransactionTask(String transactionNum) {
            this.transactionNum = transactionNum;
        }

        @Override
        public void run() {
            try {
//                LogUtil.info("start rollback " + transactionNum);
                rollBack(transactionNum);
            } catch (Exception e) {
//                LogUtil.error("error when rollback transaction, " + e.getMessage());
            }
        }
    }


    /**
     * 根据事务号进行事务回滚
    * @Title: rollBack 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param transactionNum
    * @param @throws Exception  参数说明 
    * @return void    返回类型 
    * @throws
     */
    public static void rollBack(String transactionNum) throws Exception {
    	//根据事务号在事务池中获取事务
        final MushroomTransaction transaction = getTransaction(transactionNum);
        //如果存在事务
        if (transaction != null) {
            synchronized (transaction) { // 锁住
                if (!transaction.isDone) { // 获取锁后，判断是不是已处理完
                    transaction.task.cancel(); //取消定时器
                    transaction.isDone = true; //设置以及处理完成

                    try {
                        final MushroomConnection connection = transaction.connection;
                        connection.jdbcConn.rollback();
                        ConnectionPool.offer(connection, true);
                    } finally {
                        removeTransaction(transactionNum); // 移除
                    }
                } else {
                    throw new RuntimeException("已结束的事务号, " + transactionNum);
                }
            }
        } else {
            throw new RuntimeException("不存在或者已结束的事务号, " + transactionNum);
        }
    }

    /**
     * 生成一个事务
    * @Title: newTransaction 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param nid 节点编号
    * @param @param transactionTimeout 事务持续时间
    * @param @param currentSecond 调用该方法的时间片段
    * @param @return
    * @param @throws Exception  参数说明 
    * @return MushroomTransaction    返回类型 
    * @throws
     */
    public static MushroomTransaction newTransaction(Integer nid, int transactionTimeout, long currentSecond)
            throws Exception {
    	//绝对值操作，防止负数情况
        transactionTimeout = Math.abs(transactionTimeout);
        //设置超时，最大超时为18s
        transactionTimeout = transactionTimeout > 1800 ? 1800 : transactionTimeout;
        //调用方法，计算一个系统中唯一的事务号
        final String transactionNum = Utils.getTransactionNum();
        //得到一个数据库连接
        final MushroomConnection connection = ConnectionPool.poll(nid, currentSecond, true);
        //新建一个MushroomTransaction
        final MushroomTransaction transaction = new MushroomTransaction(transactionNum, connection, new HashSet<String>());
        final CheckTransactionTask task = new CheckTransactionTask(transactionNum);
        transaction.task = task;
        //将得到的事务和连接放入pool中
        pool.put(transaction.transactionNum, transaction);
        //同时设置超时时间
        timer.schedule(task, transactionTimeout * 1000); // 设置超时
        //返回获取到的事务
        return transaction;
    }


    public static MushroomTransaction getTransaction(String transactionNum) {
        if (transactionNum == null) {
            return null;
        }

        return pool.get(transactionNum);
    }

    static boolean containsT(String transactionNum) {
        if (transactionNum == null) {
            return false;
        }

        return pool.containsKey(transactionNum);
    }

    public static void removeTransaction(String transactionNum) {
        if (transactionNum != null) {
            pool.remove(transactionNum);
        }
    }
}
