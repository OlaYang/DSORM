package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

public class CASE extends Function {

	public static final String NAME = CASE.class.getSimpleName();

	static final Function EVAL_F = getFunction("EVAL");

	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 1) {
			throw new ArgsCountError(NAME);
		}
		
		for (int i = 0; i < args.length; i++) {
			
			Object[] arg = (Object[]) args[i];
			if (2 != arg.length) {
				throw new ArgsCountError(NAME);
			}
			
			if (i + 1 == args.length) {
				Object[] a=new Object[1];
				a[0]=arg[1];
				return EVAL_F.eval(calInfo, a);
			} else {
				Object[] a=new Object[1];
				a[0]=arg[0];
				Boolean isture = (Boolean) EVAL_F.eval(calInfo, a);
				if (isture) {
					a=new Object[1];
					a[0]=arg[1];
					return EVAL_F.eval(calInfo, a);
				}
			}
		}
		return null;
	}

}
