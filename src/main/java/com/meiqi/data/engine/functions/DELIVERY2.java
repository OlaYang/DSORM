package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.util.LogUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-6-17
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public class DELIVERY2 extends Function {
    static final String NAME = DELIVERY2.class.getSimpleName();
    static Map<Object, Map<Object, Object>> goods_supplier_num_cache;
    static Map<Object, Object> notPartialShipmentAndNotSuit = new HashMap<Object, Object>();

    static String orderID = "";

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        Map<Object, Object> cache = calInfo.getCache(NAME);
        goods_supplier_num_cache = (Map<Object, Map<Object, Object>>)
                cache.get("goods_supplier_num_cache");   // 缓存全局 商品编号-供应商编号-供应商商品数量
        if (goods_supplier_num_cache == null) {
            goods_supplier_num_cache = new HashMap<Object, Map<Object, Object>>();
            cache.put("goods_supplier_num_cache", goods_supplier_num_cache);
        }


        // args: 商品编号集 订单id集  商品数量集 商品价格集 是否套装集 是否部分发货集 套装id集 分组方式 当前订单id  当前商品编号  当前商品套装id  供应商json
        try {
            if (args.length < 12) {
                throw new ArgsCountError(NAME);
            }
            if (args[0] instanceof ExcelRange
                    && args[1] instanceof ExcelRange
                    && args[2] instanceof ExcelRange
                    && args[3] instanceof ExcelRange
                    && args[4] instanceof ExcelRange
                    && args[5] instanceof ExcelRange
                    && args[6] instanceof ExcelRange
                    ) {

            } else {
                throw new RengineException(calInfo.getServiceName(), "输入不是数列");
            }




            final Iterator<Object> goods_sns = ((ExcelRange) args[0]).getIterator();
            final Iterator<Object> order_ids = ((ExcelRange) args[1]).getIterator();
            final Iterator<Object> goods_nums = ((ExcelRange) args[2]).getIterator();
            final Iterator<Object> goods_prices = ((ExcelRange) args[3]).getIterator();
            final Iterator<Object> suits = ((ExcelRange) args[4]).getIterator();
            final Iterator<Object> partial_shipments = ((ExcelRange) args[5]).getIterator();  // 是否部分发货
            final Iterator<Object> suit_ids = ((ExcelRange) args[6]).getIterator(); // 套装id集合
            String order_id = DataUtil.getStringValue(args[8]); //当前订单id依据值
            String goods_sn_now = DataUtil.getStringValue(args[9]);
            String goods_suit_now = DataUtil.getStringValue(args[10]);

            String supplier_json = DataUtil.getStringValue(args[11]); //供应商json
            Map<Object, Map<Object, Object>> orginal_supplier = JSON.parseObject(supplier_json, Map.class);
            Set<Object> orginal_supplier_goods = orginal_supplier.keySet();
            Iterator<Object> iterator = orginal_supplier_goods.iterator();
            while (iterator.hasNext()) {
                Object good = iterator.next();
                if (!goods_supplier_num_cache.keySet().contains(good)) {
                    goods_supplier_num_cache.put(good, orginal_supplier.get(good));
                }
            }

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


            if (notPartialShipment) {  //非部分发货
                if (notSuit) { //非部分发货 非套装
                    if (!"".equals(orderID) & orderID.equals(order_id)) {
                        LogUtil.info("进入1");
                        Object result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        if (result == null) {
                            notPartialShipmentAndNotSuit = op(order_id, goods2num, goods_supplier_num_cache);
                            result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        }
                        LogUtil.info("结果:" + JSON.toJSONString(goods_supplier_num_cache));
                        return result;
                    } else {
                        LogUtil.info("进入2");
                        notPartialShipmentAndNotSuit.clear();
                        Object result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        if (result == null) {
                            notPartialShipmentAndNotSuit = op(order_id, goods2num, goods_supplier_num_cache);
                            result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        }

                        return result;
                    }


                } else { // 非部分发货 套装

                    if (!"".equals(orderID) & orderID.equals(order_id)) {
                        LogUtil.info("进入7");
                        Object result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        if (result == null) {
                            notPartialShipmentAndNotSuit = op2(order_id, goods2num, suit2goods, goods_supplier_num_cache);
                            result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        }
                        LogUtil.info("结果:" + JSON.toJSONString(goods_supplier_num_cache));
                        return result;
                    } else {
                        LogUtil.info("进入8");
                        notPartialShipmentAndNotSuit.clear();
                        Object result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        if (result == null) {
                            notPartialShipmentAndNotSuit = op2(order_id, goods2num, suit2goods, goods_supplier_num_cache);
                            result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        }

                        return result;
                    }
                }

            } else {
                if (notSuit) { // 部分发货非套装
                    if (!"".equals(orderID) & orderID.equals(order_id)) {
                        LogUtil.info("进入3");
                        Object result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        if (result == null) {
                            notPartialShipmentAndNotSuit = op3(order_id, goods2num, goods_supplier_num_cache);
                            result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        }
                        LogUtil.info("结果:" + JSON.toJSONString(goods_supplier_num_cache));
                        return result;
                    } else {
                        LogUtil.info("进入4");
                        notPartialShipmentAndNotSuit.clear();
                        Object result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        if (result == null) {
                            notPartialShipmentAndNotSuit = op3(order_id, goods2num, goods_supplier_num_cache);
                            result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        }

                        return result;
                    }
                } else {  // 部分发货 套装
                    if (!"".equals(orderID) & orderID.equals(order_id)) {
                        LogUtil.info("进入5");
                        Object result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        if (result == null) {
                            notPartialShipmentAndNotSuit = op4(order_id, goods2num, suit2goods, goods_supplier_num_cache);
                            result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        }
                        LogUtil.info("结果:" + JSON.toJSONString(goods_supplier_num_cache));
                        return result;
                    } else {
                        LogUtil.info("进入6");
                        notPartialShipmentAndNotSuit.clear();
                        Object result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        if (result == null) {
                            notPartialShipmentAndNotSuit = op4(order_id, goods2num, suit2goods, goods_supplier_num_cache);
                            result = notPartialShipmentAndNotSuit.get(goods_sn_now);
                        }

                        return result;
                    }
                }
            }

        } catch (Exception e) {
            LogUtil.info("", e);
            return StringPool.EMPTY;
        }


    }


    public static Map<Object, Object> op(String order_id, Map<Object, Object> goods2num, Map<Object, Map<Object, Object>> supplier2goods2num) {
        // 同一个订单下，非部分发货，非套装计算
        Set<Object> goods4order = goods2num.keySet();
        Iterator<Object> iterator = goods4order.iterator();
        boolean delivery = true;
        Map<Object, Map<Object, Object>> temp = new HashMap<Object, Map<Object, Object>>();  // 临时存储相减数据
        while (iterator.hasNext()) {
            Object good = iterator.next();
            Object order_goods_num = goods2num.get(good);
            //获取供应商该商品的数量

            Map<Object, Object> supplierID2numByGood = supplier2goods2num.get(good);
            // 循环供应商
            Set<Object> supplierID = supplierID2numByGood.keySet();
            Iterator<Object> iterator1 = supplierID.iterator();
            boolean supplierFlag = false;  // 标记每个供应商是否满足订单商品的需求个数,默认否
            while (iterator1.hasNext()) {
                Object supplier = iterator1.next();
                int num = Integer.parseInt(String.valueOf(supplierID2numByGood.get(supplier))) - Integer.parseInt(String.valueOf(order_goods_num)); //供应商数量减去订单商品数量
                if (num >= 0) {
                    Map<Object, Object> temp_supplier_num = new HashMap<Object, Object>();
                    temp_supplier_num.put(supplier, num);
                    temp.put(good, temp_supplier_num);
                    supplierFlag = true;
                    break;
                }
            }
            if (!supplierFlag) {
                delivery = false;
                break;
            }

        }
        Map<Object, Object> result = new HashMap<Object, Object>();
        Iterator<Object> iterator_new = goods4order.iterator();
        while (iterator_new.hasNext()) {
            Object good = iterator_new.next();
            if (delivery) {
                Map<Object, Object> supplier_num_by_goods = goods_supplier_num_cache.get(good);  // 按商品取出供应商对应的商品个数
                supplier_num_by_goods.putAll(temp.get(good));
                goods_supplier_num_cache.put(good, supplier_num_by_goods);
                result.put(good, "Y");
            } else {
                result.put(good, "N");
            }
        }
        orderID = order_id;
        return result;
    }

    public static Map<Object, Object> op2(String order_id, Map<Object, Object> goods2num, Map<Object, List<Object>> suit2goods, Map<Object, Map<Object, Object>> supplier2goods2num) {
        // 同一个订单下，非部分发货，套装计算
        Set<Object> goods4order = goods2num.keySet();
        Iterator<Object> iterator = goods4order.iterator();
        Set<Object> set = new HashSet<Object>(); // 存储可发货的套装
        boolean delivery = true;
        Map<Object, Map<Object, Object>> temp = new HashMap<Object, Map<Object, Object>>();  // 临时存储相减数据
        while (iterator.hasNext()) {
            Object good = iterator.next();

            // 如果这个商品在已经计算过的套装中，则跳出
            if (set.contains(getSuitByGoods(suit2goods, good))) {
                continue;
            }

            Object order_goods_num = goods2num.get(good);
            //获取供应商该商品的数量

            Map<Object, Object> supplierID2numByGood = supplier2goods2num.get(good);
            // 循环供应商
            Set<Object> supplierID = supplierID2numByGood.keySet();
            Iterator<Object> iterator1 = supplierID.iterator();

            Map<Object, Map<Object, Object>> _temp = new HashMap<Object, Map<Object, Object>>();
            while (iterator1.hasNext()) {

                Object supplier = iterator1.next();
                int num = Integer.parseInt(String.valueOf(supplierID2numByGood.get(supplier))) - Integer.parseInt(String.valueOf(order_goods_num)); //
                if (num >= 0) {
                    Map<Object, Object> temp_supplier_num = new HashMap<Object, Object>();
                    temp_supplier_num.put(supplier, num);
                    _temp.put(good, temp_supplier_num);
                    boolean suitIsDelivery = true;  // 套装是否可发货
                    // 对该供应商 循环其余商品数量
                    // 首先获取该商品所在的套装
                    Object suit_id = getSuitByGoods(suit2goods, good);
                    List<Object> all_goods_in_suit = suit2goods.get(suit_id);
                    for (int i = 0; i < all_goods_in_suit.size(); i++) {
                        if (all_goods_in_suit.get(i).equals(good)) {
                            continue;
                        }
                        if (!supplier2goods2num.keySet().contains(all_goods_in_suit.get(i))) {
                            suitIsDelivery = false;
                            break;
                        }
                        Map<Object, Object> _supplierID2numByGood = supplier2goods2num.get(all_goods_in_suit.get(i));
                        Object _order_goods_num = goods2num.get(all_goods_in_suit.get(i));
                        int _num = Integer.parseInt(String.valueOf(_supplierID2numByGood.get(supplier))) - Integer.parseInt(String.valueOf(_order_goods_num));
                        if (_num < 0) {
                            suitIsDelivery = false;
                            break;
                        } else {
                            Map<Object, Object> _temp_supplier_num = new HashMap<Object, Object>();
                            _temp_supplier_num.put(supplier, _num);
                            _temp.put(all_goods_in_suit.get(i), _temp_supplier_num);
                        }

                    }

                    if (suitIsDelivery == false) {
                        _temp.clear();
                        continue;
                    } else {
                        set.add(suit_id);
                        temp.putAll(_temp);
                    }


                }
            }


        }
        Map<Object, Object> result = new HashMap<Object, Object>();
        Iterator<Object> iterator_new = goods4order.iterator();
        while (iterator_new.hasNext()) {
            Object good = iterator_new.next();
            if (set.containsAll(suit2goods.keySet())) { // 如果存储的可发货的套装全部包含该订单下的套装id，则整个订单可发货，否则不能发货
                Map<Object, Object> supplier_num_by_goods = goods_supplier_num_cache.get(good);  // 按商品取出供应商对应的商品个数
                supplier_num_by_goods.putAll(temp.get(good));
                goods_supplier_num_cache.put(good, supplier_num_by_goods);
                result.put(good, "Y");
            } else {
                result.put(good, "N");
            }
        }
        orderID = order_id;
        return result;
    }


    public static Map<Object, Object> op3(String order_id, Map<Object, Object> goods2num, Map<Object, Map<Object, Object>> supplier2goods2num) {
        // 同一个订单下，部分发货，非套装计算
        Set<Object> goods4order = goods2num.keySet();
        Iterator<Object> iterator = goods4order.iterator();
        Set<Object> set = new HashSet<Object>(); // 存储可发货的商品
        boolean delivery = true;
        Map<Object, Map<Object, Object>> temp = new HashMap<Object, Map<Object, Object>>();  // 临时存储相减数据
        while (iterator.hasNext()) {
            Object good = iterator.next();
            Object order_goods_num = goods2num.get(good);
            //获取供应商该商品的数量

            Map<Object, Object> supplierID2numByGood = supplier2goods2num.get(good);
            // 循环供应商
            Set<Object> supplierID = supplierID2numByGood.keySet();
            Iterator<Object> iterator1 = supplierID.iterator();
            while (iterator1.hasNext()) {
                Object supplier = iterator1.next();
                int num = Integer.parseInt(String.valueOf(supplierID2numByGood.get(supplier))) - Integer.parseInt(String.valueOf(order_goods_num)); //
                if (num >= 0) {
                    Map<Object, Object> temp_supplier_num = new HashMap<Object, Object>();
                    temp_supplier_num.put(supplier, num);
                    temp.put(good, temp_supplier_num);
                    set.add(good);
                    break;
                }
            }


        }
        Map<Object, Object> result = new HashMap<Object, Object>();
        Iterator<Object> iterator_new = goods4order.iterator();
        while (iterator_new.hasNext()) {
            Object good = iterator_new.next();
            if (set.contains(good)) {
                Map<Object, Object> supplier_num_by_goods = goods_supplier_num_cache.get(good);  // 按商品取出供应商对应的商品个数
                supplier_num_by_goods.putAll(temp.get(good));
                goods_supplier_num_cache.put(good, supplier_num_by_goods);
                result.put(good, "Y");
            } else {
                result.put(good, "N");
            }
        }
        orderID = order_id;
        return result;
    }


    public static Map<Object, Object> op4(String order_id, Map<Object, Object> goods2num, Map<Object, List<Object>> suit2goods, Map<Object, Map<Object, Object>> supplier2goods2num) {
        // 同一个订单下，部分发货，套装计算


        Set<Object> goods4order = goods2num.keySet();
        Iterator<Object> iterator = goods4order.iterator();
        Set<Object> set = new HashSet<Object>(); // 存储可发货的商品
        boolean delivery = true;
        Map<Object, Map<Object, Object>> temp = new HashMap<Object, Map<Object, Object>>();  // 临时存储相减数据
        while (iterator.hasNext()) {
            Object good = iterator.next();

            // 如果这个商品在已经计算过的套装中，则跳出
            if (set.contains(getSuitByGoods(suit2goods, good))) {
                continue;
            }

            Object order_goods_num = goods2num.get(good);
            //获取供应商该商品的数量

            Map<Object, Object> supplierID2numByGood = supplier2goods2num.get(good);
            // 循环供应商
            Set<Object> supplierID = supplierID2numByGood.keySet();
            Iterator<Object> iterator1 = supplierID.iterator();

            Map<Object, Map<Object, Object>> _temp = new HashMap<Object, Map<Object, Object>>();
            while (iterator1.hasNext()) {

                Object supplier = iterator1.next();
                int num = Integer.parseInt(String.valueOf(supplierID2numByGood.get(supplier))) - Integer.parseInt(String.valueOf(order_goods_num)); //
                if (num >= 0) {
                    Map<Object, Object> temp_supplier_num = new HashMap<Object, Object>();
                    temp_supplier_num.put(supplier, num);
                    _temp.put(good, temp_supplier_num);
                    boolean suitIsDelivery = true;  // 套装是否可发货
                    // 对该供应商 循环其余商品数量
                    // 首先获取该商品所在的套装
                    Object suit_id = getSuitByGoods(suit2goods, good);
                    List<Object> all_goods_in_suit = suit2goods.get(suit_id);
                    for (int i = 0; i < all_goods_in_suit.size(); i++) {
                        if (all_goods_in_suit.get(i).equals(good)) {
                            continue;
                        }
                        if (!supplier2goods2num.keySet().contains(all_goods_in_suit.get(i))) {
                            suitIsDelivery = false;
                            break;
                        }
                        Map<Object, Object> _supplierID2numByGood = supplier2goods2num.get(all_goods_in_suit.get(i));
                        Object _order_goods_num = goods2num.get(all_goods_in_suit.get(i));
                        int _num = Integer.parseInt(String.valueOf(_supplierID2numByGood.get(supplier))) - Integer.parseInt(String.valueOf(_order_goods_num));
                        if (_num < 0) {
                            suitIsDelivery = false;
                            break;
                        } else {
                            Map<Object, Object> _temp_supplier_num = new HashMap<Object, Object>();
                            _temp_supplier_num.put(supplier, _num);
                            _temp.put(all_goods_in_suit.get(i), _temp_supplier_num);
                        }

                    }

                    if (suitIsDelivery == false) {
                        _temp.clear();
                        continue;
                    } else {
                        set.add(suit_id);
                        temp.putAll(_temp);
                    }


                }
            }


        }
        Map<Object, Object> result = new HashMap<Object, Object>();
        Iterator<Object> iterator_new = goods4order.iterator();
        while (iterator_new.hasNext()) {
            Object good = iterator_new.next();
            Object suit_id = getSuitByGoods(suit2goods, good);
            if (set.contains(suit_id)) {
                Map<Object, Object> supplier_num_by_goods = goods_supplier_num_cache.get(good);  // 按商品取出供应商对应的商品个数
                supplier_num_by_goods.putAll(temp.get(good));
                goods_supplier_num_cache.put(good, supplier_num_by_goods);
                result.put(good, "Y");
            } else {
                result.put(good, "N");
            }
        }
        orderID = order_id;
        return result;
    }


    public static Object getSuitByGoods(Map<Object, List<Object>> suit2goods, Object good) {
        Set<Object> keys = suit2goods.keySet();
        Iterator<Object> iterator = keys.iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            List<Object> list = suit2goods.get(key);
            if (list.contains(good)) {
                return key;
            }
        }

        return null;
    }
}
