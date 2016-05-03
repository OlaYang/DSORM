package com.meiqi.dsmanager.action.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.Cache4BaseService;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.InvokeNode;
import com.meiqi.data.engine.ProcessInfos;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.user.handler.OrderRow;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IPreviewAction;
import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.po.rule.preview.PreviewReqInfo;
import com.meiqi.dsmanager.po.rule.preview.PreviewRespInfo;
import com.meiqi.dsmanager.po.rule.preview.ProcessInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.commons.util.RuleExceptionUtil;
import com.meiqi.openservice.commons.util.StringUtils;

@Service
public class PreviewActionImpl implements IPreviewAction {
    @Autowired
    private RuleExceptionUtil ruleExceptionUtil;
    @Autowired
    private IDataAction dataAction;
    
	@Override
	public String getPreview(PreviewReqInfo reqInfo) {
		long start = System.currentTimeMillis();
        final Integer serviceID = reqInfo.getServiceID();
        PreviewRespInfo respInfo = null;
        ResponseBaseData respData = new ResponseBaseData();
        try {
            final TService info;
            Map<String, Object> param = reqInfo.getParam();
            
            if(param==null){
                param=new HashMap<String, Object>();
            }
            final Integer previewCount = reqInfo.getPreviewCount();
            
            if (reqInfo.getPreviewType() == 1) {
                info = Services.getService(Services.id2Name(serviceID));
            } else {
                info = reqInfo.getInfo();

                if (info.getBaseServiceID() != null) {
                    String columnIndex = "A";

                    TService basePo = Services.getService(Services.id2Name(info.getBaseServiceID()));
                    if (basePo == null) {
							throw new RengineException(info.getName(), "基础数据源没找到, id=" + info.getBaseServiceID());
                    }

                    for (TServiceColumn po : basePo.getColumns()) {
                        if (DataUtil.countIndex(columnIndex) < DataUtil.countIndex(po.getColumnIndex())) {
                            columnIndex = po.getColumnIndex();
                        }
                    }

                    info.setType("advance");
                    for (TServiceColumn po : info.getColumns()) {
                        columnIndex = DataUtil.getNextColumnIndex(columnIndex);
                        po.setColumnIndex(columnIndex);
                        po.setColumnName("预览");
                    }
                } else {
                    info.setType("base");
                    String columnIndex = "A";
                    for (TServiceColumn po : info.getColumns()) {
                        po.setColumnIndex(columnIndex);
                        columnIndex = DataUtil.getNextColumnIndex(columnIndex);
                    }
                }
            }

            if (info == null) {
                throw new RengineException(null, "数据源未找到, id=" + serviceID);
            }

            respInfo = new PreviewRespInfo();
            D2Data data=DataUtil.getD2Data(info, param);
            
            respInfo.setRows(data2Rows(data, previewCount
                    , reqInfo.getOrderColumnName(), reqInfo.getOrder()));
            respInfo.setProcessInfo(convert(ProcessInfos.getInvokeNode()));

            if ("base".equals(info.getType())) {
                // 基础数据源执行计划
                String serviceName = info.getName();
                String sql = info.getSql();
                String dbID = info.getDbID();
                try {
                    List<LinkedHashMap<String, Object>> explains = Cache4BaseService.explain(serviceName, sql, dbID, param);
                    if (explains != null) {
                        if (explains.size() != 0) {
                            respInfo.setExplains(explains);
                        }
                    }
                } catch (Exception e) {
                    LogUtil.error("执行计划:" + serviceName + "失败,@" + e.getMessage());
                    ruleExceptionUtil.run(new RengineException(serviceName,"执行计划:" + serviceName + "失败,@" + e.getMessage()));
                }
            }

            long end = System.currentTimeMillis();
            Logger.getLogger("monitor").info(Services.id2Name(serviceID) + "|" + (end - start));
		} catch (Exception e1) {
			e1.printStackTrace();
			respInfo = new PreviewRespInfo();
            respInfo.setCode("1");
            respInfo.setDescription(e1.getMessage());
            LogUtil.error(e1.getMessage());
            ruleExceptionUtil.run(e1);
		}
        
		return respInfo==null?"":JSON.toJSONString(respInfo);
	}

    private ProcessInfo convert(InvokeNode root) {
        ProcessInfo processInfo = new ProcessInfo();

        if (root != null) {
            bfs(root, processInfo.getParamRequired()
                    , processInfo.getParamROptional()
                    , processInfo.getServiceNames());
            processInfo.setRoot(root);
        }

        return processInfo;
    }

    static final int TREE_NODE_MAX = 100;

    private void bfs(InvokeNode root
            , Set<String> paramRequired
            , Set<String> paramROptional, Set<String> serviceNames) {
        if (root == null) {
            return;
        }

        LinkedList<InvokeNode> queue = new LinkedList<InvokeNode>();
        ArrayList<InvokeNode> lst = new ArrayList<InvokeNode>();
        queue.offer(root);
        int cnt = 0;

        while (!queue.isEmpty()) {
            cnt++;
            InvokeNode cur = queue.poll();

            if (cnt <= TREE_NODE_MAX) {
                lst.add(cur);
            }

            paramRequired.addAll(getParamRequired(cur.getNodeService().getSql()));
            paramROptional.addAll(getParamOptional(cur.getNodeService().getSql()));
            serviceNames.add(cur.getNodeService().getName());

            for (InvokeNode child : cur.getChilds()) {
                queue.offer(child);
            }
        }

        root.setNodeSum(cnt);

        if (cnt > TREE_NODE_MAX) {
            for (int i = 0; i < lst.size(); i++) {
                Iterator<InvokeNode> ite = lst.get(i).getChilds().iterator();

                for (; ite.hasNext(); ) {
                    InvokeNode cur = ite.next();

                    if (!lst.contains(cur)) {
                        ite.remove();
                    }
                }
            }
        }
    }

    private Collection<? extends String> getParamRequired(String sql) {
        Set<String> set = new HashSet<String>();

        if (sql == null) {
            return set;
        }

        Pattern p = Pattern.compile("#[\\u4e00-\\u9fa5a-zA-Z0-9_-]+#");
        Matcher m = p.matcher(sql);

        while (m.find()) {
            String str = m.group();
            str = str.substring(1, str.length() - 1);
            if (!set.contains(str)) {
                set.add(str);
            }
        }

        return set;
    }

    private Collection<? extends String> getParamOptional(String sql) {
        Set<String> set = new HashSet<String>();
        if (sql == null) {
            return set;
        }
        Pattern pa = Pattern.compile("\\$[a-zA-Z]+[a-zA-Z0-9_-]*");
        Matcher ma = pa.matcher(sql);
        while (ma.find()) {
            String str = ma.group();
            str = str.substring(1, str.length());
            if (!set.contains(str)) {
                set.add(str);
            }
        }


        return set;
    }


    private List<Map<String, String>> data2Rows(D2Data data, Integer previewCount
            , String orderColumnName, String order) throws Exception {
        List<Map<String, String>> rows = new ArrayList<Map<String, String>>(data.getData().length);
        if (data.getData().length == 0) {
            return rows;
        }

        Map<String, String> rowData;
        TServiceColumn[] columns = new TServiceColumn[data.getData()[0].length];
        int orderColumnInt = -1;

        for (TServiceColumn column : data.getColumnList()) {
            columns[column.getColumnIntIndex()] = column;

            if (orderColumnInt == -1
                    && orderColumnName != null
                    && orderColumnName.equals(column.getColumnName())) {
                orderColumnInt = column.getColumnIntIndex();
            }
        }


        if (orderColumnInt != -1) {
            List<OrderRow> orderRows = new ArrayList<OrderRow>(data.getData().length);

            for (int i = 0; i < data.getData().length; i++) {
                final Object[] row = data.getData()[i];
                rowData = new LinkedHashMap<String, String>();

                for (int j = 0; j < row.length; j++) {
                    if (columns[j] != null) { // 需要该列
                        rowData.put(columns[j].getColumnIndex()
                                + "-"
                                + columns[j].getColumnName(), DataUtil.getStringValue(row[j]));
                    }
                }

                orderRows.add(new OrderRow(row[orderColumnInt], rowData));
            }

            Collections.sort(orderRows);

            if ("desc".equalsIgnoreCase(order)) {
                for (int i = orderRows.size() - 1; i >= 0; i--) {
                    rows.add(orderRows.get(i).row);
                }
            } else {
                for (int i = 0, size = orderRows.size(); i < size; i++) {
                    rows.add(orderRows.get(i).row);
                }
            }
        } else {
            for (int i = 0; i < data.getData().length && i < previewCount; i++) {
                final Object[] row = data.getData()[i];
                rowData = new LinkedHashMap<String, String>();

                for (int j = 0; j < row.length; j++) {
                    if (columns[j] != null) { // 需要该列
                        rowData.put(columns[j].getColumnIndex()
                                + "-"
                                + columns[j].getColumnName(), DataUtil.getStringValue(row[j]));
                    }
                }

                rows.add(rowData);
            }
        }

        return rows;
    }
}
