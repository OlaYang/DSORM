package com.meiqi.data.user.handler;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.excel.NumberPool;

import java.util.Map;

/**
 * User: 
 * Date: 13-12-30
 * Time: 上午10:21
 */
public class OrderRow implements Comparable<OrderRow> {
    public final Map<String, String> row;
    public final Object id;

    public OrderRow(Object id, Map<String, String> row) {
        this.id = id;
        this.row = row;
    }

    @Override
    public int compareTo(OrderRow o) {
        try {
            long cmp = DataUtil.compareO(id, o.id);
            return cmp > NumberPool.LONG_0 ? 1 : (cmp == NumberPool.LONG_0 ? 0 : -1);
        } catch (Exception e) {
            //
        }

        return 0;
    }
}