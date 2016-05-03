/**
 * @Title: RegisterAction.java
 * @Package com.meiqi.openservice.action.register
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * @version V1.0
 */
package com.meiqi.openservice.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.pay.alipay.sign.Base64;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.RsaKeyTools;
import com.meiqi.openservice.commons.util.StringUtils;

/**
 * @ClassName: RegisterAction
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * 
 */
@Service
public class GetAction extends BaseAction {

    @Autowired
    private IDataAction dataAction;

    @Autowired
    private EtagService eTagService;



    public String get(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        String rsaverifyopen_get=RsaKeyTools.getRsaConfig("rsaverifyopen_get");
        if("1".equals(rsaverifyopen_get)){
            // RSA授权认证
            boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
            if (!rsaVerify) {
                ResponseInfo respInfo = new ResponseInfo();
                respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                return JSONObject.toJSONString(respInfo);
            }
        }
        
        String content = repInfo.getParam();
        DsManageReqInfo dsReqInfo = DataUtil.parse(content, DsManageReqInfo.class);
        String resultData = dataAction.getData(dsReqInfo, repInfo.getMemKey().trim());
        if (BaseAction.isFromApp(request)) {
                StringBuffer key = new StringBuffer();
                key.append(BaseAction.getPlatString(request)).append(BaseAction.validationAuthorization(request))
                        .append(Base64.encode(content.getBytes()));
                // 支持 2951 添加304缓存
                boolean result = eTagService.toUpdatEtag1(request, response, key.toString(), resultData);
                if (result) {
                    return null;
                }
        }
        return resultData;
    }
    
    public String gets(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        String rsaverifyopen_get=RsaKeyTools.getRsaConfig("rsaverifyopen_get");
        if("1".equals(rsaverifyopen_get)){
            // RSA授权认证
            boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
            if (!rsaVerify) {
                ResponseInfo respInfo = new ResponseInfo();
                respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                return JSONObject.toJSONString(respInfo);
            }
        }
        String content = repInfo.getParam();
        DsManageReqInfo dsReqInfo = DataUtil.parse(content, DsManageReqInfo.class);
        String resultData = dataAction.getDatas(dsReqInfo, repInfo.getMemKey().trim());
        if (BaseAction.isFromApp(request)) {
            StringBuffer key = new StringBuffer();
            key.append(BaseAction.getPlatString(request)).append(BaseAction.validationAuthorization(request))
                    .append(Base64.encode(content.getBytes()));
            // 支持 2951 添加304缓存
            boolean result = eTagService.toUpdatEtag1(request, response, key.toString(), resultData);
            if (result) {
                return null;
            }
        }
        return resultData;
    }
    
    public void getSec(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        RsaKeyTools.doRSA(request, response,repInfo);
    }
}
