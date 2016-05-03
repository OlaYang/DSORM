package com.meiqi.openservice.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.ValidateXmlUtil;
import com.meiqi.openservice.service.IApplyService;
import com.meiqi.openservice.vo.ApplyVo;

@Service
public class ApplyService implements IApplyService
{
    
    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    
    private final static String DB_MSG = "数据插入有误";
    
    @Autowired
    private IDataAction dataAction;
    
    @Autowired
    private IMushroomAction mushroomAction;
    
    @Override
    public ResponseInfo saveRegisterinfo(String xmlPath, String xsdPath, ApplyVo apply)
        throws Exception
    {
        ResponseInfo resp = new ResponseInfo();
        if (StringUtils.isBlank(apply.getFormName()))
        {
            return new ResponseInfo(DsResponseCodeData.ERROR.code, DB_MSG);
        }
        SAXReader reader = new SAXReader();
        Document document;
        document = reader.read(new File(xmlPath));
        Element el = document.getRootElement();
        el.element("registerName").setText(apply.getRegisterName());
        el.element("province").setText(String.valueOf(apply.getProvince()));
        el.element("city").setText(String.valueOf(apply.getCity()));
//        el.element("district").setText(String.valueOf(apply.getDistrict()));
        el.element("budget").setText(apply.getBudget());
        // TODO 代码存在问题有待优化
        if (StringUtils.isBlank(apply.getDecorateDate()) || StringUtils.equals(apply.getDecorateDate().trim(), "请选择"))
        {
            el.element("decorateDate").setText(SDF.format(new Date()));
        }
        else
        {
            el.element("decorateDate").setText(apply.getDecorateDate());
        }
        el.element("phone").setText(apply.getPhone());
        Map<String, Object> map = ValidateXmlUtil.viladateMsg(document, xsdPath, apply.getFormName());
        boolean flag = Boolean.parseBoolean(String.valueOf(map.get(ValidateXmlUtil.FLAG_TAG)));
        if (flag)
        {
            if (StringUtils.isBlank(apply.getDecorateDate())
                || StringUtils.equals(apply.getDecorateDate().trim(), "请选择"))
            {
                String time = new Date().getTime() / 1000 + "";
                apply.setDecorateDate(time);
            }
            else
            {
                String time = SDF.parse(apply.getDecorateDate()).getTime() / 1000 + "";
                apply.setDecorateDate(time);
            }
            apply.setAddDate(Integer.parseInt(String.valueOf(System.currentTimeMillis() / 1000)));
            String maxId = getMaxId(apply);
            if (StringUtils.isEmpty(maxId))
            {
                String res = saveApply(apply);
                ActionRespInfo actionResponse = DataUtil.parse(res, ActionRespInfo.class);
                if (DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode()))
                {
                    resp.setCode(DsResponseCodeData.SUCCESS.code);
                    resp.setDescription(DsResponseCodeData.SUCCESS.description);
                    resp.setObject(false);
                }
                else
                {
                    resp.setCode(DsResponseCodeData.ERROR.code);
                    resp.setDescription(actionResponse.getDescription());
                }
            }
            else
            {//重复申请
                resp.setCode(DsResponseCodeData.SUCCESS.code);
                resp.setDescription(DsResponseCodeData.SUCCESS.description);
                resp.setObject(true);
            }
        }
        else
        {
            resp.setCode(DsResponseCodeData.ERROR.code);
            resp.setDescription(String.valueOf(map.get(ValidateXmlUtil.ERROR_MSG)).trim());
        }
        return resp;
    }

    private String saveApply(ApplyVo apply)
    {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        
        Action action = new Action();
        action.setType("C");
        action.setServiceName("decorate_info_apply");
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("user_name", apply.getRegisterName());
        set.put("phone", apply.getPhone());
        set.put("scheduled_time", apply.getDecorateDate());
        set.put("budget", apply.getBudget());
        set.put("province", apply.getProvince());
        set.put("city", apply.getCity());
        set.put("district", apply.getDistrict());
        set.put("add_time", apply.getAddDate());
        set.put("from_user_id", apply.getUserId());
        set.put("qq", apply.getQq());
        set.put("building_name", apply.getBuildingName());
        set.put("type", apply.getType());
        set.put("source_id", apply.getSourceId());
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        actionReqInfo.setActions(actions);
        String res = mushroomAction.offer(actionReqInfo);
        return res;
    }
    
    private String getMaxId(ApplyVo apply)
    {
        // 查询当天是否已经申请过
        String repeatServiceName = "MFSJ_BUV1_decorateinfo";// 获取当天已经申请过的最新记录id
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName(repeatServiceName);
        Map<String, Object> param = new HashMap<String, Object>();
//        param.put("registerName", apply.getRegisterName());
        param.put("phone", apply.getPhone());
//        param.put("province", apply.getProvince());
//        param.put("city", apply.getCity());
//        param.put("district", apply.getDistrict());
        serviceReqInfo.setParam(param);
        serviceReqInfo.setNeedAll("1");
        
        RuleServiceResponseData responseData = null;
        String data = dataAction.getData(serviceReqInfo,"");
        responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        List<Map<String, String>> rows = responseData.getRows();
        String maxId = null;
        if (rows != null && !rows.isEmpty())
        {
            maxId = rows.get(0).get("maxId");
        }
        return maxId;
    }
    
}
