package com.meiqi.data.engine;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-8-31
 * Time: 上午10:13
 */
@JSONType(orders = {"切入信息", "节点耗时/ms", "数据行数", "节点总数", "节点数据源", "节点参数", "子节点"}, asm = false)
public class InvokeNode {
    @JSONField(name = "切入信息")
    final String attr;
    @JSONField(name = "节点耗时/ms")
    long time = -1;
    @JSONField(name = "数据行数")
    long lines = -1;
    @JSONField(name = "节点总数")
    Integer nodeSum;
    @JSONField(name = "节点数据源")
    final NodeService nodeService;
    @JSONField(name = "节点参数")
    final Map<String, Object> param;
    @JSONField(name = "子节点")
    List<InvokeNode> childs = new ArrayList<InvokeNode>();

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setChilds(List<InvokeNode> childs) {
        this.childs = childs;
    }

    public Integer getNodeSum() {
        return nodeSum;
    }

    public void setNodeSum(Integer nodeSum) {
        this.nodeSum = nodeSum;
    }

    public long getLines() {
        return lines;
    }

    public void setLines(long lines) {
        this.lines = lines;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getAttr() {
        return attr;
    }

    public List<InvokeNode> getChilds() {
        return childs;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public InvokeNode(String attr, Map<String, Object> param, TService servicePo) {
        this.attr = attr;
        this.param = param;
        this.nodeService = new NodeService();
        this.nodeService.setName(servicePo.getName());
        this.nodeService.setSql(servicePo.getSql());
        this.nodeService.setCacheTime(servicePo.getCacheTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof InvokeNode)) {
            return false;
        }

        InvokeNode tmp = (InvokeNode) obj;
        return nodeService.getName().equals(tmp.nodeService.getName()) && param.equals(tmp.param);
    }

    @Override
    public int hashCode() {
        return nodeService.getName().hashCode() + param.hashCode();
    }


    public static class NodeService {
        private String name;
        @JSONField(serialize = false)
        private String sql;
        private Integer cacheTime;

        public Integer getCacheTime() {
            return cacheTime;
        }

        public void setCacheTime(Integer cacheTime) {
            this.cacheTime = cacheTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }

}
