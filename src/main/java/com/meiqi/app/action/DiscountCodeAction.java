package com.meiqi.app.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.DiscountInfo;
import com.meiqi.app.pojo.DiscountInfoList;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.DiscountCodeService;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.util.DataUtil;

@Service
public class DiscountCodeAction extends BaseAction {
    private static final Logger LOG                        = Logger.getLogger(DiscountCodeAction.class);

    private static final String DISCOUNTINFO_LIST_PROPERTY = "sendTime,receivePhone";

    @Autowired
    private DiscountCodeService discountCodeService;

    @Autowired
    private EtagService         eTagService;



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        long userId = StringUtils.StringToLong(appRepInfo.getHeader().get("userId").toString());
        String param = appRepInfo.getParam();
        if (url.equals("discountCodeList")) { // /discountCodeList GET
            // 2.5.4.4 获取折扣码发送记录（list）
            String data = getDiscountInfoList(userId, param, getPlatString(request));
            String key = "discountCodeList";
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;
        } else if (url.equals("discountCodeTotal")) { // /discountCodeTotal GET
            // 2.5.4.3 获取折扣码发送记录（total）
            String data = getDiscountInfoTotal(userId);
            String key = "discountCodeTotal";
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;
        }

        return null;
    }



    private String getDiscountInfoTotal(long userId) {
        LOG.info("Function:getDiscountInfoTotal.Start.");
        DiscountInfoList discountInfoList = new DiscountInfoList();
        int total = discountCodeService.getDiscountInfoTotal(userId);
        discountInfoList.setTotal(total);
        LOG.info("Function:getDiscountInfoTotal.End.");
        return JsonUtils.objectFormatToString(discountInfoList);
    }



    private String getDiscountInfoList(long userId, String param, String plat) {
        LOG.info("Function:getDiscountCodeList.Start.");
        DiscountInfo discountInfo = DataUtil.parse(param, DiscountInfo.class);
        int pageIndex = 0;
        int pageSize = 0;
        if (null != discountInfo) {
            pageIndex = discountInfo.getPageIndex();
            pageSize = discountInfo.getPageSize();
        }
        String discountInfoListJson = null;
        List<DiscountInfo> discountInfoList = discountCodeService
                .getDiscountInfoList(userId, pageIndex, pageSize, plat);
        discountInfoListJson = JsonUtils.listFormatToString(discountInfoList,
                StringUtils.getStringList(DISCOUNTINFO_LIST_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getDiscountCodeList.End.");
        return discountInfoListJson;
    }

}
