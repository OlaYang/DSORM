package com.meiqi.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Shop;
import com.meiqi.app.pojo.Store;
import com.meiqi.app.service.ShopService;
import com.meiqi.app.service.utils.ImageService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

@Service
public class ShopServiceImpl implements ShopService {
    private static final Logger LOG = Logger.getLogger(ShopServiceImpl.class);
    @Autowired
    private IDataAction         dataAction;



    @Override
    public Shop getShopByGoodsId(long goodsId, int cityId) {
        LOG.info("Function:getShopByGoodsId.Start.");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goods_id", goodsId);
        if (-1 != cityId) {
            param.put("city_id", cityId);
        }
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("IPAD_HSV1_GoodsShop");
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
        JSONArray shopJson = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) shopJson;
        Shop shop = null;
        try {
            if (mapListJson != null && mapListJson.size() > 0) {
                shop = (Shop) BeanBuilder.buildBean(Shop.class.newInstance(), mapListJson.get(0));
                if (null == shop || StringUtils.isBlank(shop.getName())) {
                    return null;
                }
                // 图片地址前缀
                shop.setLogo(ImageService.getHaveImagePerfixUrl(shop.getLogo()));
                String storeAddress = shop.getStoreAddress();
                if (!StringUtils.isBlank(storeAddress)) {
                    JSONArray storeJson = JSONArray.fromObject(storeAddress);
                    if (null != storeJson && storeJson.size() > 0) {
                        List<Store> storeList = JSON.parseArray(storeAddress, Store.class);
                        if (!CollectionsUtils.isNull(storeList)) {
                            for (Store store : storeList) {
                                // 图片地址前缀
                                store.setLogo(ImageService.getHaveImagePerfixUrl(store.getLogo()));
                                store.setRemark(AppSysConfig.getValue("store_remark_start") + store.getStoreName()
                                        + AppSysConfig.getValue("store_remark_end"));
                            }
                            shop.setStoreList(storeList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        LOG.info("Function:getShopByGoodsId.End.");
        return shop;
    }

}
