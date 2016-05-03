package com.meiqi.mushroom.entity;
/**
 * 数据库实体
 * @author 	DaunRan
 * @date 2015年6月18日
 */
public class TMushroomDB {
	private Integer did;
    private Integer nid;
    private String name;
    private String dbSplitField;
    private Integer dbSplitNum;
    private Integer split;
    private Integer pool;  // 指定线程池
	public Integer getDid() {
		return did;
	}
	public void setDid(Integer did) {
		this.did = did;
	}
	public Integer getNid() {
		return nid;
	}
	public void setNid(Integer nid) {
		this.nid = nid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getSplit() {
		return split;
	}
	public void setSplit(Integer split) {
		this.split = split;
	}
	public Integer getPool() {
		return pool;
	}
	public void setPool(Integer pool) {
		this.pool = pool;
	}
	
	
	public String getDbSplitField() {
		return dbSplitField;
	}
	public void setDbSplitField(String dbSplitField) {
		this.dbSplitField = dbSplitField;
	}
	public Integer getDbSplitNum() {
		return dbSplitNum;
	}
	public void setDbSplitNum(Integer dbSplitNum) {
		this.dbSplitNum = dbSplitNum;
	}
	@Override
    public String toString() {
        return "TMushroomDB{" +
                "dbSplitField='" + dbSplitField + '\'' +
                ", did=" + did +
                ", nid=" + nid +
                ", name='" + name + '\'' +
                ", dbSplitNum=" + dbSplitNum +
                ", split=" + split +
                '}';
    }
}
