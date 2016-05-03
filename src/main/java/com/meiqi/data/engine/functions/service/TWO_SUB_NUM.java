package com.meiqi.data.engine.functions.service;


import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-9-15
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
/*
*  业务函数
*  二维存量余量函数
* */
public class TWO_SUB_NUM extends Function {
    public static final String NAME = TWO_SUB_NUM.class.getSimpleName();
    // static Map<Object, LinkedHashMap<Object, Long>> global; // <第一维度分组数值,<第二维度数值,第二维度存量>>

    @Override
    /*
    * args: 第一维度分组数列，第一维度分组值，第二维度数列，第二维度值，第二维度存量数列
    * */
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {

        if (args.length != 5) {
            throw new ArgsCountError(NAME);
        }
        final Iterator<Object> firstDim; // 第一维度分组数列
        final Iterator<Object> secondDim;  // 第二维度数列
        final Iterator<Object> secondDimNum;
        if (args[0] instanceof ExcelRange && args[2] instanceof ExcelRange && args[4] instanceof ExcelRange) {
            firstDim = ((ExcelRange) args[0]).getIterator();
            secondDim = ((ExcelRange) args[2]).getIterator();
            secondDimNum = ((ExcelRange) args[4]).getIterator();
        } else {
            throw new RengineException(calInfo.getServiceName(), "输入不是数列");
        }
        Object firstDimCurrentValue = args[1];  // 第一维度当前分组值
        Object secondDimCurrentValue = args[3];   // 第二维度当前数值
        // String secondDimNumCurrentValue = DataUtil.getStringValue(args[5]);  // 第二维度当前存量数值

        Map<Object, Object> cache = calInfo.getCache(NAME);
        _Key key = new _Key(args[0], args[2], args[4]);
        Map<Object, LinkedHashMap<Object, Long>> global = (Map<Object, LinkedHashMap<Object, Long>>) cache.get(key);
        if (global == null) {
            global = new LinkedHashMap<Object, LinkedHashMap<Object, Long>>();
            init(global, firstDim, secondDim, secondDimNum);
            cache.put(key, global);
        } else {
            //  LogUtil.info("命中缓存");
        }
        // LogUtil.info("global:"+ JSON.toJSONString(global));
        //  LogUtil.info("firstDimCurrentValue:"+firstDimCurrentValue);
        // LogUtil.info("secondDimCurrentValue:"+secondDimCurrentValue);
        // LogUtil.info("global.get(firstDimCurrentValue):"+JSON.toJSONString(global.get(args[1])));
        // LogUtil.info("global_:" + JSON.toJSONString(global));
        return global.get(firstDimCurrentValue).get(secondDimCurrentValue);


    }

    static Map<Object, LinkedHashMap<Object, Long>> init(Map<Object, LinkedHashMap<Object, Long>> global, Iterator<Object> firstDim, Iterator<Object> secondDim, Iterator<Object> secondDimNum) throws RengineException {
        List<Object> firstDimList = new ArrayList<Object>();
        List<Object> secondDimList = new ArrayList<Object>();
        List<Object> secondDimNumList = new ArrayList<Object>();
        while (firstDim.hasNext()) {
            Object firstDimValue = firstDim.next();
            if (firstDimValue == null) {
                firstDimValue = StringPool.EMPTY;
            }
            firstDimList.add(firstDimValue);

        }

        while (secondDim.hasNext()) {
            Object secondDimValue = secondDim.next();
            if (secondDimValue == null) {
                secondDimValue = StringPool.EMPTY;
            }
            secondDimList.add(secondDimValue);
        }

        while (secondDimNum.hasNext()) {
            Object secondDimNumValue = secondDimNum.next();
            if (secondDimNumValue == null) {
                secondDimNumValue = 0;
            }
            secondDimNumList.add(secondDimNumValue);
        }

        for (int i = 0; i < firstDimList.size(); i++) {
            Object firstDimValue = firstDimList.get(i);
            LinkedHashMap<Object, Long> secondDim2num = global.get(firstDimValue);
            if (secondDim2num == null) {
                secondDim2num = new LinkedHashMap<Object, Long>();
            }
            secondDim2num.put(secondDimList.get(i), Long.parseLong(String.valueOf(secondDimNumList.get(i))));
            global.put(firstDimValue, secondDim2num);
        }
        //  LogUtil.info("global开始:" + JSON.toJSONString(global));
        Set<Object> globalKeys = global.keySet();
        Iterator<Object> iterator = globalKeys.iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            // LogUtil.info("key:" + key);

            LinkedHashMap<Object, Long> secondDim2num = global.get(key);

            //  LogUtil.info("secondDim2num:" + JSON.toJSONString(secondDim2num));
            Object minKey = findMinKey(secondDim2num);
            Long minValue = secondDim2num.get(minKey);
            // LogUtil.info("minKey:" + minKey + ";minValue:" + minValue);
            Set<Object> secondDim2numKeys = secondDim2num.keySet();
            Iterator<Object> secondIterator = secondDim2numKeys.iterator();

            while (secondIterator.hasNext()) {
                Object secondKey = secondIterator.next();
                Long secondValue = secondDim2num.get(secondKey);
                secondDim2num.put(secondKey, secondValue - minValue);
            }


            updateGlobal(global, secondDim2num, secondDim2numKeys, globalKeys, key);

            // LogUtil.info("global:" + JSON.toJSONString(global));


        }


        return null;
    }


    static Object findMinKey(Map<Object, Long> map) throws RengineException {
        Map<Object, Integer> sortByValueMap = DataUtil.sortByValue(map);
        Object[] keys = sortByValueMap.keySet().toArray();
        return keys[0];
    }


    static void updateGlobal(Map<Object, LinkedHashMap<Object, Long>> globalUpdate, LinkedHashMap<Object, Long> secondDim2numUpdate,
                             Set<Object> secondDim2numUpdateKeys, Set<Object> globalKeys, Object globalKey) {
        // LogUtil.info("进入");
        Iterator<Object> globalIterator = globalKeys.iterator();
        while (globalIterator.hasNext()) {

            Object key = globalIterator.next();
            //  LogUtil.info("updateKey:" + key);
            if (key.equals(globalKey)) {
                continue;
            }
            //  LogUtil.info("globalKey:" + globalKey + "更新");
            LinkedHashMap<Object, Long> secondDim2numOld = globalUpdate.get(key);
            Iterator<Object> iterator = secondDim2numOld.keySet().iterator();
            while (iterator.hasNext()) {
                Object secondDim2numKey = iterator.next();
                if (secondDim2numUpdateKeys.contains(secondDim2numKey)) {
                    secondDim2numOld.put(secondDim2numKey, secondDim2numUpdate.get(secondDim2numKey));
                } else {
                    continue;
                }
            }
            globalUpdate.put(key, secondDim2numOld);
        }

    }


    class _Key {
        Object firstDim, secondDim, secondDimNum;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof _Key)) return false;

            _Key key = (_Key) o;

            if (firstDim != null ? !firstDim.equals(key.firstDim) : key.firstDim != null)
                return false;
            if (secondDim != null ? !secondDim.equals(key.secondDim) : key.secondDim != null)
                return false;
            if (secondDimNum != null ? !secondDimNum.equals(key.secondDimNum) : key.secondDimNum != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = firstDim != null ? firstDim.hashCode() : 0;
            result = 31 * result + (secondDim != null ? secondDim.hashCode() : 0);
            result = 31 * result + (secondDimNum != null ? secondDimNum.hashCode() : 0);
            return result;
        }

        public _Key(Object firstDim, Object secondDim, Object secondDimNum) {
            this.firstDim = firstDim;
            this.secondDim = secondDim;
            this.secondDimNum = secondDimNum;
        }
    }
}
