package com.meiqi.dsmanager.po.mushroom.offer;

import java.util.List;

/**
 * offer请求where逻辑条件实体
 * User: 
 * Date: 13-10-7
 * Time: 下午3:39
 */
public class Where {
    private String prepend;
    private List<SqlCondition> conditions;
    private List<Where> wheres;

    @Override
    public String toString() {
        return "Where{" +
                "conditions=" + conditions +
                ", prepend='" + prepend + '\'' +
                ", wheres=" + wheres +
                '}';
    }

    public List<SqlCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<SqlCondition> conditions) {
        this.conditions = conditions;
    }

    public String getPrepend() {
        return prepend;
    }

    public void setPrepend(String prepend) {
        this.prepend = prepend;
    }

    public List<Where> getWheres() {
        return wheres;
    }

    public void setWheres(List<Where> wheres) {
        this.wheres = wheres;
    }
}
