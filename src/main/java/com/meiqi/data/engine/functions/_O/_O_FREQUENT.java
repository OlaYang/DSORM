package com.meiqi.data.engine.functions._O;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;
import com.meiqi.data.util.LogUtil;
import com.meiqi.data.util.Type;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.Dictionary;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-7-10
 * Time: 下午7:31
 * To change this template use File | Settings | File Templates.
 */
public class _O_FREQUENT extends Function {
    public static final String NAME = _O_FREQUENT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        // args: 数据源名称-列名称-[列名称]-分隔符-统计指定关键词组(可逗号拼接)-分隔符-分词策略-[是否继承参数-参数值-参数名]
        final String serviceName = DataUtil.getServiceName(args[0]);
        List<String> columnNames = new ArrayList<String>();
        boolean isByParam = false;
        String strategy = "1";  // 智能分词 , 0表示按最小量分词

        int index = -1;
        int firstSplit = -1;
        int lastSplit = -1;
        for (int i = 1; i < args.length; i++) {
            Type type = DataUtil.getType(args[i]);
            if (type == Type.BOOLEAN) {
                index = i;          // 获取是否继承参数的位数，-1表示不带参数
                isByParam = (Boolean) args[i];
                break;
            }
        }
        for (int i = 1; i < args.length; i++) {
            if ("|".equals(DataUtil.getStringValue(args[i]))) {
                firstSplit = i;
                for (int j = i + 1; j < args.length; j++) {
                    if ("|".equals(DataUtil.getStringValue(args[j]))) {
                        lastSplit = j;
                        break;
                    }
                }
                break;
            }
        }
        for (int i = 1; i < firstSplit; i++) {
            columnNames.add(DataUtil.getStringValue(args[i]));  // 列名称
        }

        Set<String> keywords = new HashSet<String>();  // 关键词
        for (int i = firstSplit + 1; i < lastSplit; i++) {
            String oneKeys = DataUtil.getStringValue(args[i]);
            Iterator<String> iterator = Splitter.on(",").omitEmptyStrings().trimResults().split(oneKeys).iterator();
            while (iterator.hasNext()) {
                String keyword = iterator.next();
                keywords.add(keyword);
            }
        }
        strategy = DataUtil.getStringValue(args[lastSplit + 1]);

        boolean isStrategy;
        if ("1".equals(strategy)) {
            // 智能分词
            isStrategy = true;
        } else if ("0".equals(strategy)) {
            isStrategy = false; // 最小粒度分词
        } else {
            throw new RengineException(calInfo.getServiceName(), NAME + "无法识别分词策略");
        }


        if (keywords.size() == 0) {
            // 无需关键词,智能处理,后续开发
            return StringPool.EMPTY;
        } else {
            Map currentParam = calInfo.getParam();
            if (index == -1) {
                //不带参数
            } else {
                currentParam = getParam(args, index + 1, calInfo.getParam(), isByParam);
            }

            Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);
            __Key key = new __Key(columnNames, strategy, isByParam, keywords);
            String result = (String) cache.get(key);
            if (result == null) {
                result = init(calInfo, serviceName, currentParam, columnNames, keywords, NAME, isStrategy);
                cache.put(key, result);
            }
            return result;

        }

    }

    static String init(CalInfo calInfo, String serviceName, Map<String, Object> current
            , List<String> columnNames, Set<String> keywords, String funcName, boolean strategy) throws RengineException, CalculateError {
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }

        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, current,
                        calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), funcName);
        final Object[][] value = d2Data.getData();
        ArrayList<Object> objText = new ArrayList<Object>();    // 目标文本集合
        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            int colCalInt = DataUtil.getColumnIntIndex(columnName, d2Data.getColumnList());
            if (colCalInt == -1) {
                throw new ArgColumnNotFound(NAME, columnName);
            }
            ArrayList<Object> columnData = new ArrayList<Object>();
            for (int j = 0; j < value.length; j++) {
                final Object colCalValue = value[j][colCalInt];
                if (colCalValue == null) {
                    //
                } else {
                    columnData.add(colCalValue);
                }

            }
            objText.addAll(columnData);
        }

      //  LogUtil.info("text:"+JSON.toJSONString(objText));
        Multimap<Object, Object> splitTextMap = HashMultimap.create();
        Reader input = new StringReader(Joiner.on(",").join(objText));
        IKSegmenter iks = new IKSegmenter(input, strategy);

        Lexeme lexeme = null;
        org.wltea.analyzer.dic.Dictionary dictionary = org.wltea.analyzer.dic.Dictionary.getSingleton();
        dictionary.addWords(IKDictionary.getDictionary());
        AtomicLong k = new AtomicLong();
        try {
            while ((lexeme = iks.next()) != null) {
                String text = lexeme.getLexemeText();
              //  LogUtil.info("text:"+text);
                splitTextMap.put(text, k.incrementAndGet());
            }
        } catch (IOException e) {
            throw new RengineException(calInfo.getServiceName(), NAME + "分词失败");
        }
        // 按指定关键词返回出现频率最高的组合
        // 获取指定关键词集合
        Iterator<String> it = keywords.iterator();
        int frequent = 0;   // 频率最高词出现的次数
        Object wordFreq = "";  //频率最高的词
        while (it.hasNext()) {
            Object keyword = it.next();
            Collection collection = splitTextMap.get(keyword);
            if (collection == null) {
                continue;
            }
            int freq = collection.size();
            if (freq < frequent) {
                continue;
            }
            frequent = freq;
            wordFreq = keyword;
        }

        if (frequent == 0) {
            return StringPool.EMPTY;
        }
        // Map<Object, Object> word = new HashMap<Object, Object>();
        // word.put(wordFreq, frequent);
        // return JSON.toJSONString(word);
        return "{\"" + wordFreq + "\":\"" + frequent + "\"" + "}";

    }

    class __Key {
        Object columnNames, strategy, isByParam, keywords;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (columnNames != null ? !columnNames.equals(key.columnNames) : key.columnNames != null)
                return false;
            if (isByParam != null ? !isByParam.equals(key.isByParam) : key.isByParam != null)
                return false;
            if (keywords != null ? !keywords.equals(key.keywords) : key.keywords != null)
                return false;
            if (strategy != null ? !strategy.equals(key.strategy) : key.strategy != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = columnNames != null ? columnNames.hashCode() : 0;
            result = 31 * result + (strategy != null ? strategy.hashCode() : 0);
            result = 31 * result + (isByParam != null ? isByParam.hashCode() : 0);
            result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
            return result;
        }

        public __Key(Object columnNames, Object strategy, Object isByParam, Object keywords) {
            this.columnNames = columnNames;
            this.isByParam = isByParam;
            this.keywords = keywords;
            this.strategy = strategy;
        }
    }
}
