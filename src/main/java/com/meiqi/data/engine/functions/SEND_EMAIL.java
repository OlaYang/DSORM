package com.meiqi.data.engine.functions;

import javax.mail.MessagingException;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.commons.util.EmailUtil;
import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月19日 下午2:47:15 
 * 类说明  发送邮件函数
 */

public class SEND_EMAIL extends Function{

	public static final String NAME = SEND_EMAIL.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		
		if(args.length != 4){
			throw new ArgsCountError(NAME);
		}
		String toEmail=String.valueOf(args[0]);
		String[] split = toEmail.split(",");
		if(StringUtils.isEmpty(toEmail)){
			throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		String title=String.valueOf(args[1]);
		if(StringUtils.isEmpty(title)){
			throw new ArgsCountError(NAME+"第二个参数不能为空!");
		}
		String content=String.valueOf(args[2]);
		if(StringUtils.isEmpty(content)){
			throw new ArgsCountError(NAME+"第三个参数不能为空!");
		}
		String type=String.valueOf(args[3]);
		if(StringUtils.isEmpty(type)){
			throw new ArgsCountError(NAME+"第四个参数不能为空!");
		}
		String fromEmail = SysConfig.getValue("mail.customer_service_userName");
		try {
			for (int i = 0; i < split.length; i++) {
				if("0".equals(type)){
					EmailUtil.sendMessage(null, split[i], null, fromEmail, title, content, null);
				}else if("1".equals(type)){
					EmailUtil.sendMessage(null, split[i], null, fromEmail, title, null, content);
				}
			}
		} catch (MessagingException e) {
			throw new ArgsCountError(NAME+"========发送邮件异常，异常信息为: "+e.getMessage());
		}
		
		return "成功";
	}
}
