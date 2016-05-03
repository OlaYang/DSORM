package com.meiqi.data.engine.functions;


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
 * Date: 14-4-16
 * Time: 上午9:26
 * To change this template use File | Settings | File Templates.
 */
public class MODE extends Function {
    static final String NAME = MODE.class.getSimpleName();



    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }


        Map<Object, Object> cache = calInfo.getCache(NAME);


        // __Key key = new __Key(args);
        // String key = MD5Util.md5encode(JSON.toJSONString(args));
        // LogUtil.info("key:"+ MD5Util.md5encode(JSON.toJSONString(key)));

        // String key = "tttt";
        //  Object obj = cache.get(key);
        Object obj = null;
        if (obj == null) {
            List<Double> numbers = new ArrayList<Double>();
            for (Object arg : args) {
                if (arg instanceof ExcelRange) {
                    ExcelRange range = (ExcelRange) arg;


                    List<Double> result = (List<Double>) cache.get(range);
                    if (result == null) {

                        result = new ArrayList<Double>();
                        Iterator<Object> ite = range.getIterator();
                        while (ite.hasNext()) {
                            Object tmp = ite.next();
                            if (tmp != null && canNumberOP(tmp)) {
                                result.add(DataUtil.getNumberValue(tmp).doubleValue());
                            }
                        }

                        cache.put(range, result);
                    } else {
                     //   LogUtil.debug("命中数列缓存");
                    }

                    numbers.addAll(result);
                } else if (arg != null && canNumberOP(arg)) {
                    numbers.add(DataUtil.getNumberValue(arg).doubleValue());
                }
            }

            if (numbers.size() == 0) {
                return StringPool.EMPTY;
            } else {

                Collections.sort(numbers);
                Integer count = 1; //记录出现次数
                Integer longest = 0;  //重数
                double mode = 0;
                int temp = 0;
                for (int i = 0; i < numbers.size() - 1; i++) {
                    if (numbers.get(i).doubleValue() == numbers.get(i + 1).doubleValue()) {
                        count++;
                    } else {
                        count = 1;//如果不等于，就换到了下一个数，那么计算下一个数的次数时，count的值应该重新符值为1
                        continue;
                    }
                    if (count > longest) {
                        mode = numbers.get(i);
                        longest = count;
                        temp = i;
                    }
                }
                Double MODE = numbers.get(temp);

              //  LogUtil.debug("出现次数" + longest);//打印出这个数出现的次数已判断是否正确
                if (longest == 0 || longest == 1) {
                    // cache.put(key, StringPool.EMPTY);
                    return StringPool.EMPTY;

                } else {
                //    LogUtil.debug("众数是" + MODE);
                    // cache.put(key, MODE);
                    return MODE;
                }
                // return cache.get(key);

            }
        } else {
           // LogUtil.info("命中缓存");
            return obj;
        }


    }


}
