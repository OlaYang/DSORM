package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.util.LogUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-6-14
 * Time: 下午3:59
 * To change this template use File | Settings | File Templates.
 */
public class DELIVERY extends Function {
    static final String NAME = DELIVERY.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        // args: 商品编号 订单id  商品数量 商品价格 是否套装  分组方式 订单id依据值  供应商json 是否部分发货 商品编号依据值 套装id 当前商品套装id
        try {
            if (args.length < 7) {
                throw new ArgsCountError(NAME);
            }
            if (args[0] instanceof ExcelRange
                    && args[1] instanceof ExcelRange
                    && args[2] instanceof ExcelRange
                    && args[3] instanceof ExcelRange
                    && args[4] instanceof ExcelRange
                    ) {

            } else {
                throw new RengineException(calInfo.getServiceName(), "输入不是数列");
            }
            Map<Object, Object> cache = calInfo.getCache(NAME);
            final Iterator<Object> goods_sns = ((ExcelRange) args[0]).getIterator();
            final Iterator<Object> order_ids = ((ExcelRange) args[1]).getIterator();
            final Iterator<Object> goods_nums = ((ExcelRange) args[2]).getIterator();
            final Iterator<Object> goods_prices = ((ExcelRange) args[3]).getIterator();
            final Iterator<Object> suits = ((ExcelRange) args[4]).getIterator();
            final Iterator<Object> partial_shipments = ((ExcelRange) args[8]).getIterator();  // 是否部分发货
            final Iterator<Object> suit_ids = ((ExcelRange) args[10]).getIterator(); // 套装id集合

            String goods_sn_now = DataUtil.getStringValue(args[9]);

            String goods_suit_now = DataUtil.getStringValue(args[11]);

            List<Object> goods_sn_list = new ArrayList<Object>();
            List<Object> order_id_list = new ArrayList<Object>();
            List<Object> goods_num_list = new ArrayList<Object>();
            List<Object> goods_price_list = new ArrayList<Object>();
            List<Object> suit_list = new ArrayList<Object>();
            List<Object> partial_shipment_list = new ArrayList<Object>();
            List<Object> suit_id_list = new ArrayList<Object>();
            while (goods_sns.hasNext()) {
                goods_sn_list.add(goods_sns.next());
            }
            while (order_ids.hasNext()) {
                order_id_list.add(order_ids.next());
            }

            while (goods_nums.hasNext()) {
                goods_num_list.add(goods_nums.next());
            }
            while (goods_prices.hasNext()) {
                goods_price_list.add(goods_prices.next());
            }
            while (suits.hasNext()) {
                suit_list.add(suits.next());
            }
            while (partial_shipments.hasNext()) {
                partial_shipment_list.add(partial_shipments.next());
            }
            while (suit_ids.hasNext()) {
                suit_id_list.add(suit_ids.next());
            }
            // Map<Object,Object> order2goods = new HashMap<Object, Map<Object, Object>>(); // 订单id - <商品编号,订单商品数量>
            String order_id = DataUtil.getStringValue(args[6]); //订单id依据值
            Map<Object, Object> goods2num = new HashMap<Object, Object>(); //商品编号-订单商品数量
            Map<Object, Object> goods2price = new HashMap<Object, Object>(); //商品编号 - 订单商品价格
            Map<Object, Object> goods2suit = new HashMap<Object, Object>(); //商品编号-是否套装
            Map<Object, Object> goods2partial_shipment = new HashMap<Object, Object>();
            Map<Object, List<Object>> suit2goods = new HashMap<Object, List<Object>>();  // 套装id - 商品id


            List<Object> order_id2goods_sn = new ArrayList<Object>(); // 订单id下所有的商品编号
            List<Object> order_id2suit_id = new ArrayList<Object>();  // 订单id下所有的套装id
            boolean notSuit = true;
            boolean notPartialShipment = true;
            for (int i = 0; i < order_id_list.size(); i++) {
                if (String.valueOf(order_id_list.get(i)).equals(order_id)) {
                    order_id2goods_sn.add(goods_sn_list.get(i));
                    goods2num.put(goods_sn_list.get(i), goods_num_list.get(i));
                    goods2price.put(goods_sn_list.get(i), goods_price_list.get(i));
                    goods2suit.put(goods_sn_list.get(i), suit_list.get(i));
                    if (Integer.parseInt(String.valueOf(suit_list.get(i))) != 0) {
                        notSuit = false;   //是套装
                    }
                    if (Integer.parseInt(String.valueOf(partial_shipment_list.get(i))) != 0) {
                        notPartialShipment = false;
                    }

                    if (suit2goods.keySet().contains(suit_id_list.get(i))) {
                        List<Object> goods = suit2goods.get(suit_id_list.get(i));
                        goods.add(goods_sn_list.get(i));
                        suit2goods.put(suit_id_list.get(i), goods);
                    } else {
                        order_id2suit_id.add(suit_id_list.get(i));
                        List<Object> goods = new ArrayList<Object>();
                        goods.add(goods_sn_list.get(i));
                        suit2goods.put(suit_id_list.get(i), goods);
                    }
                }
            }
            // Object[] order_id2goods_sn = goods2num.keySet().toArray();  // 订单id下所有的商品编号
           // LogUtil.info("all:" + JSON.toJSONString(order_id2goods_sn));
            String supplier_json = DataUtil.getStringValue(args[7]); //供应商json
            Map<Object, Map<Object, Object>> supplier2goods = null;
            Map<Object, Map<Object, Object>> supplier2goods_temp = null;
            if (cache.get("gongyingshang_json_temp") == null) {
                supplier2goods = JSON.parseObject(supplier_json, Map.class);    // 供应商-商品编号-商品数量
                supplier2goods_temp = supplier2goods;
            } else {
                if (order_id.equals((String) cache.get("order_id"))) {
                    if (cache.get("finalSupplierJson") == null) {
                        supplier2goods = JSON.parseObject(supplier_json, Map.class);
                    } else {
                        supplier2goods = JSON.parseObject((String) cache.get("finalSupplierJson"), Map.class);
                    }
                    supplier2goods_temp = (Map<Object, Map<Object, Object>>) cache.get("gongyingshang_json_temp");
                } else {
                    supplier2goods = (Map<Object, Map<Object, Object>>) cache.get("gongyingshang_json_temp");
                    supplier2goods_temp = supplier2goods;
                }
            }
            // LogUtil.info("供应商数量:" + JSON.toJSONString(supplier2goods));
            // LogUtil.info("供应商临时数量:" + JSON.toJSONString(supplier2goods_temp));
            Object[] suppliers = supplier2goods.keySet().toArray();
            boolean delivery = true;
            //  Map<Object, Map<Object, Object>> supplier2goods_temp = JSON.parseObject(supplier_json, Map.class);    // 临时存放 供应商-商品编号-商品数量
            // 非套装 非部分发货的情况
            //  LogUtil.info("order_id2goods_sn1:" + JSON.toJSONString(order_id2goods_sn));
            if (notPartialShipment) {
                if (notSuit) {  // 非部分发货且非套装
                    // LogUtil.info("order_id2goods_sn2:" + JSON.toJSONString(order_id2goods_sn));
                    for (int i = 0; i < order_id2goods_sn.size(); i++) {
                        if (i == 0) {
                            cache.put("finalSupplierJson", JSON.toJSONString(supplier2goods_temp));
                        }
                        // LogUtil.info("order_id2goods_sn:" + order_id2goods_sn.get(i));
                        // LogUtil.info("goods_sn:" + goods_sn_now);
                        Integer order_goods_num = Integer.parseInt(String.valueOf(goods2num.get(order_id2goods_sn.get(i)))); //订单商品需求数量
                        for (int j = 0; j < suppliers.length; j++) {
                            Map<Object, Object> supplier2goods2num = supplier2goods.get(suppliers[j]); //供应商的商品编号-商品数量
                            Map<Object, Object> supplier2goods2num_temp = supplier2goods_temp.get(suppliers[j]); //临时存放供应商的商品编号-商品数量
                            //LogUtil.info("供应商商品数量:" + JSON.toJSONString(supplier2goods2num));
                            // LogUtil.info("供应商临时商品数量:" + JSON.toJSONString(supplier2goods2num_temp));
                            if (supplier2goods2num.get(order_id2goods_sn.get(i)) == null) {     //该供应商下没有该商品
                                continue;
                            }

                            Integer supplier_goods_num = Integer.parseInt(String.valueOf(supplier2goods2num.get(order_id2goods_sn.get(i))));
                            if (order_goods_num <= supplier_goods_num) {  // 订单商品数量少于供应商商品数量
                                if (goods_sn_now.equals(order_id2goods_sn.get(i))) {  // 对于当前计算的商品编号，满足发货条件的删除其商品数量
                                    supplier2goods2num_temp.put(order_id2goods_sn.get(i), (supplier_goods_num - order_goods_num));
                                    supplier2goods_temp.put(suppliers[j], supplier2goods2num_temp);
                                }
                                continue;

                            } else {
                                delivery = false;
                                break;
                            }
                        }
                    }


                    if (delivery) {
                        supplier2goods_temp.putAll(supplier2goods_temp);
                        cache.put("gongyingshang_json_temp", supplier2goods_temp);
                        cache.put("order_id", order_id); //记录当前订单id值
                        // LogUtil.info("supplier2goods:" + JSON.toJSONString(supplier2goods));
                    } else {
                        cache.put("gongyingshang_json_temp", JSON.parseObject(String.valueOf(cache.get("finalSupplierJson")), Map.class));
                        cache.put("order_id", order_id);
                    }

                } else {  // 非部分发货且为套装


                }
            } else if (!notPartialShipment) {    // 部分发货
                if (notSuit) { // 不是套装
                    for (int i = 0; i < order_id2goods_sn.size(); i++) {
                        if (i == 0) {
                            cache.put("finalSupplierJson", JSON.toJSONString(supplier2goods_temp));
                        }
                        if (goods_sn_now.equals(order_id2goods_sn.get(i))) {
                            Integer order_goods_num = Integer.parseInt(String.valueOf(goods2num.get(order_id2goods_sn.get(i)))); //订单商品需求数量
                            for (int j = 0; j < suppliers.length; j++) {
                                Map<Object, Object> supplier2goods2num_temp = supplier2goods_temp.get(suppliers[j]); //临时存放供应商的商品编号-商品数量
                                if (supplier2goods2num_temp.get(order_id2goods_sn.get(i)) == null) {     //该供应商下没有该商品
                                    continue;
                                }
                                Integer supplier_goods_num = Integer.parseInt(String.valueOf(supplier2goods2num_temp.get(order_id2goods_sn.get(i))));
                                if (order_goods_num <= supplier_goods_num) {  // 订单商品数量少于供应商商品数量
                                    supplier2goods2num_temp.put(order_id2goods_sn.get(i), (supplier_goods_num - order_goods_num));
                                    supplier2goods_temp.put(suppliers[j], supplier2goods2num_temp);
                                    delivery = true;
                                    break;
                                } else {
                                    delivery = false;
                                    continue;
                                }
                            }
                        }
                    }

                    if (delivery) {
                        supplier2goods_temp.putAll(supplier2goods_temp);
                        cache.put("gongyingshang_json_temp", supplier2goods_temp);
                        cache.put("order_id", order_id); //记录当前订单id值
                    } else {
                        cache.put("gongyingshang_json_temp", JSON.parseObject(String.valueOf(cache.get("finalSupplierJson")), Map.class));
                        cache.put("order_id", order_id);
                    }
                } else {   // 是套装
                   // LogUtil.info("套装:" + JSON.toJSONString(suit2goods));

                    for (int i = 0; i < order_id2suit_id.size(); i++) { //遍历套装
                        if (i == 0) {
                            cache.put("finalSupplierJson", JSON.toJSONString(supplier2goods_temp));
                        }
                        if (!goods_suit_now.equals(String.valueOf(order_id2suit_id.get(i)))) {  // 判断是否是当前套装
                            continue;
                        }
                        List<Object> oneSuit2goods = suit2goods.get(order_id2suit_id.get(i)); // 获取每个套装下面的商品
                        // 判断供应商下面套装内商品的种类
                        for (int j = 0; j < suppliers.length; j++) {
                            Map<Object, Object> supplier2goods2num_temp = supplier2goods_temp.get(suppliers[j]); //临时存放供应商的商品编号-商品数量
                            if (!supplier2goods2num_temp.keySet().containsAll(oneSuit2goods)) { // 该供应商下面没有套装内所有商品
                                continue;
                            } else {
                                // 如果该供应商满足商品的种类但不满足商品的数量
                                boolean suitIsDelivery = true; //套装是否可发货
                                for (int k = 0; k < oneSuit2goods.size(); k++) {   //遍历套装中的商品


                                    Integer order_goods_num = Integer.parseInt(String.valueOf(goods2num.get(oneSuit2goods.get(k)))); //订单商品所需的商品数量

                                    Integer supplier_goods_num = Integer.parseInt(String.valueOf(supplier2goods.get(suppliers[j]).get(oneSuit2goods.get(k)))); //供应商商品数量
                                    if (goods_sn_now.equals(oneSuit2goods.get(k))) {
                                        if (order_goods_num > supplier_goods_num) { //订单数大于供应商商品数
                                            suitIsDelivery = false;  //套装不可发货
                                            break;
                                        }
                                    }

                                }

                                if (suitIsDelivery) {
                                    for (int k = 0; k < oneSuit2goods.size(); k++) {
                                        Integer order_goods_num = Integer.parseInt(String.valueOf(goods2num.get(oneSuit2goods.get(k)))); //订单商品所需的商品数量
                                        Integer supplier_goods_num = Integer.parseInt(String.valueOf(supplier2goods.get(suppliers[j]).get(oneSuit2goods.get(k)))); //供应商商品数量
                                      //  LogUtil.info("当前商品:" + goods_sn_now + ";oneSuit:" + JSON.toJSONString(oneSuit2goods));
                                        if (goods_sn_now.equals(oneSuit2goods.get(k))) {  // 对于当前计算的商品编号，满足发货条件的删除其商品数量
                                            supplier2goods2num_temp.put(oneSuit2goods.get(k), (supplier_goods_num - order_goods_num));
                                          //  LogUtil.info("supplier_goods_num:" + supplier_goods_num + ";order_goods_num:" + order_goods_num);
                                            supplier2goods_temp.put(suppliers[j], supplier2goods2num_temp);
                                           // LogUtil.info("supplier2goods2num_temp:" + JSON.toJSONString(supplier2goods2num_temp));
                                            supplier2goods_temp.putAll(supplier2goods_temp);
                                            cache.put("gongyingshang_json_temp", supplier2goods_temp);
                                            cache.put("order_id", order_id); //记录当前订单id值
                                            return true;
                                        }
                                    }
                                }
                                //  }

                            }
                            cache.put("gongyingshang_json_temp", JSON.parseObject(String.valueOf(cache.get("finalSupplierJson")), Map.class));
                            cache.put("order_id", order_id);
                            return false;

                        }
                    }

                }
            }


            return delivery;  //To change body of implemented methods use File | Settings | File Templates.
        } catch (Exception e) {
            LogUtil.info("", e);
            return null;
        }

    }
}
