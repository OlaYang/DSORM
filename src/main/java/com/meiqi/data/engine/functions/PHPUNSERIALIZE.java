package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.util.LogUtil;
import com.meiqi.data.util.MD5Util;
import com.meiqi.data.util.PHPSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-6-14
 * Time: 下午7:37
 * To change this template use File | Settings | File Templates.
 */
public class PHPUNSERIALIZE extends Function {
    public static final String NAME = PHPUNSERIALIZE.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        try {
            if (args.length < 1) {
                throw new ArgsCountError(NAME);
            }

            String phpString = DataUtil.getStringValue(args[0]);
            List<String> columns = null;
            if (args.length > 1) {
                columns = new ArrayList<String>();
                for (int i = 1; i < args.length; i++) {
                    columns.add(DataUtil.getStringValue(args[i]));
                }
            }

            __Key key = new __Key(phpString, columns);
            Map<Object, Object> cache = calInfo.getCache(NAME);


            String result = (String) cache.get(key);
            if (result == null) {

                try {

                    String php_key = MD5Util.md5encode(phpString);
                    Object obj = (Object) cache.get(php_key);
                    if (obj == null) {
                        obj = (Object) PHPSerializer.unserialize(phpString.getBytes());
                        cache.put(php_key, obj);
                    }


                    if (args.length == 1) {
                        if (obj instanceof String) {
                            result = String.valueOf(obj);
                            cache.put(key, result);
                            return result;
                        } else {
                            result = JSON.toJSONString(obj);
                            cache.put(key, result);
                            return result;
                        }
                    } else {
                        if (obj instanceof List) {
                            List list = (List) obj;
                            obj = list.get(0);
                        }
                        if (obj instanceof Map) {
                            Map map = (Map) obj;
                            Object res = StringPool.EMPTY;
                            for (int i = 0; i < columns.size(); i++) {

                                res = map.get(columns.get(i));
                                if (res instanceof List) {
                                    List list = (List) res;
                                    res = list.get(0);
                                }
                                if (res instanceof Map) {
                                    if (i != columns.size() - 1) {
                                        map = (Map) res;
                                        continue;
                                    } else {
                                        result = JSON.toJSONString(res);
                                        cache.put(key, result);
                                        return result;
                                    }
                                } else {
                                    if (i != columns.size() - 1) {
                                        result = StringPool.EMPTY;
                                        cache.put(key, result);
                                        return result;
                                    } else {
                                        // result = JSON.toJSONString(res);
                                        result = String.valueOf(res);
                                        cache.put(key, result);
                                        return result;
                                    }
                                }
                            }
                            if (res == null) {
                                res = StringPool.EMPTY;
                            }
                            result = String.valueOf(res);
                            cache.put(key, result);
                            return result;

                        } else {
                            result = StringPool.EMPTY;
                            cache.put(key, result);
                            return result;
                        }

                    }
                } catch (Exception e) {
                    result = StringPool.EMPTY;
                    cache.put(key, result);
                    return result;
                }
            }

            return cache.get(key);
        } catch (Exception e) {
            LogUtil.error("", e);
            return "";
        }

    }


    class __Key {
        Object phpString, keyList;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (keyList != null ? !keyList.equals(key.keyList) : key.keyList != null)
                return false;
            if (phpString != null ? !phpString.equals(key.phpString) : key.phpString != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = phpString != null ? phpString.hashCode() : 0;
            result = 31 * result + (keyList != null ? keyList.hashCode() : 0);
            return result;
        }

        public __Key(Object phpString, Object keyList) {
            this.phpString = phpString;
            this.keyList = keyList;
        }
    }
}
