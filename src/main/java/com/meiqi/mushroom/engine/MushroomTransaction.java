package com.meiqi.mushroom.engine;

import java.util.Set;

/**
 * User: 
 * Date: 13-11-28
 * Time: 下午5:17
 */
public class MushroomTransaction {
    public final String transactionNum;
    public final MushroomConnection connection;
    public final Set<String> notifyChanged;
    public Transactions.CheckTransactionTask task;
    public volatile boolean isDone = false;

    public MushroomTransaction(String transactionNum, MushroomConnection connection, Set<String> notifyChanged) {
        this.transactionNum = transactionNum;
        this.connection = connection;
        this.notifyChanged = notifyChanged;
    }

}
