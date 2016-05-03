package com.meiqi.app.pojo.dsm.action;

/**
 * User: 
 * Date: 13-10-7
 * Time: 下午3:40
 */
public class SqlCondition {
    private String key;
    private Object value;
    private String op;

    @Override
    public String toString() {
        return "SqlCondition{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", op='" + op + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
