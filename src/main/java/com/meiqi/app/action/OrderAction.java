package com.meiqi.app.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meiqi.app.common.utils.*;
import com.meiqi.dsmanager.action.IPushAction;
import com.meiqi.util.MyApplicationContextUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.CodeUtils;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.DeliveryGoods;
import com.meiqi.app.pojo.DiscountInfo;
import com.meiqi.app.pojo.Order;
import com.meiqi.app.pojo.PayStatus;
import com.meiqi.app.pojo.TransportMethod;
import com.meiqi.app.pojo.UserAddress;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.service.EtagService;
import com.meiqi.app.service.OrderService;
import com.meiqi.app.service.UsersService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.action.IPushAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 
 * @author fangqi
 * @date 2015年7月1日 下午2:34:44
 * @discription
 */
@Service
public class OrderAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(OrderAction.class);

    @Autowired
    private OrderService        orderService;

    @Autowired
    private EtagService         eTagService;

    @Autowired
    private UsersService        usersService;

    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private IMushroomAction     mushroomAction;

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        String method = appRepInfo.getMethod();
        String param = appRepInfo.getParam();
        Map<String, Object> header = appRepInfo.getHeader();
        long userId = (Long) header.get("userId");
        String plat = (String) header.get("plat");
        String ip=getIp(request);
        String resultJson = "";// 返回json结果
        if (method.equals("get")) {
            String key = "";
            if (url.equals("order")) {// 获取所有order
                resultJson = getAllOrder(userId, param, plat);
                key = "order/" + userId;
            } else if (url.equals("queryOrderAtPhone")) {
                // 根据phone获取对应订单
                return getAllOrderByPhone(param, plat);
            } else if (StringUtils.matchByRegex(url, "^order\\/\\d+$")) {
                long orderId = Long.parseLong(url.replace("order/", ""));
                key = "order/" + orderId;
                resultJson = getOrderDetail(userId, orderId, plat);
            } else if (StringUtils.matchByRegex(url, "^order/payStatus\\/\\d+$")) {
                long orderId = Long.parseLong(url.replace("order/payStatus/", ""));
                key = "order/payStatus/" + orderId;
                resultJson = getPayStatus(userId, orderId, plat);
            } else if (StringUtils.matchByRegex(url, "^order/payWay\\/\\d+$")) {
                long orderId = Long.parseLong(url.replace("order/payWay/", ""));
                key = "order/payStatus/" + orderId;
                String userAgent=(String) header.get("User-Agent");
                resultJson = getpayWay(userId, orderId, plat,ip,userAgent);
            } else if (StringUtils.matchByRegex(url, "^order\\/discount$")) {
                DiscountInfo discountParam = DataUtil.parse(appRepInfo.getParam(), DiscountInfo.class);
                resultJson = getDiscountInfo(discountParam);
            }
            boolean result = eTagService.toUpdatEtag1(request, response, key, resultJson);
            if (result) {
                return null;
            } else {
                eTagService.putEtagMarking(request, key, resultJson);
            }
        } else if (method.equals("put") && url.equals("order")) {
            resultJson = addGoodsToOrder(request, userId, param);
        } else if (method.equals("patch") && StringUtils.matchByRegex(url, "^order\\/\\d+$")) {
            long orderId = Long.parseLong(url.replace("order/", ""));
            // eTagService.putEtagMarking("order/" + orderId,
            // Long.toString(System.currentTimeMillis()));
            resultJson = updateOrder(userId, orderId, param);
        } else if (method.equals("delete") && StringUtils.matchByRegex(url, "^order\\/\\d+$")) {
            long orderId = Long.parseLong(url.replace("order/", ""));
            // eTagService.putEtagMarking("order/" + orderId,
            // Long.toString(System.currentTimeMillis()));
            resultJson = deleteOrder(userId, orderId);
        }
        return resultJson;
    }



    /**
     * 
     * @Title: getDiscountInfo
     * @Description:获取折扣信息
     * @param @param order
     * @param @return
     * @return String
     * @throws
     */
    private String getDiscountInfo(DiscountInfo discountParam) {
        LogUtil.info("Function:getDiscountInfo.Start.");

        if (null == discountParam || !(discountParam.getGoodsPrice() > 0)
                || StringUtils.isBlank(discountParam.getCode())) {
            return JsonUtils.getErrorJson("传入参数不正确!", null);
        }
        String discountInfoJson = null;
        DiscountInfo discountInfo = orderService.getDiscountInfo(discountParam);
        if (null != discountInfo) {
            discountInfoJson = JsonUtils.objectFormatToString(discountInfo,
                    StringUtils.getStringList("isFlag,discountInfo,discountPrice", ContentUtils.COMMA));
        } else {
            discountInfoJson = JsonUtils.getErrorJson("折扣码无效!", null);
        }
        LogUtil.info("Function:getDiscountInfo.End.");
        return discountInfoJson;
    }



    /**
     * 
     * @Title: getAllOrder
     * @Description:获取所有订单
     * @param @param userId
     * @param @return
     * @return String
     * @throws
     */
    public String getAllOrder(long userId, String param, String plat) {
        Order order = DataUtil.parse(param, Order.class);
        int pageIndex = 0;
        int pageSize = 0;
        if (null != order) {
            pageIndex = order.getPageIndex();
            pageSize = order.getPageSize();
        }
        String orderList = orderService.getAllOrder(userId, plat, pageIndex, pageSize);
        if (!StringUtils.isBlank(orderList)) {
            // xml to json
            orderList = JsonUtils.xmlStringToJson(orderList);
        }
        return orderList;
    }



    /**
     * 
     * @Title: getAllOrderByPhone
     * @Description: TODO根据电话号码查询订单信息
     * @param @param phone
     * @param @param plat
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    public String getAllOrderByPhone(String params, String plat) {
        LOG.info("Function:getAllOrderByPhone.Start.");
        Order order = DataUtil.parse(params, Order.class);
        int pageIndex = 0;
        int pageSize = 0;
        String phone = null;
        if (null != order) {
            phone = order.getPhone();
            pageIndex = order.getPageIndex();
            pageSize = order.getPageSize();
        } else {
            return JsonUtils.getErrorJson("请正确输入参数!", null);
        }
        if (StringUtils.isBlank(phone)) {
            return JsonUtils.getErrorJson("请输入手机号!", null);
        }
        String resultData = orderService.getAllOrderByPhone(phone, pageIndex, pageSize, plat);
        if (!StringUtils.isBlank(resultData)) {
            resultData = JsonUtils.xmlStringToJson(resultData);
        }
        LOG.info("Function:getAllOrderByPhone.End.");
        return resultData;
    }



    /**
     * 
     * @Title: getOrderDetail
     * @Description:获取某个订单详情
     * @param @param orderId
     * @param @return
     * @return String
     * @throws
     */
    public String getOrderDetail(long userId, long orderId, String plat) {
        LOG.info("Function:getOrderDetail.Start.");
        String orderDetail = orderService.getOrderDetail(userId, orderId, plat);
        // xml to json
        orderDetail = JsonUtils.xmlStringToJson(orderDetail);
        LOG.info("Function:getOrderDetail.End.");
        return orderDetail;
    }



    /**
     * 
     * @Title: verifyUserAddress
     * @Description:验证usersAddres
     * @param @param userAddress
     * @param @return
     * @return String
     * @throws
     */
    private String verifyUserAddress(UserAddress userAddress) {
        if (null == userAddress) {
            return JsonUtils.getErrorJson("请输入收货地址!", null);
        }
        if (StringUtils.isBlank(userAddress.getName())) {
            return JsonUtils.getErrorJson("请输入名字!", null);
        }
        if (StringUtils.isBlank(userAddress.getPhone())) {
            return JsonUtils.getErrorJson("请输入手机号!", null);
        }
        if (0 == userAddress.getRegionId()) {
            return JsonUtils.getErrorJson("请选择地区!", null);
        }
        if (StringUtils.isBlank(userAddress.getDetail())) {
            return JsonUtils.getErrorJson("请输入详细地址!", null);
        }

        return null;
    }



    /**
     * 
     * @Title: verifyOrder
     * @Description:验证order信息合法性
     * @param @param order
     * @param @return
     * @return String
     * @throws
     */
    private String verifyOrder(Order order) {
        if (null == order) {
            return JsonUtils.getErrorJson("订单不为空！", null);
        }
        // 收货地址
        String verifyUsersAddress = verifyUserAddress(order.getConsignee());
        if (!StringUtils.isBlank(verifyUsersAddress)) {
            return verifyUsersAddress;
        }
        List<DeliveryGoods> deliveryGoodList = order.getCarts();
        if (CollectionsUtils.isNull(deliveryGoodList)) {
            return JsonUtils.getErrorJson("订单不能没有商品!", null);
        }
        List<TransportMethod> transportMethodList = order.getTransportMethods();
        if (CollectionsUtils.isNull(transportMethodList)) {
            return JsonUtils.getErrorJson("订单不能没有商品!", null);
        }
        List<DeliveryGoods> carts = transportMethodList.get(0).getCarts();
        if (CollectionsUtils.isNull(carts)) {
            return JsonUtils.getErrorJson("订单不能没有商品!", null);
        }
        return null;
    }



    /**
     * 
     * @Title: addGoodsToOrder
     * @Description:下单:添加商品到order
     * @param @param order
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String addGoodsToOrder(HttpServletRequest request, long userId, String param) {
        LOG.info("Function:addGoodsToOrder.Start.");
        String addGoodsToOrderJson = JsonUtils.getErrorJson("提交订单失败，请重试!", null);
        Order order = DataUtil.parse(param, Order.class);
        addGoodsToOrderJson = verifyOrder(order);
        if (!StringUtils.isBlank(addGoodsToOrderJson)) {
            LOG.error("下单失败：addGoodsToOrderJson");
            return addGoodsToOrderJson;
        }
        order.setUserId(userId);
        order.getConsignee().setUserId(userId);
        
        // 订单来源 , 0：PC端，1：M站扫码，2：IPAD，3：安卓APP，4：IOS APP
        // 订单来源为 0：PC端，1：M站扫码时，由前端处理
        String plat = getPlatString(request);
        if (ContentUtils.PLAT_IPAD.equals(plat)) {
            order.setOrderSource(2);
        }
        else if (ContentUtils.PLAT_ANDROID.equals(plat)) {
            order.setOrderSource(3);
        }
        else if (ContentUtils.PLAT_IPHONE.equals(plat)) {
            order.setOrderSource(4);
        }
        else {
            addGoodsToOrderJson = JsonUtils.getErrorJson("不能识别订单来源" + plat, null);
            LOG.error("不能识别订单来源，plat：" + plat);
            return addGoodsToOrderJson;
        }
        
        order = orderService.addGoodsToOrder(order);
        if (null != order) {
            eTagService.putEtagMarking(request, "order/" + order.getOrderId(), Long.toString(System.currentTimeMillis()));
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("orderId", order.getOrderId());
            addGoodsToOrderJson = JsonUtils.getSuccessJson(data);
        } else {
            addGoodsToOrderJson = JsonUtils.getErrorJson("提交订单失败，请重试!", null);
        }
        
        if(order.getGetId()!=null){
            //生成红包折扣码
            String serviceName_esc_pay_log = "IPAD_HSV1_discount_new";
            DsManageReqInfo serviceReqInfo=new DsManageReqInfo();
            serviceReqInfo.setServiceName(serviceName_esc_pay_log);
            serviceReqInfo.setNeedAll("1");
            RuleServiceResponseData responseData = null;
            String data1 =dataAction.getData(serviceReqInfo,"");
            responseData = DataUtil.parse(data1, RuleServiceResponseData.class);
            if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
                List<Map<String, String>> list=responseData.getRows();
                Map<String, String> resultMap=list.get(0);
                String bonus_id=resultMap.get("bonus_id");
                Map<String,Object> paramTmp=new HashMap<String, Object>();
                paramTmp.put("user_id", userId);
                paramTmp.put("user_time", "$UnixTime");
                paramTmp.put("order_id", order.getOrderId());
                paramTmp.put("bonus_id", bonus_id);
                paramTmp.put("use_user_id", order.getGetId());
                paramTmp.put("cellPhone", order.getCellPhone());
                paramTmp.put("bonus_status", "1");//已使用
                paramTmp.put("sequence_type", "1");//折扣
                addBonusSequence(paramTmp);
            }
        }
        LOG.info("Function:addGoodsToOrder.End.");
        return addGoodsToOrderJson;
    }

    public void addBonusSequence(Map<String,Object> param){
        
        String sequence_sn = CodeUtils.getBonusCode();
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        String serviceName = "test_ecshop_ecs_bonus_sequence";
        Action action = new Action();
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        action.setServiceName(serviceName);
        set.put("user_id",param.get("user_id"));
        set.put("user_time",param.get("user_time"));
        set.put("order_id",param.get("order_id"));
        set.put("bonus_id",param.get("bonus_id"));
        set.put("sequence_sn", sequence_sn);
        set.put("create_time", "$UnixTime");
        set.put("get_time", "$UnixTime");
        set.put("get_id", param.get("use_user_id"));
        set.put("bonus_status", param.get("bonus_status"));//已领取
        set.put("sequence_type", param.get("sequence_type"));//折扣
        set.put("cell_phone", param.get("cellPhone"));
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        SetServiceResponseData actionResponse = null;
        String res1 = mushroomAction.offer(actionReqInfo);
        actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);
        if (!Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())) {
            LogUtil.info("addOrder addBonusSequence error param:"+param+",errorMsg:"+res1);
            addBonusSequence(param);
        }
    }

    /**
     * 
     * @Title: cancelOrder
     * @Description:取消订单
     * @param @param orderId
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String updateOrder(long userId, long orderId, String param) {
        LOG.info("Function:updateOrder.Start.");
        LOG.info("取消订单，order：id=" + orderId);
        String orderJson = null;
        Order order = DataUtil.parse(param, Order.class);
        if (null == order) {
            return JsonUtils.getErrorJson("请求参数错误!", null);
        }

        byte orderStatus = order.getOrderStatus();
        if (1 == orderStatus) {
            // 确认收货
            boolean result = orderService.confirmShipping(orderId, userId);
            if (result) {
                orderJson = JsonUtils.getSuccessJson(null);
            } else {
                orderJson = JsonUtils.getErrorJson("确认收货失败,请重试.", null);
            }
        } else if (2 == orderStatus) {
            // 取消订单
            boolean result = orderService.cancelOrder(orderId, userId);
            if (result) {
                orderJson = JsonUtils.getSuccessJson(null);
            } else {
                orderJson = JsonUtils.getErrorJson("订单取消失败，注意订单如果不存在、已经支付、或者配送中，不能取消订单.", null);
            }
        }
        LOG.info("Function:updateOrder.End.");
        return orderJson;

    }



    /**
     * 
     * @Title: deleteOrder
     * @Description:删除订单
     * @param @param orderId
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String deleteOrder(long userId, long orderId) {
        LOG.info("Function:deleteOrder.Start.");
        LOG.info("删除订单，order：id=" + orderId);
        String orderJson = null;
        boolean result = orderService.deleteOrder(orderId, userId);
        if (result) {
            orderJson = JsonUtils.getSuccessJson(null);
            IPushAction iPushAction =(IPushAction) MyApplicationContextUtil.getBean("pushAction");
            try {
                iPushAction.updateService("IPAD_BUV1_Myordercount");
                iPushAction.updateService("IPAD_HSV1_Myorder");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            orderJson = JsonUtils.getErrorJson("该订单不可删除!", null);
        }

        LOG.info("Function:deleteOrder.End.");
        return orderJson;
    }



    /**
     * 
     * @Title: getCheckoutInfo
     * @Description:获取订单支付信息
     * @param @param request
     * @param @param orderId
     * @param @return
     * @return String
     * @throws
     */
    public String getPayStatus(long userId, long orderId, String plat) {
        LOG.info("Function:getPayStatus.Start.");
        if (!(orderId > 0)) {
            return JsonUtils.getErrorJson("订单不存在!", null);
        }
        PayStatus payStatus = orderService.getPayStatus(userId, orderId, plat);
        // json
        String payStatusJson = JsonUtils.objectFormatToString(payStatus);
        LOG.info("Function:getPayStatus.End.");
        return payStatusJson;
    }



    /**
     * 
     * @Title: getpayWay
     * @Description:2.4.3.7 获取订单支付方式
     * @param @param request
     * @param @param orderId
     * @param @return
     * @return String
     * @throws
     */
    public String getpayWay(long userId, long orderId, String plat,String ip,String userAgent) {
        LOG.info("Function:getpayWay.Start.");
        if (!(orderId > 0)) {
            return JsonUtils.getErrorJson("订单不存在!", null);
        }

        String payMethod = orderService.getpayWay(orderId, userId, plat,ip,userAgent);
        // xml to json
        payMethod = JsonUtils.xmlStringToJson(payMethod);

        LOG.info("Function:getpayWay.End.");
        return payMethod;

    }

}
