package com.meiqi.data.engine.functions.matlab.scatte3d;

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
import com.meiqi.data.util.LogUtil;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-25
 * Time: 上午9:21
 * To change this template use File | Settings | File Templates.
 */
public class SCATTE3DPLOT extends Function {
    public static final String NAME = SCATTE3DPLOT.class.getSimpleName(); //绘制3d散点图

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        // args: 数列1 - 数列2 -数列3- x轴名称 - y轴名称 -z轴名称- 标题 - 另存图片名称
        if (args.length < 8) {
            throw new ArgsCountError(NAME);
        }
        String xName = DataUtil.getStringValue(args[3]);
        String yName = DataUtil.getStringValue(args[4]);
        String zName = DataUtil.getStringValue(args[5]);
        if (xName.equals(yName) || yName.equals(zName)) {
            throw new RengineException(calInfo.getServiceName(), NAME + "轴名称重复");
        }
        String title = DataUtil.getStringValue(args[6]);
        if ("".equals(title)) {
            title = "三维散点图";
        }

        String imageName = DataUtil.getStringValue(args[7]);
        if ("".equals(imageName)) {
            imageName = "scatte3D";
        }

        Map<Object, Object> cache = calInfo.getCache(NAME);
        __Key key = new __Key(args[0], args[1], args[2], xName, yName, zName, title,imageName);
        String result = (String) cache.get(key);
        if (result == null) {
            List<Object> xData = new ArrayList<Object>();
            if (args[0] instanceof ExcelRange) {
                ExcelRange range = (ExcelRange) args[0];
                Iterator<Object> ite = range.getIterator();
                while (ite.hasNext()) {
                    Object tmp = ite.next();
                    try {
                        xData.add(DataUtil.getNumberValue(tmp).doubleValue());
                    } catch (Exception e) {
                        throw new RengineException(calInfo.getServiceName(), NAME + "输入数列不是数字类型");
                    }
                }
                if (xData.size() == 0) {
                    return StringPool.EMPTY;
                }
            } else {
                throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
            }

            List<Object> yData = new ArrayList<Object>();
            if (args[1] instanceof ExcelRange) {
                ExcelRange range = (ExcelRange) args[1];
                Iterator<Object> ite = range.getIterator();
                while (ite.hasNext()) {
                    Object tmp = ite.next();
                    try {
                        yData.add(DataUtil.getNumberValue(tmp).doubleValue());
                    } catch (Exception e) {
                        throw new RengineException(calInfo.getServiceName(), NAME + "输入数列不是数字类型");
                    }
                }
                if (yData.size() == 0) {
                    return StringPool.EMPTY;
                }
            } else {
                throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
            }

            if (xData.size() != yData.size()) {
                throw new RengineException(calInfo.getServiceName(), NAME + "输入的两个数列数目不一致");
            }

            List<Object> zData = new ArrayList<Object>();
            if (args[2] instanceof ExcelRange) {
                ExcelRange range = (ExcelRange) args[2];
                Iterator<Object> ite = range.getIterator();
                while (ite.hasNext()) {
                    Object tmp = ite.next();
                    try {
                        zData.add(DataUtil.getNumberValue(tmp).doubleValue());
                    } catch (Exception e) {
                        throw new RengineException(calInfo.getServiceName(), NAME + "输入数列不是数字类型");
                    }
                }
                if (zData.size() == 0) {
                    return StringPool.EMPTY;
                }
            } else {
                throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
            }

            if (zData.size() != yData.size()) {
                throw new RengineException(calInfo.getServiceName(), NAME + "输入的两个数列数目不一致");
            }



            MatlabReqInfo reqInfo = new MatlabReqInfo();
            reqInfo.setFunctionName("SCATTE3D");
            reqInfo.setOperationType("plot");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("xName", xName);
            param.put("yName", yName);
            param.put("zName", zName);
            param.put("title", title);
            param.put("imageName", imageName);
            reqInfo.setParam(param);
            Map<String, List<Object>> data = new HashMap<String, List<Object>>();
            data.put(xName, xData);
            data.put(yName, yData);
            data.put(zName, zData);
            reqInfo.setData(data);
            try {
                String json = HttpUtil.getJsonFromData("/data/matlab.do", JSON.toJSONString(reqInfo));
                MatlabRespInfo respInfo = JSON.parseObject(json, MatlabRespInfo.class);
                String imagePath = respInfo.getImagePath();
                result = URLEncoder.encode(URLEncoder.encode(imagePath));
                cache.put(key, result);
            } catch (Exception e) {
                LogUtil.error("", e);
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
        Object xRange, yRange, zRange, xName, yName, zName, title, imageName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (imageName != null ? !imageName.equals(key.imageName) : key.imageName != null)
                return false;
            if (title != null ? !title.equals(key.title) : key.title != null)
                return false;
            if (xName != null ? !xName.equals(key.xName) : key.xName != null)
                return false;
            if (xRange != null ? !xRange.equals(key.xRange) : key.xRange != null)
                return false;
            if (yName != null ? !yName.equals(key.yName) : key.yName != null)
                return false;
            if (yRange != null ? !yRange.equals(key.yRange) : key.yRange != null)
                return false;
            if (zName != null ? !zName.equals(key.zName) : key.zName != null)
                return false;
            if (zRange != null ? !zRange.equals(key.zRange) : key.zRange != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = xRange != null ? xRange.hashCode() : 0;
            result = 31 * result + (yRange != null ? yRange.hashCode() : 0);
            result = 31 * result + (zRange != null ? zRange.hashCode() : 0);
            result = 31 * result + (xName != null ? xName.hashCode() : 0);
            result = 31 * result + (yName != null ? yName.hashCode() : 0);
            result = 31 * result + (zName != null ? zName.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
            return result;
        }

        __Key(Object xRange, Object yRange, Object zRange, Object xName, Object yName, Object zName, Object title, Object imageName) {
            this.xRange = xRange;
            this.yRange = yRange;
            this.zRange = zRange;
            this.xName = xName;
            this.yName = yName;
            this.zName = zName;
            this.title = title;
            this.imageName = imageName;
        }
    }
}
