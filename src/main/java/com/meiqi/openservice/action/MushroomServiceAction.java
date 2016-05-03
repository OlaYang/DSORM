package com.meiqi.openservice.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.meiqi.dsmanager.action.IMyBatisManualTransactionalAction;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.mushroom.dao.ITMushroomDBDao;
import com.meiqi.mushroom.entity.TMushroomDB;
import com.meiqi.mushroom.entity.TMushroomService;
import com.meiqi.mushroom.entity.TMushroomTable;
import com.meiqi.mushroom.entity.TMushroomTableField;
import com.meiqi.openservice.bean.RepInfo;

@Service
public class MushroomServiceAction extends BaseAction{
	@Autowired
	private ITMushroomDBDao tMushroomDBDao;
	@Autowired
	private IMyBatisManualTransactionalAction myBatisManualTransactionalAction;
	public String addMushroomService(HttpServletRequest req, HttpServletResponse resp,RepInfo repInfo){
		Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		//获取数据库名
		String dbName=paramMap.get("dbName");
		if(null==dbName||"".equals(dbName.trim())){
			return "缺少参数dbName(数据库名)！";
		}
		String tableName=paramMap.get("tableName");
		if(null==tableName||"".equals(tableName.trim())){
			return "缺少参数tableName(表名)！";
		}
		String desc=paramMap.get("desc");
		if(null==desc||"".equals(desc.trim())){
			return "缺少参数desc(服务描述)！";
		}
		String scope=paramMap.get("scope").toUpperCase();;
		if(null==scope||"".equals(scope.trim())){
			return "缺少参数scope(操作集CUDLP)！";
		}
		if(!checkScope(scope)){
			return "操作集中存在非法字符！";
		}
		String reg_login=paramMap.get("reg_login");
		//默认为不需登录
		boolean regLogin="1".equals(reg_login);
		TMushroomDB tMushroomDB = tMushroomDBDao.find(dbName);
		if(null==tMushroomDB){
			return dbName+"数据库不存在！";
		}
		String mushroomServiceName=dbName+"_"+tableName;
		TMushroomService tMushroomService=new TMushroomService();
		tMushroomService.setDesc(desc);
		tMushroomService.setName(mushroomServiceName);
		tMushroomService.setScope(scope);
		tMushroomService.setRegLogin(regLogin);
		
		TMushroomTable tMushroomTable=new TMushroomTable();
		tMushroomTable.setName(tableName);
		tMushroomTable.setDid(tMushroomDB.getDid());
		tMushroomTable.setNid(tMushroomDB.getNid());
		
		TMushroomTableField tMushroomTableField=new TMushroomTableField();
		tMushroomTableField.setDbName(dbName);
		tMushroomTableField.setTableName(tableName);
		try {
			myBatisManualTransactionalAction.saveMushroomConfig(tMushroomService, tMushroomTable,tMushroomTableField);
			return "mushroom服务："+mushroomServiceName+"添加成功";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	

	/**
	 * 检查scope操作类型是否合法 C U D L P
	 * @param scope
	 * @return
	 */
	private boolean checkScope(String scope){
		char[] scopeArray=scope.toCharArray();
		boolean tag=false;
		for(char scopeChar:scopeArray){
			switch (scopeChar) {
			case 'C':
				tag=true;
				break;
			case 'U':
				tag=true;
				break;
			case 'D':
				tag=true;
				break;
			case 'L':
				tag=true;
				break;
			case 'P':
				tag=true;
				break;
			default:
				tag=false;
				break;
			}
			if(!tag){
				break;
			}
		}
		return tag;
	}
}
