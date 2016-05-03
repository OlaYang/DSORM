package com.meiqi.dsmanager.po.mushroom.resp;

import java.util.ArrayList;
import java.util.List;

import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.po.mushroom.req.ActionResult;

/**
 * offer返回报文实体
 * User: 
 * Date: 13-10-7
 * Time: 下午4:05
 */
public class ActionRespInfo extends ResponseBaseData {
    private Integer site_id;//站点ID
    private String transactionNum;
    private List<ActionResult> results = new ArrayList<ActionResult>(16);


    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }
    public String getTransactionNum() {
        return transactionNum;
    }

    public void setTransactionNum(String transactionNum) {
        this.transactionNum = transactionNum;
    }

    @Override
    public String toString() {
        return "ActionRespInfo{" +
                "results=" + results +
                ", transactionNum='" + transactionNum + '\'' +
                '}';
    }

    public List<ActionResult> getResults() {
        return results;
    }

    public void setResults(List<ActionResult> results) {
        this.results = results;
    }
}
