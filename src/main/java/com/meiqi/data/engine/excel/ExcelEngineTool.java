package com.meiqi.data.engine.excel;

import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.parser.ExcelParser;
import com.meiqi.data.engine.excel.parser.ExcelTool;
import com.meiqi.data.entity.TServiceColumn;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static com.meiqi.data.engine.DataUtil.getType;
import static com.meiqi.data.util.Type.BOOLEAN;

/**
 * User: 
 * Date: 13-10-31
 * Time: 下午1:26
 */
public class ExcelEngineTool {
    private static final ConcurrentHashMap<String, ParseTreeInfo> cache
            = new ConcurrentHashMap<String, ParseTreeInfo>();


    /**
     * 往d2data里填充数据, calInfo是实时的计算信息, 含有当前计算的行和列号
     *
     * @param d2Data
     * @param calInfo
     * @throws RengineException
     */
    public static void process(D2Data d2Data, CalInfo calInfo) throws RengineException {
        final ArrayList<ExcelParser.ProgContext> contextFormulas =
                new ArrayList<ExcelParser.ProgContext>(calInfo.servicePo.getColumns().size());

        final ArrayList<ExcelParser.ProgContext> contextConditions =
                new ArrayList<ExcelParser.ProgContext>(calInfo.servicePo.getColumns().size());

        for (TServiceColumn columnPo : calInfo.servicePo.getColumns()) {
            calInfo.curColumnPo = columnPo;
            calInfo.columnName = columnPo.getColumnName();
            calInfo.curColumn = columnPo.getColumnIntIndex();

            String formulaString=columnPo.getFormula();
//            if(formulaString.startsWith("MUSHROOM")){
//            	formulaString=mushroomTransform(formulaString);
//            }
            
            final ExcelParser.ProgContext contextFormula = getContext(formulaString);
            final ExcelParser.ProgContext contextCondition = getContext(columnPo.getCondition());

            if (contextFormula == null) {
                throw new RengineException(calInfo.serviceName, "列公式为空");
            }

            contextFormulas.add(contextFormula);
            contextConditions.add(contextCondition);
        }


        for (int i = 0; i < contextFormulas.size(); i++) {
            calInfo.curColumnPo = calInfo.servicePo.getColumns().get(i);
            calInfo.columnName = calInfo.curColumnPo.getColumnName();
            calInfo.curColumn = calInfo.curColumnPo.getColumnIntIndex();

            processColumn(d2Data, contextFormulas.get(i), contextConditions.get(i)
                    , calInfo);
        }

    }

//    //mushroom报文转换
//    private static String mushroomTransform(String formulaString){
//    	StringBuilder resultString=new StringBuilder("MUSHROOM(");
//    	formulaString=formulaString.trim();
//    	formulaString=formulaString.substring(9, formulaString.length()-1);
//    	String[] formulaStrings=formulaString.split(";");
//    	int formulaStringsLength=formulaStrings.length;
//    	for(int i=0;i<formulaStringsLength;i++){
//    		String yhString=formulaStrings[i].trim();
//    		String[] yhStrings=yhString.split(",");
//    		if(4==yhStrings.length){
//    			yhString="\"\","+yhString;
//    		}
//    		resultString.append(yhString);
//    		if((i+1)<formulaStringsLength){
//    			resultString.append(",");
//    		}
//    		
//    	}
//    	resultString.append(")");
//    	return resultString.toString();
//    }
//    
    /**
     * 计算一列
     * 从第一行到最后一行
     * @param d2Data
     * @param contextFormula
     * @param contextCondition
     * @param calInfo
     * @throws RengineException
     */
    private static void processColumn(D2Data d2Data, ExcelParser.ProgContext contextFormula
            , ExcelParser.ProgContext contextCondition, CalInfo calInfo) throws RengineException {
        int columnIntIndex = 0;
        columnIntIndex = calInfo.curColumnPo.getColumnIntIndex();
        final Object[][] rC = d2Data.getData();
        for (int row = 0; row < calInfo.maxRow; row++) {
            calInfo.curRow = row;

            if (contextCondition != null) { //匹配列条件
                Object isPassCondition = null;
                try {
                    isPassCondition = ExcelVisitor.visitProg(contextCondition, calInfo);
                    if (getType(isPassCondition) != BOOLEAN) {
                        throw new RengineException(calInfo.serviceName, "适用条件返回值不是布尔值, " + isPassCondition);
                    } else if (!((Boolean) isPassCondition)) {
                        continue;
                    }
                } catch (CalculateError e) {
                    throw new RengineException(calInfo.serviceName, "适用条件" + e.getMessage());
                }

            }
            try {
                Object ret = ExcelVisitor.visitProg(contextFormula, calInfo);
                if (ret == null) {
                    rC[row][columnIntIndex] = NumberPool.LONG_0;
                } else {
                    rC[row][columnIntIndex] = ret;
                }
            } catch (CalculateError e) {
                // 对于计算错误的, 忽略, 当前单元格为null
            }
        }
    }

    /**
     * 获取对应公式的语法树, 这里带有全局缓存
     *
     * @param input
     * @return
     * @throws RengineException
     */
    public static ExcelParser.ProgContext getContext(String input) throws RengineException {
        if (input == null || input.trim().length() == 0) {
            return null;
        }

        ParseTreeInfo info = cache.get(input);
        if (info == null) {
            info = new ParseTreeInfo();

            try {
                // LogUtil.info("parse " + input);
                ExcelParser.ProgContext context = ExcelTool.parse(input);
                info.context = context;
                // LogUtil.info("parse end");  //线上屏蔽日志
            } catch (Exception e) {
                info.e = e;
            }

            cache.put(input, info);
        }

        if (info.e != null) {
            throw new RengineException(null, info.e.getMessage());
        }

        return info.context;
    }
}
