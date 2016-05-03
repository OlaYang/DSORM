package com.meiqi.data.engine.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.commons.config.SysConfig;

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
		return connectImgServer(linuxDirectory);
	}
	
	private String connectImgServer(String command){
		Session session =null;
	    ChannelExec openChannel =null;
	    BufferedReader reader=null;
		try {
			JSch jsch=new JSch();
			String ip=SysConfig.getValue("img_server_ip");
			String user=SysConfig.getValue("img_server_user");
			String pwd=SysConfig.getValue("img_erver_pwd");
			Integer port=Integer.parseInt(SysConfig.getValue("img_erver_port"));
			session=jsch.getSession(user, ip, port);
			session.setPassword(pwd); 
			java.util.Properties config = new java.util.Properties();  
		    config.put("StrictHostKeyChecking", "no");  
		    session.setConfig(config);  
		    session.connect(3000);  
		    
		    Channel channel = session.openChannel("exec");  
		    ((ChannelExec) channel).setCommand(command);  
		    channel.setInputStream(null);  
		    ((ChannelExec) channel).setErrStream(System.err);
		    channel.connect();  
		    InputStream in = channel.getInputStream();  
		    reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));  
		    StringBuilder buf = new StringBuilder();
		    String tempBuf=null;
		    while ((tempBuf = reader.readLine()) != null)  
		     {  
		    	buf.append(tempBuf);
		     }  
		    return buf.toString();
		}catch(Exception e){
			return "图片服务器连接失败！";
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(openChannel!=null){
				openChannel.disconnect();
			}
			if(session!=null){
				session.disconnect();
			}
		}
	}
}
