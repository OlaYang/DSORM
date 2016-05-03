package com.meiqi.data.engine.functions._O;

import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;
import com.meiqi.data.util.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-11
 * Time: 下午1:55
 * <hr> 分组计数 </hr>
 */
public class _O_CONCATBYGROUP extends Function {
    public static final String NAME = _O_CONCATBYGROUP.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 4) {
            throw new ArgsCountError(NAME);
        }

        final String reqKey = DataUtil.getStringValue(args[0]);
        final String serviceName = DataUtil.getServiceName(args[1]);
        final String colBy = DataUtil.getStringValue(args[2]);
        final String colCal = DataUtil.getStringValue(args[3]);

        boolean needFilter = false;
        if (args.length > 4 && DataUtil.getType(args[4]) == Type.BOOLEAN) {
            needFilter = (Boolean) args[4];
        }

        String flag = ",";
        if (args.length > 5) {
            flag = DataUtil.getStringValue(args[5]);
            if (flag.length() == 0) {
                flag = ",";
            }
        }


        final Map<String, Object> param = getParam(args, 6, calInfo.getParam(), false);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, param, NAME);

        __Key key = new __Key(colBy, colCal, needFilter, flag);
        Map<String, String> result = (Map<String, String>) cache.get(key);

        if (result == null) {
            result = init(calInfo, serviceName, param, colBy, colCal, needFilter, flag);
            cache.put(key, result);
        }

        String ret = result.get(reqKey);
        if (ret == null) {
            return StringPool.EMPTY;
        }

        return ret;
    }

    class __Key {
        Object colBy, colCal, needFilter, flag;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (colBy != null ? !colBy.equals(key.colBy) : key.colBy != null)
                return false;
            if (colCal != null ? !colCal.equals(key.colCal) : key.colCal != null)
                return false;
            if (flag != null ? !flag.equals(key.flag) : key.flag != null)
                return false;
            if (needFilter != null ? !needFilter.equals(key.needFilter) : key.needFilter != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = colBy != null ? colBy.hashCode() : 0;
            result = 31 * result + (colCal != null ? colCal.hashCode() : 0);
            result = 31 * result + (needFilter != null ? needFilter.hashCode() : 0);
            result = 31 * result + (flag != null ? flag.hashCode() : 0);
            return result;
        }

        __Key(Object colBy, Object colCal, Object needFilter, Object flag) {
            this.colBy = colBy;
            this.colCal = colCal;
            this.needFilter = needFilter;
            this.flag = flag;
        }
    }

    private Map<String, String> init(CalInfo calInfo, String serviceName
            , Map<String, Object> param, String colBy, String colCal
            , boolean needFilter, String flag) throws RengineException, CalculateError {

        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }

        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, param, calInfo.getCallLayer()
                        , calInfo.getServicePo(), calInfo.getParam(), NAME);

        Map<String, String> result = new HashMap<String, String>();
        Map<String, StringBuilder> strResult = new HashMap<String, StringBuilder>();
        Map<String, HashSet<String>> setResult = new HashMap<String, HashSet<String>>();
        final Object[][] value = d2Data.getData();
        int colByInt = DataUtil.getColumnIntIndex(colBy, d2Data.getColumnList());
        int colCalInt = DataUtil.getColumnIntIndex(colCal, d2Data.getColumnList());

        if (colByInt == -1) {
            throw new ArgColumnNotFound(NAME, colBy);
        }
        if (colCalInt == -1) {
            throw new ArgColumnNotFound(NAME, colCal);
        }

        if ("&char(10)&".equals(flag.toLowerCase())) {
            flag = "\n";
        }

        for (int i = 0; i < value.length; i++) {
            final Object colByValue = value[i][colByInt];
            final Object colCalValue = value[i][colCalInt];
            if (colByValue == null || colCalValue == null) {
                continue;
            }

            String _key = DataUtil.getStringValue(colByValue);
            String _value = DataUtil.getStringValue(colCalValue);

            HashSet<String> childs = setResult.get(_key);
            StringBuilder sb = strResult.get(_key);

            if (childs == null) {
                childs = new HashSet<String>();
                setResult.put(_key, childs);
            }

            if (sb == null) {
                sb = new StringBuilder();
                strResult.put(_key, sb);
            }

            if (needFilter) {
                if (!childs.contains(_value)) {
                    childs.add(_value);
                    sb.append(_value).append(flag);
                }
            } else {
                sb.append(_value).append(flag);
            }
        }

        for (Map.Entry<String, StringBuilder> entry : strResult.entrySet()) {
            StringBuilder sb = entry.getValue();
            final String str = sb.length() == 0 ? ""
                    : sb.substring(0, sb.length() - flag.length());

            result.put(entry.getKey(), str);
        }

        return result;
    }
}
