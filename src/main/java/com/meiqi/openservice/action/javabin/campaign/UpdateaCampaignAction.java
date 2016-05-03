/**
 * Copyright (c) meiqi
 * 百度推广相关api接口实现
 * @Title: UpdateaCampaignAction.java 
 * @author wanghuanwei
 * @since 2015.7.13
 * @Desciption 实现百度推广（搜索推广和网盟推广）相关API
 * 
 * */
package com.meiqi.openservice.action.javabin.campaign;

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
import com.baidu.drapi.autosdk.sms.service.UpdateCampaignRequest;
import com.baidu.drapi.autosdk.sms.service.UpdateCampaignResponse;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;

@Service
public class UpdateaCampaignAction extends BaseAction{

    public List<String> updateCampaign(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo)
    {
        List<String> dataList = null;
        return dataList;
    }
    
    private List<CampaignType> innerUpdateCampaignTypes(List<CampaignType> infos, List<String> fiels)
    {
        UpdateCampaignRequest request = new UpdateCampaignRequest();
        List<CampaignType> data = null;
        List<Long> ids = null;
        try {
            CommonService factory = ServiceFactory.getInstance();
            CampaignService campaignService = factory.getService(CampaignService.class);
            UpdateCampaignResponse response = campaignService.updateCampaign(request);
            ResHeader rheader = ResHeaderUtil.getResHeader(campaignService, true);
            
            if (rheader.getStatus() != 0) {
                // TODO 更新失败
            }
            
            data = response.getData();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return data;
    }
}
