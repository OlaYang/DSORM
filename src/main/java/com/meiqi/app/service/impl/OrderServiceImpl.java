package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CodeUtils;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.LejjBeanUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.common.utils.XmlUtils;
import com.meiqi.app.dao.CartDao;
import com.meiqi.app.dao.DeliveryGoodsDao;
import com.meiqi.app.dao.DeliveryOrderDao;
import com.meiqi.app.dao.GoodsDao;
import com.meiqi.app.dao.MyClientDao;
import com.meiqi.app.dao.OrderDao;
import com.meiqi.app.dao.OrderGoodsDao;
import com.meiqi.app.dao.PayLogDao;
import com.meiqi.app.dao.ProductsDao;
import com.meiqi.app.dao.RegionDao;
import com.meiqi.app.dao.UsersDao;
import com.meiqi.app.pojo.DeliveryGoods;
import com.meiqi.app.pojo.DeliveryOrder;
import com.meiqi.app.pojo.DiscountInfo;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.MyClient;
import com.meiqi.app.pojo.Order;
import com.meiqi.app.pojo.OrderDiscount;
import com.meiqi.app.pojo.OrderGoods;
import com.meiqi.app.pojo.PayLog;
import com.meiqi.app.pojo.PayStatus;
import com.meiqi.app.pojo.Products;
import com.meiqi.app.pojo.Region;
import com.meiqi.app.pojo.TransportMethod;
import com.meiqi.app.pojo.UserAddress;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.pojo.checkout.CodReqHeader;
import com.meiqi.app.pojo.checkout.CodTradeReqBody;
import com.meiqi.app.pojo.checkout.CodTradeReqDTO;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.app.service.OrderService;
import com.meiqi.app.service.utils.ImageService;
import com.meiqi.app.service.utils.PriceCalculateService;
import com.meiqi.app.service.utils.SMSService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.action.pay.wechat.WeChatPayService;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.service.impl.UserService;

/**
 * 
 * @ClassName: OrderServiceImpl
 * @Description:订单service
 * @author 杨永川
 * @date 2015年5月8日 下午6:16:05
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOG          = Logger.getLogger(OrderServiceImpl.class);

    private static final String ORDER_LIST   = "orderList";

    private static final String ORDER_DETAIL = "orderDetail";

    private static final String PAY_WAY      = "payWay";

    // private static final String PAY_STATUS = "payStatus";
    private static final String XML_PATH     = AppSysConfig.getValue(ContentUtils.XML_PATH);

    Class<Order>                cls          = Order.class;

    @Autowired
    private OrderDao            orderDao;

    @Autowired
    private OrderGoodsDao       orderGoodsDao;

    @Autowired
    private MyClientDao         myClientDao;

    @Autowired
    private RegionDao           regionDao;

    @Autowired
    private DeliveryGoodsDao    deliveryGoodsDao;

    @Autowired
    private DeliveryOrderDao    deliveryOrderDao;

    @Autowired
    private GoodsDao            goodsDao;

    @Autowired
    private CartDao             cartDao;

    @Autowired
    private ProductsDao         productsDao;

    @Autowired
    private PayLogDao           payLogDao;

    @Autowired
    private UsersDao            usersDao;

    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private IMushroomAction     mushroomAction;
    
    @Autowired
    private WeChatPayService    weChatPayService;



    /**
     * 
     * @Title: addGoodsToOrder
     * @Description:提交订单
     * @param @param order
     * @param @return
     * @throws
     */
    @Override
    public Order addGoodsToOrder(Order order) {
        LOG.info("Function:addGoodsToOrder.Start.");
        long userId = order.getUserId();
        String discountCode = order.getCode();
//        String discountCode = order.getDiscountCode();  ipad传入折扣吗为code 杨永川使用discountcode 导致一直无法取得折扣吗
        int currentTime = DateUtils.getSecond();
        // 该订单商品个数
        int goodsNumber = 0;
        // 收货地址
        UserAddress consignee = order.getConsignee();
        consignee.setUserId(userId);
        assembleUsesAddress(consignee);
        // consignee id ==0 保存这个新客户地址
        if (0 == consignee.getConsigneeId()) {
            MyClient myClient = new MyClient(0, consignee, userId, currentTime);
            myClientDao.addObejct(myClient);
        }
        // 设置 userAddress
        LejjBeanUtils.copyProperties(order, consignee);
        // set 四川 成都 高新区
        String regionDetail = regionDao.getLinkedRegionByRegion(order.getConsignee().getRegionId());
        order.setDetail(regionDetail + ContentUtils.TWO_BLANK + order.getDetail());
        // 支付方式
        order.setPayId(order.getPaymentMethodType());
        // 订单生成时间
        order.setAddTime(currentTime);

        // TODO 订单号
        String orderSn = "sn" + DateUtils.getTime() + CodeUtils.getCode();
        order.setOrderSn(orderSn);

        // 设置订单价格（总额 运费 折扣）
        setOrderAmount(order, userId);

        LOG.info("orderDao add:" + JSONObject.toJSON(order));
        long orderId = orderDao.addObejct(order);

        if (0 == orderId) {
            LOG.error("提交订单失败!orderDao.addObejct(order) 返回orderid=0.");
            return null;
        }
        order.setOrderId(orderId);
        // 运输方式 发货清单商品
        List<TransportMethod> transportMethodList = order.getTransportMethods();
        if (CollectionsUtils.isNull(transportMethodList)) {
            LOG.error("提交订单失败.订单配送方式异常!");
            // 数据异常（没有配送方式） 手动删除刚提交订单
            orderDao.deleteObejct(order);
            return null;
        }
        List<Long> cartIdList = new ArrayList<Long>();
        for (TransportMethod transportMethod : transportMethodList) {
            List<DeliveryGoods> deliveryGoodList = transportMethod.getCarts();
            if (CollectionsUtils.isNull(deliveryGoodList)) {
                continue;
            }
            for (DeliveryGoods deliveryGoods : deliveryGoodList) {
                Goods goods = deliveryGoods.getGoods();
                if (null == goods) {
                    continue;
                }
                LejjBeanUtils.copyProperties(deliveryGoods, deliveryGoods.getGoods());
                
                long goodsId = deliveryGoods.getGoodsId();
                goods = (Goods) goodsDao.getObjectById(Goods.class, goodsId);
                
                // 防止hibernate 自动保存goods
                Goods goodTmp = new Goods();
                LejjBeanUtils.copyProperties(goodTmp, goods);
                
                // 价格计算
                PriceCalculateService.priceCalculate(goodTmp);
                Products product = productsDao.getProducts(Products.class, goodsId);
                String goodsAttrValue = "";
                String goodsAttrId = "";
                long productId = 0;
                if (null != product) {
                    productId = product.getProductId();
                    goodsAttrValue = product.getGoodsAttrValue();
                    goodsAttrId = product.getGoodsAttr();
                }

                OrderGoods orderGoods = new OrderGoods(0, orderId, goodsId, goods.getName(), goods.getGoodsSn(),
                        productId, deliveryGoods.getGoodsAmount(), goodTmp.getOriginalPrice(), goodTmp.getPrice(),
                        goodsAttrValue, (byte) 0, (byte) 0, "", 0, 0, goodsAttrId);
                // 设置配送方式名称
                orderGoods.setShippingName(transportMethod.getShippingName());
                //System.out.println("orderGoodsDao add:" + JSONObject.toJSON(orderGoods));
                orderGoodsDao.addObejct(orderGoods);
                long cartId = deliveryGoods.getCartId();
                if (0 != cartId) {
                    cartIdList.add(cartId);
                }
                // 订单商品数量加1
                goodsNumber++;
            }
        }
        // 数据异常（订单没有商品） 手动删除刚提交订单
        if (0 == goodsNumber) {
            LOG.error("提交订单失败.数据异常（订单没有商品）!");
            orderDao.deleteObejct(order);
            return null;
        }
        // 移除购物车
        removeCarts(cartIdList, userId);

        // 更新折扣码记录
        // discountCode!=null
        if (!StringUtils.isBlank(discountCode)) {
            updateDiscountCode(discountCode, orderId);
        }
        // 发送下单信息短息
        sendPlaceOrderSms(userId, orderSn, goodsNumber, consignee);
        LOG.info("Function:addGoodsToOrder.End.");
        return order;
    }



    /**
     * 
     * 发送下单信息短息
     *
     * @param userId
     * @param orderSn
     * @param goodsNumber
     * @param consignee
     */
    private void sendPlaceOrderSms(long userId, String orderSn, int goodsNumber, UserAddress consignee) {
        LOG.info("Function:sendPlaceOrderSms.Start.");
        Users user = usersDao.getObjectById(userId);
        String phone = "";
        if (null != user && !StringUtils.isBlank(user.getPhone())) {
            phone = user.getPhone();
        } else {
            phone = consignee.getPhone();
        }
        // 下单后 发送下单成功短信
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("order_sn", orderSn);
        param.put("goods_number", goodsNumber);
        SMSService.sendPlaceOrder(phone, param);
        LOG.info("用户下单成功，短信已发送，userId=" + userId + ",订单sn=" + orderSn);
        LOG.info("Function:sendPlaceOrderSms.End.");
    }



    /**
     * 
     * 设置订单价格
     *
     * @param order
     */
    private void setOrderAmount(Order order, long userId) {
        // 获取运费价格
        double transportPrice = getTransportPrice(order, userId);
        // 设置订单运费
        order.setTransportPrice(transportPrice);
        double discountPrice = 0;
        // 设置订单折扣价格
        if (!StringUtils.isBlank(order.getCode())) {
            discountPrice = order.getDiscountPrice();
            order.setPreferent(discountPrice);
        }
        // 商品总价格+运费-折扣=订单总额
        double amount = order.getGoodsPrice() + transportPrice - discountPrice;
        order.setAmount(amount);
    }



    /**
     * 
     * 获取该订单的运费
     *
     * @param order
     * @param userId
     * @return
     */
    private double getTransportPrice(Order order, long userId) {
        double transportPrice = order.getTransportPrice();
        // 运输方式
        List<TransportMethod> transportMethodList = order.getTransportMethods();
        if (CollectionsUtils.isNull(transportMethodList)) {
            return transportPrice;
        }
        // 购物车主键rec_id和运费详细模板对应的关系数组
        Map<String, Object> recDetailIdMap = new HashMap<String, Object>();
        // 所有商品的运费详细模板id集合
        StringBuffer detailId = new StringBuffer();
        for (int i = 0; i < transportMethodList.size(); i++) {
            TransportMethod transportMethod = transportMethodList.get(i);
            int datailId = transportMethod.getDetailId();
            if (0 == datailId) {
                break;
            }
            detailId.append(datailId);
            if (i != transportMethodList.size() - 1) {
                detailId.append(ContentUtils.COMMA);
            }
            List<DeliveryGoods> deliveryGoodList = transportMethod.getCarts();
            if (CollectionsUtils.isNull(deliveryGoodList)) {
                continue;
            }
            for (DeliveryGoods deliveryGoods : deliveryGoodList) {
                recDetailIdMap.put("" + deliveryGoods.getCartId(), "" + datailId);
            }
        }

        if (CollectionsUtils.isNull(recDetailIdMap) || StringUtils.isBlank(detailId.toString())) {
            return transportPrice;
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", userId);
        param.put("rec_detail_id", recDetailIdMap);
        param.put("detail_id", detailId.toString());

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("HMJ_HSV1_ShopCart_Order");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setFormat("json");
        System.out.println(JSONObject.toJSON(dsReqInfo));
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return transportPrice;
        }
        // 传出：fee_amout（总运费） goods_total_money（商品总金额） privilege（共优惠）
        // order_total_money（应付金额）
        Map<String, String> map = responseBaseData.getRows().get(0);
        if (CollectionsUtils.isNull(map)) {
            return transportPrice;
        }
        // 获取运费
        transportPrice = StringUtils.StringToDouble(map.get("fee_amout"));
        return transportPrice;
    }



    /**
     * 
     * @Title: updateDiscountCode
     * @Description: 更新折扣码记录
     * @param @param discountCode
     * @param @param orderId
     * @return void
     * @throws
     */
    private void updateDiscountCode(String discountCode, long orderId) {
        String serviceName = "lejj_discount_info_app";

        // 调用mushroom 更新折扣码记录
        DsManageReqInfo reqInfo = new DsManageReqInfo();
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("relate_order", orderId);
        // 已使用
        set.put("status", 1);

        Action action = new Action();
        action.setType("U");
        action.setServiceName(serviceName);
        action.setSet(set);

        Where where = new Where();
        where.setPrepend("and");
        List<SqlCondition> cons = new ArrayList<SqlCondition>();
        SqlCondition con = new SqlCondition();
        con.setKey("discount_code");
        con.setOp("=");
        con.setValue(discountCode);
        cons.add(con);
        where.setConditions(cons);
        action.setWhere(where);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("transaction", 1);
        param.put("actions", actions);
        reqInfo.setServiceName("MUSH_Offer");
        reqInfo.setParam(param);
        // set Data
        String reslut = mushroomAction.offer(reqInfo);
        LOG.info("修改折扣码状态为已使用，结果：" + reslut);
    }



    /**
     * 
     * @Title: removeCarts
     * @Description:移除购物车
     * @param @param cartIdList
     * @return void
     * @throws
     */
    public void removeCarts(List<Long> cartIdList, long userId) {
        LOG.info("Function:removeCarts.Start.");
        if (!CollectionsUtils.isNull(cartIdList)) {
            LOG.info("移除user id=" + userId + "的购物车");
            LOG.info("移除购物车 第一个cartId=" + cartIdList.get(0));
            cartDao.removeCarts(cartIdList, userId);
        }
        LOG.info("Function:removeCarts.End.");

    }



    /**
     * 
     * @Title: assembleUsesAddress
     * @Description:拼装 usersaddress
     * @param @param userAddress
     * @return void
     * @throws
     */
    private void assembleUsesAddress(UserAddress userAddress) {
        long regionId = userAddress.getRegionId();
        Region region = regionDao.getRegionByRegionId(regionId);

        if (null != region) {
            userAddress.setRegion(region);
            // 获取父region
            region = regionDao.getLinkedRegionByRegion(region);
            userAddress.assembleUsesAddress(userAddress, region);
        }
    }



    /**
     * 
     * @Title: getAllOrder
     * @Description:获取用户订单
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public String getAllOrder(long userId, String plat, int pageIndex, int pageSize) {
        LOG.info("Function:getAllOrder.Start.");
        String result = "";
        DsManageReqInfo dsManageReqInfo=new DsManageReqInfo();
        dsManageReqInfo.setServiceName("IPAD_BUV1_Myordercount");
        dsManageReqInfo.setNeedAll("1");
    	Map<String,Object> param=new HashMap<String, Object>();
        param.put("user_id", userId);
        dsManageReqInfo.setParam(param);
        RuleServiceResponseData responseData = null;
        responseData = DataUtil.parse(dataAction.getData(dsManageReqInfo,""), RuleServiceResponseData.class); 
        List<Map<String, String>> rows=responseData.getRows();
        int orderTotal=0;
        LOG.info("Function:getAllOrder.getData."+rows.toString());
        if(1==rows.size()){
        	orderTotal=Integer.parseInt(rows.get(0).get("total"));
            LOG.info("Function:getAllOrder.orderTotal."+orderTotal);
        }
        if (orderTotal > 0) {
        	dsManageReqInfo.setServiceName("IPAD_HSV1_Myorder");
        	param.put("limitStart", pageIndex * pageSize);
        	param.put("limitEnd", pageSize > 0 ? pageSize : 10);
        	String resultData = dataAction.getData(dsManageReqInfo,"");
        	LOG.info("Function:getAllOrder.resultData."+resultData);
    		responseData = DataUtil.parse(resultData, RuleServiceResponseData.class); 
    		rows=responseData.getRows();
    		
    		List<Order> allOrderList=dataToObject(rows);
    		if (null == allOrderList || orderTotal != allOrderList.size()) {
    		    LOG.error("Function:getAllOrder:orderTotal and orderList sizenot equal!");
    		}
//            List<Order> alOrderList = orderDao.getAllOrderByUserId(userId, pageIndex * pageSize,pageSize > 0 ? pageSize : 10);
    		LOG.info("Function:getAllOrder.allOrderList.");
            result = assembleOrderListBySchema(allOrderList, plat, orderTotal);
        }
        LOG.info("Function:getAllOrder.Start.");
        return result;
    }

    /**
     * 规则数据转对象
     * @return
     */
    private List<Order> dataToObject(List<Map<String,String>> rows){
    	int rowsSize=rows.size();
    	if(0==rowsSize){
    		return null;
    	}
    	List<Order> orders=new ArrayList<Order>();
    	Map<String,String> map=null;
    	for(int i=0;i<rowsSize;i++){
    		Order order=new Order();
    		map=rows.get(i);
    		order.setOrderSn(map.get("order_sn"));
    		order.setName(map.get("consignee"));
    		order.setPhone(map.get("mobile"));
    		order.setOrderId(Long.parseLong(map.get("order_id")));
    		if (StringUtils.isNotEmpty(map.get("order_amount"))) {
    		    order.setAmount(Double.parseDouble(map.get("order_amount")));
    		}
    		order.setPayStatus(Byte.parseByte(map.get("pay_status")));
    		order.setOrderStatus(Byte.parseByte(map.get("order_status")));
    		order.setShippingStatus(Byte.parseByte(map.get("shipping_status")));
    		order.setAddDate(map.get("order_time"));
    		order.setAddTime(Integer.parseInt(map.get("add_time")));
    		orders.add(order);
    		LOG.info("Function:getAllOrder.dataToObject."+map.toString());
    	}
		return orders;
    }


    /**
     * 
     * @Title: assembleOrderList
     * @Description:装配 数据
     * @param @param allOrderList
     * @param @return
     * @return OrderDetail
     * @throws
     */
    private String assembleOrderListBySchema(List<Order> allOrderList, String plat, int orderTotal) {
        if (CollectionsUtils.isNull(allOrderList)) {
            return null;
        }
        // 设置收货人
        setConsigneeForOrderList(allOrderList);
        Document document = XmlUtils.createDocument();

        for (Order order : allOrderList) {
            long orderId = order.getOrderId();
            byte orderStatus = order.getOrderStatus();
            
            // #错误 8278, 订单接口整改前特殊处理：旧的订单查询接口中订单状态=0 未确认 时当做订单状态=1 已确认处理
            orderStatus = orderStatus == 0 ? 1 : orderStatus;
            
            byte payStatus = order.getPayStatus();
            byte shippingStatus = order.getShippingStatus();
            long addTime = order.getAddTime();
            // 获取订单商品
            List<OrderGoods> orderGoodsList = getGoodsByOrderId(orderId);
            if (CollectionsUtils.isNull(orderGoodsList)) {
                LOG.error("订单非法,没有商品,order:id = " + orderId);
                continue;
            }
            // 下单时间 string
            String addDate = DateUtils.formatDateToString(new Date(addTime * 1000));
            order.setAddDate(addDate);

            List<DeliveryGoods> deliveryGoods = orderGoodsListToDeliveryGoodsList(orderGoodsList);
            order.setCarts(deliveryGoods);
            // 获取订单配送列表
            List<DeliveryOrder> deliverys = deliveryOrderDao.getDeliveryOrderByOrderId(orderId);
            if (!CollectionsUtils.isNull(deliverys)) {
                order.setDeliverys(deliverys);
                // 计算没有发货的商品 与状态
                setNodelivery(order, orderGoodsList);
            }
            
            // 商品数量
            int goodsSize = 0;
            for (int i=0; i<deliveryGoods.size(); i++) {
                goodsSize += deliveryGoods.get(i).getGoodsAmount();
            }
            order.setGoodsSize(goodsSize);
            LOG.info("goodsSize = " + goodsSize);

            String xmlName = getXmlNameByOrderStatus(ORDER_LIST, orderStatus, payStatus, shippingStatus,
                    order.getAllDeliveryStatus(), orderGoodsList.size(), plat);
            Element sectionsEle = XmlUtils.getSectionsEle(xmlName);
            sectionsEle = XmlUtils.assembleElement(sectionsEle, order, ORDER_LIST);
            if (null != sectionsEle) {
                // document.getRootElement().add(sectionsEle);
                document.getRootElement().appendContent(sectionsEle);
            }
        }

        // add ToolBar
        String xmlName = getToolBarXmlName(ORDER_LIST, plat);
        Element sectionsEle = XmlUtils.getSectionsEle(xmlName);
        sectionsEle = XmlUtils.assembleElement(sectionsEle, orderTotal, ORDER_LIST);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }

        return document.asXML();
    }



    /**
     * 
     * @Title: orderGoodsToDeliveryGoods
     * @Description:
     * @param @param orderGoodsList
     * @param @return
     * @return List<DeliveryGoods>
     * @throws
     */
    private List<DeliveryGoods> orderGoodsListToDeliveryGoodsList(List<OrderGoods> orderGoodsList) {
        List<DeliveryGoods> deliveryGoodsList = new ArrayList<DeliveryGoods>();
        for (OrderGoods orderGoods : orderGoodsList) {
            DeliveryGoods deliveryGoods = orderGoodsToDeliveryGoods(orderGoods);
            deliveryGoodsList.add(deliveryGoods);
        }
        return deliveryGoodsList;

    }



    /**
     * 
     * @Title: orderGoodsToDeliveryGoods
     * @Description:
     * @param @param orderGoods
     * @param @return
     * @return DeliveryGoods
     * @throws
     */
    private DeliveryGoods orderGoodsToDeliveryGoods(OrderGoods orderGoods) {
        if (null == orderGoods) {
            return null;
        }
        long goodsId = orderGoods.getGoodsId();
        DeliveryGoods deliveryGoods = new DeliveryGoods();
        Goods goods = new Goods();
        goods.setGoodsId(goodsId);
        // 商品名
        goods.setName(orderGoods.getName());

        String goodsAttr = orderGoods.getGoodsAttr();
        if (!StringUtils.isBlank(goodsAttr)) {
            goodsAttr = goodsAttr.replaceAll(ContentUtils.LINE_FEED, ContentUtils.TWO_BLANK);
            goods.setStandardName(goodsAttr);
        }

        goods.setCover(goodsDao.getCover(goodsId));
        // 设置封面url前缀
        ImageService.setGoodsCover(goods);
        goods.setPrice(orderGoods.getPrice());

        deliveryGoods.setGoods(goods);
        deliveryGoods.setGoodsAmount(orderGoods.getGoodsNumber());
        return deliveryGoods;

    }



    /**
     * 
     * @Title: setConsigneeForOrder
     * @Description:设置收货地址 为order
     * @param @param orderList
     * @return void
     * @throws
     */
    private void setConsigneeForOrderList(List<Order> orderList) {
        if (CollectionsUtils.isNull(orderList)) {
            return;
        }
        for (Order order : orderList) {
            setConsigneeForOrder(order);
        }
    }



    /**
     * 
     * @Title: setConsigneeForOrder
     * @Description:
     * @param @param order
     * @return void
     * @throws
     */
    private void setConsigneeForOrder(Order order) {
        if (null == order) {
            return;
        }
        UserAddress consignee = new UserAddress();
        consignee.setName(order.getName());
        consignee.setPhone(order.getPhone());
        consignee.setDetail(order.getDetail());
        order.setConsignee(consignee);
    }



    /**
     * 
     * @Title: getTransactionStatus
     * @Description:判断交易状态
     * @param @param orderStatus
     * @param @param payStatus
     * @param @param shippingStatus
     * @param @return
     * @return String
     * @throws
     */
    private String getXmlNameByOrderStatus(String xmlType, byte orderStatus, byte payStatus, byte shippingStatus,
            int allDeliveryStatus, int goodsSize, String plat) {
    	LogUtil.info("order xml params: xmlType:"+xmlType+" orderStatus:"+orderStatus+" payStatus:"+payStatus+" shippingStatus:"+shippingStatus+" allDeliveryStatus:"+allDeliveryStatus+" goodsSize:"+goodsSize+" plat:"+plat);
        String transactionStatus = "";
        StringBuffer xmlName = new StringBuffer();
        xmlName.append(XML_PATH).append(xmlType).append("/").append(xmlType + "_");
        // app App订单状态 等待支付
        if (0 == orderStatus && 0 == shippingStatus && 0 == payStatus) {
            transactionStatus = "waitPay_";
        } else if (1 == orderStatus && 0 == shippingStatus && 0 == payStatus) {
            transactionStatus = "waitPay_";
        } else if (1 == orderStatus && 0 == shippingStatus && 2 == payStatus) {
            // app App订单状态 等待发货
            transactionStatus = "waitShipping_";
        } else if (1 == orderStatus && 3 == shippingStatus && 2 == payStatus) {
            transactionStatus = "waitShipping_";
        } else if (6 == orderStatus && 5 == shippingStatus && 2 == payStatus) {
            transactionStatus = "waitShipping_";
        } else if (4 == shippingStatus && 2 == payStatus) {
            // app App订单状态 商品都已配送（可能配送未完成） 等待收货
            transactionStatus = "shipping_";
        } else if (4 == shippingStatus && 2 == payStatus && 3 == allDeliveryStatus) {
            // app App订单状态 商品都已配送 并所有配送完成 确认收货
            transactionStatus = "shippingSucceed_";
        } else if (7 == orderStatus && 2 == shippingStatus && 2 == payStatus) {
            // app App订单状态 交易成功
            transactionStatus = "dealSucceed_";
        }else if (5 == orderStatus && 2 == shippingStatus && 2 == payStatus) {
            // app App订单状态 交易成功
            transactionStatus = "dealSucceed_";
        } else if (4 == orderStatus && 0 == shippingStatus && 0 == payStatus) {
            // app App订单状态 交易退货
            transactionStatus = "returnGoods_";
        } else if (3 == orderStatus || 2 == orderStatus) {
            // app App订单状态 交易
            transactionStatus = "dealClosed_";
        } else {
            // 状态不明的当做关闭处理
            transactionStatus = "dealClosed_";
        }

        xmlName.append(transactionStatus);
        if (xmlType.equals(ORDER_LIST)) {
            if (goodsSize > 1) {
                xmlName.append("multGoods_");
            } else {
                xmlName.append("singleGoods_");
            }
        }
        xmlName.append(plat).append(".xml");
        LogUtil.info("order xml xmlName:"+xmlName.toString());
        return xmlName.toString();
    }



    /**
     * 
     * @Title: getToolBarXmlName
     * @Description:判断交易状态
     * @param @return
     * @return String
     * @throws
     */
    private String getToolBarXmlName(String xmlType, String plat) {
        StringBuffer xmlName = new StringBuffer();
        xmlName.append(XML_PATH).append(xmlType).append("/").append(xmlType + "_");
        xmlName.append("toolBar" + "_");
        xmlName.append(plat).append(".xml");
        return xmlName.toString();
    }



    /**
     * 
     * @Title: getGoodsByOrderId
     * @Description:获取订单商品
     * @param @param orderId
     * @param @return
     * @return List<Goods>
     * @throws
     */
    private List<OrderGoods> getGoodsByOrderId(long orderId) {
        if (0 == orderId) {
            return null;
        }
        List<OrderGoods> orderGoodsList = orderGoodsDao.getGoodsByOrderId(orderId);
        return orderGoodsList;
    }



    /**
     * 
     * @Title: assembleOrderDetailBySchema
     * @Description:拼装order detail xml
     * @param @param order
     * @param @param plat
     * @param @return
     * @return String
     * @throws
     */
    private String assembleOrderDetailBySchema(Order order, String plat) {
        // 订单状态
        long orderId = order.getOrderId();
        byte orderStatus = order.getOrderStatus();
        byte payStatus = order.getPayStatus();
        byte shippingStatus = order.getShippingStatus();

        // 转换
        List<OrderGoods> orderGoodsList = orderGoodsDao.getGoodsByOrderId(orderId);
        if (CollectionsUtils.isNull(orderGoodsList)) {
            return null;
        }

        order.setCarts(orderGoodsListToDeliveryGoodsList(orderGoodsList));
        // 设置收货地址
        setConsigneeForOrder(order);

        // 获取订单配送列表
        List<DeliveryOrder> deliverys = deliveryOrderDao.getDeliveryOrderByOrderId(orderId);
        if (!CollectionsUtils.isNull(deliverys)) {
            order.setDeliverys(deliverys);
            // 计算没有发货的商品 与状态
            setNodelivery(order, orderGoodsList);
        }

        String xmlName = getXmlNameByOrderStatus(ORDER_DETAIL, orderStatus, payStatus, shippingStatus,
                order.getAllDeliveryStatus(), orderGoodsList.size(), plat);
        // 获取root docment
        Document document = XmlUtils.createDocument();

        Element sectionsEle = XmlUtils.getSectionsEle(xmlName);
        
        // 错误 #6947 配置方式不支持显示deliverySize，特殊处理
        if (order.getAllDeliveryStatus() == 2) {
            updateDeliverySize(sectionsEle, order.getDeliverySize());
        }
        
        // 设置值
        sectionsEle = XmlUtils.assembleElement(sectionsEle, order, ORDER_DETAIL);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }
        return document.asXML();
    }
    
    private static void updateDeliverySize(Element element, int deliverySize) {
        if (null == element) {
            return;
        }
        
        List<Element> tipsList = element.selectNodes("//lblTips");
        for (Element tips : tipsList) {
            String text = tips.getText();
            text = text != null ? text.replace("OrderItem.deliverySize", "" + deliverySize) : "";
            tips.setText(text);
        }
    }



    /**
     * 
     * @Title: setNodelivery
     * @Description:计算没有发货的商品 商品配送的总体状态
     * @param @param order
     * @param @param orderGoodsList
     * @param @param deliverys
     * @return void
     * @throws
     */
    private void setNodelivery(Order order, List<OrderGoods> orderGoodsList) {
        // 已配送 商品
        List<DeliveryOrder> deliverys = order.getDeliverys();
        int allDeliveryStatus = 0;
        int shippingStatus = 1;
        for (DeliveryOrder deliveryOrder : deliverys) {
            if (0 == deliveryOrder.getStatus()) {
                shippingStatus = 0;
            }
            // 该配送单 配送的商品集合
            List<DeliveryGoods> carts = deliveryOrder.getCarts();
            if (CollectionsUtils.isNull(carts)) {
                continue;
            }
            for (DeliveryGoods deliveryGoods : carts) {
                Goods goods = deliveryGoods.getGoods();
                if (null != goods) {
                    // 设置商品图片前缀
                    ImageService.setGoodsCover(goods);
                    // 设置商品规格
                    
                    goods.setStandardName(null==deliveryGoods.getGoodsAttr()?ContentUtils.TWO_BLANK:deliveryGoods.getGoodsAttr().replaceAll("\r\n", ContentUtils.TWO_BLANK));
                    OrderGoods orderGoods = new OrderGoods();
                    orderGoods.setGoodsId(goods.getGoodsId());
                    // remove 已经发货的商品
                    if (orderGoodsList.contains(orderGoods)) {
                        orderGoodsList.remove(orderGoods);
                    }
                }
            }
        }
        // 还有 没发货的商品
        if (orderGoodsList.size() > 0) {
            allDeliveryStatus = 1;
            DeliveryOrder deliveryOrder = new DeliveryOrder();
            deliveryOrder.setCarts(orderGoodsListToDeliveryGoodsList(orderGoodsList));
            // 未发货状态
            deliveryOrder.setStatus((byte) 3);
            deliverys.add(deliveryOrder);
        } else {
            // 所有商品都已经配送
            if (1 == deliverys.size() && 0 == shippingStatus) {
                // 只有一个配送单 配送中
                allDeliveryStatus = 0;
            } else if (1 < deliverys.size() && 0 == shippingStatus) {
                // 所有配送单都发货 有配送单 还在配送中
                allDeliveryStatus = 2;
            } else if (1 == shippingStatus) {
                // 所有配送单都发货 并都配送完成
                allDeliveryStatus = 3;
            }
        }
        order.setDeliverySize(deliverys.size());
        order.setAllDeliveryStatus(allDeliveryStatus);
    }



    /**
     * 
     * @Title: getOrderDetail
     * @Description:获取某个订单详情
     * @param @param userId
     * @param @param orderId
     * @param @return
     * @throws
     */
    @Override
    public String getOrderDetail(long userId, long orderId, String plat) {
        LOG.info("Function:getOrderDetail.Start.");
        String orderDetail = null;

        Order order = (Order) orderDao.getObjectById(cls, orderId);
        if (null == order) {
            return orderDetail;
        }
        
        // 设置订单配送方式组合字符串
        order.setShippingStr(getShippingStr(orderId));
        
        //order.setDiscountPrice(order.getGoodsPrice()+order.getTransportPrice()-order.getAmount());
        OrderDiscount orderDiscount = getOrderDiscount(orderId);
        if (null == orderDiscount) {
            orderDiscount = new OrderDiscount();
        }

        orderDiscount.setOrderId(orderId);
        order.setOrderDiscount(orderDiscount);
        //LOG.info("getOrderDetail: order:" + JSONObject.toJSON(order));
        
        orderDetail = assembleOrderDetailBySchema(order, plat);

        LOG.info("Function:getOrderDetail.End.");
        return orderDetail;
    }



    /**
     * 
     * 获取订单折扣
     *
     * @param orderId
     * @return
     */
    private OrderDiscount getOrderDiscount(long orderId) {
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("YJG_BUV1_Order_discount");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("order_id", orderId);
        dsReqInfo.setParam(param);

        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (null != responseBaseData && !CollectionsUtils.isNull(responseBaseData.getRows())) {
            Map<String, String> dataMap = responseBaseData.getRows().get(0);

            OrderDiscount orderDiscount = new OrderDiscount();
            String discount = dataMap.get("discount");
            if (!StringUtils.isEmpty(discount)) {
                orderDiscount.setDiscount(Double.parseDouble(discount));
            }
            String orderDiscountStr = dataMap.get("order_discount");
            if (!StringUtils.isEmpty(orderDiscountStr)) {
                orderDiscount.setOrderDiscount(Double.parseDouble(orderDiscountStr));
            }
            String bonusDiscount = dataMap.get("bonus_discount");
            if (!StringUtils.isEmpty(bonusDiscount)) {
                orderDiscount.setBonusDiscount(Double.parseDouble(bonusDiscount));
            }

            return orderDiscount;
        }
        return null;
    }



    private String getShippingStr(long orderId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orderid", orderId);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("IPAD_HSV1_shipping");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (null != responseBaseData && !CollectionsUtils.isNull(responseBaseData.getRows())) {
            Map<String, String> shippingStrMap = responseBaseData.getRows().get(0);
            return shippingStrMap.get("shippping_name");
            
        }
        return null;
    }



    /**
     * 
     * @Title: updatePayInfo
     * @Description:根据易宝的交易信息 更新订单的支付信息
     * @param @param codTradeReqDTO
     * @param @return
     * @throws
     */
    @Override
    public boolean updatePayInfoByYeePay(CodTradeReqDTO codTradeReqDTO) {
        CodReqHeader reqHeader = codTradeReqDTO.getReqHeader();
        CodTradeReqBody reqBody = codTradeReqDTO.getReqBody();
        String transactionID = reqHeader.getTransactionID();
        String orderSn = reqBody.getOrderNo();
        // 本次支付的金额
        double thisPayedOrderAmount = StringUtils.StringToDouble(reqBody.getAmount());
        Order order = orderDao.getOrderByOrderSn(cls, orderSn);
        if (null != order) {
            long orderId = order.getOrderId();
            double orderAmount = order.getAmount();
            // 订单已经支付的金额
            double moneyPaid = order.getMoneyPaid();

            if (0 != thisPayedOrderAmount) {
                PayLog payLog = new PayLog(0, orderId, thisPayedOrderAmount, (byte) 0, (byte) 0, DateUtils.getSecond(),
                        transactionID, 1, "易宝支付");
                // 记录本次支付
                payLogDao.addObejct(payLog);
                if (orderAmount <= (thisPayedOrderAmount + moneyPaid)) {
                    order.setPayStatus((byte) 2);
                }
                order.setMoneyPaid(thisPayedOrderAmount + moneyPaid);
                orderDao.updateObejct(order);
                return true;
            }
        }
        return false;
    }



    /**
     * 
     * @Title: getPayMenthod
     * @Description:获取订单的支付方式
     * @param @param orderId
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public String getpayWay(long orderId, long userId, String plat,String ip,String userAgent) {
        LOG.info("Function:getpayWay.Start.");
        Order order = orderDao.getOrderByUserIdAndOrderId(orderId);
        if (null == order) {
            return null;
        }
        // 支付宝回调URL
        order.setAlipayNotifyURL(AppSysConfig.getValue("alipay_notifyURL"));
        // 快钱回调URL
        order.setBillpayNotifyURL(AppSysConfig.getValue("billpay_notifyURL"));
        //微信回调URL
        order.setWechatNotifyURL(AppSysConfig.getValue("wechat_notifyURL"));

        List<OrderGoods> orderGoodsList = orderGoodsDao.getGoodsByOrderId(orderId);
        if (CollectionsUtils.isNull(orderGoodsList)) {
            LOG.error("该订单没有商品，数据异常,order id=" + orderId);
            return null;
        }
        // 订单商品个数
        order.setGoodsAmount(orderGoodsList.size());
        order.setCarts(orderGoodsListToDeliveryGoodsList(orderGoodsList));
        order.setWechatPayJson(weChatPayService.yjgAppToPay(order.getOrderSn(),ip,userAgent,null,null,null));
        StringBuffer xmlNameStringBuffer = new StringBuffer();
        xmlNameStringBuffer.append(AppSysConfig.getValue(ContentUtils.XML_PATH)).append("payWay/payWay_").append(plat)
                .append(".xml");

        // 获取root docment
        Document document = XmlUtils.createDocument();
        Element sectionsEle = XmlUtils.getSectionsEle(xmlNameStringBuffer.toString());
        // 设置值
        // XmlUtils.assembleElement(sectionsEle, order);
        sectionsEle = XmlUtils.assembleElement(sectionsEle, order, PAY_WAY);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }
        LOG.info("Function:getpayWay.End.");
        return document.asXML();
    }



    /**
     * 
     * @Title: getCheckoutInfo
     * @Description:获取订单支付信息
     * @param @param orderId
     * @param @param platString
     * @param @return
     * @throws
     */
    @Override
    public PayStatus getPayStatus(long userId, long orderId, String plat) {
        LOG.info("Function:getPayStatus.Start.");
        Order order = orderDao.getOrderByUserIdAndOrderId(userId, orderId);
        if (null == order) {
            LOG.error("该订单不存在,order:id=" + orderId);
            return null;
        }
        // 支付状态
        int status = 1;
        String payDesc = "";
        if (order.getPayStatus() == 0) {
            status = 0;
            payDesc = "未支付";
        } else if (order.getPayStatus() == 2) {
            status = 2;
            payDesc = "支付成功";
        } else if (order.getPayStatus() == 5) {
            status = 5;
            payDesc = "部分付款";
        }
        // bug:2199
        PayStatus payStatus = new PayStatus(status, payDesc, ContentUtils.MONEY_SIGN
                + StringUtils.savePriceTwoDecimal(order.getAmount()), orderId);
        LOG.info("Function:getPayStatus.End.");
        return payStatus;
    }



    /**
     * 
     * @Title: cancelOrder
     * @Description:取消订单
     * @param @param orderId
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public boolean cancelOrder(long orderId, long userId) {
        LOG.info("Function:cancelOrder.Start.");
        boolean result = false;
        Order order = orderDao.getOrderByUserIdAndOrderId(orderId);
        if (null == order) {
            StringBuffer error = new StringBuffer();
            error.append("订单order:id=").append(orderId).append("不存在!");
            LOG.error(error.toString());
            return result;
        }
        byte orderStatus = order.getOrderStatus();
        byte payStatus = order.getPayStatus();
        byte shippingStatus = order.getShippingStatus();
        if ((0 == orderStatus || 1 == orderStatus) && 0 == payStatus && 0 == shippingStatus) {
            order.setOrderStatus((byte) 2);
            orderDao.updateObejct(order);
            result = true;
        } else {
            LOG.info("订单已支付或者配送中不能取消订单!");
        }
        LOG.info("Function:cancelOrder.End.");
        if(result){
        	String user_name = getUserName(userId);
        	if(StringUtils.isEmpty(user_name)){
        		return false;
        	}
        	String action_note = "订单已被取消";
        	String str = "取消订单写入日志失败！！";
        	result = writeOrderLog(String.valueOf(orderId),user_name,String.valueOf(order.getOrderStatus()),String.valueOf(order.getShippingStatus()),String.valueOf(order.getPayStatus()),action_note,str);
        	
        }
        
        return result;
    }



    /**
     * 
     * @Title: confirmShipping
     * @Description:确认收货 交易成功
     * @param @param orderId
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public boolean confirmShipping(long orderId, long userId) {
        LOG.info("Function:confirmShipping.Start.");
        boolean result = false;
        Order order = orderDao.getOrderByUserIdAndOrderId(userId, orderId);
        if (null == order) {
            StringBuffer error = new StringBuffer();
            error.append("用户id=").append(userId).append("的订单order:id=").append(orderId).append("不存在!");
            LOG.error(error.toString());
            return result;
        }
        //order.setOrderStatus((byte) 7);
        order.setShippingStatus((byte) 2);
        order.setPayStatus((byte) 2);
        orderDao.updateObejct(order);
        result = true;
        LOG.info("Function:confirmShipping.End.");
        if(result){
        	String user_name = getUserName(userId);
        	if(StringUtils.isEmpty(user_name)){
        		return false;
        	}
        	String action_note = "您已确认收货，感谢你在优家购购物，欢迎下次光临";
        	String str = "确认收货写入日志失败！";
        	result = writeOrderLog(String.valueOf(orderId),user_name,String.valueOf(order.getOrderStatus()),String.valueOf(order.getShippingStatus()),String.valueOf(order.getPayStatus()),action_note,str);
        }
        return result;
    }



    /**
     * 
     * @Title: deleteOrder
     * @Description:删除订单 set isDel=1
     * @param @param orderId
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public boolean deleteOrder(long orderId, long userId) {

        LOG.info("Function:deleteOrder.Start.");
        boolean result = false;
        Order order = orderDao.getOrderByUserIdAndOrderId(userId, orderId);
        if (null == order) {
            StringBuffer error = new StringBuffer();
            error.append("用户id=").append(userId).append("的订单order:id=").append(orderId).append("不存在!");
            LOG.error(error.toString());
            return result;
        }
        int orderStatus = order.getOrderStatus();

        // 只有交易状态为关闭（状态码为3）和取消(2)，订单才允许删除
        if (3 == orderStatus || 2 == orderStatus) {
            order.setIsDel((byte) 1);
            orderDao.updateObejct(order);
            result = true;
            LOG.info("Function:deleteOrder.End.");
            return result;
        }

        StringBuffer error = new StringBuffer();
        error.append("订单 id=").append(orderId).append("不能删除!,orderStatus=" + orderStatus);
        LOG.error(error.toString());
        return result;
    }



    /**
     * 
     * @Title: getDiscountInfo
     * @Description:获取折扣信息
     * @param @param order
     * @param @return
     * @throws
     */
    @Override
    public DiscountInfo getDiscountInfo(DiscountInfo discountParam) {
        LogUtil.info("Function:getDiscountInfo.Start.");
        DiscountInfo discountInfo = null;
        String discountCode = discountParam.getCode();
        double goodsPrice = discountParam.getGoodsPrice();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("discount_code", discountCode);
        param.put("goods_amount", goodsPrice);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("YJG_HSV1_Create_Order");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (null != responseBaseData && !CollectionsUtils.isNull(responseBaseData.getRows())) {
            Map<String, String> discountInfoMap = responseBaseData.getRows().get(0);
            // 1-折扣码错误 2—折扣码正确，金额不满足 3-折扣码正确，金额满足
            int isFlag = StringUtils.StringToInt(discountInfoMap.get("is_flag"));
            String explains = discountInfoMap.get("activity_explain");
            double discountPrice = StringUtils.StringToDouble(discountInfoMap.get("order_discount"));
            if (1 == isFlag) {
                LOG.info("折扣码错误，discount code:" + discountCode);
                return null;
            }
            discountInfo = new DiscountInfo();
            discountInfo.setIsFlag(isFlag);
            discountInfo.setExplains(explains);
            discountInfo.setDiscountInfo(explains);
            discountInfo.setDiscountPrice(discountPrice);
        }
        LogUtil.info("Function:getDiscountInfo.End.");
        return discountInfo;
    }



    /**
     * 
     * @Title: getAllOrderByPhone
     * @Description:根据电话号码获取对应订单
     * @param @param order
     * @param @return String
     * @throws
     */
    @Override
    public String getAllOrderByPhone(String phone, int pageIndex, int pageSize, String plat) {
        LogUtil.info("Function:getAllOrderByPhone.Start.");
        int orderTotal = orderDao.getOrderTotalByPhone(phone);
        String result = "";
        if (orderTotal > 0) {
            List<Order> allOrderList = orderDao.getAllOrderByPhone(phone, pageIndex * pageSize, pageSize > 0 ? pageSize
                    : 10);
            result = assembleOrderListBySchema(allOrderList, plat, orderTotal);
        }
        LogUtil.info("Function:getAllOrderByPhone.End.");
        return result;
    }
    
    /**
     * 通过用户id得到用户名
     * @return
     */
    public String getUserName(long userId){
    	String user_name = "";
    	DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
		Map<String, Object> param2 = new HashMap<String, Object>();
		dsManageReqInfo2.setNeedAll("1");
		dsManageReqInfo2.setServiceName("HMJ_BUV1_USERS");
		param2.put("user_id", userId);
		dsManageReqInfo2.setParam(param2);
		String resultData = dataAction.getData(dsManageReqInfo2, "");
		RuleServiceResponseData responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
		LOG.info("OrderServiceImpl createDeliverGoods HMJ_BUV1_USERS param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
		LOG.info("OrderServiceImpl createDeliverGoods HMJ_BUV1_USERS result is: "+ resultData);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("OrderServiceImpl getUserName HMJ_BUV1_USERS param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
			LOG.error("OrderServiceImpl getUserName HMJ_BUV1_USERS result is: "+ resultData);
			LOG.error("app查询用户失败！");
			return null;
	    }
		List<Map<String,String>> mapList = responseData.getRows();
		if(null != mapList && mapList.size() > 0){
			user_name = mapList.get(0).get("user_name");
		}else{
			LOG.error("OrderServiceImpl getUserName HMJ_BUV1_USERS param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
			LOG.error("OrderServiceImpl getUserName HMJ_BUV1_USERS result is: "+ resultData);
			LOG.error("app查询用户无数据！");
			return null;
		}
		return user_name;
    }

    /**
     * 写入订单日志
     */
    public boolean writeOrderLog(String orderId,String user_name,String order_status,String shipping_status,String pay_status,String action_note,String str){
    	List<Action> newActions=new ArrayList<Action>();
    	Action action4 = new Action();
		action4.setServiceName("test_ecshop_ecs_order_action");
		Map<String, Object> setMap4 = new HashMap<String, Object>();
		action4.setType("C");
		setMap4.put("order_id", orderId);
		setMap4.put("action_user", user_name);
		setMap4.put("order_status", order_status);
		setMap4.put("shipping_status", shipping_status);
		setMap4.put("pay_status", pay_status);
		setMap4.put("action_note", action_note);
		setMap4.put("log_time", "$UnixTime");
		action4.setSet(setMap4);
		newActions.add(action4);
		
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		paramMap1.put("actions", newActions);
		paramMap1.put("transaction",1);
		DsManageReqInfo dsReqInfo = new DsManageReqInfo();
		dsReqInfo.setServiceName("MUSH_Offer");
		dsReqInfo.setParam(paramMap1);
		String result1 = mushroomAction.offer(dsReqInfo);
		JSONObject job = JSONObject.parseObject(result1);
		LOG.info("OrderServiceImpl writeOrderLog set param is: "+JSONObject.toJSONString(dsReqInfo));
		LOG.info("OrderServiceImpl writeOrderLog set result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
			LOG.error("OrderServiceImpl writeOrderLog set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.error("OrderServiceImpl writeOrderLog set result is: "+result1);
			LOG.error(str);
            return false;
	    }
		return true;
    }
}
