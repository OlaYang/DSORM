package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-5-6
 * Time: 下午3:34
 * To change this template use File | Settings | File Templates.
 */
public class SUBSTITUTEMORE extends Function {
    static final String NAME = SUBSTITUTEMORE.class.getSimpleName(); //多条件字符串替换函数
    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        // args : 文本, 旧文本, 新文本，替换第几次旧文本 , [旧文本,新文本,替换第几次旧文本]
        if (args.length < 4) {
            throw new ArgsCountError(NAME);
        }
        String text = DataUtil.getStringValue(args[0]);
        List<ConcurrentHashMap<String,String>> list = new ArrayList<ConcurrentHashMap<String,String>>();
        for(int i=1;i< args.length;){
            if(i+3> args.length){
               break;
            }
            ConcurrentHashMap<String,String> map =new ConcurrentHashMap<String, String>();
            map.put("oldText",DataUtil.getStringValue(args[i]));
            map.put("newText",DataUtil.getStringValue(args[i+1]));
            String index = DataUtil.getStringValue(args[i+2]);
            try{
                Integer.parseInt(index);
            }catch (Exception e){
                throw new RengineException(calInfo.getServiceName(), NAME + "替换第几次参数格式不正确");
            }
            map.put("index",index);
            list.add(map);
            i = i+3;
        }

        for(int i=0;i<list.size();i++){
            ConcurrentHashMap<String,String> map = list.get(i);
            text = replace(text,map.get("oldText"),map.get("newText"),Integer.parseInt(map.get("index")));
        }

        return text;
    }

    public static String replace(String text , String oldText, String newText , Integer index){
        if(index == -1){  // -1表示全部替换
            return text.replace(oldText, newText);
        } else {
            if (index == 0) {
                index = 1;
            }
            int flag = text.indexOf(oldText);
            int count = 0;
            while (flag != -1) {
                count++;
                if (count == index) {
                    return text.substring(0, flag) + newText + text.substring(flag + oldText.length());
                }
                flag = text.indexOf(oldText, flag + 1);
            }

            return text;
        }
    }

}
