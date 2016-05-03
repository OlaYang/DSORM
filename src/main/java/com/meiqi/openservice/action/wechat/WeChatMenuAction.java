package com.meiqi.openservice.action.wechat;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.wechat.service.IWeChatMenuService;

@Service
public class WeChatMenuAction extends BaseAction{

	@Autowired
	private IWeChatMenuService weChatMenuService;
	@Autowired
	private IDataAction dataAction;
	
	/**
	 * 设置微信菜单目录
	 */
	public String setMenu(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
		String microNo="youjiagougw";
		JSONArray menuDBArray=getMenuFromDB(microNo);
		int menuDBArraySize=menuDBArray.size();
		JSONObject tempJson=null;
		JSONObject menuObject=new JSONObject();
		JSONArray menuArray=new JSONArray();
		for(int i=0;i<menuDBArraySize;i++){
			tempJson=menuDBArray.getJSONObject(i);
			JSONArray childMenuArray=JSONArray.parseArray(tempJson.getString("child_info"));
			JSONObject myMenu=new JSONObject();
			//一级菜单不存在子菜单
			if(0==childMenuArray.size()){
				String type=tempJson.getString("type");
				myMenu.put("type", type);
				myMenu.put("name", tempJson.getString("name"));
				if("click".equals(type)){
					myMenu.put("key", tempJson.getString("content"));
				}else{
					myMenu.put("url", tempJson.getString("content"));
				}
				menuArray.add(myMenu);
			}else{//存在子菜单
				myMenu.put("name", tempJson.getString("name"));
				JSONArray childArray=new JSONArray();
				for(int q=0;q<childMenuArray.size();q++){
					JSONObject childJson=new JSONObject();
					JSONObject tj=childMenuArray.getJSONObject(q);
					String type=tj.getString("type");
					childJson.put("type", type);
					childJson.put("name", tj.getString("name"));
					if("click".equals(type)){
						childJson.put("key", tj.getString("content"));
					}else{
						childJson.put("url", tj.getString("content"));
					}
					childArray.add(childJson);
				}
				myMenu.put("sub_button", childArray);
				menuArray.add(myMenu);
			}
		}
		menuObject.put("button", menuArray);
		JSONObject resultJSONObject=weChatMenuService.createMenu(menuObject);

		return resultJSONObject.toJSONString();
	}
	
	/**
	 * 从规则中获取目录数据
	 */
	private JSONArray getMenuFromDB(String microNo){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("MC_HSV1_CustomMenus");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("micro_no",microNo);
		param.put("parent_id",0);
		param.put("is_show",1);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getInnerData(dsManageReqInfo);
		JSONObject resultDataJson=JSONObject.parseObject(resultData);
		if(resultDataJson.containsKey("rows")){
			return resultDataJson.getJSONArray("rows");
		}else{
			return null;
		}
	}
}
