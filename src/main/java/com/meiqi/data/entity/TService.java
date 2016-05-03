package com.meiqi.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 
 */
public class TService implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer serviceID;
    private Integer baseServiceID;
    private String type;
    private String name;
    private String sql;
    private String dbID;
    private Integer host;
    private List<TServiceColumn> columns;
    private Integer cacheTime;
    private Date updateTime;
    private String state;
    private List<String> dbIDs = null;
    private int nextID = 0;//无数据库字段映射
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TService)) return false;

        TService servicePo = (TService) o;

        if (baseServiceID != null ? !baseServiceID.equals(servicePo.baseServiceID) : servicePo.baseServiceID != null)
            return false;
        if (cacheTime != null ? !cacheTime.equals(servicePo.cacheTime) : servicePo.cacheTime != null)
            return false;
        if (columns != null ? !columns.equals(servicePo.columns) : servicePo.columns != null)
            return false;
        if (dbID != null ? !dbID.equals(servicePo.dbID) : servicePo.dbID != null)
            return false;
        if (name != null ? !name.equals(servicePo.name) : servicePo.name != null)
            return false;
        if (serviceID != null ? !serviceID.equals(servicePo.serviceID) : servicePo.serviceID != null)
            return false;
        if (sql != null ? !sql.equals(servicePo.sql) : servicePo.sql != null)
            return false;
        if (state != null ? !state.equals(servicePo.state) : servicePo.state != null)
            return false;
        if (type != null ? !type.equals(servicePo.type) : servicePo.type != null)
            return false;
        if (updateTime != null ? !updateTime.equals(servicePo.updateTime) : servicePo.updateTime != null)
            return false;
        if (host != null ? !host.equals(servicePo.host) : servicePo.host != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceID != null ? serviceID.hashCode() : 0;
        result = 31 * result + (baseServiceID != null ? baseServiceID.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (sql != null ? sql.hashCode() : 0);
        result = 31 * result + (dbID != null ? dbID.hashCode() : 0);
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        result = 31 * result + (cacheTime != null ? cacheTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    @Override
    public String toString() {
        return "TServicePo{" +
                "baseServiceID=" + baseServiceID +
                ", serviceID=" + serviceID +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", sql='" + sql + '\'' +
                ", dbID='" + dbID + '\'' +
                ", columns=" + columns +
                ", cacheTime=" + cacheTime +
                ", updateTime=" + updateTime +
                ", state='" + state + '\'' +
                '}';
    }

  

    public Integer getServiceID() {
		return serviceID;
	}

	public void setServiceID(Integer serviceID) {
		this.serviceID = serviceID;
	}

	public Integer getBaseServiceID() {
		return baseServiceID;
	}

	public void setBaseServiceID(Integer baseServiceID) {
		this.baseServiceID = baseServiceID;
	}

	public Integer getHost() {
		return host;
	}

	public void setHost(Integer host) {
		this.host = host;
	}

	public Integer getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(Integer cacheTime) {
		this.cacheTime = cacheTime;
	}

	public List<String> getDbIDs() {
		return dbIDs;
	}

	public void setDbIDs(List<String> dbIDs) {
		this.dbIDs = dbIDs;
	}

	public int getNextID() {
		return nextID;
	}

	public void setNextID(int nextID) {
		this.nextID = nextID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<TServiceColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<TServiceColumn> columns) {
        this.columns = columns;
    }

    public String getDbID() {
        return dbID;
    }


    public String nextDbID() {
        if (dbIDs == null) {
            String[] tmp = dbID.split(",");
            ArrayList<String> tmpDbIDS = new ArrayList<String>();

            for (String dbid : tmp) {
                dbid = dbid.trim();
                if (dbid.length() != 0) {
                    tmpDbIDS.add(dbid);
                }
            }

            dbIDs = tmpDbIDS;
        }

        final List<String> dbIDsCur = dbIDs;
        final int size = dbIDsCur.size();

        if (size == 0) {
            return null;
        } else if (size == 1) {
            return dbIDsCur.get(0);
        }

        final int curIndex = Math.abs(nextID++) % size;
       // LogUtil.info("cuIndex:" + curIndex);
        return dbIDsCur.get(curIndex);
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
