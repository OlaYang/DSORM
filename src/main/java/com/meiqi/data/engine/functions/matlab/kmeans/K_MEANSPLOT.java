package com.meiqi.data.engine.functions.matlab.kmeans;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.engine.functions.matlab.HttpUtil;
import com.meiqi.data.engine.functions.matlab.MatlabReqInfo;
import com.meiqi.data.engine.functions.matlab.MatlabRespInfo;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-25
 * Time: 下午1:08
 * To change this template use File | Settings | File Templates.
 */
public class K_MEANSPLOT extends Function {
    public static final String NAME = K_MEANSPLOT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        // args: 输入数列-输入数列[输入数列]-分隔符-k值-标题-另存为图片名称
        if (args.length < 6) {
            throw new ArgsCountError(NAME);
        }
        Integer index = 1;
        for (int i = 0; i < args.length; i++) {
            String flag = DataUtil.getStringValue(args[i]);
            if ("&".equals(flag)) {
                index = i+1;
                break;
            }
        }
        if (index < 3) {
            throw new RengineException(calInfo.getServiceName(), NAME + "无法匹配数列分隔符'&',指定数列不能少于2个");
        }
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < index - 1; i++) {
            list.add(args[i]);
        }
        Integer k = 2; //默认为2均值
        try {
            k = Integer.parseInt(DataUtil.getStringValue(args[index]));
        } catch (Exception e) {
            throw new RengineException(calInfo.getServiceName(), NAME + "k值输入格式不正确");
        }
        String title = DataUtil.getStringValue(args[index + 1]);
        if ("".equals(title)) {
            title = "k-means";
        }
        String imageName = DataUtil.getStringValue(args[index + 2]);
        if ("".equals(imageName)) {
            imageName = "k_means";
        }
        Map<Object, Object> cache = calInfo.getCache(NAME);
        __Key key = new __Key(list, k, title, imageName);
        String result = (String) cache.get(key);
        Map<String, List<Object>> data = new HashMap<String, List<Object>>();
        Integer size = 0; //记录每一列数据长度
        if (result == null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof ExcelRange) {
                    List<Object> dataArray = new ArrayList<Object>();
                    ExcelRange range = (ExcelRange) list.get(i);
                    Iterator<Object> ite = range.getIterator();
                    while (ite.hasNext()) {
                        Object tmp = ite.next();
                        try {
                            dataArray.add(DataUtil.getNumberValue(tmp).doubleValue());
                        } catch (Exception e) {
                            throw new RengineException(calInfo.getServiceName(), NAME + "输入数列不是数字类型");
                        }
                    }
                    if (dataArray.size() == 0) {
                        return StringPool.EMPTY;
                    }
                    if (i != 0) {
                        if (dataArray.size() != size) {
                            throw new RengineException(calInfo.getServiceName(), NAME + "数列长度不一致");
                        }
                    }
                    size = dataArray.size();
                    data.put(String.valueOf(i), dataArray);
                } else {
                    throw new RengineException(calInfo.getServiceName(), NAME + "输入数据不是数列");
                }
            }
            MatlabReqInfo reqInfo = new MatlabReqInfo();
            reqInfo.setFunctionName("KMEANS");
            reqInfo.setOperationType("plot");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("k", k);
            param.put("title", title);
            param.put("imageName", imageName);
            param.put("size",size);
            reqInfo.setParam(param);
            reqInfo.setData(data);
            try {
                String json = HttpUtil.getJsonFromData("/data/matlab.do", JSON.toJSONString(reqInfo));
                MatlabRespInfo respInfo = JSON.parseObject(json, MatlabRespInfo.class);
                String imagePath = respInfo.getImagePath();
                result = URLEncoder.encode(URLEncoder.encode(imagePath));
                cache.put(key, result);
            } catch (Exception e) {
                cache.put(key, StringPool.EMPTY);
            }
        }
        if (result == null) {
            return StringPool.EMPTY;
        } else {
            return result;
        }
    }

    class __Key {
        List<Object> list;
        Object k, title, imageName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (imageName != null ? !imageName.equals(key.imageName) : key.imageName != null)
                return false;
            if (k != null ? !k.equals(key.k) : key.k != null) return false;
            if (list != null ? !list.equals(key.list) : key.list != null)
                return false;
            if (title != null ? !title.equals(key.title) : key.title != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = list != null ? list.hashCode() : 0;
            result = 31 * result + (k != null ? k.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
            return result;
        }

        __Key(List<Object> list, Object k, Object title, Object imageName) {
            this.list = list;
            this.k = k;
            this.title = title;
            this.imageName = imageName;
        }
    }
}
