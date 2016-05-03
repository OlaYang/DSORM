package com.meiqi.data.engine.functions.matlab.hist;

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
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.data.po.TServiceColumnPo;

import java.net.URLEncoder;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-22
 * Time: 下午5:37
 * To change this template use File | Settings | File Templates.
 */
public class HISTPLOT extends Function {
    public static final String NAME = HISTPLOT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        //args: 数列- x轴名称 - 步长 - 标题 - 是否产生网格 -是否对原始数据进行变换(0,1,2,3) - 图片名称
        if (args.length < 7) {
            throw new ArgsCountError(NAME);
        }
        String xName = DataUtil.getStringValue(args[1]);
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

        Map<Object, Object> cache = calInfo.getCache(NAME);
        __Key key = new __Key(args[0], h, title, grid,drawModel,imageName);
        String result = (String) cache.get(key);
        if (result == null) {
            List<Object> data = new ArrayList<Object>();
            if (args[0] instanceof ExcelRange) {
                ExcelRange range = (ExcelRange) args[0];
                Iterator<Object> ite = range.getIterator();
                while (ite.hasNext()) {
                    Object tmp = ite.next();
                    try {
                        data.add(DataUtil.getNumberValue(tmp).doubleValue());
                    } catch (Exception e) {
                        throw new RengineException(calInfo.getServiceName(), NAME + "输入数列不是数字类型");
                    }
                }
                if (data.size() == 0) {
                    return StringPool.EMPTY;
                }
                List<TServiceColumn> columnPos = range.getData().getColumnList();
                if (columnPos == null || columnPos.size() == 0) {
                    throw new RengineException(calInfo.getServiceName(), NAME + "数据源没有列");
                }
            } else {
                throw new RengineException(calInfo.getServiceName(), NAME + "输入不是数列");
            }

            MatlabReqInfo reqInfo = new MatlabReqInfo();
            reqInfo.setFunctionName("HIST");
            reqInfo.setOperationType("plot");
            Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();
            map.put(xName, data);
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
        Object columnName, h, title, grid, drawModel, imageName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (columnName != null ? !columnName.equals(key.columnName) : key.columnName != null)
                return false;
            if (drawModel != null ? !drawModel.equals(key.drawModel) : key.drawModel != null)
                return false;
            if (grid != null ? !grid.equals(key.grid) : key.grid != null)
                return false;
            if (h != null ? !h.equals(key.h) : key.h != null) return false;
            if (imageName != null ? !imageName.equals(key.imageName) : key.imageName != null)
                return false;
            if (title != null ? !title.equals(key.title) : key.title != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = columnName != null ? columnName.hashCode() : 0;
            result = 31 * result + (h != null ? h.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (grid != null ? grid.hashCode() : 0);
            result = 31 * result + (drawModel != null ? drawModel.hashCode() : 0);
            result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
            return result;
        }

        __Key(Object columnName, Object h, Object title, Object grid,Object drawModel, Object imageName) {
            this.columnName = columnName;
            this.h = h;
            this.grid = grid;
            this.title = title;
            this.drawModel = drawModel;
            this.imageName = imageName;
        }


    }


}
