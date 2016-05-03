package com.meiqi.data.engine.functions.matlab.kmeans;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.engine.functions.matlab.HttpUtil;
import com.meiqi.data.engine.functions.matlab.MatlabReqInfo;
import com.meiqi.data.engine.functions.matlab.MatlabRespInfo;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;
import com.meiqi.data.util.LogUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-25
 * Time: 下午1:08
 * To change this template use File | Settings | File Templates.
 */
public class _O_K_MEANSPLOT extends Function {
    public static final String NAME = _O_K_MEANSPLOT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        // args: 数据源名称 - 列名称 - 列名称 [列名称...] - 分隔符-k值-标题-另存为图片名称 - 参数分隔符 - 是否继承参数 -参数值-参数名
        if(args.length<7){
            throw new ArgsCountError(NAME);
        }
        final String serviceName = DataUtil.getServiceName(args[0]);
        Integer index = 1;
        for (int i = 0; i < args.length; i++) {
            String flag = DataUtil.getStringValue(args[i]);
            if ("&".equals(flag)) {  //列名称分隔符
                index = i+1;
                break;
            }
        }
        if (index < 4) {
            throw new RengineException(calInfo.getServiceName(), NAME + "无法匹配数列分隔符'|',指定数列不能少于2个");
        }
        LogUtil.info("index:"+index);
        Map currentParam = calInfo.getParam();
        if(args.length>index+3){
            if(args.length<index+7){
                throw new RengineException(calInfo.getServiceName(), NAME + "参数个数不匹配");
            }
            String flag = DataUtil.getStringValue(args[index+3]);
            if (!"|".equals(flag)) {   //参数分隔符
                throw new RengineException(calInfo.getServiceName(), NAME + "参数分隔符'|'未找到");
            }
            boolean isByParam = false;
            if (args[index+4] instanceof Boolean) {
                isByParam = (Boolean) args[index+4];
            }
            currentParam = getParam(args, index+5, calInfo.getParam(), isByParam);
        }

        List<String> columnNames = new ArrayList<String>();
        for (int i = 1; i < index - 1; i++) {
            String columnName = DataUtil.getStringValue(args[i]);
            columnNames.add(columnName);
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

        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);
        __Key key = new __Key(columnNames,serviceName,k,title,imageName);
        String result = (String) cache.get(key);
        if (result == null) {
            result = init(calInfo, serviceName, currentParam, columnNames, k, title,imageName, NAME);
            cache.put(key, result);
        }
        return result;
    }

    static String init(CalInfo calInfo, String serviceName, Map<String, Object> current
            , List<String> columnNames, Integer k,String title, String imageName, String funcName) throws RengineException, CalculateError {
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }
        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, current,
                        calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), funcName);
        final Object[][] value = d2Data.getData();
        Map<String, List<Object>> data = new HashMap<String, List<Object>>();
        Integer size =0;
        for (String columnName : columnNames) {
            List<Object> columnData = new ArrayList<Object>();
            for (int j = 0; j < value.length; j++) {
                int colCalInt = DataUtil.getColumnIntIndex(columnName, d2Data.getColumnList());
                if (colCalInt == -1) {
                    throw new ArgColumnNotFound(NAME, columnName);
                } else {
                    final Object colCalValue = value[j][colCalInt];
                    if (canNumberOP(colCalValue)) {
                        columnData.add(colCalValue);
                    } else {
                        throw new RengineException(calInfo.getServiceName(), NAME + "输入数列不是数字类型");
                    }

                }
            }
            if(columnData.size()==0){  //去除空数据
                return StringPool.EMPTY;
            }
            if (!columnName.equals(columnNames.get(0))) {
                if (columnData.size() != size) {
                    throw new RengineException(calInfo.getServiceName(), NAME + "数列长度不一致");
                }
            }
            size = columnData.size();
            data.put(columnName, columnData);
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
            return URLEncoder.encode(URLEncoder.encode(imagePath));
        } catch (Exception e) {
            return StringPool.EMPTY;
        }

    }


    class __Key{
        List<String> columnNames;
        Object serviceName,k, title, imageName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (columnNames != null ? !columnNames.equals(key.columnNames) : key.columnNames != null)
                return false;
            if (imageName != null ? !imageName.equals(key.imageName) : key.imageName != null)
                return false;
            if (k != null ? !k.equals(key.k) : key.k != null) return false;
            if (serviceName != null ? !serviceName.equals(key.serviceName) : key.serviceName != null)
                return false;
            if (title != null ? !title.equals(key.title) : key.title != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = columnNames != null ? columnNames.hashCode() : 0;
            result = 31 * result + (serviceName != null ? serviceName.hashCode() : 0);
            result = 31 * result + (k != null ? k.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
            return result;
        }

        __Key(List<String> columnNames, Object serviceName,Object k, Object title,Object imageName){
            this.columnNames = columnNames;
            this.serviceName = serviceName;
            this.k = k;
            this.title = title;
            this.imageName = imageName;
        }
    }
}
