/**
 * Copyright (c) meiqi
 * 百度推广相关api接口实现
 * @Title: DeleteCamaignAction.java 
 * @author wanghuanwei
 * @since 2015.7.13
 * @Desciption 实现百度推广（搜索推广和网盟推广）相关API
 * 
 * */
package com.meiqi.openservice.action.javabin.campaign;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.baidu.drapi.autosdk.core.CommonService;
import com.baidu.drapi.autosdk.core.ResHeader;
import com.baidu.drapi.autosdk.core.ResHeaderUtil;
import com.baidu.drapi.autosdk.core.ServiceFactory;
import com.baidu.drapi.autosdk.exception.ApiException;
import com.baidu.drapi.autosdk.sms.service.CampaignService;
import com.baidu.drapi.autosdk.sms.service.CampaignType;
import com.baidu.drapi.autosdk.sms.service.DeleteCampaignRequest;
import com.baidu.drapi.autosdk.sms.service.DeleteCampaignResponse;
import com.meiqi.openservice.bean.RepInfo;

/**
 * 提供推广计划删除功能
 * @author wanghuanwei
 * @date 2015/7/13
 * @version 1.0
 * */
@Service
public class DeleteCamaignAction {

    public void deleteCamaign(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        
    }
    
    private void innderDeleteCampaign(List<CampaignType> infos)
    {
        DeleteCampaignRequest request = new DeleteCampaignRequest();
        List<Long> ids = new ArrayList<Long>();
        for (CampaignType campaignType : infos) {
            ids.add(campaignType.getCampaignId());
        }
        request.setCampaignIds(ids);
        
        CommonService factory;
        try {
            factory = ServiceFactory.getInstance();
            CampaignService campaignService = factory.getService(CampaignService.class);
            
            @SuppressWarnings("unused")
            DeleteCampaignResponse response = campaignService.deleteCampaign(request);
            ResHeader rheader = ResHeaderUtil.getResHeader(campaignService, true);
            if (rheader.getStatus() != 0) {
                // TODO 删除失败
            }
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
}
