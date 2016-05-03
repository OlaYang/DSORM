/**   
 * @Title: ResponseInfo.java 
 * @Package com.meiqi.openservice.bean 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author zhouyongxiong
 * @date 2015年7月8日 下午2:06:54 
 * @version V1.0   
 */
package com.meiqi.openservice.bean;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ResponseInfo
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhouyongxiong
 * @date 2015年7月8日 下午2:06:54
 * 
 */
public class ResponseInfo
{
    private String code;
    
    private String description;
    
    private Object object;
    
    private List<Map<String,String>> rows;
    
    public ResponseInfo()
    {
        
    }
    
    public List<Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, String>> rows) {
        this.rows = rows;
    }

    public ResponseInfo(String code, String description)
    {
        this.code = code;
        this.description = description;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public Object getObject()
    {
        return object;
    }
    
    public void setObject(Object object)
    {
        this.object = object;
    }
    
}
