package com.meiqi.data.engine.functions.matlab.scatte;

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
 * Date: 14-4-24
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public class SCATTEPLOT extends Function {   //本表绘制散点图
    public static final String NAME = SCATTEPLOT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        // args: 数列1 - 数列2 - x轴名称 - y轴名称 - 标题 - 是否加网格（默认加网格）- 另存图片名称
        if (args.length < 7) {
            throw new ArgsCountError(NAME);
        }
        String xName = DataUtil.getStringValue(args[2]);
        String yName = DataUtil.getStringValue(args[3]);
        if (xName.equals(yName)) {
            throw new RengineException(calInfo.getServiceName(), NAME + "横轴和纵轴名称重复");
        }
        String title = DataUtil.getStringValue(args[4]);
        if ("".equals(title)) {
            title = "二维散点图";
        }
        Integer grid = 1; //默认加网格
        if (args[5] instanceof Boolean) {
            if ((Boolean) args[5] == false) {
                grid = 0;
            }
        }
        String imageName = DataUtil.getStringValue(args[6]);
        if ("".equals(imageName)) {
            imageName = "scatte";
        }

        Map<Object, Object> cache = calInfo.getCache(NAME);
        __Key key = new __Key(args[0], args[1], xName, yName, title, grid, imageName);
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

            //整理二维数据

            MatlabReqInfo reqInfo = new MatlabReqInfo();
            reqInfo.setFunctionName("SCATTE");
            reqInfo.setOperationType("plot");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("xName", xName);
            param.put("yName", yName);
            param.put("title", title);
            param.put("grid", grid);
            param.put("imageName", imageName);
            reqInfo.setParam(param);
            Map<String,List<Object>> data = new HashMap<String, List<Object>>();
            data.put(xName,xData);
            data.put(yName,yData);
            reqInfo.setData(data);
            try {
                String json = HttpUtil.getJsonFromData("/data/matlab.do", JSON.toJSONString(reqInfo));
                MatlabRespInfo respInfo = JSON.parseObject(json, MatlabRespInfo.class);
                String imagePath = respInfo.getImagePath();
                result = URLEncoder.encode(URLEncoder.encode(imagePath));
                cache.put(key, result);
            } catch (Exception e) {
                LogUtil.error("",e);
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
        Object xRange, yRange, xName, yName, title, grid, imageName;

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
            if (xRange != null ? !xRange.equals(key.xRange) : key.xRange != null)
                return false;
            if (yName != null ? !yName.equals(key.yName) : key.yName != null)
                return false;
            if (yRange != null ? !yRange.equals(key.yRange) : key.yRange != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = xRange != null ? xRange.hashCode() : 0;
            result = 31 * result + (yRange != null ? yRange.hashCode() : 0);
            result = 31 * result + (xName != null ? xName.hashCode() : 0);
            result = 31 * result + (yName != null ? yName.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (grid != null ? grid.hashCode() : 0);
            result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
            return result;
        }

        __Key(Object xRange, Object yRange, Object xName, Object yName, Object title, Object grid, Object imageName) {
            this.xRange = xRange;
            this.yRange = yRange;
            this.xName = xName;
            this.yName = yName;
            this.title = title;
            this.grid = grid;
            this.imageName = imageName;
        }
    }
}
