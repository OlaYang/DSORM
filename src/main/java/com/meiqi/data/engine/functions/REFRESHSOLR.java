package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.dsmanager.util.SolrClientUtil;

public class REFRESHSOLR extends Function{

	static final String NAME = REFRESHSOLR.class.getSimpleName();
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
		// TODO Auto-generated method stub
		
		if (args.length != 2) {
			throw new ArgsCountError(NAME);
        }
		
		int type=DataUtil.getNumberValue(args[0]).intValue();
		String retrunStr="只支持 type=1 的增量更新！";
		if(1==type){
			String indexName=(String) args[1];
			
			retrunStr=SolrClientUtil.refreshSolr(indexName);
		}
		
		return retrunStr;
	}

}
