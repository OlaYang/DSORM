package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.IKDictionary;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.util.LogUtil;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-7-4
 * Time: 下午2:37
 * To change this template use File | Settings | File Templates.
 */
public class FREQUENT extends Function {
    static final String NAME = FREQUENT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        //目标数列,目标文本,竖线分隔符,分词策略,统计指定关键词组(可逗号拼接)
        if (args.length < 4) {
            throw new ArgsCountError(NAME);
        }
        try {
            int frequent = 0;   // 频率最高词出现的次数
            Object wordFreq = "";  //频率最高的词
            Map<Object, Object> cache = calInfo.getCache(NAME);
            List<Object> objText = new ArrayList<Object>();
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    continue;
                } else if (arg instanceof ExcelRange) {
                    ExcelRange range = (ExcelRange) arg;
                    List<Object> rangeList = (List<Object>) cache.get(range);
                    if (rangeList == null) {
                        rangeList = new ArrayList<Object>();
                        Iterator<Object> ite = range.getIterator();
                        while (ite.hasNext()) {
                            Object value = ite.next();
                            if (value != null) {
                                rangeList.add(value);
                            }
                        }
                        cache.put(range, rangeList);
                    }
                    objText.addAll(rangeList);
                } else {
                    if ("|".equals(DataUtil.getStringValue(arg))) {
                        index = i;  // 确定分隔符的位置
                        break;
                    } else {
                        objText.add(arg);
                    }
                }
            }
            String strategy = "1";  // 智能分词 , 0表示按最小量分词
            strategy = DataUtil.getStringValue(args[index + 1]);
            boolean isStrategy;
            if ("1".equals(strategy)) {
                // 智能分词
                isStrategy = true;
            } else if ("0".equals(strategy)) {
                isStrategy = false; // 最小粒度分词
            } else {
                throw new RengineException(calInfo.getServiceName(), NAME + "无法识别分词策略");
            }

            __Key key = new __Key(objText, strategy);

            Multimap<Object, Object> splitTextMap = (Multimap<Object, Object>) cache.get(key);

            if (splitTextMap == null) {
                splitTextMap = HashMultimap.create();
                Reader input = new StringReader(Joiner.on(",").join(objText));
                IKSegmenter iks = new IKSegmenter(input, isStrategy); // 智能分词
                Lexeme lexeme = null;
                Dictionary dictionary = Dictionary.getSingleton();
                dictionary.addWords(IKDictionary.getDictionary());
                AtomicLong k = new AtomicLong();
                try {
                    while ((lexeme = iks.next()) != null) {
                        String text = lexeme.getLexemeText();
                      //  LogUtil.info("text:" + text);
                        splitTextMap.put(text, k.incrementAndGet());
                    }
                    cache.put(key, splitTextMap);
                } catch (Exception e) {
                    throw new RengineException(calInfo.getServiceName(), NAME + "分词失败");
                }
            }


            if (index == 0 || index == args.length - 1) {
                // 智能
                return StringPool.EMPTY;
            } else {
                // 按指定关键词返回出现频率最高的组合
                // 获取指定关键词集合
                Set<String> keywords = new HashSet<String>();
                for (int i = index + 2; i < args.length; i++) {
                    String oneKeys = DataUtil.getStringValue(args[i]);
                    Iterator<String> iterator = Splitter.on(",").omitEmptyStrings().trimResults().split(oneKeys).iterator();
                    while (iterator.hasNext()) {
                        keywords.add(iterator.next());
                    }
                }
                Iterator<String> it = keywords.iterator();

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
              //  word.put(wordFreq, frequent);
               // return JSON.toJSONString(word);
                //return JSON.toJSONString(word, SerializerFeature.QuoteFieldNames);
               return "{\""+wordFreq+"\":\""+frequent+"\""+"}";

            }
        } catch (Exception e) {
            LogUtil.error("", e);
            return StringPool.EMPTY;
        }

    }

    class __Key {
        List<Object> textList;
        Object strategy;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (strategy != null ? !strategy.equals(key.strategy) : key.strategy != null)
                return false;
            if (textList != null ? !textList.equals(key.textList) : key.textList != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = textList != null ? textList.hashCode() : 0;
            result = 31 * result + (strategy != null ? strategy.hashCode() : 0);
            return result;
        }

        public __Key(List<Object> textList, Object strategy) {
            this.textList = textList;
            this.strategy = strategy;
        }
    }
}
