package com.meiqi.data.entity;

import java.io.Serializable;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;

/**
 * User: 
 * Date: 13-7-2
 * Time: 上午10:39
 */
public class TServiceColumn implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * t_service表id
	 */
	private Integer serviceID;
	
	/**
	 * 数据库里面某个表的列名
	 */
    private String sqlColumnName;
    
    /**
     * 中文名
     */
    private String columnName;
    
    /**
     * 备注
     */
    private String description;
    
    /**
     * 列的序号
     */
    private String columnIndex;
    
    private Integer columnIntIndex;//不能映射数据库
    
    /**
     * 函数
     */
    private String formula;
    
    /**
     * 逻辑条件
     */
    private String condition;
    
    
    private String isTransfer;

    private String column_type;
    private String parent_column;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TServiceColumn)) return false;

        TServiceColumn columnPo = (TServiceColumn) o;

        if (columnIndex != null ? !columnIndex.equals(columnPo.columnIndex) : columnPo.columnIndex != null)
            return false;
        if (columnIntIndex != null ? !columnIntIndex.equals(columnPo.columnIntIndex) : columnPo.columnIntIndex != null)
            return false;
        if (columnName != null ? !columnName.equals(columnPo.columnName) : columnPo.columnName != null)
            return false;
        if (condition != null ? !condition.equals(columnPo.condition) : columnPo.condition != null)
            return false;
        if (formula != null ? !formula.equals(columnPo.formula) : columnPo.formula != null)
            return false;
        if (isTransfer != null ? !isTransfer.equals(columnPo.isTransfer) : columnPo.isTransfer != null)
            return false;
        if (serviceID != null ? !serviceID.equals(columnPo.serviceID) : columnPo.serviceID != null)
            return false;
        if (sqlColumnName != null ? !sqlColumnName.equals(columnPo.sqlColumnName) : columnPo.sqlColumnName != null)
            return false;

        return true;
    }

    public String getColumn_type() {
		return column_type;
	}

	public void setColumn_type(String column_type) {
		this.column_type = column_type;
	}

	public String getParent_column() {
		return parent_column;
	}

	public void setParent_column(String parent_column) {
		this.parent_column = parent_column;
	}

	@Override
    public int hashCode() {
        int result = serviceID != null ? serviceID.hashCode() : 0;
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }

    public String getIsTransfer() {
        return isTransfer;
    }

    public void setIsTransfer(String transfer) {
        isTransfer = transfer;
    }

    public String getSqlColumnName() {
        return sqlColumnName;
    }

    public void setSqlColumnName(String sqlColumnName) {
        this.sqlColumnName = sqlColumnName;
    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "TServiceColumnPo{" +
                "columnIndex='" + columnIndex + '\'' +
                ", serviceID=" + serviceID +
                ", sqlColumnName='" + sqlColumnName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnIntIndex=" + columnIntIndex +
                ", formula='" + formula + '\'' +
                ", condition='" + condition + '\'' +
                ", isTransfer='" + isTransfer + '\'' +
                '}';
    }

    public String getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(String columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Integer getColumnIntIndex() throws RengineException {
        if (columnIntIndex == null) {
            columnIntIndex = DataUtil.countIndex(this.columnIndex);
        }

        return columnIntIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }


    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
