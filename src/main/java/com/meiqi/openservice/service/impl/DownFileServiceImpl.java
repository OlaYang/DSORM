package com.meiqi.openservice.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.service.IDownFileService;
import com.meiqi.openservice.service.IUploadFileService;

@Service
public class DownFileServiceImpl implements IDownFileService{

	@Autowired
	private IUploadFileService uploadFileService;
	
	@Override
	public String downPicture(String urlString) {
		try {
			String endType=".jpg";
			if(urlString.endsWith(".gif")){
				endType=".gif";
			}else if(urlString.endsWith(".png")){
				endType=".png";
			}else if(urlString.endsWith(".jpeg")){
				endType=".jpeg";
			}else if(urlString.endsWith(".bmp")){
				endType=".bmp";
			}
			
			String randomString=RandomStringUtils.randomAlphabetic(8);
			
			String tempDir=System.getProperty("user.dir")+File.separator+"tempFile";
			
			URL url = new URL(urlString);
			URLConnection con = url.openConnection(); 
			con.setConnectTimeout(5*1000);  
			InputStream is = con.getInputStream();  
			byte[] bs = new byte[1024];  
	        // 读取到的数据长度  
	        int len;  
	        // 输出的文件流  
	        File sf=new File(tempDir);  
	        if(!sf.exists()){  
	           sf.mkdirs();  
	        }  
	        String filePath=sf.getPath()+File.separator+randomString+endType;
	        LogUtil.info("headImg url="+filePath);
	        OutputStream os = new FileOutputStream(filePath);
	        while ((len = is.read(bs)) != -1) {  
	            os.write(bs, 0, len);  
	        }
	        
	        os.close();  
	        is.close(); 
	        return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		String a=new DownFileServiceImpl().downPicture("http://wx.qlogo.cn/mmopen/Q3auHgzwzM6Ec2EUmpgwCR7aazpgHkQ1VAySK18GZQ976ZnIMicMTSNqc7kkib7Z92moy5oYoWzyX5ibod1qIVQR5LzxVdEMAqTf5L7HjFTg90/0");
		System.out.println(a);
//		boolean aa=new DownFileServiceImpl().deleteFile("D:\\code\\DSORM\\tempFile\\jvXxEYTX.jpg");
//		System.out.println(aa);
	}

	@Override
	public boolean deleteFile(String filePath) {
		File file=new File(filePath);
		return file.delete();
	}
}
