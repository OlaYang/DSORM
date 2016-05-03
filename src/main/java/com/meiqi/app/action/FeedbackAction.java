package com.meiqi.app.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Feedback;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.FeedbackService;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: FeedbackController
 * @Description:
 * @author 杨永川
 * @date 2015年5月26日 下午6:26:53
 *
 */
@Service
public class FeedbackAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(FeedbackAction.class);
    @Autowired
    private FeedbackService     feedbackService;



    public FeedbackService getFeedbackService() {
        return feedbackService;
    }



    public void setFeedbackService(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }



    /**
     * 
     * @Title: verifyFeedback
     * @Description:验证反馈信息
     * @param @param feedback
     * @param @return
     * @return String
     * @throws
     */
    private String verifyFeedback(Feedback feedback) {
        if (null == feedback) {
            return JsonUtils.getErrorJson("反馈不能空", null);
        }
        if (StringUtils.isBlank(feedback.getMsgContent())) {
            return JsonUtils.getErrorJson("反馈不能空", null);
        }
        return "";
    }



    /**
     * 
     * @Title: addFeedback
     * @Description:
     * @param @param feedback
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String addFeedback(@RequestBody(required = true) Feedback feedback, HttpServletRequest request) {
        LOG.info("Function:addFeedback.Start.");
        long userId = validationAuthorization(request);
        // 授权验证
        if (0 == userId) {
            return JsonUtils.getAuthorizationErrorJson();
        }
        String addFeedbackJson = "";
        // 验证反馈信息
        addFeedbackJson = verifyFeedback(feedback);
        if (!StringUtils.isBlank(addFeedbackJson)) {
            return addFeedbackJson;
        }
        feedback.setUserId(userId);
        // TODO request.getHeaderNames("user-agent");

        feedback.setPlat(getPlatInt(request));
        long addFeedbackId = feedbackService.addFeedback(feedback);
        if (0 == addFeedbackId) {
            addFeedbackJson = JsonUtils.getErrorJson("提交反馈信息失败,请重试.", null);
        } else {
            addFeedbackJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:addFeedback.End.");
        return addFeedbackJson;
    }



	/*
	* Title: execute
	* Description: 
	* @param request
	* @param appRepInfo
	* @return 
	* @see com.meiqi.app.action.IBaseAction#execute(javax.servlet.http.HttpServletRequest, com.meiqi.app.pojo.dsm.AppRepInfo) 
	*/
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response, AppRepInfo appRepInfo) {
		String url = appRepInfo.getUrl();
        if (url.contains("feedback")) {
        	String param;
			param = appRepInfo.getParam();
			Feedback feedback=(Feedback)DataUtil.parse(param, Feedback.class);
	        return addFeedback(feedback,request);
        } 
        return null;
	}
}
