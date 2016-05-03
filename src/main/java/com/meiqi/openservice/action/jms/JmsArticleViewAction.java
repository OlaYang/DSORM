package com.meiqi.openservice.action.jms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.Message;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.jms.producer.TextMessageProducer;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.thread.ThreadHelper;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 
 * @ClassName: JmsAction
 * @Description: TODO(记录ipad用户行为)
 * @author fangqi
 * @date 2015年12月9日 下午1:14:10
 *
 */
@Service
public class JmsArticleViewAction extends BaseAction {

    private static final Log                             LOG          = LogFactory.getLog("article");

    private static ApplicationContext                    context      = new ClassPathXmlApplicationContext(
                                                                              "spring/spring-common-jms.xml");

    private JmsTemplate                                  jmsTemplate;

    @Autowired
    private IMushroomAction                              mushroomAction;

    @Autowired
    private ThreadHelper                                 indexTheadHelper;
    
    @Autowired
    private TextMessageProducer jmsProducer;


    public Object addArticleView(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        
        Map<String, Object> param = DataUtil.parse(repInfo.getParam(),Map.class);
        ResponseInfo respInfo=new ResponseInfo();
        String article_id=param.get("article_id")==null?"":param.get("article_id").toString();
        if(StringUtils.isEmpty(article_id)){
            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription("article_id不能为空");
            return respInfo;
        }         
        jmsProducer.setDestination("meiqi/ArticleView");
        jmsProducer.publish(param);
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        return respInfo;
    }
        
    
    public void updateArticleViewIntoDB(){
        jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
        jmsTemplate.setReceiveTimeout(1000);
        Date date = new Date();
        LOG.info("Meiqi_MQ_updateArticleView_read_start:开始获取消息,时间[" + DateUtils.formatDateToString(date) + "]");
        ApplicationContext applicationContext = MyApplicationContextUtil.getContext();
        mushroomAction = (IMushroomAction) applicationContext.getBean("mushroomAction");
        int num = 5000;
        int count = 0;
        while (num > 0) {
            try {
                Message message = (Message) jmsTemplate.receive("meiqi/ArticleView");
                if (message != null) {
                    ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) message;
                    Map<String, Object> m = activeMQMapMessage.getContentMap();
                    if (null != m && m.size() > 0) {
                        LOG.info("Meiqi_MQ_updateArticleView_GetInfo:Map消息内容:"+JSONObject.toJSONString(m));
                        String article_id=m.get("article_id")==null?"":m.get("article_id").toString();
                        if(StringUtils.isNotEmpty(article_id)){
                            DsManageReqInfo reqInfo = new DsManageReqInfo();
                            reqInfo.setServiceName("MUSH_Offer");
                            List<Action> actions = new ArrayList<Action>();
                            Action action = new Action();
                            action.setType("U");
                            action.setServiceName("test_ecshop_ecs_article");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("views", "$EP.views +1");
                            action.setSet(map);
                            
                            Where where = new Where();
                            where.setPrepend("and");
                            List<SqlCondition> cons = new ArrayList<SqlCondition>();
                            SqlCondition con = new SqlCondition();
                            con.setKey("article_id");
                            con.setOp("=");
                            con.setValue(article_id);
                            cons.add(con);
            
                            where.setConditions(cons);
                            action.setWhere(where);
                            
                            actions.add(action);
                            
                            Map<String,Object> param=new HashMap<String, Object>();
                            param.put("actions", actions);
                            param.put("transaction", 1);
                            reqInfo.setParam(param);
                            
                            String res1 = mushroomAction.offer(reqInfo);
                            ActionRespInfo respInfo = DataUtil.parse(res1, ActionRespInfo.class);
                            if ("0".equals(respInfo.getCode())) {
                                LOG.info("Meiqi_MQ_updateArticleView_Mushroom_success:消息[" + DataUtil.toJSONString(reqInfo)
                                        + "]写入成功！");
                            } else {
                                LOG.info("Meiqi_MQ_updateArticleView_Mushroom_error:消息[" + DataUtil.toJSONString(reqInfo)
                                        + "]写入失败,[" + res1 + "]");
                            }
                        }
                    } else {
                        LOG.info("Meiqi_MQ_updateArticleView_GetInfo:Map消息内容为空！");
                    }
                    num--;
                    count++;
                } else {
                    num = 0;
                    LOG.info("Meiqi_MQ_updateArticleView_read_empty:消息已为空,时间[" + DateUtils.formatDateToString(new Date()) + "]");
                    break;
                }
            } catch (Exception e) {
                LOG.info("Meiqi_MQ_updateArticleView_read_exception:" + e.getMessage() + ",时间["
                        + DateUtils.formatDateToString(new Date()) + "]");
                num = 0;
            }
        }
        LOG.info("Meiqi_MQ_updateArticleView_read_end:[" + count + "]条消息获取、写入数据库结束,时间["
                + DateUtils.formatDateToString(new Date()) + "],共耗时[" + (System.currentTimeMillis() - date.getTime())
                + "]");
    }
}
