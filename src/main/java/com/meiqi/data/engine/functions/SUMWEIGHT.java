package com.meiqi.data.engine.functions;

import java.util.HashMap;
import java.util.Map;

import com.meiqi.app.common.utils.Arith;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.commons.util.StringUtils;

/**
 * 微信粉丝自动打标签 计算权重函数
* @ClassName: GETPOLYGONAREA 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhouyongxiong
* @date 2016年4月20日 下午10:45:20 
*
 */
public class SUMWEIGHT extends Function {

    public static final String NAME = SUMWEIGHT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (1 > args.length) {
            throw new ArgsCountError(NAME);
        }
        String result="";
        String param1=String.valueOf(args[0]);
        String param2=String.valueOf(args[1]);
        String param=param1+","+param2;
        String[] array=param.split(",");
        Map<String,Double> map=new HashMap<String, Double>();
        for(String str:array){
            String[] a=str.split("\\^");
            if(a.length==2){
                String key=a[0];
                String value=a[1];
                Double currentVallue=map.get(key);
                if(StringUtils.isNotEmpty(value)){
                    if(currentVallue!=null){
                        Double sum=Arith.add(currentVallue, Double.valueOf(value));
                        map.put(key, sum);
                    }else{
                        map.put(key, Double.valueOf(value));
                    }
                }
            }
        }
        StringBuffer lastResult=new StringBuffer();
        for(String key:map.keySet()){
            lastResult.append(key).append("^").append(map.get(key)).append(",");
        }
        if(lastResult.length()!=0){
            result=lastResult.toString().substring(0,lastResult.length()-1);
        }
        return result;
    }
}
