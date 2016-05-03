package com.meiqi.liduoo.base.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.common.CommonUtil;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.ServiceReqInfo;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.ListUtil;

@Component
public class LiduooDataServicImpl implements ILiduooDataService {
	@Autowired
	private IDataAction dataAction;

	@Override
	public String getJsonData(ServiceReqInfo serviceInfo) {
		String content = JSON.toJSONString(serviceInfo);
		String decodeContent = CommonUtil.getDecodeContent(content);
		DsManageReqInfo reqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);

		String resultData = dataAction.getData(reqInfo, decodeContent);

		return resultData;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> getListData(ServiceReqInfo serviceInfo) {
		String resultData = getJsonData(serviceInfo);
		Map map = (Map) JSON.parse(resultData);
		//List<Map<String,Object>> retList = new ArrayList<Map<String,Object>>();
		if ("0".equals(map.get("code"))) {
			return (List<Map>) map.get("rows");
		} else {
			throw new IllegalStateException((String) map.get("description"));
		}
		// LoginRespInfo respInfo = JSON.parseObject(jsonFromData,
		// LoginRespInfo.class);
		// if(!respInfo.getCode().equals("0")){
		// resp.getOutputStream().write(JSON.toJSONString(respInfo,
		// true).getBytes());
		// return;
		// }
		// Map<String, String> respMap = respInfo.getRows().get(0);
		// if(null==respMap){
		// respInfo.setCode("1");
		// respInfo.setDescription("登录失败，返回用户信息为空！");
		// resp.getOutputStream().write(JSON.toJSONString(respInfo,
		// true).getBytes());
		// return;
		// }
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String,Object> getOneRow(ServiceReqInfo serviceInfo) {
		String resultData = getJsonData(serviceInfo);
		Map<String,Object> map = (Map<String,Object>) JSON.parse(resultData);
		if ("0".equals(map.get("code"))) {
			List list = (List) map.get("rows");
			if (ListUtil.isNullOrEmpty(list)) {
				return null;
			}
			return (Map<String,Object>) list.get(0);
		} else {
			throw new IllegalStateException((String) map.get("description"));
		}
	}
}
