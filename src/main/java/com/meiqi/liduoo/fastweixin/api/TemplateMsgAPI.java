package com.meiqi.liduoo.fastweixin.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.entity.Industry;
import com.meiqi.liduoo.fastweixin.api.entity.TemplateMsg;
import com.meiqi.liduoo.fastweixin.api.response.AddTemplateResponse;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.liduoo.fastweixin.api.response.SendTemplateResponse;
import com.meiqi.liduoo.fastweixin.util.BeanUtil;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;

/**
 * 模版消息 api
 */
public class TemplateMsgAPI extends BaseAPI {
    private static final Logger LOG = LoggerFactory.getLogger(CustomAPI.class);

    public TemplateMsgAPI(ApiConfig config) {
        super(config);
    }

    /**
     * 设置行业
     *
     * @param industry 行业参数
     * @return 操作结果
     */
    public BaseResponse setIndustry(Industry industry) {
        LOG.debug("设置行业......");
        BeanUtil.requireNonNull(industry, "行业对象为空");
        String url = BASE_API_URL + "cgi-bin/template/api_set_industry?access_token=#";
        BaseResponse response = executePost(url, industry.toJsonString());
        return response;
    }

    /**
     * 添加模版
     *
     * @param shortTemplateId 模版短id
     * @return 操作结果
     */
    public AddTemplateResponse addTemplate(String shortTemplateId) {
        LOG.debug("获取模版id......");
        BeanUtil.requireNonNull(shortTemplateId, "短模版id必填");
        String url = BASE_API_URL + "cgi-bin/template/api_add_template?access_token=#";
        Map<String, String> params = new HashMap<String, String>();
        params.put("template_id_short", shortTemplateId);
        BaseResponse r = executePost(url, JSONUtil.toJson(params));
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        AddTemplateResponse result = JSONUtil.toBean(resultJson, AddTemplateResponse.class);
        return result;
    }

    /**
     * 发送模版消息
     *
     * @param msg 消息
     * @return 发送结果
     */
    public SendTemplateResponse send(TemplateMsg msg) {

        LOG.debug("获取模版id......");
        BeanUtil.requireNonNull(msg.getTouser(), "openid is null");
        BeanUtil.requireNonNull(msg.getTemplateId(), "template_id is null");
        BeanUtil.requireNonNull(msg.getData(), "data is null");
        BeanUtil.requireNonNull(msg.getTopcolor(), "top color is null");
        BeanUtil.requireNonNull(msg.getUrl(), "url is null");
        String url = BASE_API_URL + "cgi-bin/message/template/send?access_token=#";
        BaseResponse r = executePost(url, msg.toJsonString());
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        SendTemplateResponse result = JSONUtil.toBean(resultJson, SendTemplateResponse.class);
        return result;
    }


}
