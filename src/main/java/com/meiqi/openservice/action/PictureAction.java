/*
 * File name: PictureAction.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年8月12日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.cache.CachePool;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.jms.producer.TextMessageProducer;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;

/**
 * <class description>
 * 
 * @author: luzicong
 * @version: 1.0, 2015年8月12日
 */
@Service
public class PictureAction extends BaseAction {

    public interface EVENT_TYPE {
        String LIKE      = "1";
        String FAVORITES = "2";
        String COMMENT   = "3";
        String OTHER     = "4";
    }

    public interface EVENT_TYPE_INT {
        int LIKE      = 1;
        int FAVORITES = 2;
        int COMMENT   = 3;
        int OTHER     = 4;
    }

    public interface EVENT_TYPE_STRING {
        String LIKE      = "喜欢了";
        String FAVORITES = "收藏了";
        String COMMENT   = "发布了";
        String OTHER     = "";
    }

    private static final Logger LOG                           = Logger.getLogger(PictureAction.class);

    // 累积点击商品图片达到此次数则加1次心
    private static final int    MAX_COUNT                     = 5;

    // memcached缓存 key:lejj_picture_total
    private static final String MKEY_LEJJ_PICTURE_TOTAL       = MD5Util.MD5("lejj_picture_total");

    // memcached缓存 key:lejj_picture_total_map
    private static final String MKEY_LEJJ_PICTURE_TOTAL_MAP   = MD5Util.MD5("lejj_picture_total_map");

    // memcached缓存 key:lejj_picture_event
    private static final String MKEY_LEJJ_PICTURE_EVENT       = MD5Util.MD5("lejj_picture_event");

    // 装修效果图、案例图
    private static final String MKEY_LEJJ_FINISH_PICTURE_CASE = MD5Util.MD5("lejj_finish_picture_case");

    // key前缀 pid
    private static final String MAP_KEY_PID                   = "pid";

    @Autowired
    private IMushroomAction     mushroomAction;
    @Autowired
    private IDataAction         dataAction;
    @Autowired
    private IMemcacheAction     memcacheService;

    @Autowired
    private ThreadHelper        indexTheadHelper;

    @Autowired
    private TextMessageProducer  removeCacheProducer;


    public String increasePictureClick(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.debug("Function: increasePictureClick.Start.");
        ResponseInfo respInfo = new ResponseInfo();

        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);

        // 获取site_id
        String site_id = paramMap.get("site_id");
        if (StringUtils.isBlank(site_id)) {
            site_id = "0";
        }

        // 检查输入参数是否为空
        String key = paramMap.get("key");
        if (StringUtils.isBlank(key)) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description);
            return JSON.toJSONString(respInfo);
        }

        String type = paramMap.get("type"); // 1:喜欢 2:收藏 3:评论
        if (StringUtils.isBlank(type)) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description);
            return JSON.toJSONString(respInfo);
        }

        if (!type.equals(EVENT_TYPE.LIKE)) {
            respInfo.setCode(DsResponseCodeData.NOT_SET_DATASOURCE_TYPE.code);
            respInfo.setDescription(DsResponseCodeData.NOT_SET_DATASOURCE_TYPE.description);
            return JSON.toJSONString(respInfo);
        }

        String uid = paramMap.get("uid");
        String userName = paramMap.get("user_name");
        String avatar = paramMap.get("avatar");
        if (StringUtils.isEmpty(uid)) {
            Map<String, String> userMap = getUser();
            uid = userMap.get("user_id");
            userName = userMap.get("user_name");
            avatar = userMap.get("avatar");
        }

        int lastUpdateTime = DateUtils.getSecond();
        
        Map<String, Object> m = CachePool.getInstance().incLikeCache(key, uid);
        int count = Integer.valueOf(String.valueOf(m.get("count")));

        if (count >= MAX_COUNT) {
            // 累积点击商品图片次数10次则加1次心

            Map<String, Map<String, Object>> pictureTotalListMap = (LinkedHashMap<String, Map<String, Object>>) memcacheService
                    .getCache(MKEY_LEJJ_PICTURE_TOTAL_MAP);
            if (null == pictureTotalListMap) {
                pictureTotalListMap = new LinkedHashMap<String, Map<String, Object>>();
            }
            Map<String, Object> pictureTotalMap = null;
            if (CollectionsUtils.isNull(pictureTotalListMap) || !pictureTotalListMap.containsKey(MAP_KEY_PID + key)) {
                // 缓存里面没有 就去rule获取
                Map<String, Map<String, Object>> pictureTotalListMapForRule = getFavouriteForRule(com.meiqi.app.common.utils.StringUtils
                        .getStringList(key, ","));
                if(!CollectionsUtils.isNull(pictureTotalListMapForRule)){
                    pictureTotalListMap.putAll(pictureTotalListMapForRule);
                }
                pictureTotalMap = pictureTotalListMap.get(MAP_KEY_PID + key);
            } else {
                pictureTotalMap = pictureTotalListMap.get(MAP_KEY_PID + key);
            }
            if (null == pictureTotalMap) {
                pictureTotalMap = new HashMap<String, Object>();
                pictureTotalMap.put("total_like", 0);
            }

            pictureTotalMap.put("total_like",
                    com.meiqi.app.common.utils.StringUtils.StringToInt(pictureTotalMap.get("total_like").toString()) + 1);
            pictureTotalMap.put("user_name", userName);
            pictureTotalMap.put("last_update_time", lastUpdateTime);
            pictureTotalMap.put("uid", uid);
            pictureTotalMap.put("type", type);
            if (!com.meiqi.app.common.utils.StringUtils.isBlank(avatar)) {
                pictureTotalMap.put("avatar", avatar);
            }
            pictureTotalMap.put("type_info", getTypeInfo(Integer.parseInt(type)));
            setEventInfo(pictureTotalMap);

            // update 缓存先删除以前的 把刚更新的放在最后(避免被put失败时删除)
            pictureTotalListMap.remove(MAP_KEY_PID + key);
            pictureTotalListMap.put(MAP_KEY_PID + key, pictureTotalMap);
            // 存入缓存
            putFavouriteToCache(MKEY_LEJJ_PICTURE_TOTAL_MAP, pictureTotalListMap);
            
            // 清除点击图片次数的缓存
            CachePool.getInstance().removeLikeCache(key);
            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
            
            // 多线程存入memcached for shell
            FavouriteThreadForShell threadForShell = new FavouriteThreadForShell(pictureTotalMap, Integer.parseInt(key), 1, userName,
                    uid, avatar, lastUpdateTime);
            indexTheadHelper.execute(threadForShell);
        }else{
        	respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription("点击次数为："+count+"次");
        }

        LOG.debug("Function: increasePictureClick.End.");
        return JSON.toJSONString(respInfo);
    }



    public boolean increasePictureTotalLike(String key, Map<String, Object> m, String site_id) {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");

        List<Action> actions = new ArrayList<Action>();

        Action a = getUpdatePictureAction(key, m, site_id);
        if (a == null) {
            return false;
        }
        actions.add(a);

        Action b = getInsertPictureEventAction(key, m, site_id);
        if (b != null) {
            actions.add(b);
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        SetServiceResponseData actionResponse = null;
        String res = mushroomAction.offer(actionReqInfo);
        actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            return true;
        } else {
            return false;
        }
    }



    private Action getUpdatePictureAction(String key, Map<String, Object> m, String site_id) {
        String serviceName = "zx_new1023_ecs_pictures";
        Action action = new Action();
        action.setSite_id(Integer.parseInt(site_id));
        action.setType("U");
        action.setServiceName(serviceName);

        SqlCondition condition = new SqlCondition();
        condition.setKey("id"); // user_name 与邮件地址一致，能唯一查找到用户
        condition.setOp("=");
        condition.setValue(key);

        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        conditions.add(condition);

        Where where = new Where();
        where.setPrepend("and");
        where.setConditions(conditions);

        action.setWhere(where);

        Map<String, Object> set = new HashMap<String, Object>();
        try {
            set.put("total_like", "$EP.total_like +1");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        action.setSet(set);

        return action;
    }



    private Action getInsertPictureEventAction(String pid, Map<String, Object> m, String site_id) {
        if (m.get("uid") == null || m.get("eventTime") == null) {
            return null;
        }

        String serviceName = "zx_new1023_picture_event";
        Action action = new Action();
        action.setSite_id(Integer.parseInt(site_id));
        action.setType("C");
        action.setServiceName(serviceName);

        Map<String, Object> set = new HashMap<String, Object>();
        try {
            String uid = String.valueOf(m.get("uid"));
            int eventTime = Integer.valueOf(String.valueOf(m.get("eventTime")));

            set.put("uid", Integer.valueOf(String.valueOf(uid)));
            set.put("event_time", eventTime);

            set.put("pid", Integer.valueOf(String.valueOf(pid)));
            set.put("type", Integer.valueOf(String.valueOf(EVENT_TYPE.LIKE)));
            set.put("album_id", 0);
            set.put("pid_old", 0);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        action.setSet(set);

        return action;
    }



    public Object favourite(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        ResponseInfo respInfo = new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

        String param = repInfo.getParam();
        DsManageReqInfo dsReqInfo = DataUtil.parse(param, DsManageReqInfo.class);
        Map<String, Object> params = dsReqInfo.getParam();
        String userId = "";
        if (params.get("uId") != null) {
            userId = params.get("uId").toString();
        }
        String picId = params.get("picId").toString();
        if (com.meiqi.openservice.commons.util.StringUtils.isEmpty(userId)) {
            userId = getUser().get("user_id");
        }
        // 异步去操作数据库
        FavouriteThread thread = new FavouriteThread(userId, picId);
        indexTheadHelper.execute(thread);
        return respInfo;
    }



    /**
     * 
     * 到缓存中随机选取一个
     *
     * @return
     */
    private Map<String, String> getUser() {
        Map<String, String> resultMap = null;
        // 如果用户ID没有值，那么到缓存中随机选取一个
        DsManageReqInfo reqInfo = new DsManageReqInfo();
        reqInfo.setServiceName("LJJ_BUV1_anonymoususer");
        reqInfo.setNeedAll("1");
        List<Map<String, String>> list = (List<Map<String, String>>) memcacheService.getCache("randomUserLists");
        if (list == null || list.size() == 0) {
            // 去缓存
            String result = dataAction.getData(reqInfo);
            RuleServiceResponseData responseData = DataUtil.parse(result, RuleServiceResponseData.class);
            list = responseData.getRows();
        }
        resultMap = list.get(0);
        list.remove(0);
        // 放缓存
        memcacheService.putCache("randomUserLists", list, 60 * 60 * 1000);
        return resultMap;
    }

    class FavouriteThread implements ThreadCallback {
        private String userId;
        private String picId;



        public FavouriteThread(String userId, String picId) {
            super();
            this.userId = userId;
            this.picId = picId;
        }



        public void run() {
            DsManageReqInfo reqInfo = new DsManageReqInfo();
            String serviceName = "zx_new1023_ecs_pictures";
            Map<String, Object> set = new HashMap<String, Object>();
            set.put("uid", userId);
            set.put("total_like", "$EP.total_like +1");
            set.put("last_update_time", "$UnixTime");

            Action action = new Action();
            action.setType("U");
            action.setServiceName(serviceName);
            SqlCondition condition = new SqlCondition();
            condition.setKey("id"); // user_name 与邮件地址一致，能唯一查找到用户
            condition.setOp("=");
            condition.setValue(picId);

            action.setSet(set);

            List<SqlCondition> conditions = new ArrayList<SqlCondition>();
            conditions.add(condition);

            Where where = new Where();
            where.setPrepend("and");
            where.setConditions(conditions);
            action.setWhere(where);

            String serviceName1 = "zx_new1023_picture_event";
            Action action1 = new Action();
            action1.setType("C");
            action1.setServiceName(serviceName1);
            Map<String, Object> set1 = new HashMap<String, Object>();
            set1.put("uid", userId);
            set1.put("event_time", "$UnixTime");
            set1.put("pid", Integer.valueOf(picId));
            set1.put("type", Integer.valueOf(EVENT_TYPE.LIKE));
            set1.put("album_id", 0);
            set1.put("pid_old", 0);
            action1.setSet(set1);

            List<Action> actions = new ArrayList<Action>();
            actions.add(action);
            actions.add(action1);

            Map<String, Object> param1 = new HashMap<String, Object>();
            param1.put("actions", actions);
            param1.put("transaction", 1);
            reqInfo.setParam(param1);
            String res = mushroomAction.offer(reqInfo);
            LogUtil.info("PictureAction favourite res:" + res);
        }
    }



    /**
     * 
     * 设置喜欢
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public Object setFavourite(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.info("Function:setFavourite.Start.");
        ResponseInfo respInfo = new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

        String param = repInfo.getParam();
        DsManageReqInfo dsReqInfo = DataUtil.parse(param, DsManageReqInfo.class);
        Map<String, Object> params = dsReqInfo.getParam();
        String pid = (String) params.get("pid");
        int pidInt = com.meiqi.app.common.utils.StringUtils.StringToInt(pid);
        Object typeObj = params.get("type");
        // if type 没有传递 默认为喜欢
        int type = 1;
        if (null == typeObj) {
            type = 1;
        } else {
            type = com.meiqi.app.common.utils.StringUtils.StringToInt(typeObj.toString());
        }
        String userName = (String) params.get("user_name");
        String uid = (String) params.get("uid");
        String avatar = (String) params.get("avatar");
        if (StringUtils.isEmpty(uid)) {
            Map<String, String> userMap = getUser();
            uid = userMap.get("user_id");
            userName = userMap.get("user_name");
            avatar = userMap.get("avatar");
        }

        int lastUpdateTime = DateUtils.getSecond();
        // 从memcached 获取
        Map<String, Map<String, Object>> pictureTotalListMap = (LinkedHashMap<String, Map<String, Object>>) memcacheService
                .getCache(MKEY_LEJJ_PICTURE_TOTAL_MAP);
        // if is null
        if (null == pictureTotalListMap) {
            pictureTotalListMap = new LinkedHashMap<String, Map<String, Object>>();
        }
        Map<String, Object> pictureTotalMap = null;
        if (CollectionsUtils.isNull(pictureTotalListMap) || !pictureTotalListMap.containsKey(MAP_KEY_PID + pid)) {
            // 缓存里面没有 就去rule获取
            Map<String, Map<String, Object>> pictureTotalListMapForRule = getFavouriteForRule(com.meiqi.app.common.utils.StringUtils
                    .getStringList(pid, ","));
            if(!CollectionsUtils.isNull(pictureTotalListMapForRule)){
                pictureTotalListMap.putAll(pictureTotalListMapForRule);
            }
            pictureTotalMap = pictureTotalListMap.get(MAP_KEY_PID + pid);
        } else {
            pictureTotalMap = pictureTotalListMap.get(MAP_KEY_PID + pid);
        }
        if (null == pictureTotalMap) {
            pictureTotalMap = new HashMap<String, Object>();
            pictureTotalMap.put("total_like", 0);
        }

        pictureTotalMap.put("total_like",
                com.meiqi.app.common.utils.StringUtils.StringToInt(pictureTotalMap.get("total_like").toString()) + 1);
        pictureTotalMap.put("user_name", userName);
        pictureTotalMap.put("last_update_time", lastUpdateTime);
        pictureTotalMap.put("uid", uid);
        pictureTotalMap.put("type", type);
        if (!com.meiqi.app.common.utils.StringUtils.isBlank(avatar)) {
            pictureTotalMap.put("avatar", avatar);
        }
        pictureTotalMap.put("type_info", getTypeInfo(type));
        setEventInfo(pictureTotalMap);

        // update 缓存先删除以前的 把刚更新的放在最后(避免被put失败时删除)
        pictureTotalListMap.remove(MAP_KEY_PID + pid);
        pictureTotalListMap.put(MAP_KEY_PID + pid, pictureTotalMap);
        // 存入缓存
        putFavouriteToCache(MKEY_LEJJ_PICTURE_TOTAL_MAP, pictureTotalListMap);
        // 返回前端结果
        List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        Map<String, String> result = new HashMap<String, String>();
        result.put(pid, DataUtil.toJSONString(pictureTotalMap));
        rows.add(result);
        respInfo.setRows(rows);

        // 多线程存入memcached for shell
        FavouriteThreadForShell threadForShell = new FavouriteThreadForShell(pictureTotalMap, pidInt, type, userName,
                uid, avatar, lastUpdateTime);
        indexTheadHelper.execute(threadForShell);
        LOG.info("Function:setFavourite.End.");
        return respInfo;

    }

    /**
     * 
     * 将需要shell脚本更新的数据库的数据存入缓存
     *
     * @author: 杨永川
     * @version: 1.0, 2015年8月20日
     */
    class FavouriteThreadForShell implements ThreadCallback {
        Map<String, Object> pictureTotalMap;
        int                 pidInt = 0;
        int                 type   = 1;
        String              userName;
        String              uid;
        String              avatar;
        int                 lastUpdateTime;



        public FavouriteThreadForShell(Map<String, Object> pictureTotalMap, int pidInt, int type, String userName,
                String uid, String avatar, int lastUpdateTime) {
            super();
            this.pictureTotalMap = pictureTotalMap;
            this.pidInt = pidInt;
            this.type = type;
            this.userName = userName;
            this.uid = uid;
            this.avatar = avatar;
            this.lastUpdateTime = lastUpdateTime;
        }



        public void run() {
            // total
            Map<String, Map<String, Object>> pictureTotalListMap = null;
            Object pictureTotalListMapJson = memcacheService.getCache(MKEY_LEJJ_PICTURE_TOTAL);
            if (null == pictureTotalListMapJson) {
                pictureTotalListMap = new HashMap<String, Map<String, Object>>();
            } else {
                pictureTotalListMap = DataUtil.parse(pictureTotalListMapJson.toString(), Map.class);
            }
            // 存入缓存
            pictureTotalListMap.put(MAP_KEY_PID + pidInt, pictureTotalMap);
            boolean result=memcacheService.putCache(MKEY_LEJJ_PICTURE_TOTAL, DataUtil.toJSONString(pictureTotalListMap));
            LogUtil.info("picture favourite put memcache MKEY_LEJJ_PICTURE_TOTAL result:"+result);
            // event
            Map<String, Map<String, Object>> pictureEventMap = null;
            // 获取 picture_event_map
            Object pictureEventMapJson = memcacheService.getCache(MKEY_LEJJ_PICTURE_EVENT);
            if (null == pictureEventMapJson) {
                pictureEventMap = new LinkedHashMap<String, Map<String, Object>>();
            } else {
                pictureEventMap = DataUtil.parse(pictureEventMapJson.toString(), LinkedHashMap.class);
            }
            Map<String, Object> pictureEventItem = new HashMap<String, Object>();
            pictureEventItem.put("pid", pidInt);
            pictureEventItem.put("uid", uid);
            pictureEventItem.put("total_like", pictureTotalMap.get("total_like"));
            pictureEventItem.put("event_time", lastUpdateTime);
            pictureEventItem.put("type", type);
            pictureEventItem.put("pid_old", 0);
            pictureEventItem.put("album_id", 0);
            pictureEventMap.put("key" + (pictureEventMap.size() + 1), pictureEventItem);
            // update 缓存
            boolean result1=memcacheService.putCache(MKEY_LEJJ_PICTURE_EVENT, DataUtil.toJSONString(pictureEventMap));
            LogUtil.info("picture favourite put memcache MKEY_LEJJ_PICTURE_EVENT result:"+result1);
        }
    }



    /**
     * 
     * 放进memcached 如果失败（到达限制1M,删除100数据）
     *
     * @param cacheKey
     * @param map
     */
    private void putFavouriteToCache(String cacheKey, Map map) {
        boolean result = memcacheService.putCache(cacheKey, map);
        if (!result) {
            int number = com.meiqi.app.common.utils.StringUtils.StringToInt(SysConfig
                    .getValue("favourite_memcached_number"));
            Iterator iterator = map.keySet().iterator();
            int i = 0;
            while (iterator.hasNext() && i < number) {
                String key = (String) iterator.next();
                // 同时remove
                iterator.remove();
                map.remove(key);
                i++;
            }
            // 删除一些数据后在put
            memcacheService.putCache(cacheKey, map);
        }
    }



    /**
     * 
     * 获取pictures 信息，从memcache
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public Object getFavourite(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.info("Function:getFavourite.Start.");
        ResponseInfo respInfo = new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

        String param = repInfo.getParam();
        DsManageReqInfo dsReqInfo = DataUtil.parse(param, DsManageReqInfo.class);
        Map<String, Object> params = dsReqInfo.getParam();
        String pid = (String) params.get("pid");
        if (StringUtils.isEmpty(pid)) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("pid不能为空!");
            return respInfo;
        }

        // 返回前端结果
        Map<String, String> result = null;
        List<String> noCachePids = new ArrayList<String>();
        List<String> pidList = com.meiqi.app.common.utils.StringUtils.getStringList(pid, ",");
        if (!CollectionsUtils.isNull(pidList)) {
            result = new HashMap<String, String>();
            // 从memcached 获取
            Map<String, Map<String, Object>> pictureTotalMap = (LinkedHashMap<String, Map<String, Object>>) memcacheService
                    .getCache(MKEY_LEJJ_PICTURE_TOTAL_MAP);
            if (CollectionsUtils.isNull(pictureTotalMap)) {
                // 缓存里面没有 就去rule获取
                pictureTotalMap = getFavouriteForRule(com.meiqi.app.common.utils.StringUtils.getStringList(pid, ","));
                LogUtil.info("getFavourite 从memcached中去取的pictureTotalMap值为空,从数据库取值："+pictureTotalMap);
                // 并加入缓存
                if(!CollectionsUtils.isNull(pictureTotalMap)){
                    putFavouriteToCache(MKEY_LEJJ_PICTURE_TOTAL_MAP, pictureTotalMap);
                }
            }
            for (int i = 0; i < pidList.size(); i++) {
                String id = pidList.get(i);
                Map<String, Object> favourite = (Map<String, Object>) pictureTotalMap.get(MAP_KEY_PID + id);
                LogUtil.info("getFavourite pid="+id+",value:"+favourite);
                if (null == favourite) {
                    noCachePids.add(id);
                } else {
                    // setEventInfo 如：5月前发布了
                    setEventInfo(favourite);
                    result.put(id, DataUtil.toJSONString(favourite));
                }
            }

            // 没有缓存的Favourite
            if (!CollectionsUtils.isNull(noCachePids)) {
                Map<String, Map<String, Object>> noCachepictureTotals = getFavouriteForRule(noCachePids);
                if (!CollectionsUtils.isNull(noCachepictureTotals)) {
                    pictureTotalMap.putAll(noCachepictureTotals);
                    // 并加入缓存
                    putFavouriteToCache(MKEY_LEJJ_PICTURE_TOTAL_MAP, pictureTotalMap);
                    for (int i = 0; i < pidList.size(); i++) {
                        String id = pidList.get(i);
                        Map<String, Object> noCacheFavourite = (Map<String, Object>) noCachepictureTotals
                                .get(MAP_KEY_PID + id);
                        if (null != noCacheFavourite) {
                            // setEventInfo 如：5月前发布了
                            setEventInfo(noCacheFavourite);
                            result.put(id, DataUtil.toJSONString(noCacheFavourite));
                        }
                    }
                }
            }
            List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
            rows.add(result);
            respInfo.setRows(rows);
        }
        LOG.info("Function:getFavourite.End.");
        return respInfo;
    }



    /**
     * 
     * 当memca里面没有数据时，获取pictures 信息，从rule
     *
     * @param pidList
     */
    public Map<String, Map<String, Object>> getFavouriteForRule(List<String> pidList) {
        if (CollectionsUtils.isNull(pidList)) {
            return null;
        }

        Map<String, Object> param = new HashMap<String, Object>();
        StringBuffer pids = new StringBuffer();
        for (int i = 0; i < pidList.size(); i++) {
            pids.append(pidList.get(i));
            pids.append(i < pidList.size() - 1 ? "," : "");
        }

        param.put("pic_id", pids.toString());

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("ZXZX_HSV1_picture_info");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        Map<String, Map<String, Object>> pictureTotals = new LinkedHashMap<String, Map<String, Object>>();
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) json;
        for (Map<String, Object> item : mapListJson) {
            String pid = (String) item.get("pic_id");
            item.put("pid", pid);
            item.put("last_update_time", item.get("event_time"));
            pictureTotals.put(MAP_KEY_PID + pid, item);
        }
        return pictureTotals;
    }



    /**
     * 
     * setEventInfo 5月前发布了
     *
     * @param favourite
     */
    private void setEventInfo(Map<String, Object> favourite) {
        int lastUpdateTime = com.meiqi.app.common.utils.StringUtils.StringToInt(favourite.get("last_update_time")
                .toString());
        String date = DateUtils.dateDiff(lastUpdateTime, DateUtils.getSecond());
        String typeInfo = (String) favourite.get("type_info");
        favourite.put("event_info", date + (null == typeInfo ? "" : typeInfo));
    }



    /**
     * 
     * 获取typeInfo 喜欢了 发布了
     *
     * @param type
     * @return
     */
    private String getTypeInfo(int type) {
        String typeInfo = null;
        switch (type) {
        case EVENT_TYPE_INT.LIKE:
            typeInfo = EVENT_TYPE_STRING.LIKE;
            break;
        case EVENT_TYPE_INT.FAVORITES:
            typeInfo = EVENT_TYPE_STRING.FAVORITES;
            break;
        case EVENT_TYPE_INT.COMMENT:
            typeInfo = EVENT_TYPE_STRING.COMMENT;
            break;
        default:
            typeInfo = "";
            break;
        }
        return typeInfo;
    }



    /**
     * 
     * @Title: sendFitmentMessage
     * @Description: TODO(将装修效果图和案例消息写入MQ)
     * @param @param request
     * @param @param response
     * @param @param repInfo
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    public String sendFitmentMessage(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        if("false".equals(SysConfig.getValue("isOpenMQ"))){
            return "";
        }
        ResponseInfo respInfo=new ResponseInfo();
        String code = "1";
        String description = "参数不能为空,数据写入MQ失败！";
        Map<String, Object> map = DataUtil.parse(repInfo.getParam(), Map.class);
        try{
            if(!checkParamIsNull(map)){
                LogUtil.info("MQ_Fitment_Message：发送消息!");
                removeCacheProducer.setDestination("meiqi/picExposure");
                removeCacheProducer.publish(map);
                code = DsResponseCodeData.SUCCESS.code;
                description = DsResponseCodeData.SUCCESS.description;
            }else{
                description = repInfo.getParam() + description;
            }
        }catch(Exception e){
            code = "-1";
            description = e.getMessage();
            LogUtil.info("MQ_Fitment_Message:"+e.getMessage());
            e.printStackTrace();
        }
        respInfo.setCode(code);
        respInfo.setDescription(description);
        String json = JSON.toJSONString(respInfo);
        LogUtil.info("MQ_Fitment_Message:"+json);
        return json;
    }


    /**
     * 
    * @Title: checkParamIsNull 
    * @Description: TODO(判断参数是否为空) 
    * @param @param map
    * @param @return  参数说明 
    * @return boolean    返回类型 
    * @throws
     */
    private boolean checkParamIsNull(Map<String, Object> map){
        if(null==map){
            return true;
        }
        boolean idIsNull = true;
        boolean urlIsNull = true;
        boolean typeIsNull = true;
        if(map.containsKey("lejj_mq_info")&&!map.get("lejj_mq_info").equals("")){
            idIsNull = false;
        }
        if(map.containsKey("currentUrl")&&!map.get("currentUrl").equals("")){
            urlIsNull = false;
        }
        if(map.containsKey("type")&&!map.get("type").equals("")){
            typeIsNull = false;
        }
        return idIsNull && urlIsNull && typeIsNull;
    }
}