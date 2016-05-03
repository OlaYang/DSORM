package com.meiqi.openservice.commons.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

public class FileUtil {
	/**
     * 将文本文件中的内容读入到buffer中
     * @param buffer buffer
     * @param filePath 文件路径
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    /**
     * 读取文本文件内容
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static String readFile(String filePath) throws IOException {
        StringBuffer sb = new StringBuffer();
        FileUtil.readToBuffer(sb, filePath);
        return sb.toString();
    }
    
    /**
     * 把生成的文件，下载到本地
     * @param csvFile 要下载的文件
     * @param fileName 下载下来的文件的名称
     * @param filePrefix 下载下来的文件的后缀
     * @param encode 下载下来的文件的编码
     * @param response
     * @throws Exception
     */
    public static void downloadCsvToLocal(File csvFile,String fileName,String filePrefix,String encode,HttpServletResponse response) throws Exception {
        Date now = new Date();
        String fileNm = new StringBuilder(fileName).append(now.getTime()).append(".").append(filePrefix).toString();

        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileNm, encode));

        InputStream in = new FileInputStream(csvFile);
        OutputStream tempOS = response.getOutputStream();
        byte[] b = new byte[1024];
        int hasRead = 0;
        while ((hasRead = in.read(b)) != -1) {
            tempOS.write(b, 0, hasRead);
        }
        tempOS.flush();
        tempOS.close();
        in.close();

    }
    
    //把生成的文件，下载到本地
    public static void downloadFileToLocal(File file,String contentType,String fileName,String filePrefix,String encode,HttpServletResponse response) throws Exception {
    	
	     InputStream in = null;
	     OutputStream tempOS = null;
    	try {
    	        if(StringUtils.isEmpty(fileName)){
    	        	fileName=new Date().getTime()+"";
    	        }
    	        String fileNm="";
    	        if(fileName.indexOf(".")!=-1){
    	        	fileNm = new StringBuilder(fileName).toString();
    	        }else{
    	        	fileNm = new StringBuilder(fileName).append(".").append(filePrefix).toString();
    	        }
    	        response.setContentType(contentType);
    	        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileNm, encode));
    	        in = new FileInputStream(file);
    	        tempOS = response.getOutputStream();
    	        byte[] b = new byte[1024];
    	        int hasRead = 0;
    	        while ((hasRead = in.read(b)) != -1) {
    	            tempOS.write(b, 0, hasRead);
    	        }
    	        
    	} catch (Exception e) {
    		e.printStackTrace();
		}finally{
			 if(tempOS!=null){tempOS.flush();tempOS.close();}
 	         if(in!=null){in.close();}
		}
    }
    
    public static void mergeFiles(File outFile,   File[] files) {  
        FileChannel outChannel = null;  
        int BUFSIZE = 1024 * 8; 
        try {  
            outChannel = new FileOutputStream(outFile).getChannel();  
            for(File f : files){  
                FileChannel fc = new FileInputStream(f).getChannel();   
                ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);  
                while(fc.read(bb) != -1){  
                    bb.flip();  
                    outChannel.write(bb);  
                    bb.clear();  
                }  
                fc.close();  
            }  
        } catch (IOException ioe) {  
            ioe.printStackTrace();  
        } finally {  
            try {if (outChannel != null) {outChannel.close();}} catch (IOException ignore) {}  
        }  
    }  
    public static boolean deleteDir(File dir) {        
        if (dir.isDirectory()) {            
            String[] children = dir.list();
            //递归删除目录中的子目录下            
            for (int i=0; i<children.length; i++) {                
                boolean success = deleteDir(new File(dir, children[i]));               
                if (!success) {                   
                    return false;               
                }            
            }        
        }
         // 目录此时为空，可以删除       
        return dir.delete(); 
    }
}
