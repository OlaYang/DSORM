package com.meiqi.openservice.action.jms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import net.sf.json.JSONArray;

import org.apache.activemq.Message;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.store.kahadb.KahaDBStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.PictureInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 
 * @ClassName: PictureMessageAction
 * @Description: TODO(将图片的曝光量和点击量写入数据库)
 * @author fangqi
 * @date 2015年9月6日 下午4:19:22
 *
 */
@Service
public class PictureMessageAction extends BaseAction {

    private static final int          DEFAULT_PRIORITY = 4;
    private static ApplicationContext context          = new ClassPathXmlApplicationContext(
                                                               "spring/spring-common-jms.xml");
    private JmsTemplate               jmsTemplate;

    private static final String mapKey = "lejj_mq_info";//获取图片或者案例ID的key值
    
    @Autowired
    private IMushroomAction           mushroomAction;



    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }



    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }


    /**
     * 
     * @Title: setPictureExposureInfo
     * @Description: TODO(曝光量写入数据库)
     * @param 参数说明
     * @return void 返回类型
     * @throws
     */
    public void receiveMQInfo() {
            if("false".equals(SysConfig.getValue("isOpenMQ"))){
                return ;
            }
            long time = System.currentTimeMillis();
            jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
            jmsTemplate.setReceiveTimeout(1000);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            LogUtil.info("Meiqi_MQ_Pictrue_Message:开始获取消息！");
            int num = 1500;
            while (num > 0) {
                try {
                    Message message = (Message) jmsTemplate.receive("meiqi/picExposure");
                    long endTime = System.currentTimeMillis();
                    if (message != null) {
                        ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) message;
                        Map<String, Object> m = activeMQMapMessage.getContentMap();
                        list.add(m);
                        num--;
                        if(endTime-time>60000){
                            break;
                        }
                    } else {
                        num=0;
                        LogUtil.info("Meiqi_MQ_Pictrue_Message:消息已为空！");
                        break;
                    }
                } catch (Exception e) {
                    LogUtil.info("Meiqi_MQ_Pictrue_Message:" + e.getMessage());
                    //e.printStackTrace();
                    num=0;
                }
            }
            if(null!=list&&list.size()>0){
                dealMQInfo(list);
            }
    }


    /**
     * 
     * @Title: dealMQInfo
     * @Description: TODO(整合图片的曝光量和点击量)
     * @param @param list 参数说明
     * @return void 返回类型
     * @throws
     */
    public void dealMQInfo(List<Map<String, Object>> list) {
        long time = System.currentTimeMillis();
        Map<String,PictureInfo> picMap = new HashMap<String,PictureInfo>();
        LogUtil.info("Meiqi_MQ_Pictrue_Object:开始处理信息！");
        for (Map<String, Object> map : list) {
            JSONArray jsonArr = JSONArray.fromObject(map.get(mapKey));// 获取前端传入图片id数组
            String markUrl = String.valueOf(map.get("currentUrl"));// 当前url
            String type = String.valueOf(map.get("type"));// 当前类别：0代表装修效果图，1代表装修案例;
            int size = jsonArr.size();
            for (int i = 0; i < jsonArr.size(); i++) {
                String id = String.valueOf(jsonArr.get(i));
                String mapKey = id+"_"+markUrl+"_"+type;
                PictureInfo picInfo = null;
                if (picMap.containsKey(mapKey)) {
                    picInfo = picMap.remove(mapKey);
                    if(size==1){//点击量更新
                        picInfo.setClick_num(picInfo.getClick_num() + 1);
                    }else{//曝光量更新
                        picInfo.setExposure_num(picInfo.getExposure_num() + 1);
                    }
                }else{
                    picInfo = new PictureInfo(id, type, markUrl);
                }
                picMap.put(mapKey, picInfo);
            }
        }
//        System.out.println(list.size()+"条消息，对象化处理耗时(ms)："+(System.currentTimeMillis()-time));
        LogUtil.info("Meiqi_MQ_Pictrue_Object:"+list.size()+"条消息对象化处理完成，耗时["+(System.currentTimeMillis()-time)+"ms]");
        insertMQInfo2DB(picMap);
    }



    /**
     * 
     * @Title: insert2DB
     * @Description: TODO(将消息写入数据库)
     * @param @param set 参数说明
     * @return void 返回类型
     * @throws
     */
    public void insertMQInfo2DB(Map<String,PictureInfo> map) {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        String serviceName = "meiqiserver_lejj_exposure_info";
        List<Action> actions = new ArrayList<Action>();
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        LogUtil.info("Meiqi_MQ_Pictrue_Mushroom:消息写入Action");
        while (it.hasNext()) {
            PictureInfo picInfo = map.get(it.next());
            Action action = new Action();
            action.setType("C");
            action.setServiceName(serviceName);
            Map<String, Object> mapSet = new HashMap<String, Object>();
            mapSet.put("val_id", picInfo.getVal_id());
            mapSet.put("val_type", picInfo.getVal_type());
            mapSet.put("mark_url", picInfo.getMark_url());
            mapSet.put("exposure_num", picInfo.getExposure_num());
            mapSet.put("click_num", picInfo.getClick_num());
            action.setSet(mapSet);
            actions.add(action);
        }
        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        ApplicationContext applicationContext=MyApplicationContextUtil.getContext();
        mushroomAction=(IMushroomAction)applicationContext.getBean("mushroomAction");
        LogUtil.info("Meiqi_MQ_Pictrue_Mushroom:调用mushroom，将数据写入数据库！");
        long get = System.currentTimeMillis();
        String res1 = mushroomAction.offer(actionReqInfo);
        ActionRespInfo respInfo = DataUtil.parse(res1, ActionRespInfo.class);
        if(respInfo.getCode().equals("0")){
//            System.out.println(actions.size()+"消息入库时间："+(System.currentTimeMillis()-get));
            LogUtil.info("Meiqi_MQ_Pictrue_Mushroom:消息写入成功，耗时["+(System.currentTimeMillis()-get)+"ms]");
        }else{
            LogUtil.info("Meiqi_MQ_Pictrue_Mushroom:消息写入失败！["+res1+"]");
        }
    }
    
    public void pp(){
        /*String jmxDomain = "jms-broker";
        int connectorPort = 2011;
        String connectorPath = "/jmxrmi";
        
        String queueName = "meiqi/picExposure";
        
        BrokerService broker = new BrokerService();

        // 以下是持久化的配置
        // 持久化文件存储位置
        File dataFilterDir = new File("activemq/amq-in-action/kahadb");
        KahaDBStore kaha = new KahaDBStore();
        kaha.setDirectory(dataFilterDir);
        // use a bigger journal file
        kaha.setJournalMaxFileLength(1024*100);
        // small batch means more frequent and smaller writes
        kaha.setIndexWriteBatchSize(100);
        // do the index write in a separate thread
        kaha.setEnableIndexWriteAsync(true);
       
        try {
            broker.setPersistenceAdapter(kaha);
            broker.addConnector("tcp://jms.lejj.com:61616");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        broker.setUseJmx(true);
       

        // 以下是ManagementContext的配置，从这个容器中可以取得消息队列中未执行的消息数、消费者数、出队数等等
        // 设置ManagementContext
        ManagementContext context = broker.getManagementContext();
        context.
        context.setConnectorPort(connectorPort);
        context.setJmxDomainName(jmxDomain);
        context.setConnectorPath(connectorPath);
        try {
            broker.start();
        } catch (Exception e) {
            e.printStackTrace();
        } */
    }
    
    public void tp(){
        /*JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"+
                RunServer.connectorPort+RunServer.connectorPath);
        JMXConnector connector = JMXConnectorFactory.connect(url, null);
        connector.connect();
        MBeanServerConnection connection = connector.getMBeanServerConnection();

         // 需要注意的是，这里的jms-broker必须和上面配置的名称相同
        ObjectName name = new ObjectName(RunServer.jmxDomain+":BrokerName=localhost,Type=Broker");
        BrokerViewMBean mBean =  (BrokerViewMBean)MBeanServerInvocationHandler.newProxyInstance(connection,  
                name, BrokerViewMBean.class, true);
        // System.out.println(mBean.getBrokerName());
        
        for(ObjectName queueName : mBean.getQueues()) {
            QueueViewMBean queueMBean =  (QueueViewMBean)MBeanServerInvocationHandler
                        .newProxyInstance(connection, queueName, QueueViewMBean.class, true);
            System.out.println("\n------------------------------\n");

            // 消息队列名称
            System.out.println("States for queue --- " + queueMBean.getName());
            queueMBean.getMemoryPercentUsage();//获取队列深度
            // 队列中剩余的消息数
            System.out.println("Size --- " + queueMBean.getQueueSize());

            // 消费者数
            System.out.println("Number of consumers --- " + queueMBean.getConsumerCount());

            // 出队数
            System.out.println("Number of dequeue ---" + queueMBean.getDequeueCount() );
        }
        */
        } 
}
