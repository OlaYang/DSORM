package com.meiqi.data.engine.functions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.util.MyApplicationContextUtil;

public class SIMPLEBETCHMUSHROOM extends Function{
	static final String NAME = SIMPLEBETCHMUSHROOM.class.getSimpleName();
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
		
		if(null==args){
			throw new ArgsCountError(NAME);
		}
		
		String splitSet="#";
		String splitSet1="=";
		String tableSplit=";";
		if(5==args.length){
			splitSet=args[2].toString();
			tableSplit=args[3].toString();
			splitSet1 = args[4].toString();
			args=transform(args[0].toString(),calInfo,args[1].toString(),tableSplit);
			
		}
		
		if(1==args.length){
			if(null==args[0]){
				throw new ArgsCountError(NAME);
			}
			args=transform(args[0].toString(),calInfo,",",tableSplit);
		}
		
		if(2==args.length){
			args=transform(args[0].toString(),calInfo,args[1].toString(),tableSplit);
		}
		
		if(3==args.length){
			splitSet=args[2].toString();
			args=transform(args[0].toString(),calInfo,args[1].toString(),tableSplit);
			
		}
		
		if(4==args.length){
			splitSet=args[2].toString();
			tableSplit=args[3].toString();
			args=transform(args[0].toString(),calInfo,args[1].toString(),tableSplit);
			
		}
		
		if(5>args.length){
			throw new ArgsCountError(NAME);
		}
		if(args.length%5!=0){
			throw new ArgsCountError(NAME);
		}
		
		
		if("|".equals(splitSet)){
			splitSet="\\|";
		}else if(".".equals(splitSet)){
			splitSet="\\.";
		}else if("^".equals(splitSet)){
			splitSet="\\^";
		}
		
		int mushroomActionsSize=args.length/5;
		
		DsManageReqInfo reqInfo=new DsManageReqInfo();
		reqInfo.setServiceName("MUSH_Offer");
		List<Action> actions = new ArrayList<Action>();
		int masterIndex=-1;
		for(int i=0;i<mushroomActionsSize;i++){
			String relation=args[(i*5)+0].toString().toLowerCase();
			boolean isMaster="m".equals(relation);
			if(isMaster){
				masterIndex=i;
			}
			String type=(String) args[(i*5)+1].toString().trim();
			String serviceName=(String) args[(i*5)+2];
			String setString=(String) args[(i*5)+3];
			String whereString=(String) args[(i*5)+4];
			Map<String,Object> setMap=new HashMap<String, Object>();
			String[] setStrings=setString.split(splitSet);
			int setStringsLength=setStrings.length;
			for(int q=0;q<setStringsLength;q++){
				String kv=setStrings[q];
				String[] kvs=kv.split(splitSet1,-1);
				int kvsLength= kvs.length;
				if(1>kvsLength){
					throw new RengineException(calInfo.getServiceName(), "set参数中存在键值错误！");
				}
				String value="";
				
				if(kvsLength>2){
					StringBuilder sb=new StringBuilder();
					
					for(int tempKVi=1;tempKVi<kvsLength;tempKVi++){
						sb.append(kvs[tempKVi]);
						if((tempKVi+1)!=kvsLength){
							sb.append("=");
						}
					}
					value=sb.toString();
				}else if(kvsLength==2){
					value=kvs[1];
				}
				if("$generateKey".equals(value)){
					value="$-"+(i-masterIndex)+".generateKey";
				}else if(value.startsWith("$")&&value.endsWith(".generateKey")){
					String indexString=value.substring(1, value.length()-12);
					int indexIndex=Integer.parseInt(indexString);
					if(indexIndex>0){
						value="$-"+(i-indexIndex+1)+".generateKey";
					}
				}
				setMap.put(kvs[0], value);
			}
			String message=verification(type, serviceName, setMap, whereString);
			if(null!=message){
				throw new RengineException(calInfo.getServiceName(), message);
			}
			Action action=new Action();
			action.setType(type);
			action.setServiceName(serviceName);
			action.setSet(setMap);
			action.setWhereSql(whereString);
			action.setRequestType("1");//'来源 1 来自规则,2 来自mushroom接口'
			actions.add(action);
		}
		Map<String, Object> reqParam = new HashMap<String, Object>();
		reqParam.put("actions", actions);
		reqParam.put("transaction", 1);// 设置开启事务
		
		reqInfo.setParam(reqParam);
		IMushroomAction mushroomAction = (IMushroomAction) MyApplicationContextUtil.getBean("mushroomAction");
		String result=mushroomAction.simpleBetchOffer(reqInfo);
		return result;
	}

	private Object[] transform(String param,CalInfo calInfo,String splitStr,String tableSplit) throws RengineException{
		List<Object> argsList=new ArrayList<Object>();
		try {
			if("|".equals(splitStr)){
				splitStr="\\|";
			}else if(".".equals(splitStr)){
				splitStr="\\.";
			}else if("^".equals(splitStr)){
				splitStr="\\^";
			}
			
			
			if("|".equals(tableSplit)){
				tableSplit="\\|";
			}else if(".".equals(tableSplit)){
				tableSplit="\\.";
			}else if("^".equals(tableSplit)){
				tableSplit="\\^";
			}
			
			
			//切割分号
			String[] fhStrings=param.split(tableSplit);
			int fhStringsLength=fhStrings.length;
			if(fhStringsLength==0){
				return null;
			}
			
			for(int i=0;i<fhStringsLength;i++){
				String[] dhStrings=fhStrings[i].toString().split(splitStr);
				int dhStringsLength=dhStrings.length;
				String firstString=dhStrings[0].trim();
				
				//参数只有4个的时候
				//因为截取的时候，如果尾参数不存在会截取为空，会出现参数只有4个的情况
				if(4==dhStringsLength){
					//判断第一个参数是否是cud是，则表示缺少关系标识参数，不是则表示缺少where
					if("c".equalsIgnoreCase(firstString)||"u".equalsIgnoreCase(firstString)||"d".equalsIgnoreCase(firstString)){
						argsList.add("");
						argsList.addAll(Arrays.asList(dhStrings));
					}else{
						argsList.addAll(Arrays.asList(dhStrings));
						argsList.add("");
					}
				}else if(3==dhStringsLength){
					argsList.add("");
					argsList.addAll(Arrays.asList(dhStrings));
					argsList.add("");
				}else{
					if(5==dhStringsLength){
						if(dhStrings[3].contains(" in ")){
							argsList.add("");
							argsList.add(dhStrings[0]);
							argsList.add(dhStrings[1]);
							argsList.add(dhStrings[2]);
							argsList.add(dhStrings[3]+","+dhStrings[4]);
						}else{
							argsList.addAll(Arrays.asList(dhStrings));
						}
					}else{
						argsList.add("");
						argsList.add(dhStrings[0]);
						argsList.add(dhStrings[1]);
						argsList.add(dhStrings[2]);
						StringBuilder sb=new StringBuilder();
						for(int q=3;q<dhStringsLength;q++){
							sb.append(dhStrings[q]);
							if((q+1)!=dhStringsLength){
								sb.append(",");
							}
						}
						argsList.add(sb.toString());
					}
				}
				
			}
			return argsList.toArray();
		} catch (Exception e) {
			throw new RengineException(calInfo.getServiceName(), "mushroom参数拼接错误！"+e.getMessage()+"==="+argsList);
			
		}
	}
	
	private String verification(String type,String serviceName,Map<String,Object> set,String where){
		if(null==type){
			return "缺少type参数!";
		}
		if(null==serviceName){
			return "缺少serviceName参数!";
		}
		String message=null;
		type=type.toLowerCase();
		switch (type) {
		case "c":
			if(set.size()<0){
				message = "c操作必须有set参数!";
			}
			break;
		case "u":
			if(set.size()<0){
				message = "u操作必须有set参数!";
			}else if(null==where||"".equals(where.trim())){
				message = "u操作必须有where参数!";
			}
			break;
		case "d":
			if(null==where||"".equals(where.trim())){
				message = "d操作必须有where参数!";
			}
			break;
		default:
			message = "mushroom只支持c、u、d操作!";
			break;
		}
		return message;
	}
	
	
}
