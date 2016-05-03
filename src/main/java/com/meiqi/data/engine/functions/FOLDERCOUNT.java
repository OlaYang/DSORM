package com.meiqi.data.engine.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

/**
 * 根据传入的目录 获取子文件夹个数 或者 子文件个数
 * @author Administrator
 *
 */
public class FOLDERCOUNT extends Function{
	static final String NAME = FOLDERCOUNT.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
		if(2>args.length){
			throw new ArgsCountError(NAME);
		}
		String linuxDirectory=DataUtil.getStringValue(args[0]);
		String type=DataUtil.getStringValue(args[1]);
		String cmd="find "+linuxDirectory+" -type "+type+" | wc -l";
		try {
			String[] cmds=new String[]{"/bin/sh", "-c",cmd};
			Process ps = Runtime.getRuntime().exec(cmds);
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
			sb.append(line);
			}
			String src=sb.toString().trim();
			Integer rc=Integer.parseInt(src);
			if("d".equals(type)){
				rc=rc-1;
			}
			return rc.toString();
			} catch (Exception e) {
				return "调用linux命令错误,执行命令:"+cmd+"错误信息："+e.getMessage();
			}
	}
}
