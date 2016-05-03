package com.meiqi.data.engine.functions.matlab.scatte3d;

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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-25
 * Time: 上午9:38
 * To change this template use File | Settings | File Templates.
 */
public class _O_SCATTE3DPLOT extends Function {
    public static final String NAME = _O_SCATTE3DPLOT.class.getSimpleName(); //跨表绘制3d散点图

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        //args: 数据源名称-x列名称-y列名称-z列名称- 标题 - 另存图片名称 -参数分隔符- 是否继承参数 - 参数值 - 参数名
        if (args.length < 6) {
            throw new ArgsCountError(NAME);
        }
        final String serviceName = DataUtil.getServiceName(args[0]);
        Map currentParam = calInfo.getParam();
        if (args.length >= 7) {   //跨表带参数
            if (args.length < 10) {
                throw new RengineException(calInfo.getServiceName(), NAME + "参数个数不匹配");
            }
            String flag = DataUtil.getStringValue(args[7]);
            if (!"|".equals(flag)) {
                throw new RengineException(calInfo.getServiceName(), NAME + "参数分隔符'|'未找到");
            }
            boolean isByParam = false;
            if (args[8] instanceof Boolean) {
                isByParam = (Boolean) args[8];
            }
            currentParam = getParam(args, 9, calInfo.getParam(), isByParam);
        }
        final String xName = DataUtil.getStringValue(args[1]);
        final String yName = DataUtil.getStringValue(args[2]);
        final String zName = DataUtil.getStringValue(args[3]);
        if (xName.equals(yName) || yName.equals(zName)) {
            throw new RengineException(calInfo.getServiceName(), NAME + "列名称重复");
        }
        String title = DataUtil.getStringValue(args[4]);
        if ("".equals(title)) {
            title = "三维散点图";
        }

        String imageName = DataUtil.getStringValue(args[5]);
        if ("".equals(imageName)) {
            imageName = "_o_scatte3d";
        }
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);
        __Key key = new __Key(xName, yName, zName, title, imageName);

        String result = (String) cache.get(key);
        if (result == null) {
            result = init(calInfo, serviceName, currentParam, xName, yName, zName, title, imageName, NAME);
            cache.put(key, result);
        }
        return result;

    }

    static String init(CalInfo calInfo, String serviceName, Map<String, Object> current
            , String xName, String yName, String zName, String title, String imageName, String funcName) throws RengineException, CalculateError {
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }
        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, current,
                        calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), funcName);
        final Object[][] value = d2Data.getData();
        Map<String, List<Object>> data = new HashMap<String, List<Object>>();
        String[] columnNames = new String[]{xName, yName, zName};
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
            if(columnData.size()==0){
                return StringPool.EMPTY;     //去除空数据
            }
            data.put(columnName, columnData);
        }
        if (data.get(xName).size() == 0 || data.get(xName).size() != data.get(yName).size()) {
            throw new RengineException(calInfo.getServiceName(), NAME + "输入两列数长度不一致");
        }
        MatlabReqInfo reqInfo = new MatlabReqInfo();
        reqInfo.setFunctionName("SCATTE3D");
        reqInfo.setOperationType("plot");
        reqInfo.setData(data);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("xName", xName);
        param.put("yName", yName);
        param.put("zName", zName);
        param.put("title", title);
        param.put("imageName", imageName);
        reqInfo.setParam(param);
        try {
            String json = HttpUtil.getJsonFromData("/data/matlab.do", JSON.toJSONString(reqInfo));
            MatlabRespInfo respInfo = JSON.parseObject(json, MatlabRespInfo.class);
            String imagePath = respInfo.getImagePath();
            return URLEncoder.encode(URLEncoder.encode(imagePath));
        } catch (Exception e) {
            return StringPool.EMPTY;
        }
    }

    class __Key {
        Object xName, yName, zName, title, grid, imageName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (grid != null ? !grid.equals(key.grid) : key.grid != null)
                return false;
            if (imageName != null ? !imageName.equals(key.imageName) : key.imageName != null)
                return false;
            if (title != null ? !title.equals(key.title) : key.title != null)
                return false;
            if (xName != null ? !xName.equals(key.xName) : key.xName != null)
                return false;
            if (yName != null ? !yName.equals(key.yName) : key.yName != null)
                return false;
            if (zName != null ? !zName.equals(key.zName) : key.zName != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = xName != null ? xName.hashCode() : 0;
            result = 31 * result + (yName != null ? yName.hashCode() : 0);
            result = 31 * result + (zName != null ? zName.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (grid != null ? grid.hashCode() : 0);
            result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
            return result;
        }

        __Key(Object xName, Object yName, Object zName, Object title, Object imageName) {
            this.xName = xName;
            this.yName = yName;
            this.zName = zName;
            this.title = title;
            this.imageName = imageName;
        }
    }
}
