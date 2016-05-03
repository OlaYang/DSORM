package com.meiqi.data.engine.functions._O;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.Cache4_O_;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.util.LogUtil;
import com.meiqi.data.util.Type;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-3-10
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
public class _O_JSONBYPARA extends Function {
    public static final String NAME = _O_JSONBYPARA.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 5) {
            throw new ArgsCountError(NAME);
        }
        final String serviceName = DataUtil.getServiceName(args[0]);
        boolean isByParam = false;
        boolean needFilter = false;
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        int count=0;
        for(int i=2;i<args.length;i++){
            Type type = DataUtil.getType(args[i]);
            if (type == Type.BOOLEAN) {
                count ++;
                indexs.add(i);           //第一个布尔值标记为是否继承参数，第二个布尔值标记为是否去重，默认不去重
            }
        }

        if(count==1 ? true : (count==2 ? true : false)){

        }else{
            throw new IllegalArgumentException("需要指定是否继承参数,或函数输入参数有歧义");
        }


        if (args[indexs.get(0)] instanceof Boolean) {
            isByParam = (Boolean) args[indexs.get(0)];
        }
        if(indexs.size()==2){
            needFilter = (Boolean) args[indexs.get(1)];
        }

        final Map currentParam = getParam(args, indexs.get(indexs.size()-1)+1, calInfo.getParam(), isByParam);

        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);

        ArrayList<String> alist = new ArrayList<String>();
        for (int i = 1; i < indexs.get(0); i++) {
            alist.add(DataUtil.getStringValue(args[i]));
        }
        _Key Key = new _Key(alist);
        String result = (String) cache.get(Key);
        if (result == null) {
            result = _O_JSON.init(calInfo, serviceName, currentParam
                    , alist, NAME,needFilter);
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
}
