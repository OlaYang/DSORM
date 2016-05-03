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
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Company;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.CompanyService;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: CompanyController
 * @Description:
 * @author 杨永川
 * @date 2015年4月23日 下午5:24:34
 *
 */
@Service
public class CompanyAction extends BaseAction {
    private static final Logger LOG                   = Logger.getLogger(CompanyAction.class);
    private static final String COMPANY_PROPERTY_LIST = "companyId,companyName,city,regionId,parentId,regionName,headChar,companyList";
    @Autowired
    private CompanyService      companyService;
    @Autowired
    private EtagService         eTagService;



    /**
     * 
     * :获取公司
     *
     * @param request
     * @param response
     * @param appRepInfo
     * @return
     */
    private String getCompanyTotal(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        LOG.info("Function:getCompanyTotal.Start.");
        String url = appRepInfo.getUrl();
        Company companyParam = DataUtil.parse(appRepInfo.getParam(), Company.class);
        String checkResult = checkCompanyParam(companyParam);
        if (!StringUtils.isBlank(checkResult)) {
            // 请求参数验证不通过
            return checkResult;
        }
        String companyTotalJson = null;

        int companyTotal = companyService.getCompanyTotal(companyParam);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("totalSize", companyTotal);
        companyTotalJson = JsonUtils.objectFormatToString(map);
        // 304 缓存
        StringBuffer key = new StringBuffer();
        key.append(url).append("/").append(companyParam.getRegionId()).append("/").append(companyParam.getType());
        boolean result = eTagService.toUpdatEtag1(request, response, key.toString(), companyTotalJson);
        if (result) {
            return null;
        }
        LOG.info("Function:getCompanyTotal.End.");
        return companyTotalJson;
    }



    /**
     * 
     * @Title: getCompanyByregion
     * @Description:获取公司或者门店信息,通过区域
     * @param @return
     * @return String
     * @throws
     */
    public String getCompanyByregion(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        LOG.info("Function:getCompanyByregion.Start.");
        String url = appRepInfo.getUrl();
        Company companyParam = DataUtil.parse(appRepInfo.getParam(), Company.class);
        String checkResult = checkCompanyParam(companyParam);
        if (!StringUtils.isBlank(checkResult)) {
            // 请求参数验证不通过
            return checkResult;
        }
        String companyJson = null;
        List<Company> companyList = companyService.getCompanyByregion(companyParam);
        companyJson = JsonUtils.listFormatToString(companyList,
                 StringUtils.getStringList(COMPANY_PROPERTY_LIST, ContentUtils.COMMA));
        StringBuffer key = new StringBuffer();
        key.append(url).append("/").append(companyParam.getRegionId()).append("/").append(companyParam.getType());
        boolean result = eTagService.toUpdatEtag1(request, response, key.toString(), companyJson);
        if (result) {
            return null;
        }
        LOG.info("Function:getCompanyByregion.End.");
        return companyJson;

    }



    /**
     * 
     * 检查获取公司门店参数
     *
     * @param companyParm
     * @return
     */
    private String checkCompanyParam(Company companyParam) {
        if (null == companyParam) {
            return JsonUtils.getErrorJson("请求参数不正确!", null);
        } else if (companyParam.getRegionId() <= 0) {
            return JsonUtils.getErrorJson("区域id不正确!", null);
        } else if (1 != companyParam.getType() && 2 != companyParam.getType()) {
            return JsonUtils.getErrorJson("合作单位类型不正确!", null);
        }

        return null;

    }



    /*
     * Title: execute Description:
     * 
     * @param request
     * 
     * @param appRepInfo
     * 
     * @return
     * 
     * @see com.meiqi.app.action.IBaseAction#execute(javax.servlet.http.
     * HttpServletRequest, com.meiqi.app.pojo.dsm.AppRepInfo)
     */
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        if ("companyTotal".equals(url)) {
            return getCompanyTotal(request, response, appRepInfo);
        } else if ("company".equals(url)) {
            return getCompanyByregion(request, response, appRepInfo);
        }

        return null;
    }

}
