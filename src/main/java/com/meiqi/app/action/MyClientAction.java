package com.meiqi.app.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.LejjBeanUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.MyClient;
import com.meiqi.app.pojo.Region;
import com.meiqi.app.pojo.UserAddress;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.EtagService;
import com.meiqi.app.service.MyClientService;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @author fangqi
 * @date 2015年7月1日 下午1:23:53
 * @discription 客户
 */
@Service
public class MyClientAction extends BaseAction {
    private static final Logger LOG                     = Logger.getLogger(MyClientAction.class);
    private static final String MYCLIENTADDRESSPROPERTY = "consigneeId,name,detail,phone,region,regionId,regionName,headChar,parentRegion";
    @Autowired
    private MyClientService     myClientService;
    @Autowired
    private EtagService eTagService;
    
    @Override
	public String execute(HttpServletRequest request,HttpServletResponse response, AppRepInfo appRepInfo) {
    	String url = appRepInfo.getUrl();
		String method = appRepInfo.getMethod();
		String param = appRepInfo.getParam();
		Map<String, Object> header = appRepInfo.getHeader();
		long userId = (Long) header.get("userId");
		String resultJson = "";//返回json结果
		if(method.equals("get")){
			if(url.equals("consignee")){
				resultJson = getMyClientAddress(userId);
			}else if(url.equals("myClient")){
				resultJson = getMyClient(userId);
			}
			String data=resultJson;
			String key= "consignee/"+userId;
			boolean result=eTagService.toUpdatEtag1(request,response,key,data);
			if(result){
	       	     return null;
	       	}
		}else if(method.equals("put") && url.equals("consignee")){
			resultJson = addMyClientAddress(param,userId);
		}else if(method.equals("patch") && StringUtils.matchByRegex(url, "^consignee\\/\\d+$")){
			long consigneeId=Long.parseLong(url.replace("consignee/", ""));
			resultJson = updateMyClientAddress(param,consigneeId,userId);
			//eTagService.putEtagMarking("consignee/"+userId,Long.toString(System.currentTimeMillis()));
		}else if(method.equals("delete") && StringUtils.matchByRegex(url, "^consignee\\/\\d+$")){
			resultJson = deleteMyClientAddress(Long.parseLong(url.replace("consignee/", "")),userId);
			//eTagService.putEtagMarking("consignee/"+userId,Long.toString(System.currentTimeMillis()));
		}
		return resultJson;
	}


    /**
     * 获取设计师的客户信息
     * 
     * @param request
     * @return
     */
    public String getMyClient(long userId) {
        LOG.info("Function:getMyClient.Start.");
        String myClientJson = null;
        List<Users> myClientList = myClientService.getMyClientList(userId);
        myClientJson = JsonUtils.listFormatToString(myClientList);
        LOG.info("Function:getMyClient.End.");
        return myClientJson;

    }



    /**
     * 
     * @Title: getMyClientAddress
     * @Description:获取设计师的客户收货地址
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String getMyClientAddress(long userId) {
        LOG.info("Function:getMyClient.Start.");
        String myClientAddressJson = null;
        List<UserAddress> myClientAddressList = myClientService.getMyClientAddress(userId);
        myClientAddressJson = JsonUtils.listFormatToString(myClientAddressList,
                StringUtils.getStringList(MYCLIENTADDRESSPROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getMyClient.End.");
        return myClientAddressJson;

    }



    /**
     * 
     * @Title: verifyUserAddress
     * @Description:验证addresss 作废
     * @param @param myClient
     * @param @return
     * @return boolean
     * @throws
     */
    public boolean verifyMyClient(long designerId, String phone) {
        LOG.info("Function:verifyUserAddress.Start.");
        // 验证该地址是不是已经添加了
        MyClient OldMyClient = myClientService.getMyClientByProperty(designerId, phone);
        LOG.info("Function:verifyUserAddress.End.");
        return null == OldMyClient;
    }


    @SuppressWarnings("unused")
	private String verifyUserAddress(UserAddress userAddress) {
        if (null == userAddress) {
            return JsonUtils.getErrorJson("请输入正确的数据!", null);
        }
        if (StringUtils.isBlank(userAddress.getName())) {
            return JsonUtils.getErrorJson("请输入名字!", null);
        }
        if (StringUtils.isBlank(userAddress.getPhone())) {
            return JsonUtils.getErrorJson("请输入手机号!", null);
        }
        if (0 == userAddress.getRegionId()) {
            return JsonUtils.getErrorJson("请选择地区!", null);
        }
        Region region = myClientService.getRegionBySelRegionId(userAddress.getRegionId());
        if (null == region) {
            return JsonUtils.getErrorJson("请选择地区!", null);
        } else {
            userAddress.setRegion(region);
        }

        if (StringUtils.isBlank(userAddress.getDetail())) {
            return JsonUtils.getErrorJson("请输入详细地址!", null);
        }

        return null;
    }



    /**
     * 
     * @Title: getMyClientAddress
     * @Description:新增客户收货地址
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String addMyClientAddress(String param, long userId) {
        LOG.info("Function:addMyClientAddress.Start.");
        String addAddressJson = JsonUtils.getErrorJson("新增失败,请重试", null);
        UserAddress userAddress = DataUtil.parse(param, UserAddress.class);
        // 检查输入地址
        addAddressJson = verifyUserAddress(userAddress);
        if (!StringUtils.isBlank(addAddressJson)) {
            return addAddressJson;
        }
        userAddress.setUserId(userId);
        MyClient myClient = new MyClient(0, userAddress, userId, DateUtils.getSecond());
        boolean result = myClientService.addMyClientAddress(myClient);
        if (result) {
            // 添加成功 返回consigneeId
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("consigneeId", userAddress.getConsigneeId());
            addAddressJson = JsonUtils.objectFormatToString(map);
        }
        LOG.info("Function:addMyClientAddress.End.");
        return addAddressJson;
    }



    /**
     * 
     * @Title: verifyUserAddress
     * @Description:验证userAddress 是否存在 不存在为true
     * @param @param consigneeId
     * @param @return
     * @return boolean
     * @throws
     */
    public UserAddress getUserAddress(long userId, long consigneeId) {
        return myClientService.getUserAddressByConsigneeId(userId, consigneeId);
    }



    /**
     * 
     * @Title: getMyClientAddress
     * @Description:修改客户收货地址
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String updateMyClientAddress(String param,long consigneeId,long userId) {
        LOG.info("Function:addMyClientAddress.Start.");
        String updateAddressJson = JsonUtils.getErrorJson("修改失败,请重试", null);
        UserAddress userAddress = DataUtil.parse(param, UserAddress.class);
        userAddress.setUserId(userId);
        userAddress.setConsigneeId(consigneeId);
        // 检查输入地址
        updateAddressJson = verifyUserAddress(userAddress);
        if (!StringUtils.isBlank(updateAddressJson)) {
            return updateAddressJson;
        }
        UserAddress oldUserAddress = getUserAddress(userId, consigneeId);
        if (null == oldUserAddress) {
            return JsonUtils.getErrorJson("该地址不存在!", null);
        }
        LejjBeanUtils.copyProperties(oldUserAddress, userAddress);
        boolean result = myClientService.updateMyClientAddress(oldUserAddress);
        if (result) {
            updateAddressJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:updateMyClientAddress.End.");
        return updateAddressJson;
    }



    /**
     * 
     * @Title: getMyClientAddress
     * @Description:删除客户收货地址
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String deleteMyClientAddress(long consigneeId,long userId) {
        LOG.info("Function:addMyClientAddress.Start.");
        String updateAddressJson = JsonUtils.getErrorJson("修改失败,请重试", null);
        UserAddress oldUserAddress = getUserAddress(userId, consigneeId);
        if (null == oldUserAddress) {
            return JsonUtils.getErrorJson("该地址不存在!", null);
        }

        boolean result = myClientService.deleteMyClientAddress(oldUserAddress, userId);
        if (result) {
            updateAddressJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:updateMyClientAddress.End.");
        return updateAddressJson;
    }

}
