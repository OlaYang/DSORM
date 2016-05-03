package com.meiqi.data.engine.functions.matlab.hist;


import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.Cache4_O_;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;



import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-22
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class _O_HISTBYPARAPLOT extends Function { //带参数直方图函数
    public static final String NAME = _O_HISTBYPARAPLOT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 8) {
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
                if (drawModel > 3 || drawModel < 0) {
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
        boolean isByParam = false;
        if (args[7] instanceof Boolean) {
            isByParam = (Boolean) args[7];
        }
        final Map currentParam = getParam(args, 8, calInfo.getParam(), isByParam);
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);
        __Key key = new __Key(columnName, h, title, grid, drawModel, imageName);
        String result = (String) cache.get(key);
        if (result == null) {
            result = _O_HISTPLOT.init(calInfo, serviceName, currentParam, columnName, h, title, grid, drawModel, imageName, NAME);
            cache.put(key, result);
        }
        return result;
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

        __Key(Object columnName, Object h, Object title, Object grid, Object drawModel, Object imageName) {
            this.columnName = columnName;
            this.h = h;
            this.grid = grid;
            this.title = title;
            this.drawModel = drawModel;
            this.imageName = imageName;
        }
    }
}
