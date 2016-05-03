package com.meiqi.app.pojo.dsm.action;

/**
 * User: 
 * Date: 13-10-7
 * Time: 下午4:06
 */
public class ActionResult {
    private String code = "0";
    private String description = "Succeed";
    private int updateCount = 0;
    private Long generateKey = null;

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
