/**
 * Copyright (c) meiqi
 * 百度推广相关api接口实现
 * @Title: GetCampaignAction.java 
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
import com.baidu.drapi.autosdk.sms.service.GetCampaignRequest;
import com.baidu.drapi.autosdk.sms.service.GetCampaignResponse;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;

@Service
public class GetCampaignAction extends BaseAction{

    public List<String> getCampaignes(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo)
    {
        List<String> data = null;
        return data;
    }
    
    
    private List<CampaignType> innerGetCampaignTypes(List<CampaignType> infos, List<String> fields)
    {
        GetCampaignRequest request = new GetCampaignRequest();
        List<CampaignType> data = null;
        List<Long> ids = null;
        
        GetCampaignResponse response = null;
        if (infos != null && infos.size() > 0) {
            // 传入的campaignid数量不为0，获取指定的campaigntype
            ids = new ArrayList<Long>();
            for (CampaignType campaignType : infos) {
                ids.add(campaignType.getCampaignId());
            }
        }
        for (CampaignType campaignType : infos) {
            ids.add(campaignType.getCampaignId());
        }
        request.setCampaignFields(fields);
        request.setCampaignIds(ids);
        try {
            CommonService factory = ServiceFactory.getInstance();
            CampaignService campaignService = factory.getService(CampaignService.class);
            response = campaignService.getCampaign(request);
        
            ResHeader rheader = ResHeaderUtil.getResHeader(campaignService, true);
            if (rheader.getStatus() != 0) {
                // 获取信息出错
                
            }
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        data = response.getData();

        return data;
    }
}
