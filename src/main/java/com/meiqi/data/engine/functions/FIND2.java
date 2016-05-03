package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

public class FIND2 extends Function{
	
	static final String NAME = FIND2.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
		// TODO Auto-generated method stub
		if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }
		
		final String target = DataUtil.getStringValue(args[0]);
        final String source = DataUtil.getStringValue(args[1]);
		int start = 1;

        if (args.length > 2 && args[2] instanceof Number) {
            start = ((Number) args[2]).intValue();
        }

        if (start < 1) {
            start = 1;
        }

        int ret = (source.indexOf(target, start - 1) + 1);

        return new Long(ret);
	}

}
