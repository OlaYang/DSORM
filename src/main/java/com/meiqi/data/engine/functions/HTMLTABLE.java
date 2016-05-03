package com.meiqi.data.engine.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.Tool;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月21日 上午11:26:25 
 * 类说明   发送HTML表格形式的函数
 */

public class HTMLTABLE extends Function{

	public static final String NAME = HTMLTABLE.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		
		if(3 != args.length){
			throw new ArgsCountError(NAME);
		}
		
		String ruleName = String.valueOf(args[0]);
		if(StringUtils.isEmpty(ruleName)){
			throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		
		JSONObject param = JSONObject.parseObject(String.valueOf(args[1]));
		if(null == param){
			throw new ArgsCountError(NAME+"第二个参数不能为空!");
		}
		
		JSONObject table = JSONObject.parseObject(String.valueOf(args[2]));
		if(null == table){
			throw new ArgsCountError(NAME+"第三个参数不能为空!");
		}
		boolean flag = false;
		String str = "<table border=\"1\" cellspacing=\"0\"><tr>";
		String str1 = "</tr>";
		Tool tool = new Tool();
		List<Map<String, String>> mapList = new ArrayList<Map<String,String>>();
		 DsManageReqInfo dsReqInfo = new DsManageReqInfo();
         dsReqInfo.setServiceName(ruleName);
         dsReqInfo.setNeedAll("1");
         dsReqInfo.setParam(param);
		 try {
			String data = tool.getData(dsReqInfo, calInfo,NAME);
			RuleServiceResponseData responseData1 = DataUtil.parse(data, RuleServiceResponseData.class);
			mapList = responseData1.getRows();
		} catch (Exception e) {
			throw new ArgsCountError(NAME+"查询"+ruleName+"错误!===参数是: "+param);
		}
		
		for (int i = 0; i < mapList.size(); i++) {
			if(i != 0){
				flag = true;
			}
			str1 += "<tr>";
			for (Map.Entry<String, Object> entry : table.entrySet()) {
				if(!flag){
					str += "<th>"+entry.getValue().toString()+"</th>";
				}
				str1 += "<td>"+mapList.get(i).get(entry.getKey())+"</td>";
			}
			str1 += "</tr>";
		}
		String result = str+str1+"</table>";
		return result;
	}

}
