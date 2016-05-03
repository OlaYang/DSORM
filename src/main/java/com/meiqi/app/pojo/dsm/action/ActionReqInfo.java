package com.meiqi.app.pojo.dsm.action;

import java.util.List;

/**
 * User: 
 * Date: 13-10-7
 * Time: 下午3:34
 */
public class ActionReqInfo {
    private List<Action> actions;
    private int transaction = 1;
    private String transactionNum = null;

    public String getTransactionNum() {
        return transactionNum;
    }

    public void setTransactionNum(String transactionNum) {
        this.transactionNum = transactionNum;
    }

    @Override
    public String toString() {
        return "ActionReqInfo{" +
                "actions=" + actions +
                ", transaction=" + transaction +
                ", transactionNum='" + transactionNum + '\'' +
                '}';
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public int getTransaction() {
        return transaction;
    }

    public void setTransaction(int transaction) {
        this.transaction = transaction;
    }
}
