package com.meiqi.data.engine.functions.matlab.hist;

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
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-21
 * Time: 上午11:48
 * To change this template use File | Settings | File Templates.
 */
public class _O_HISTPLOT extends Function {   //直方图函数
    public static final String NAME = _O_HISTPLOT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        //args:  数据源名称 - 列名称 - 步长 - 标题 - 是否产生网格 -是否对原始数据进行变换(0,1,2,3)- 图片名称
        if (args.length < 7) {
            throw new ArgsCountError(NAME);
        }
        final String serviceName = DataUtil.getServiceName(args[0]);
        final String columnName = DataUtil.getStringValue(args[1]);
        double h = 0;
        try {
            if (!"".equals(args[2])) {
                h = Double.parseDouble(DataUtil.getStringValue(args[2]));
            }
        } catch (Exception e) {
            throw new RengineException(calInfo.getServiceName(), NAME + "步长输入格式不正确");
        }
        String title = DataUtil.getStringValue(args[3]);
        if ("".equals(title)) {
            title = "hist_picture";
        }
        Integer grid = 0;
        if (args[4] instanceof Boolean) {
            if ((Boolean) args[4] == true) {
                grid = 1;
            }
        }

        double drawModel = 0;
        try {
            if (!"".equals(args[5])) {
                drawModel = Double.parseDouble(DataUtil.getStringValue(args[5]));
                if(drawModel>3 || drawModel<0){
                    throw new RengineException(calInfo.getServiceName(), NAME + "是否对原始数据进行变换输入仅支持0,1,2,3");
                }
            }
        } catch (Exception e) {
            throw new RengineException(calInfo.getServiceName(), NAME + "输入原始数据变换格式不正确");
        }

        String imageName = DataUtil.getStringValue(args[6]);
        if ("".equals(imageName)) {
            imageName = "hist";
        }

        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, calInfo.getParam(), NAME);
        ArrayList<String> alist = new ArrayList<String>();
        for (int i = 1; i < args.length; i++) {
            alist.add(DataUtil.getStringValue(args[i]));
        }
        __Key key = new __Key(alist);
        String result = (String) cache.get(key);
        if (result == null) {
            result = init(calInfo, serviceName, calInfo.getParam(), columnName, h, title, grid,drawModel, imageName, NAME);
            cache.put(key, result);
        }

        return result;
    }

    static String init(CalInfo calInfo, String serviceName, Map<String, Object> current
            , String columnName, double h, String title, Integer grid,double drawModel, String imageName, String funcName) throws RengineException, CalculateError {
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }
        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, current,
                        calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), funcName);
        final Object[][] value = d2Data.getData();
        //保存列的数据
        Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();
        {
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
            map.put(columnName, columnData);
            if(columnData.size()==0){  //过滤空数据
                return StringPool.EMPTY;
            }


            MatlabReqInfo reqInfo = new MatlabReqInfo();
            reqInfo.setFunctionName("HIST");
            reqInfo.setOperationType("plot");
            reqInfo.setData(map);
            Map<String, Object> matlabParam = new HashMap<String, Object>();
            matlabParam.put("h", h);
            matlabParam.put("title", title);
            matlabParam.put("grid", grid);
            matlabParam.put("drawModel",drawModel);
            matlabParam.put("imageName", imageName);
            reqInfo.setParam(matlabParam);
            try {
                String json = HttpUtil.getJsonFromData("/data/matlab.do", JSON.toJSONString(reqInfo));
                MatlabRespInfo respInfo = JSON.parseObject(json, MatlabRespInfo.class);
                String imagePath = respInfo.getImagePath();
                return URLEncoder.encode(URLEncoder.encode(imagePath));
            } catch (Exception e) {
                return StringPool.EMPTY;
            }

        }
    }

    class __Key {
        ArrayList<String> alist;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (alist != null ? !alist.equals(key.alist) : key.alist != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return alist != null ? alist.hashCode() : 0;
        }

        public __Key(ArrayList<String> alist) {
            this.alist = alist;
        }
    }
}

