package com.meiqi.dsmanager.po.rule.preview;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.meiqi.dsmanager.po.dsmanager.BaseRespInfo;
/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午5:40:06
 * @discription
 */
public class PreviewRespInfo extends BaseRespInfo {
    private List<Map<String, String>> rows;
    private ProcessInfo processInfo;
    private List<LinkedHashMap<String,Object>> explains;

    public List<LinkedHashMap<String, Object>> getExplains() {
        return explains;
    }

    public void setExplains(List<LinkedHashMap<String, Object>> explains) {
        this.explains = explains;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public void setProcessInfo(ProcessInfo processInfo) {
        this.processInfo = processInfo;
    }

    @Override
    public String toString() {
        return "PreviewRespInfo{" +
                "processInfo=" + processInfo +
                ", rows=" + rows +
                '}';
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, String>> rows) {
        this.rows = rows;
    }
}
