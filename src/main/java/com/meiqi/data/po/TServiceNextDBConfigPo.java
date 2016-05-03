package com.meiqi.data.po;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-8-1
 * Time: 下午3:52
 * To change this template use File | Settings | File Templates.
 */
// 针对nextDB轮训的数据库id配置
public class TServiceNextDBConfigPo {
    private Integer id;  // 主键id
    private String serviceName; // 规则名称
    private String dbNameKey;  // 传入参数代表数据库名称的key值
    private String dbNameValue; //数据库名称
    private String dbID;    // 对应的数据库连接id
    private Integer nodeID;  //mysql物理节点

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDbNameKey() {
        return dbNameKey;
    }

    public void setDbNameKey(String dbNameKey) {
        this.dbNameKey = dbNameKey;
    }

    public String getDbNameValue() {
        return dbNameValue;
    }

    public void setDbNameValue(String dbNameValue) {
        this.dbNameValue = dbNameValue;
    }

    public String getDbID() {
        return dbID;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    public Integer getNodeID() {
        return nodeID;
    }

    public void setNodeID(Integer nodeID) {
        this.nodeID = nodeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TServiceNextDBConfigPo)) return false;

        TServiceNextDBConfigPo that = (TServiceNextDBConfigPo) o;

        if (dbID != null ? !dbID.equals(that.dbID) : that.dbID != null)
            return false;
        if (dbNameKey != null ? !dbNameKey.equals(that.dbNameKey) : that.dbNameKey != null)
            return false;
        if (dbNameValue != null ? !dbNameValue.equals(that.dbNameValue) : that.dbNameValue != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (nodeID != null ? !nodeID.equals(that.nodeID) : that.nodeID != null)
            return false;
        if (serviceName != null ? !serviceName.equals(that.serviceName) : that.serviceName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (serviceName != null ? serviceName.hashCode() : 0);
        result = 31 * result + (dbNameKey != null ? dbNameKey.hashCode() : 0);
        result = 31 * result + (dbNameValue != null ? dbNameValue.hashCode() : 0);
        result = 31 * result + (dbID != null ? dbID.hashCode() : 0);
        result = 31 * result + (nodeID != null ? nodeID.hashCode() : 0);
        return result;
    }
}
