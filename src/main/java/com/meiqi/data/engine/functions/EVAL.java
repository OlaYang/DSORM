package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelEngineTool;
import com.meiqi.data.engine.excel.ExcelVisitor;
import com.meiqi.data.engine.excel.parser.ExcelParser;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class EVAL extends Function {
    static final String NAME = EVAL.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            String formula = DataUtil.getStringValue(args[0]);
            ExcelParser.ProgContext progContext = ExcelEngineTool.getContext(formula);

            if (progContext == null) {
                return null;
            }

            return ExcelVisitor.visitProg(progContext, calInfo);
        }

        throw new ArgsCountError(NAME);
    }
}
