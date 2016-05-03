package com.meiqi.mushroom.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表映射实体类
 * User: 
 * Date: 13-10-10
 * Time: 上午10:01
 */
public class TMushroomTable {
    private Integer tid;
    private String name;
    private Integer nid;
    private Integer did;
    private String tableSplitField;
    private Integer tableSplitNum;
    private Integer split;
    private List<TMushroomTableField> fields;
    private List<String> services;
    private Map<String, String> fieldMap;

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public List<TMushroomTableField> getFields() {
        return fields;
    }


    public Map<String, String> getFieldMap() {
        if (fieldMap == null) {
            final Map<String, String> fieldMapT = new HashMap<String, String>(fields.size());
            for (TMushroomTableField field : fields) {
                fieldMapT.put(field.getServiceField(), field.getTableField());
            }

            fieldMap = fieldMapT;
        }

        return fieldMap;
    }

    public void setFields(List<TMushroomTableField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "TMushroomTable{" +
                "tid=" + tid +
                ", name='" + name + '\'' +
                ", nid=" + nid +
                ", did=" + did +
                ", tableSplitField='" + tableSplitField + '\'' +
                ", tableSplitNum=" + tableSplitNum +
                ", split=" + split +
                ", fields=" + fields +
                ", services=" + services +
                '}';
    }

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNid() {
        return nid;
    }

    public void setNid(Integer nid) {
        this.nid = nid;
    }

    public Integer getSplit() {
        return split;
    }

    public void setSplit(Integer split) {
        this.split = split;
    }

    public String getTableSplitField() {
		return tableSplitField;
	}

	public void setTableSplitField(String tableSplitField) {
		this.tableSplitField = tableSplitField;
	}

	public Integer getTableSplitNum() {
		return tableSplitNum;
	}

	public void setTableSplitNum(Integer tableSplitNum) {
		this.tableSplitNum = tableSplitNum;
	}

	public void setFieldMap(Map<String, String> fieldMap) {
		this.fieldMap = fieldMap;
	}

	public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }
}
