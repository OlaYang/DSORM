package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Company;
import com.meiqi.app.pojo.Region;
import com.meiqi.app.service.CompanyService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: CompanyServiceimpl
 * @Description:
 * @author 杨永川
 * @date 2015年4月23日 下午5:22:28
 *
 */
@Service
public class CompanyServiceImpl implements CompanyService {
    private static final Logger LOG = Logger.getLogger(CompanyServiceImpl.class);

    @Autowired
    private IDataAction         dataAction;



    /**
     * 
     * @Title: getCompanyByregion
     * @Description:获取公司 门店信息,通过id
     * @param @param regionId
     * @param @return
     * @throws
     */
    @Override
    public Company getCompanyById(long id, int type) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", id);
        paramMap.put("type", type);
        List<Company> companyList = getCompany(paramMap);
        if (!CollectionsUtils.isNull(companyList)) {
            return companyList.get(0);
        }
        return null;
    }



    /**
     * 
     * @Title: getCompanyByregion
     * @Description:获取公司 门店信息,通过区域
     * @param @param regionId
     * @param @return
     * @throws
     */
    @Override
    public List<Company> getCompanyByregion(Company companyParam) {
        LOG.info("Function:getCompanyByregion.Start.");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        int pageIndex = companyParam.getPageIndex();
        int pageSize = companyParam.getPageSize();
        if (0 == pageSize) {
            // 默认
            pageSize = 10;
        }
        paramMap.put("city_id", companyParam.getRegionId());
        paramMap.put("type", companyParam.getType());
        paramMap.put("limit_start", pageIndex * pageSize);
        paramMap.put("limit_end", (pageIndex + 1) * pageSize);
        List<Company> companyList = getCompany(paramMap);
        LOG.info("Function:getCompanyByregion.End.");
        return companyList;
    }



    /**
     * 
     * @Title: getCompanyByregion
     * @Description:获取公司信息
     * @param @param regionId
     * @param @return
     * @throws
     */
    private List<Company> getCompany(Map<String, Object> paramMap) {
        LOG.info("Function:getCompany.Start.");
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("IOS_HSV1_com_shop");// 规则名称
        dsReqInfo.setParam(paramMap);
        dsReqInfo.setNeedAll("1");
        String resultData = dataAction.getData(dsReqInfo,"");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        String companyInfo = responseBaseData.getRows().get(0).get("info");
        if (StringUtils.isBlank(companyInfo)) {
            return null;
        }
        List<Company> companyList = new ArrayList<Company>();
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(companyInfo);
        List<Map<String, Object>> mapListJson = (List) json;
        for (int i = 0; i < mapListJson.size(); i++) {
            Map<String, Object> obj = mapListJson.get(i);
            // map 转换 object
            try {
                Company company = (Company) BeanBuilder.buildBean(Company.class.newInstance(), obj);
                if (null != company) {
                    if (!StringUtils.isBlank(company.getCityStr())) {
                        Region region = DataUtil.parse(company.getCityStr(), Region.class);
                        company.setCity(region);
                    }
                    companyList.add(company);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        LOG.info("Function:getCompany.End.");
        return companyList;
    }



    @Override
    public int getCompanyTotal(Company companyParam) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        long cityId = companyParam.getRegionId();
        int type = companyParam.getType();
        paramMap.put("city_id", cityId);
        paramMap.put("type", type);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("IOS_HSV1_com_shop_count");// 规则名称
        dsReqInfo.setParam(paramMap);
        dsReqInfo.setNeedAll("1");
        String resultData = dataAction.getData(dsReqInfo,"");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return 0;
        }
        String companyTotal = responseBaseData.getRows().get(0).get("total");
        if (StringUtils.isBlank(companyTotal)) {
            return 0;
        }

        return StringUtils.StringToInt(companyTotal);
    }
}
