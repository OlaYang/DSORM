package com.meiqi.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.pojo.Store;
import com.meiqi.app.service.StoreService;
import com.meiqi.app.service.utils.SMSService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

@Service
public class StoreServiceImpl implements StoreService {
    private static final Logger LOG = Logger.getLogger(StoreServiceImpl.class);
    @Autowired
    private IDataAction         dataAction;



    /**
     * 
     * @Title: getStoreByStoreId
     * @Description:获取store
     * @param @param storeId
     * @param @return
     * @throws
     */
    @Override
    public Store getStoreByStoreId(long goodsId) {
        LOG.info("Function:getStoreByStoreId.Start.");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goods_id", goodsId);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("App_HSV1_GoodsShop");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) json;
        Store store = null;
        try {
            if (mapListJson != null && mapListJson.size() > 0) {
                store = (Store) BeanBuilder.buildBean(Store.class.newInstance(), mapListJson.get(0));
                if (null == store || 0 == store.getStoreId()) {
                    return null;
                }
                // bug:1789
                // 设置 备注“xx”负责发货并提供售后服务，“xx”读取店铺名
                store.setRemark(AppSysConfig.getValue("store_remark_start") + store.getStoreName()
                        + AppSysConfig.getValue("store_remark_end"));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        LOG.info("Function:getStoreByStoreId.End.");
        return store;
    }



    /**
     * 发送店铺信息
     */
    @Override
    public boolean sendStoreInfo(long userId, String phone, String msg, Map<String, Object> param) {
        LOG.info("Function:sendStoreInfo.Start.");
        SMSService.sendStoreInfo(phone, param);
        LOG.info("Function:sendStoreInfo.End.");
        return true;
    }

}
