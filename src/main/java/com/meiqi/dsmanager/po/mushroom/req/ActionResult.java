package com.meiqi.dsmanager.po.mushroom.req;

/**
 * 每个action执行返回结果报文 实体
 * User: 
 * Date: 13-10-7
 * Time: 下午4:06
 */
public class ActionResult {
    private String code = "0";
    private String description = "Succeed";
    private int updateCount = 0;
    private Long generateKey = null;
    private String dbName=null;
    private String tableName=null;
    private String results;
    
    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
    public String toString() {
        return "ActionResult{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", updateCount=" + updateCount +
                ", generateKey=" + generateKey +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGenerateKey() {
        return generateKey;
    }

    public void setGenerateKey(Long generateKey) {
        this.generateKey = generateKey;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void addUpdateCount(int updateCount) {
        this.updateCount += updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }
}
