package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.util.PinyinUtil;

import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-11-13
 * Time: 上午10:47
 * To change this template use File | Settings | File Templates.
 */
public class PINYIN extends Function {
    static final String NAME = PINYIN.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }
        final String source = DataUtil.getStringValue(args[0]);

        Map<Object, Object> cache = calInfo.getCache(NAME);
        __Key key = new __Key(source);
        Object pinyin = cache.get(key);
        if(pinyin==null){
            if (source.matches("[\\w\\?%&=\\-_#*.]+")) {
                pinyin = source;
            } else {
                pinyin = PinyinUtil.getDefaultPinyin(source);
            }
            cache.put(key,pinyin);
        }
        return pinyin;
    }

    class __Key {
        Object source;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (source != null ? !source.equals(key.source) : key.source != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return source != null ? source.hashCode() : 0;
        }

        __Key(Object source) {
            this.source = source;
        }
    }



}
