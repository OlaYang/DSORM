package com.meiqi.dsmanager.action.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.action.IPushAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.dsmanager.po.mushroom.req.ActionReqInfo;
import com.meiqi.dsmanager.po.mushroom.req.ActionResult;
import com.meiqi.dsmanager.po.mushroom.req.MushroomRequestBaseData;
import com.meiqi.dsmanager.po.mushroom.req.StartReqInfo;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.dsmanager.po.mushroom.resp.StartRespInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.mushroom.config.ServicesConfig;
import com.meiqi.mushroom.dao.ITMushroomTableServiceDao;
import com.meiqi.mushroom.engine.ConnectionPool;
import com.meiqi.mushroom.engine.MushroomConnection;
import com.meiqi.mushroom.engine.MushroomTransaction;
import com.meiqi.mushroom.engine.Transactions;
import com.meiqi.mushroom.entity.TMushroomDB;
import com.meiqi.mushroom.entity.TMushroomNode;
import com.meiqi.mushroom.entity.TMushroomService;
import com.meiqi.mushroom.entity.TMushroomTable;
import com.meiqi.mushroom.entity.TMushroomTableService;
import com.meiqi.mushroom.util.Utils;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.ClearCacheAction;
import com.meiqi.openservice.action.login.LoginVerifyAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;

@Service("mushroomAction")
public class MushroomActionImpl implements IMushroomAction {

    @Autowired
    private IPushAction pushAction;
    @Autowired
    private ITMushroomTableServiceDao tMushroomTableServiceDao;
    @Autowired
    private LoginVerifyAction loginVerifyAction;
    @Autowired
    private ClearCacheAction clearCacheAction;
    private static final ObjectMapper JSON_SERIALIZER = new ObjectMapper();
    @Autowired
    private IMemcacheAction memcacheService;
    @Autowired
    private ThreadHelper indexTheadHelper;

    public String offer(DsManageReqInfo reqInfo, HttpServletRequest request,
            HttpServletResponse response) {
        ActionReqInfo actionReqInfo = new ActionReqInfo();
        String content = "";
        // 检查到有简单报文优先简单报文处理
        if (null != reqInfo.getActions()) {
            content = simple2Complex(reqInfo);
        } else {// 进行复杂报文处理
                // 得到mushroom需要的报文
            content = JSON.toJSONString(reqInfo.getParam());
        }
        final ActionRespInfo respInfo = new ActionRespInfo();
        respInfo.setSite_id(reqInfo.getSite_id());
        try {
            actionReqInfo = JSON_SERIALIZER.readValue(content,
                    ActionReqInfo.class);
            final List<Action> actions = actionReqInfo.getActions();
            if (null != request) {
                if(true!=BaseAction.isFromApp(request)){
                    RepInfo repInfoOpenService = new RepInfo();
                    repInfoOpenService.setAction("loginVerifyAction");
                    repInfoOpenService.setMethod("verifyIsLogin");
                    String sessionIdLoginKey = "login_" + request.getSession().getId();
                    Object userAndType = memcacheService.getCache(sessionIdLoginKey);
                    String userId="";
                    String type="";
                    if(userAndType!=null){
                        String userAndTypeTmp=userAndType.toString();
                        userId=userAndTypeTmp.split("_")[0];
                        type=userAndTypeTmp.split("_")[1];
                    }
                    Map<String,String> param=new HashMap<String, String>();
                    param.put("userId", userId);
                    param.put("type", type);
                    repInfoOpenService.setParam(JSONObject.toJSONString(param));
                    ResponseInfo respInfoOpenService = (ResponseInfo) loginVerifyAction.verifyIsLogin(request,
                            response, repInfoOpenService);
                    for (Action action : actions) {
                        TMushroomService tMushroomService = ServicesConfig
                                .getService(action.getServiceName());
                        //判断服务是否存在
                        if(null==tMushroomService){
                            respInfo.setCode("1");
                            String msg="*服务未找到," + action.getServiceName()+",请求参数："+JSONObject.toJSONString(actions);
                            respInfo.setDescription(msg);
                            //将错误信息记录到数据库中
                            String typeTmp="2";//'来源 1 来自规则,2 来自mushroom接口',
                            if(StringUtils.isNotEmpty(action.getRequestType())){
                                typeTmp=action.getRequestType();
                            }
                            insertMushroomError(action.getServiceName(),msg,typeTmp);
                            return JSON.toJSONString(respInfo);
                        }
                        //判断是否要求登录验证
                        if (true == tMushroomService.isRegLogin()) {
                            if(!DsResponseCodeData.SUCCESS.code.equals(respInfoOpenService.getCode())){
                                respInfo.setCode("1");
                                respInfo.setDescription("请登录！");
                                return JSON.toJSONString(respInfo);
                            }
                        }
                    }
                }
            }
            return offer(reqInfo);
        } catch (Exception e) {
            respInfo.setCode("1");
            String msg="mushroom系统错误:" + e.getMessage();
            respInfo.setDescription(msg);
            LogUtil.error(msg);
            return JSON.toJSONString(respInfo);
        }
    }

    @Override
    public String offer(DsManageReqInfo reqInfo) {
        if(reqInfo.getSite_id()==null){
            reqInfo.setSite_id(0);//如果没有则放默认值0，代表乐家居，优家购
        }
        ActionReqInfo actionReqInfo = new ActionReqInfo();

        // 存放操作日志的相关信息，为了提高效率，当所有的action处理完成之后，再提交异步任务去写
        Map<String, Map<String, Object>> opLogParam = new HashMap<String, Map<String, Object>>();
        String content = "";
        // 检查到有简单报文优先简单报文处理
        if (null != reqInfo.getActions()) {
            content = simple2Complex(reqInfo);
        } else {// 进行复杂报文处理
                // 得到mushroom需要的报文
            content = JSON.toJSONString(reqInfo.getParam());
        }

        // 新建一个返回报文实体
        final ActionRespInfo respInfo = new ActionRespInfo();
        respInfo.setSite_id(reqInfo.getSite_id());
        try {
            actionReqInfo = JSON_SERIALIZER.readValue(content,
                    ActionReqInfo.class);
            // 得到action操作实体集合
            final List<Action> actions = actionReqInfo.getActions();
            // 得到事务号
            final String transactionNum = actionReqInfo.getTransactionNum();
            // 如果事务号不存在则新建一个事务号，
            final String finalTNUM = transactionNum == null ? Utils
                    .getTransactionNum() : transactionNum;
            // 写入事务号
            respInfo.setTransactionNum(finalTNUM);

            // 如果该请求没有action操作，则直接返回报文
            if (null == actions || actions.isEmpty()) {
                respInfo.setCode("0");
                respInfo.setDescription("mushroom offer操作成功，但不存在action影响结果为0");
                return JSON.toJSONString(respInfo);
            }

            if (null != transactionNum) { // 在指定的事务中进行
                offerInSpecifiedT(actions, transactionNum, respInfo, opLogParam);
            } else {
                // 如果offer中指定开启事务
                final boolean isTransaction = actionReqInfo.getTransaction() == 1;
                offerNormal(actions, finalTNUM, respInfo, isTransaction,
                        System.currentTimeMillis() / 1000, opLogParam);
            }

            return JSON.toJSONString(respInfo);
        } catch (Exception e) {
            respInfo.setCode("1");
            respInfo.setDescription("mushroom系统错误:" + e.getMessage());
            LogUtil.error("mushroom系统错误:" + e.getMessage());
            return JSON.toJSONString(respInfo);
        }
        // finally {
        //
        // // 把操作日志写入数据库
        // if (reqInfo.getUid() != null && reqInfo.getUid().length() > 0) {
        // // uid不为空，说明本次事务是外部请求，需要记录操作日志，否则不记录
        //
        // // 需要将单个action中的code进行统一，并设置请求结束时间
        // handleOpLog(opLogParam, reqInfo.getUid(), reqInfo.getStartTime());
        // DsManageReqInfo opLogDSInfo = new DsManageReqInfo();
        //
        // buildDSInfo(opLogParam, opLogDSInfo);
        //
        // // 此处不能调用offer，否则会影响resInfo，所以写一个专门插入数据库的方法
        // //insertOpLogToDB(opLogDSInfo);
        // }
        // }
    }

    /**
     * 遍历每一条action的记录，统一请求的完成时间
     * */
    private void handleOpLog(Map<String, Map<String, Object>> opLogParam,
            String userid, long startTime) {
        String opResult = "0"; // 默认操作结果是成功，如果失败则将全部的action return_code置为非0
        long endTime = System.currentTimeMillis() / 1000;
        String msg = "success"; // 如果操作失败，记录失败描述
        for (Map.Entry<String, Map<String, Object>> actionLogInfo : opLogParam
                .entrySet()) {
            Map<String, Object> opLogInfo = actionLogInfo.getValue();
            if (!"0".equalsIgnoreCase((String) opLogInfo.get("return_code"))
                    && opLogInfo.get("return_code") != null) {
                opResult = (String) opLogInfo.get("return_code");
                msg = (String) opLogInfo.get("return_des");
                break;
            }
            opLogInfo.put("req_end_time", Long.valueOf(endTime));
            opLogInfo.put("return_code", opResult);
            opLogInfo.put("return_des", msg);
            opLogInfo.put("user_id", userid);
            opLogInfo.put("req_start_time", String.valueOf(startTime / 1000));
        }

        if (!"0".equalsIgnoreCase(opResult)) {
            for (Map.Entry<String, Map<String, Object>> actionLogInfo : opLogParam
                    .entrySet()) {
                Map<String, Object> opLoginfo = actionLogInfo.getValue();
                opLoginfo.put("return_code", opResult);
                opLoginfo.put("return_des", msg);
                opLoginfo.put("req_end_time", Long.valueOf(endTime));
                opLoginfo.put("user_id", userid);
                opLoginfo.put("req_start_time",
                        String.valueOf(startTime / 1000));
            }
        }
    }

    /**
     * 构造插入数据库的信息
     * */
    private void buildDSInfo(Map<String, Map<String, Object>> opLogParam,
            DsManageReqInfo reqInfo) {
        reqInfo.setServiceName("MUSH_Offer");
        List<Action> allActions = new ArrayList<Action>();
        for (Map.Entry<String, Map<String, Object>> actionLogInfo : opLogParam
                .entrySet()) {
            Action action = new Action();
            action.setType("C");
            action.setServiceName("t_dsm_oplog");
            Map<String, Object> set = new HashMap<String, Object>();
            Map<String, Object> logInfo = actionLogInfo.getValue();
            set.put("transaction_num", logInfo.get("transaction_num"));
            set.put("user_id", logInfo.get("user_id"));
            set.put("return_code", logInfo.get("return_code"));
            set.put("return_des", logInfo.get("return_des"));
            set.put("type", logInfo.get("type"));
            set.put("db_name", logInfo.get("db_name"));
            set.put("table_name", logInfo.get("table_name"));
            set.put("param", logInfo.get("param"));
            set.put("action_name", logInfo.get("action_name"));
            set.put("req_start_time", logInfo.get("req_start_time"));
            set.put("req_end_time", logInfo.get("req_end_time"));
            set.put("spend_time_peraction", logInfo.get("spend_time_peraction"));
            action.setSet(set);

            allActions.add(action);
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", allActions);
        param.put("transaction", 1);
        reqInfo.setParam(param);
    }

    /**
     * 简单报文转换为复杂报文
     * 
     * @Title: simple2Complex
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param reqInfo
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    private String simple2Complex(DsManageReqInfo reqInfo) {
        List<Action> actions = reqInfo.getActions();
        for (Action action : actions) {
            // 解析逻辑条件，如果是C操作则不需要解析逻辑条件
            if (!"C".equalsIgnoreCase(action.getType()) && !"P".equalsIgnoreCase(action.getType())) {
                String keyString = action.getKey();
                String[] keyValues = keyString.split(",");
                List<SqlCondition> sqlConditions = new ArrayList<SqlCondition>();
                for (String keyValue : keyValues) {
                    String[] key = keyValue.split("=");
                    if (2 != key.length) {
                        throw new RuntimeException(
                                "简单报文转换出错, 系统找不到逻辑条件 serviceName："
                                        + action.getServiceName());
                    }
                    SqlCondition sql = new SqlCondition();
                    sql.setKey(key[0]);
                    sql.setValue(key[1]);
                    sql.setOp("=");
                    sqlConditions.add(sql);
                }
                Where where = new Where();
                where.setPrepend("and");
                where.setConditions(sqlConditions);
                action.setWhere(where);
            }

            // 已转换逻辑条件后清除简单报文的格式，避免执行冲突
            //action.setKey(null);
        }

        String simpleContent = "{\"actions\":"
                + JSON.toJSONString(reqInfo.getActions())
                + ",\"transaction\":1}";
        return simpleContent;
    }

    @Override
    public String start(DsManageReqInfo reqInfo) {
        StartReqInfo startReqInfo = new StartReqInfo();
        // 得到mushroom需要的报文
        String content = JSON.toJSONString(reqInfo.getParam());
        // new一个返回报文实体类
        StartRespInfo respInfo = new StartRespInfo();
        try {
            startReqInfo = JSON_SERIALIZER.readValue(content,
                    StartReqInfo.class);
            final String serviceName = startReqInfo.getServiceName();
            final int transactionTimeout = startReqInfo.getTransactionTimeout();
            // 到mushroom配置环境中获取服务实体类
            final TMushroomService service = ServicesConfig
                    .getService(serviceName);
            if (null == service) {
                throw new RuntimeException("服务未找到, " + serviceName);
            }

            if (null != service.getState() && 0 != service.getState()) {
                throw new RuntimeException("服务已停用, " + serviceName);
            }

            if (0 == service.getTables().size()) {
                throw new RuntimeException("配置错误: 服务没有对应的物理表映射, " + serviceName);
            }
            // 获取service对应的表
            final TMushroomTable table = service.getTables().get(0);
            final Integer tid = table.getTid(); // 获取表编号
            final Integer nid = table.getNid(); // 获取表对应的数据库节点

            // 检查是否取到mysql节点信息
            if (null == nid) {
                throw new RuntimeException("配置错误: Mysql节点未找到, "
                        + table.getName() + "@tid-" + tid);
            }
            // 调用mushroom工具类，生成一个事务 事务带有mysql连接
            MushroomTransaction transaction = Transactions.newTransaction(nid,
                    transactionTimeout, System.currentTimeMillis() / 1000);
            // 将获取到的事务号放入返回报文
            respInfo.setTransactionNum(transaction.transactionNum);
            respInfo.setCode("0");
            respInfo.setDescription("成功");
            return JSON.toJSONString(respInfo);
        } catch (Exception e) {
            respInfo.setCode("1");
            respInfo.setDescription("mushroom系统错误：" + e.getMessage());
            return JSON.toJSONString(respInfo);
        }
    }

    @Override
    public String commit(DsManageReqInfo reqInfo) {
        // 声明一个commit请求报文实体
        MushroomRequestBaseData mushroomRequestBaseData = new MushroomRequestBaseData();
        // 获取request请求的mushroom报文
        String content = JSON.toJSONString(reqInfo.getParam());
        ResponseBaseData responseBaseData = new ResponseBaseData();
        try {
            // 将string 的json报文封装到实体中
            mushroomRequestBaseData = JSON_SERIALIZER.readValue(content,
                    MushroomRequestBaseData.class);
            // 获取要提交的事务
            final String transactionNum = mushroomRequestBaseData
                    .getTransactionNum();
            // 得到内存中保存的事务
            final MushroomTransaction transaction = Transactions
                    .getTransaction(transactionNum);
            if (null != transaction) {
                synchronized (transaction) {
                    if (!transaction.isDone) { // 如果检查到事务中还有没处理完成的业务
                        transaction.task.cancel(); // 取消定时器
                        transaction.isDone = true; // 设置事务中业务状态处理进度为完成

                        try {
                            // 得到该连接
                            final MushroomConnection connection = transaction.connection;
                            connection.jdbcConn.commit(); // 提交该连接
                            ConnectionPool.offer(connection, true); // 再次进行关闭操作
                        } finally {
                            // 执行完成从事务池中删除事务
                            Transactions.removeTransaction(transactionNum);
                        }
                    } else {
                        throw new RuntimeException("已结束的事务号, " + transactionNum);
                    }
                }
            } else {
                throw new RuntimeException("不存在或者已结束的事务号, " + transactionNum);
            }
            responseBaseData.setCode("0");
            responseBaseData.setDescription("成功");
            return JSON.toJSONString(responseBaseData);
        } catch (Exception e) {
            responseBaseData.setCode("1");
            responseBaseData.setDescription("mushroom系统错误:" + e.getMessage());
            return JSON.toJSONString(responseBaseData);
        }
    }

    @Override
    public String rollback(DsManageReqInfo reqInfo) {
        // 声明一个commit请求报文实体
        MushroomRequestBaseData mushroomRequestBaseData = new MushroomRequestBaseData();
        // 获取request请求的mushroom报文
        String content = JSON.toJSONString(reqInfo.getParam());
        ResponseBaseData responseBaseData = new ResponseBaseData();
        try {
            // 将string 的json报文封装到实体中
            mushroomRequestBaseData = JSON_SERIALIZER.readValue(content,
                    MushroomRequestBaseData.class);
            // 得到要进行回滚的事务号
            final String transactionNum = mushroomRequestBaseData
                    .getTransactionNum();
            Transactions.rollBack(transactionNum);
            responseBaseData.setCode("0");
            responseBaseData.setDescription("成功");
            return JSON.toJSONString(responseBaseData);
        } catch (Exception e) {
            responseBaseData.setCode("1");
            responseBaseData.setDescription("mushroom系统错误:" + e.getMessage());
            return JSON.toJSONString(responseBaseData);
        }
    }

    /**
     * 没有在指定的全局事务中执行
     * 
     * @Title: offerNormal
     * @param @param actions
     * @param @param finalTNUM
     * @param @param respInfo
     * @param @param isTransaction
     * @param @param currentSecond 参数说明
     * @return void 返回类型
     * @throws
     */
    private void offerNormal(List<Action> actions, String finalTNUM,
            ActionRespInfo respInfo, boolean isTransaction, long currentSecond,
            Map<String, Map<String, Object>> opLogParam) {
        MushroomConnection connection = null;
        final List<ActionResult> results = respInfo.getResults();

        // long actionStartTime = System.currentTimeMillis();
        boolean noError = true;
        String lastDbName = null;

        for (int i = 0; i < actions.size(); i++) {
            final ActionResult result = new ActionResult();
            final Action action = actions.get(i);
            if(action.getSite_id()==null){
                action.setSite_id(respInfo.getSite_id());//设置站点id
            }
            results.add(result);
            
            Map<String, Object> opLogActionInfo = new HashMap<String, Object>();
            // opLogActionInfo.put("transaction_num", finalTNUM);

            try {
                final String serviceName = action.getServiceName();
                final TMushroomService service = ServicesConfig
                        .getService(serviceName);

                opLogParam.put(serviceName + i, opLogActionInfo);
                // opLogActionInfo.put("action_name", serviceName);

                if (service == null) {
                    throw new RuntimeException("*服务未找到, " + serviceName);
                }

                if (service.getState() != null && service.getState() != 0) {
                    throw new RuntimeException("*服务已停用, " + serviceName);
                }

                final String scope = service.getScope() == null ? "" : service
                        .getScope();

                char opType;
                final String reqOpType = action.getType();
                if ("C".equalsIgnoreCase(reqOpType)) {
                    opType = 'C';
                } else if ("U".equalsIgnoreCase(reqOpType)) {
                    opType = 'U';
                } else if ("D".equalsIgnoreCase(reqOpType)) {
                    opType = 'D';
                } else if ("L".equalsIgnoreCase(reqOpType)) {
                    opType = 'L';
                } else if ("P".equalsIgnoreCase(reqOpType)) {
                    opType = 'P';
                }else if ("I".equalsIgnoreCase(reqOpType)) {
                    opType = 'I';
                } else {
                    throw new RuntimeException("*操作不支持 " + reqOpType);
                }

                if (scope.indexOf(opType) == -1) {
                    throw new RuntimeException("*操作仅支持 " + scope);
                }

                for (TMushroomTable table : service.getTables()) {

                    final TMushroomNode node = ServicesConfig.getNode(table
                            .getNid());
                    final TMushroomDB db = ServicesConfig.getDB(table.getDid());

                    if (node == null) {
                        throw new RuntimeException("*配置错误: Mysql节点未找到 "
                                + "@nid-" + table.getNid());
                    }

                    if (db == null) {
                        throw new RuntimeException("*配置错误: Mysql数据库未找到 "
                                + "@did-" + table.getDid());
                    }

                    if (connection == null) {
                        connection = ConnectionPool.poll(node, currentSecond,
                                false);
                        if (!isTransaction) {
                            connection.jdbcConn.setAutoCommit(true);
                        }
                    }

                    final String dbName = getDBName(db, opType, action);
                    final String tableName = getTableName(table, opType, action);

                    // opLogActionInfo.put("db_name", dbName);
                    // opLogActionInfo.put("table_name", tableName);
                    // opLogActionInfo.put("type", action.getType());
                    Map<String, String> fieldMap = table.getFieldMap();
                    if (lastDbName == null || !lastDbName.equals(dbName)) {
                        final String useSql = "use " + dbName + ";";
                        connection.jdbcConn.prepareCall(useSql).execute();
                        lastDbName = dbName;
                    }

                    @SuppressWarnings("unused")
                    int updateCount = 0;
                    switch (opType) {
                    case 'C':
                        updateCount = insertInto(action, result, results,
                                connection.jdbcConn, dbName, tableName,
                                fieldMap, table, opLogActionInfo);
                        break;
                    case 'U':
                        updateCount = update(action, result, results,
                                connection.jdbcConn, dbName, tableName,
                                fieldMap, table, opLogActionInfo);
                        break;
                    case 'D':
                        updateCount = deleteFrom(action, result, results,
                                connection.jdbcConn, dbName, tableName,
                                fieldMap, table, opLogActionInfo);
                        break;
                    case 'L':
                        updateCount = lock(action, result, results,
                                connection.jdbcConn, dbName, tableName,
                                fieldMap, opLogActionInfo);
                        break;
                    case 'P':
                        updateCount = callProcedure(action, result, results,
                                connection.jdbcConn, dbName, tableName,
                                fieldMap, opLogActionInfo);
                        break;
                    case 'I':
                        updateCount = insertIntoAdvance(action, result, results, connection.jdbcConn, dbName, tableName,
                                fieldMap, table, opLogActionInfo);
                    }
                }
            } catch (Exception e) {
                String msg = action.getServiceName() + "-" + action.getType() + " 失败, " + e.getMessage()+",请求参数："+JSONObject.toJSONString(actions);
                LogUtil.error(msg);
                noError = false;
                respInfo.setCode("-1");
                respInfo.setDescription(msg + " @Action-" + i);
                //将错误信息记录到数据库中
                String type="2";//'来源 1 来自规则,2 来自mushroom接口',
                if(StringUtils.isNotEmpty(action.getRequestType())){
                    type=action.getRequestType();
                }
                insertMushroomError(action.getServiceName(),msg,type);
                break;
            }

            // 本次action处理完成
            // long spendTime = System.currentTimeMillis() - actionStartTime;
            // opLogActionInfo.put("spend_time_peraction",
            // Long.valueOf(spendTime));

        }

        if (isTransaction) {
            if (noError) { // 无错误时才可以提交，否则不提交
                try {
                    connection.jdbcConn.commit();
                } catch (Exception e) {
                    final String msg = "事务提交失败, " + e.getMessage();
                    respInfo.setCode("-1");
                    respInfo.setDescription(msg + " @Action-All");
                    respInfo.getResults().clear();
                }
            } else { // 有错误时将正确的进行重置
                respInfo.getResults().clear();
            }
        }

        if (connection != null) {
            ConnectionPool.offer(connection, isTransaction);
        }
    }

    /**
     * @Title: callProcedure
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param action
     * @param @param result
     * @param @param results
     * @param @param jdbcConn
     * @param @param dbName
     * @param @param tableName
     * @param @param fieldMap
     * @param @param opLogActionInfo
     * @param @return 参数说明
     * @return int 返回类型
     * @throws
     */
    private int callProcedure(Action action, ActionResult result, List<ActionResult> results, Connection jdbcConn,
            String dbName, String tableName, Map<String, String> fieldMap, Map<String, Object> opLogActionInfo) throws SQLException{
        final Map<String, Object> set = action.getSet();
        CallableStatement callStmt = null;  
        try {
            //tableName 就是配置的存储过程名称
            StringBuilder callProcedureStr=new StringBuilder();
            callProcedureStr.append("{call ");
            callProcedureStr.append(tableName);
            callProcedureStr.append("(");
            if(set.size()>0){
                for (int i = 0; i < set.size(); i++) {
                    if (i == (set.size() - 1)) {
                        callProcedureStr.append("?");
                    } else {
                        callProcedureStr.append("?,");
                    }
                }
            }
            callProcedureStr.append(")}");
            callStmt = jdbcConn.prepareCall(callProcedureStr.toString());
            if(set.size()>0){
                for (String key : set.keySet()) {
                    String value = set.get(key).toString();
                    callStmt.setString(key, value);
                }
            }
            long begin = System.currentTimeMillis();
            boolean er = callStmt.execute();
            long end = System.currentTimeMillis();
            long time = end - begin;
            LogUtil.info("-------mushroom callProcedure serviceName:" + action.getServiceName() + " time:" + time);
            LogUtil.info("-------mushroom callProcedure serviceName: p sql:" + callStmt.toString());
            // LogUtil.info("-------start callProcedure serviceName:"+action.getServiceName()+" excute result:"+er);
            String is_success = "";
            String presults = "";
            if (er) {
                ResultSet resultSet = callStmt.getResultSet();
                ResultSetMetaData metaData=resultSet.getMetaData();
                int returnColumnSize=metaData.getColumnCount();
                while (resultSet.next()) {
                    if(returnColumnSize==1){
                        is_success = resultSet.getString(1);
                    }else if(returnColumnSize==2){
                        is_success = resultSet.getString(1);
                        presults = resultSet.getString(2);// 存储过程只返回一个json串,也可以用results
                    }else{
                        throw new RuntimeException(action.getServiceName() + "-" + action.getType() + " 失败,存储过程返回的结果字段个数部位2,returnColumnSize:"+returnColumnSize);
                    }
                }
                Map<String, String> map = new HashMap<String, String>();
                if("0".equals(is_success)){
                    //失败
                    map.put("is_success", "1");
                    throw new RuntimeException(action.getServiceName() + "-" + action.getType() + " 失败,"+presults);
                }else{
                    map.put("is_success", "0");
                }
                map.put("results", presults);
                result.setResults(JSONObject.toJSONString(map));
                result.setDbName(dbName);
                result.setTableName(tableName);
            }else{
                //失败
                result.setCode("1");
                result.setDescription(action.getServiceName() + "-"+ action.getType() + " 调用存储过程失败");
                result.setResults(presults);
                throw new RuntimeException(action.getServiceName() + "-" + action.getType() + " 调用存储过程失败");
                //return 0;
            }
        } catch (SQLException e) {
            String msg = action.getServiceName() + "-"
                    + action.getType() + " 调用存储过程失败, "
                    + e.getMessage() ;
            result.setCode("1");
            result.setDescription(msg);
            throw new RuntimeException(msg);
        }finally{
            try {
                if(callStmt!=null){
                   callStmt.close();
                }
            } catch (SQLException e) {
                LogUtil.error("-------mushroom callProcedure serviceName:"+action.getServiceName()+" callStmt.close() error"+e.getMessage());
            }
        }
        return 1;
    }

    /**
     * offer操作，在指定的事务中执行
     * 
     * @Title: offerInSpecifiedT
     * @param @param actions offer的操作集
     * @param @param transactionNum 要执行事务号
     * @param @param respInfo 参数说明 返回报文
     * @param opLogParam
     *            记录操作日志的相关参数
     * @return void 返回类型
     * @throws
     */
    private void offerInSpecifiedT(List<Action> actions, String transactionNum,
            ActionRespInfo respInfo, Map<String, Map<String, Object>> opLogParam) {
        final MushroomTransaction transaction = Transactions
                .getTransaction(transactionNum);
        //
        final List<ActionResult> results = respInfo.getResults();

        if (transaction != null) {
            synchronized (transaction) {
                if (!transaction.isDone) { // 是不是已处理完
                    final MushroomConnection connection = transaction.connection;
                    boolean noError = true;
                    String lastDbName = null;

                    for (int i = 0; i < actions.size(); i++) {
                        // 便于计算本次action处理所花费时间
                        long actionStartTime = System.currentTimeMillis();
                        Map<String, Object> opLogActionInfo = new HashMap<String, Object>();

                        final ActionResult result = new ActionResult();
                        final Action action = actions.get(i);
                        if(action.getSite_id()==null){
                            action.setSite_id(respInfo.getSite_id());//设置站点id
                        }
                        results.add(result);

                        try {
                            final String serviceName = action.getServiceName();
                            // 由于serviceName可能会一样，为防止冲突，在serviceName后+i区分
                            opLogParam.put(serviceName + i, opLogActionInfo);
                            opLogActionInfo.put("action_name", serviceName);
                            final TMushroomService service = ServicesConfig
                                    .getService(serviceName);
                            if (service == null) {
                                throw new RuntimeException("*服务未找到, "
                                        + serviceName);
                            }

                            if (service.getState() != null
                                    && service.getState() != 0) {
                                throw new RuntimeException("*服务已停用, "
                                        + serviceName);
                            }

                            final String scope = (service.getScope() == null || service
                                    .getScope().trim().length() == 0) ? "CUD"
                                    : service.getScope();

                            char opType;
                            final String reqOpType = action.getType();
                            opLogActionInfo.put("type", reqOpType);
                            if ("C".equalsIgnoreCase(reqOpType)) {
                                opType = 'C';
                            } else if ("U".equalsIgnoreCase(reqOpType)) {
                                opType = 'U';
                            } else if ("D".equalsIgnoreCase(reqOpType)) {
                                opType = 'D';
                            } else if ("L".equalsIgnoreCase(reqOpType)) {
                                opType = 'L';
                            } else if ("P".equalsIgnoreCase(reqOpType)) {
                                opType = 'P';
                            }else if ("I".equalsIgnoreCase(reqOpType)) {
                                opType = 'I';
                            } else {
                                throw new RuntimeException("*操作不支持 "
                                        + reqOpType);
                            }

                            if (scope.indexOf(opType) == -1) {
                                throw new RuntimeException("*操作仅支持 " + scope);
                            }

                            // 由于可能会操作多个数据库和表名，所以需要将其连成一个字符串
                            StringBuilder dbNameBuilder = new StringBuilder();
                            StringBuilder tableNameBuilder = new StringBuilder();
                            for (TMushroomTable table : service.getTables()) {
                                final TMushroomNode node = ServicesConfig
                                        .getNode(table.getNid());
                                final TMushroomDB db = ServicesConfig
                                        .getDB(table.getDid());

                                if (node == null) {
                                    throw new RuntimeException(
                                            "*配置错误: Mysql节点未找到 " + "@nid-"
                                                    + table.getNid());
                                }

                                if (db == null) {
                                    throw new RuntimeException(
                                            "*配置错误: Mysql数据库未找到 " + "@did-"
                                                    + table.getDid());
                                }

                                final String dbName = getDBName(db, opType,
                                        action);
                                final String tableName = getTableName(table,
                                        opType, action);

                                dbNameBuilder.append(dbName);
                                tableNameBuilder.append(tableName);
                                Map<String, String> fieldMap = table
                                        .getFieldMap();
                                if (lastDbName == null
                                        || !lastDbName.equals(dbName)) {
                                    final String useSql = "use " + dbName + ";";
                                    // infoSql(transactionNum, useSql);
                                    connection.jdbcConn.prepareCall(useSql)
                                            .execute();
                                    lastDbName = dbName;
                                }

                                @SuppressWarnings("unused")
                                int updateCount = 0;
                                switch (opType) {

                                case 'C':
                                    updateCount = insertInto(action, result,
                                            results, connection.jdbcConn,
                                            dbName, tableName, fieldMap, table,
                                            opLogActionInfo);
                                    break;
                                case 'U':
                                    updateCount = update(action, result,
                                            results, connection.jdbcConn,
                                            dbName, tableName, fieldMap, table,
                                            opLogActionInfo);
                                    break;
                                case 'D':
                                    updateCount = deleteFrom(action, result,
                                            results, connection.jdbcConn,
                                            dbName, tableName, fieldMap, table,
                                            opLogActionInfo);
                                    break;
                                case 'T':
                                    updateCount = truncate(action, result,
                                            results, connection.jdbcConn,
                                            dbName, tableName, fieldMap);
                                    break;
                                case 'L':
                                    updateCount = lock(action, result, results,
                                            connection.jdbcConn, dbName,
                                            tableName, fieldMap,
                                            opLogActionInfo);
                                    break;
                                case 'P':
                                    updateCount = callProcedure(action, result, results, connection.jdbcConn, dbName,
                                            tableName, fieldMap, opLogActionInfo);
                                    break;
                                case 'I':
                                    updateCount = insertIntoAdvance(action, result, results, connection.jdbcConn, dbName, tableName,
                                            fieldMap, table, opLogActionInfo);
                                }

                            }

                            opLogActionInfo.put("db_name",
                                    dbNameBuilder.toString());
                            opLogActionInfo.put("table_name",
                                    tableNameBuilder.toString());
                        } catch (Exception e) {
                            String msg = action.getServiceName() + "-"
                                    + action.getType() + " 失败, 全局事务失败, "
                                    + e.getMessage() + " @Action-" + i+",请求参数："+JSONObject.toJSONString(actions);
                            LogUtil.error(msg);
                            // Monitor.error(msg);
                            noError = false;

                            respInfo.setCode("-1");
                            respInfo.setDescription(msg);

                            // 由于本次action处理失败，导致整个事务处理失败，需要记录
                            opLogActionInfo.put("return_code", "-1");
                            opLogActionInfo.put("return_des", msg);
                            
                            //将错误信息记录到数据库中
                            String type="2";//'来源 1 来自规则,2 来自mushroom接口',
                            if(StringUtils.isNotEmpty(action.getRequestType())){
                                type=action.getRequestType();
                            }
                            insertMushroomError(action.getServiceName(),msg,type);
                            break;
                        }

                        // 本次action处理完成
                        long spendTime = System.currentTimeMillis()
                                - actionStartTime;
                        opLogActionInfo.put("spend_time_peraction",
                                Long.valueOf(spendTime / 1000));
                    }

                    if (!noError) { // 有错误，回滚
                        transaction.task.cancel();
                        transaction.isDone = true;
                        respInfo.getResults().clear();

                        try {
                            connection.jdbcConn.rollback();
                            ConnectionPool.offer(connection, true);
                        } catch (Exception e) {
                            // LogUtil.error("error when rollback transaction, "
                            // + e.getMessage());
                        } finally {
                            Transactions.removeTransaction(transactionNum); // 移除
                        }
                    }
                } else {
                    throw new RuntimeException("已结束的事务号, " + transactionNum);
                }
            }
        } else {
            throw new RuntimeException("不存在或者已结束的事务号, " + transactionNum);
        }

    }

    private static String getTableName(TMushroomTable table, char opType,
            Action action) {
        String tableName = table.getName();
        final Integer tid = table.getTid();

        if (tableName == null || tableName.length() == 0) {
            throw new RuntimeException("配置错误: 表名称为空 " + "@tid-" + tid);
        }

        if (table.getSplit() != null && table.getSplit() == 1) { // 表拆分
            final int tableSplitNum = table.getTableSplitNum() == null ? 0
                    : table.getTableSplitNum();
            final String tableSplitField = table.getTableSplitField();

            if (tableSplitNum <= 0) {
                throw new RuntimeException("配置错误: 表拆分数目小于0 " + "@tid-" + tid);
            }

            if (tableSplitField == null || tableSplitField.length() == 0) {
                throw new RuntimeException("配置错误: 表拆分字段为空 " + "@tid-" + tid);
            }

            if (opType == 'C') {
                if (action.getSet() == null
                        || !action.getSet().containsKey(tableSplitField)) {
                    throw new RuntimeException("参数错误: SET中缺失拆分字段, "
                            + tableSplitField);
                }

                final Object splitObj = action.getSet().get(tableSplitField);
                try {
                    long splitValue = getSplitLongValue(splitObj);
                    tableName += "_" + (splitValue % tableSplitNum + 1);
                } catch (Exception e) {
                    throw new RuntimeException("参数错误: SET中的拆分字段值不是数字, "
                            + splitObj);
                }
            } else {
                if (action.getSet() != null
                        && action.getSet().containsKey(tableSplitField)) {
                    throw new RuntimeException("参数错误: SET中不得更改拆分字段, "
                            + tableSplitField);
                }

                if (action.getWhere() == null
                        || action.getWhere().getConditions() == null
                        || action.getWhere().getConditions().size() == 0) {
                    throw new RuntimeException("参数错误: WHERE中缺失拆分字段, "
                            + tableSplitField);
                }

                List<SqlCondition> conditions = action.getWhere()
                        .getConditions();

                boolean found = false;
                for (SqlCondition condition : conditions) {
                    if (tableSplitField.equals(condition.getKey())) {
                        if (condition.getOp() == null
                                || !condition.getOp().trim().equals("=")) {
                            throw new RuntimeException("参数错误: 拆分字段的OP操作只允许=");
                        }

                        Object splitObj = condition.getValue();
                        try {
                            long splitValue = getSplitLongValue(splitObj);
                            tableName += "_" + (splitValue % tableSplitNum + 1);
                            found = true;
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "参数错误: WHERE中的拆分字段值不是数字, " + splitObj);
                        }
                    }
                }

                if (!found) {
                    throw new RuntimeException("参数错误: WHERE中缺失拆分字段, "
                            + tableSplitField);
                }
            }
        }

        return tableName;
    }

    private static String getDBName(TMushroomDB db, char opType, Action action) {
        String dbName = db.getName();
        final Integer did = db.getDid();

        if (dbName == null || dbName.length() == 0) {
            throw new RuntimeException("配置错误: 库名称为空 " + "@did-" + did);
        }

        if (db.getSplit() != null && db.getSplit() == 1) { // 库拆分
            final int dbSplitNum = db.getDbSplitNum() == null ? 0 : db
                    .getDbSplitNum();
            final String dbSplitField = db.getDbSplitField();

            if (dbSplitNum <= 0) {
                throw new RuntimeException("配置错误: 库拆分数目小于0 " + "@did-" + did);
            }

            if (dbSplitField == null || dbSplitField.length() == 0) {
                throw new RuntimeException("配置错误: 库拆分字段为空 " + "@did-" + did);
            }

            if (opType == 'C') {
                if (action.getSet() == null
                        || !action.getSet().containsKey(dbSplitField)) {
                    throw new RuntimeException("参数错误: SET中缺失拆分字段, "
                            + dbSplitField);
                }

                final Object splitObj = action.getSet().get(dbSplitField);
                try {
                    long splitValue = getSplitLongValue(splitObj);
                    dbName += "_" + (splitValue % dbSplitNum + 1);
                } catch (Exception e) {
                    throw new RuntimeException("参数错误: SET中的拆分字段值不是数字, "
                            + splitObj);
                }
            } else {
                if (action.getSet() != null
                        && action.getSet().containsKey(dbSplitField)) {
                    throw new RuntimeException("参数错误: SET中不得更改拆分字段, "
                            + dbSplitField);
                }

                if (action.getWhere() == null
                        || action.getWhere().getConditions() == null
                        || action.getWhere().getConditions().size() == 0) {
                    throw new RuntimeException("参数错误: WHERE中缺失拆分字段, "
                            + dbSplitField);
                }

                List<SqlCondition> conditions = action.getWhere()
                        .getConditions();

                boolean found = false;
                for (SqlCondition condition : conditions) {
                    if (dbSplitField.equals(condition.getKey())) {
                        if (condition.getOp() == null
                                || !condition.getOp().trim().equals("=")) {
                            throw new RuntimeException("参数错误: 拆分字段的OP操作只允许=");
                        }

                        Object splitObj = condition.getValue();
                        try {
                            long splitValue = getSplitLongValue(splitObj);
                            dbName += "_" + (splitValue % dbSplitNum + 1);
                            found = true;
                            break;
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "参数错误: WHERE中的拆分字段值不是数字, " + splitObj);
                        }
                    }
                }

                if (!found) {
                    throw new RuntimeException("参数错误: WHERE缺失拆分字段, "
                            + dbSplitField);
                }
            }
        }

        return dbName;
    }

    private static long getSplitLongValue(Object splitObj) {
        if (splitObj instanceof Number) {
            return ((Number) splitObj).longValue();
        }

        return Long.valueOf(String.valueOf(splitObj));
    }


    //拼接where条件，带有站点
    private static void getWhere(Action action, List<ActionResult> results, StringBuilder whereSql,
            Map<String, String> fieldMap, int layer,int site_id) {
    	
    	//如果有传入sql语句则直接取值返回
    	if(null!=action.getWhereSql()){
    		whereSql.append(action.getWhereSql());
    		return;
    	}
    	
    	Where where=action.getWhere();
        boolean isFirst = true;
        whereSql.append("(");
        if (where != null) {
            final List<SqlCondition> conditions = where.getConditions();
            final List<Where> wheres = where.getWheres();
            final String prepend = where.getPrepend();
            if (conditions != null) {
                for (SqlCondition condition : where.getConditions()) {
                    final String keyMapped = fieldMap.get(condition.getKey());

                    if (keyMapped == null) {
                        //continue;
                        throw new RuntimeException("传入的字段未配置field["+condition.getKey()+"]");
                    }

                    if (isFirst) {
                        isFirst = false;
                    } else {
                        whereSql.append(" ").append(prepend).append(" ");
                    }

                    whereSql.append("`").append(keyMapped).append("`");
                    whereSql.append(" ").append(condition.getOp()).append(" ");

                    String value = Utils.getSqlString(condition.getValue());
                    if (value.equals("'null'")) {
                        whereSql.append("NULL");
                    } else if (value.startsWith("'$") && value.endsWith(".generateKey'")) {
                        whereSql.append(fetchValue(value, results));
                    } else if (value.startsWith("'$EP.")) {
                        whereSql.append(value.substring(5, value.length() - 1));
                    } else if (value.startsWith("'$Date'")) {
                        whereSql.append("now()");
                    } else if (value.startsWith("'$UnixTime'")) {
                        whereSql.append("unix_timestamp(now())");
                    } else {
                        whereSql.append(value);
                    }
                }
            }

            if (wheres != null) {
                for (Where whereChild : wheres) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        whereSql.append(" ").append(prepend).append(" ");
                    }

                    getWhere(whereChild, results, whereSql, fieldMap, layer + 1);
                }
            }
        }

        if (isFirst) {
            throw new RuntimeException("第" + layer + "层的WHERE" + (where == null ? "为空" : "没有找到可以映射的字段"));
        }
        whereSql.append(")");
    }
    
    //拼接where条件
    private static void getWhere(Where where, List<ActionResult> results, StringBuilder whereSql,
            Map<String, String> fieldMap, int layer) {
        boolean isFirst = true;
        whereSql.append("(");

        if (where != null) {
            final List<SqlCondition> conditions = where.getConditions();
            final List<Where> wheres = where.getWheres();
            final String prepend = where.getPrepend();

            if (conditions != null) {
                for (SqlCondition condition : where.getConditions()) {
                    final String keyMapped = fieldMap.get(condition.getKey());

                    if (keyMapped == null) {
                        throw new RuntimeException("传入的字段未配置field["+condition.getKey()+"]");
                    }

                    if (isFirst) {
                        isFirst = false;
                    } else {
                        whereSql.append(" ").append(prepend).append(" ");
                    }

                    whereSql.append("`").append(keyMapped).append("`");
                    whereSql.append(" ").append(condition.getOp()).append(" ");

                    String value = Utils.getSqlString(condition.getValue());
                    if (value.equals("'null'")) {
                        whereSql.append("NULL");
                    } else if (value.startsWith("'$")
                            && value.endsWith(".generateKey'")) {
                        whereSql.append(fetchValue(value, results));
                    } else if (value.startsWith("'$EP.")) {
                        whereSql.append(value.substring(5, value.length() - 1));
                    } else if (value.startsWith("'$Date'")) {
                        whereSql.append("now()");
                    } else if (value.startsWith("'$UnixTime'")) {
                        whereSql.append("unix_timestamp(now())");
                    } else {
                        whereSql.append(value);
                    }
                }
            }

            if (wheres != null) {
                for (Where whereChild : wheres) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        whereSql.append(" ").append(prepend).append(" ");
                    }

                    getWhere(whereChild, results, whereSql, fieldMap, layer + 1);
                }
            }
        }

        if (isFirst) {
            throw new RuntimeException("第" + layer + "层的WHERE"
                    + (where == null ? "为空" : "没有找到可以映射的字段"));
        }
        whereSql.append(")");
    }

    private static long checkCount(String whereSqlStr, String db, String table,
            Connection connection) throws SQLException {
        final String sql = "SELECT COUNT(1) as S FROM `" + db + "`.`" + table
                + "` WHERE " + whereSqlStr;
        PreparedStatement statement = connection.prepareStatement(sql);
        long sum = 0;

        try {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sum = resultSet.getLong(1);
                // if (sum > 100) {
                if (sum > 500) { // lastmodify 2014-5-9 by llcheng
                    throw new RuntimeException("影响行数多于500");
                }

                break;
            }
        } finally {
            statement.close();
        }
        return sum;
    }

    private static int lock(Action action, ActionResult result,
            List<ActionResult> results, Connection jdbcConn, String dbName,
            String tableName, Map<String, String> fieldMap,
            Map<String, Object> opLogInfo) throws SQLException {
        int updateCount = 0;

        StringBuilder whereSql = new StringBuilder();
        getWhere(action.getWhere(), results, whereSql, fieldMap, 1);

        final String whereSqlStr = whereSql.toString();
        if (opLogInfo != null) {
            opLogInfo.put("param", whereSql);
        }
        if (checkCount(whereSqlStr, dbName, tableName, jdbcConn) == 0) {
            return updateCount;
        }

        final String sql = "SELECT COUNT(1) as S FROM `" + dbName + "`.`"
                + tableName + "` WHERE " + whereSqlStr + " FOR UPDATE";
        // infoSql(finalTNUM, sql);

        Statement statement = jdbcConn.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                updateCount = resultSet.getInt(1);
                break;
            }

            result.addUpdateCount(updateCount);
        } finally {
            statement.close();
        }
        return updateCount;
    }

    // 清空數據表
    private static int truncate(Action action, ActionResult result,
            List<ActionResult> results, Connection connection, String dbName,
            String tableName, Map<String, String> fieldMap) throws SQLException {
        int updateCount = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("TRUNCATE TABLE ").append("`").append(dbName).append("`")
                .append(".`").append(tableName).append("` ");
        final String sql = sb.toString();
        // infoSql(transactionNum, sql);

        Statement statement = connection.createStatement();
        try {
            updateCount = statement.executeUpdate(sql);
            result.addUpdateCount(updateCount);
            result.setDbName(dbName);
            result.setTableName(tableName);
        } finally {
            statement.close();
        }
        return updateCount;
    }

    private int deleteFrom(Action action, ActionResult result,
            List<ActionResult> results, Connection connection, String dbName,
            String tableName, Map<String, String> fieldMap,
            TMushroomTable table, Map<String, Object> opLogInfo)
            throws SQLException {
        int updateCount = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append("`").append(dbName).append("`")
                .append(".`").append(tableName).append("` ");
        
        StringBuilder whereSql = new StringBuilder();
        getWhere(action, results, whereSql, fieldMap, 1,action.getSite_id());

        String whereSqlStr = whereSql.toString();
        if (opLogInfo != null) {
            opLogInfo.put("param", whereSqlStr);
        }

        if (checkCount(whereSqlStr, dbName, tableName, connection) == 0) {
            return updateCount;
        }

        sb.append("WHERE ").append(whereSqlStr);
        final String sql = sb.toString();
        // infoSql(transactionNum, sql);

        Statement statement = connection.createStatement();
        try {
             long start=System.currentTimeMillis();
             updateCount = statement.executeUpdate(sql);
             long time=System.currentTimeMillis()-start;
            
             LogUtil.info("-------mushroom delete serviceName:"+action.getServiceName()+" time:"+time);  
             LogUtil.info("-------mushroom delete serviceName: d sql:"+sql);
            // 如果影响值大于1则调用通知，清除对应的data服务名缓存
            if (0 < updateCount) {
                updateCache(table.getTid());
            }
            result.addUpdateCount(updateCount);
            result.setDbName(dbName);
            result.setTableName(tableName);
        } finally {
            statement.close();
        }
        return updateCount;
    }

    private int update(Action action, ActionResult result,
            List<ActionResult> results, Connection connection, String dbName,
            String tableName, Map<String, String> fieldMap,
            TMushroomTable table, Map<String, Object> opLogInfo)
            throws SQLException {
        final Map<String, Object> set = action.getSet();
        int updateCount = 0;

        if (set == null || set.isEmpty() || fieldMap.isEmpty()) {
            throw new RuntimeException("没有找到可更新的映射字段");
        }

        List<String> fields = new ArrayList<String>(set.size());
        List<String> values = new ArrayList<String>(set.size());

        for (String key : set.keySet()) {
            final String keyMapped = fieldMap.get(key);

            if (keyMapped == null) {
                throw new RuntimeException("传入的字段未配置field["+key+"]");
            }

            fields.add("`" + keyMapped + "`");
            String value = Utils.getSqlString(set.get(key));

            if (value.equals("'null'")) {
                values.add(null);
            } else if (value.startsWith("'$")
                    && value.endsWith(".generateKey'")) {
                values.add(fetchValue(value, results));
            } else if (value.startsWith("'$EP.")) {
                values.add(value.substring(5, value.length() - 1));
            } else if (value.startsWith("'$Date'")) {
                values.add("now()");
            } else if (value.startsWith("'$UnixTime'")) {
                values.add("unix_timestamp(now())");
            } else {
                values.add(value);
            }
        }

        if (fields.size() == 0) {
            throw new RuntimeException("没有找到可更新的映射字段");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append("`").append(dbName).append("`")
                .append(".`").append(tableName).append("` SET ");

        boolean first = true;
        for (int i = 0; i < fields.size(); i++) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(fields.get(i)).append(" = ").append(values.get(i));
        }
        sb.append(" ");

        StringBuilder whereSql = new StringBuilder();

        getWhere(action, results, whereSql, fieldMap, 1,action.getSite_id());
        
        String whereSqlStr = whereSql.toString();

        if (opLogInfo != null) {
            opLogInfo.put("param", whereSqlStr);
        }
        
        
        if (checkCount(whereSqlStr, dbName, tableName, connection) == 0) {
            return updateCount;
        }

        sb.append("WHERE ").append(whereSqlStr);
        final String sql = sb.toString();
        // infoSql(transactionNum, sql);

        Statement statement = connection.createStatement();
        try {
            long start=System.currentTimeMillis();
            updateCount = statement.executeUpdate(sql);
            long time=System.currentTimeMillis()-start;
             LogUtil.info("-------mushroom update serviceName:"+action.getServiceName()+" time:"+time);  
             LogUtil.info("-------mushroom update serviceName: u sql:"+sql);  
            // 如果影响值大于1则调用通知，清除对应的data服务名缓存
            if (0 < updateCount) {
                updateCache(table.getTid());
            }
            result.addUpdateCount(updateCount);
            result.setDbName(dbName);
            result.setTableName(tableName);
        } finally {
            statement.close();
        }

        return updateCount;
    }

    private int insertInto(Action action, ActionResult result,
            List<ActionResult> results, Connection connection, String dbName,
            String tableName, Map<String, String> fieldMap,
            TMushroomTable table, Map<String, Object> opLogInfo)
            throws SQLException {

        final Map<String, Object> set = action.getSet();
        int updateCount = 0;

        if (set == null) {
            return updateCount;
        }
        
        List<String> fields = new ArrayList<String>(set.size());
        List<String> values = new ArrayList<String>(set.size());
        //StringBuilder notConfigField = new StringBuilder();
        for (String key : set.keySet()) {
            final String keyMapped = fieldMap.get(key);

            if (keyMapped == null) {
                //notConfigField.append(key);
                //notConfigField.append(",");
                throw new RuntimeException("传入的字段未配置field["+key+"]");
                //continue;
            }

            fields.add("`" + keyMapped + "`");
            String value = Utils.getSqlString(set.get(key));

            if (value.equals("'null'")) {
                values.add(null);
            } else if (value.startsWith("'$")
                    && value.endsWith(".generateKey'")) {
                values.add(fetchValue(value, results));
            } else if (value.startsWith("'$EP.")) {
                values.add(value.substring(5, value.length() - 1));
            } else if (value.startsWith("'$Date'")) {
                values.add("now()");
            } else if (value.startsWith("'$UnixTime'")) {
                values.add("unix_timestamp(now())");
            } else {
                values.add(value);
            }
        }

        StringBuilder sb = new StringBuilder();
        StringBuilder opLogSb = new StringBuilder(); // 用于记录本次action中的操作参数
        sb.append("INSERT INTO ").append("`").append(dbName).append("`")
                .append(".`").append(tableName).append("` ");

        sb.append("(");
        boolean first = true;
        for (String field : fields) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
                opLogSb.append(", ");
            }

            sb.append(field);
            opLogSb.append(field);
        }
        if (opLogInfo != null) {
            opLogInfo.put("param", opLogSb.toString());
        }
        sb.append(") VALUES(");
        first = true;
        for (String value : values) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            if (value == null) {
                sb.append("NULL");
            } else {
                sb.append(value);
            }
        }
        sb.append(")");

        final String sql = sb.toString();
        Statement statement = connection.createStatement();
        try {
            long start=System.currentTimeMillis();
            updateCount = statement.executeUpdate(sql,
                    Statement.RETURN_GENERATED_KEYS);
            long time=System.currentTimeMillis()-start;
            
             LogUtil.info("-------mushroom insert serviceName:"+action.getServiceName()+" time:"+time);  
             LogUtil.info("-------mushroom insert serviceName: i sql:"+sql);  
            // 如果影响值大于1则调用通知，清除对应的data服务名缓存
            if (0 < updateCount) {
                updateCache(table.getTid());
            }
            result.addUpdateCount(updateCount);
            result.setDbName(dbName);
            result.setTableName(tableName);
            ResultSet generateKeys = statement.getGeneratedKeys();
            while (generateKeys.next()) {
                long generateKey = generateKeys.getLong(1);
                if (!generateKeys.wasNull()) {
                    result.setGenerateKey(generateKey);
                }
                break;
            }
        } finally {
            statement.close();
        }

        return updateCount;
    }

    private int insertIntoAdvance(Action action, ActionResult result, List<ActionResult> results, Connection connection,
            String dbName, String tableName, Map<String, String> fieldMap, TMushroomTable table,
            Map<String, Object> opLogInfo) throws SQLException {

        final Map<String, Object> set = action.getSet();
        int updateCount = 0;

        if (set == null) {
            return updateCount;
        }
        
        List<String> fields = new ArrayList<String>(set.size());
        List<String> values = new ArrayList<String>(set.size());
        for (String key : set.keySet()) {
            final String keyMapped = fieldMap.get(key);

            if (keyMapped == null) {
                LogUtil.error("serviceName:"+action.getServiceName()+",tableName"+tableName+",传入的字段未配置field["+key+"]");
                throw new RuntimeException("传入的字段未配置field["+key+"]");
            }

            fields.add("`" + keyMapped + "`");
            String value = Utils.getSqlString(set.get(key));

            if (value.equals("'null'")) {
                values.add(null);
            } else if (value.startsWith("'$") && value.endsWith(".generateKey'")) {
                values.add(fetchValue(value, results));
            } else if (value.startsWith("'$EP.")) {
                values.add(value.substring(5, value.length() - 1));
            } else if (value.startsWith("'$Date'")) {
                values.add("now()");
            } else if (value.startsWith("'$UnixTime'")) {
                values.add("unix_timestamp(now())");
            } else {
                values.add(value);
            }
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder opLogSb = new StringBuilder(); // 用于记录本次action中的操作参数
        sb.append("INSERT ").append(action.getKey_type()).append(" INTO ").append("`").append(dbName).append("`").append(".`").append(tableName).append("` ");

        sb.append("(");
        boolean first = true;
        for (String field : fields) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
                opLogSb.append(", ");
            }

            sb.append(field);
            opLogSb.append(field);
        }
        if (opLogInfo != null) {
            opLogInfo.put("param", opLogSb.toString());
        }
        sb.append(") VALUES(");
        first = true;
        for (String value : values) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            if (value == null) {
                sb.append("NULL");
            } else {
                sb.append(value);
            }
        }
        sb.append(")");

        final String sql = sb.toString();
        Statement statement = connection.createStatement();
        try {
            long start = System.currentTimeMillis();
            updateCount = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            long time = System.currentTimeMillis() - start;

            LogUtil.info("-------mushroom insert serviceName:" + action.getServiceName() + " time:" + time);
            LogUtil.info("-------mushroom insert serviceName: i sql:" + sql);
            // 如果影响值大于1则调用通知，清除对应的data服务名缓存
            if (0 < updateCount) {
                updateCache(table.getTid());
            }
            result.addUpdateCount(updateCount);
            result.setDbName(dbName);
            result.setTableName(tableName);
            ResultSet generateKeys = statement.getGeneratedKeys();
            while (generateKeys.next()) {
                long generateKey = generateKeys.getLong(1);
                if (!generateKeys.wasNull()) {
                    result.setGenerateKey(generateKey);
                }
                break;
            }
        } finally {
            statement.close();
        }

        return updateCount;
    }


    private static String fetchValue(String value, List<ActionResult> results) {
        String tmp = value.substring(2, value.length() - 13);
        int index;
        try {
            index = Integer.valueOf(tmp);
        } catch (Exception e) {
            throw new RuntimeException("$取值用法错误, 正确为 $-N.generateKey, " + tmp);
        }

        if (index >= 0) {
            throw new RuntimeException("$取值用法错误, 取值数字应该小于0, " + tmp);
        }

        int valueIndex = results.size() - 1 + index;
        if (valueIndex < 0) {
            throw new RuntimeException("$取值用法错误, 取值范围超出界限, " + index + ", "
                    + (results.size() - 1));
        }

        Long valueChoose = results.get(valueIndex).getGenerateKey();
        if (valueChoose == null) {
            throw new RuntimeException("$取值失败, 第" + valueIndex + "个操作没有返回自增主键");
        }

        return valueChoose.toString();
    }



    private void updateCache(int tid) {
        updateMushroomCache thread=new updateMushroomCache(tid);
        indexTheadHelper.execute(thread);
    }

    class updateMushroomCache implements ThreadCallback {
        private int tid;
        public updateMushroomCache(int tid){
            this.tid=tid;
        }
        @Override
        public void run() {
            // 从缓存中取值
            List<TMushroomTableService> TMushroomTableServiceList = ServicesConfig.getMushroomTableService(tid);
            // 没有取出来，则从数据库取值
            if (null == TMushroomTableServiceList) {
                TMushroomTableServiceList = tMushroomTableServiceDao.findbyTid(tid);
                ServicesConfig.setMushroomTableService(tid, TMushroomTableServiceList);
            }
            if(CollectionsUtils.isNotEmpty(TMushroomTableServiceList)){
                clearCacheAction.clearAll(TMushroomTableServiceList);
            }
        }
    }

    private void insertOpLogToDB(DsManageReqInfo opLogDSInfo) {
        @SuppressWarnings("unchecked")
        List<Action> actions = (List<Action>) opLogDSInfo.getParam().get(
                "actions");
        MushroomConnection connection = null;

        ActionResult result = new ActionResult();
        List<ActionResult> results = new ArrayList<ActionResult>();
        results.add(result);

        String lastDbName = null;

        for (int i = 0; i < actions.size(); i++) {
            final Action action = actions.get(i);

            try {
                final String serviceName = action.getServiceName();
                final TMushroomService service = ServicesConfig
                        .getService(serviceName);

                if (service == null) {
                    throw new RuntimeException("*服务未找到, " + serviceName);
                }

                if (service.getState() != null && service.getState() != 0) {
                    throw new RuntimeException("*服务已停用, " + serviceName);
                }

                final String scope = service.getScope() == null ? "" : service
                        .getScope();

                char opType;
                final String reqOpType = action.getType();
                if ("C".equalsIgnoreCase(reqOpType)) {
                    opType = 'C';
                } else if ("D".equalsIgnoreCase(reqOpType)) {
                    opType = 'D';
                } else {
                    throw new RuntimeException("*操作不支持 " + reqOpType);
                }

                if (scope.indexOf(opType) == -1) {
                    throw new RuntimeException("*操作仅支持 " + scope);
                }

                for (TMushroomTable table : service.getTables()) {

                    final TMushroomNode node = ServicesConfig.getNode(table
                            .getNid());
                    final TMushroomDB db = ServicesConfig.getDB(table.getDid());

                    if (node == null) {
                        throw new RuntimeException("*配置错误: Mysql节点未找到 "
                                + "@nid-" + table.getNid());
                    }

                    if (db == null) {
                        throw new RuntimeException("*配置错误: Mysql数据库未找到 "
                                + "@did-" + table.getDid());
                    }

                    if (connection == null) {
                        connection = ConnectionPool.poll(node,
                                System.currentTimeMillis() / 1000, false);
                        connection.jdbcConn.setAutoCommit(true);
                    }

                    final String dbName = getDBName(db, opType, action);
                    final String tableName = getTableName(table, opType, action);

                    Map<String, String> fieldMap = table.getFieldMap();
                    if (lastDbName == null || !lastDbName.equals(dbName)) {
                        final String useSql = "use " + dbName + ";";
                        connection.jdbcConn.prepareCall(useSql).execute();
                        lastDbName = dbName;
                    }

                    @SuppressWarnings("unused")
                    int updateCount = 0;
                    switch (opType) {
                    case 'C':
                        updateCount = insertInto(action, result, results,
                                connection.jdbcConn, dbName, tableName,
                                fieldMap, table, null);
                        break;
                    case 'D':
                        updateCount = deleteFrom(action, result, results,
                                connection.jdbcConn, dbName, tableName,
                                fieldMap, table, null);
                    }
                }
            } catch (Exception e) {
                String msg = action.getServiceName() + "-" + action.getType()
                        + " 失败, " + e.getMessage();
                LogUtil.error(msg);
                break;
            }
        }

        if (connection != null) {
            ConnectionPool.offer(connection, true);
        }
    }
    
    /**
     * 
     * @Title: insertRuleException2DB
     * @Description: TODO(将规则本身出现的异常存入数据库中)
     * @param @param serviceName
     * @param @param msg 参数说明
     * @return void 返回类型
     * @throws
     */
    public void insertRuleException2DB(String serviceName, String msg) {
        // 将发送记录写入数据库
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        String sname = "meiqiserver_t_service_alarm";
        Action action = new Action();
        action.setType("C");
        action.setServiceName(sname);
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("sname", serviceName);
        set.put("msg", msg);
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);

        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        offer(actionReqInfo);
    }
    /**
     * 
    * @Title: insertMushRoomException2DB 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param serviceName
    * @param @param msg  参数说明 
    * @param @param type '来源 1 来自规则,2 来自mushroom接口',
    * @return void    返回类型 
    * @throws
     */
    public void insertMushRoomException2DB(String serviceName, String msg,String type) {
        // 将发送记录写入数据库
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        String sname = "meiqiserver_t_mushroom_alarm";
        Action action = new Action();
        action.setType("C");
        action.setServiceName(sname);
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("name", serviceName);
        set.put("description", msg);
        set.put("ctime", "$Date");
        set.put("type", type);
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);

        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        insertOpLogToDB(actionReqInfo);
    }

    /** 
    * @Title: insertMushroomError 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param msg  参数说明 
    * @param @param type '来源 1 来自规则,2 来自mushroom接口',
    * @return void    返回类型 
    * @throws 
    */
    private void insertMushroomError(String serviceName,String msg,String type) {
        InsertMushroomErrorThread thread=new InsertMushroomErrorThread(serviceName,msg,type);
        indexTheadHelper.execute(thread);
    }
    
    class InsertMushroomErrorThread implements ThreadCallback {
        private String serviceName;
        private String errorMsg;
        private String type;
        public InsertMushroomErrorThread(String serviceName,String errorMsg,String type){
            this.serviceName=serviceName;
            this.errorMsg=errorMsg;
            this.type=type;
        }
        @Override
        public void run() {
            insertMushRoomException2DB(serviceName, errorMsg,type);
        }
    }
}
