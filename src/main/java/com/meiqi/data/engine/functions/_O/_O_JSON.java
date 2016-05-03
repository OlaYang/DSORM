package com.meiqi.data.engine.functions._O;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;
import com.meiqi.data.util.LogUtil;
import com.meiqi.data.util.Type;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-3-10
 * Time: 上午10:40
 * To change this template use File | Settings | File Templates.
 */
public class _O_JSON extends Function {
    public static final String NAME = _O_JSON.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }
        final String serviceName = DataUtil.getServiceName(args[0]);
        boolean needFilter = false; //默认不去重
        boolean isHasBoolean = false;
        Type type = DataUtil.getType(args[args.length - 1]);
        if (args[args.length - 1] instanceof Boolean) {
            needFilter = (Boolean) args[args.length - 1];
            isHasBoolean = true;
        }
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, calInfo.getParam(), NAME);
        ArrayList<String> alist = new ArrayList<String>();
        if (isHasBoolean) {
            for (int i = 1; i < args.length - 1; i++) {
                alist.add(DataUtil.getStringValue(args[i]));
            }
        } else {
            for (int i = 1; i < args.length; i++) {
                alist.add(DataUtil.getStringValue(args[i]));
            }
        }
        _Key Key = new _Key(alist);
        String result = (String) cache.get(Key);
        if (result == null) {
            result = init(calInfo, serviceName, calInfo.getParam()
                    , alist, NAME, needFilter);
            cache.put(Key, result);
        }

        return result;


    }

    class _Key {
        ArrayList<String> alist;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof _Key)) return false;

            _Key key = (_Key) o;

            if (alist != null ? !alist.equals(key.alist) : key.alist != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return alist != null ? alist.hashCode() : 0;
        }

        public _Key(ArrayList<String> alist) {
            this.alist = alist;
        }
    }

    static String init(CalInfo calInfo, String serviceName, Map<String, Object> current
            , ArrayList<String> args, String funcName, boolean needFilter) throws RengineException, CalculateError {
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }

        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, current,
                        calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), funcName);
        int size = 0;
        final Object[][] value = d2Data.getData();
        Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();
        for (int i = 0; i < args.size(); i++) {
            int colCalInt = DataUtil.getColumnIntIndex(args.get(i), d2Data.getColumnList());
            if (colCalInt == -1) {
                throw new ArgColumnNotFound(NAME, args.get(i));
            }
            ArrayList<Object> collist = new ArrayList<Object>();
            for (int j = 0; j < value.length; j++) {
                final Object colCalValue = value[j][colCalInt];
                if (colCalValue == null) {
                    collist.add("");
                } else {
                    collist.add(colCalValue);
                }

            }
            size = collist.size();
            map.put(args.get(i), collist);
        }
        if (needFilter) {
            Object[] keys = map.keySet().toArray();
            ArrayList<Integer> intList = new ArrayList<Integer>();
            Set<String> all = new HashSet<String>();
            for (int i = 0; i < size; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < keys.length; j++) {
                    sb.append(map.get(keys[j]).get(i));
                }
                if (all.contains(sb.toString())) {
                    intList.add(i);
                } else {
                    all.add(sb.toString());
                }
            }
            if (intList.size() != 0) { //有重复项
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    List<Object> list = map.get(key);
                    List<Object> newList = new ArrayList<Object>();

                    for (int i = 0; i < list.size(); i++) {

                        if (intList.contains(i)) {
                            continue;
                        } else {
                            newList.add(list.get(i));

                        }
                        map.put(key, newList);
                    }
                }
            }
        }
        if("wx".equals(servicePo.getType())){
        	StringBuilder sb=new StringBuilder();
        	for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
        		  sb.append(entry.getValue());
        	}
        	return sb.toString();
        }else{
        	return JSON.toJSONString(map);
        }


    }


}
