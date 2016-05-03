package com.meiqi.dsmanager.util;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.InputStream;
import java.util.Properties;  
import java.util.Vector;  
  


import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;  
import com.jcraft.jsch.ChannelSftp;  
import com.jcraft.jsch.JSch;  
import com.jcraft.jsch.Session;  
import com.jcraft.jsch.SftpException;  
import com.meiqi.dsmanager.rmi.impl.RmiSolrService;
/**
 * 
* @ClassName: Sftp 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 
* @date 2015年6月24日 下午8:14:37 
*
 */
public class SftpUtil {  
	private static final Logger LOG               = Logger.getLogger(RmiSolrService.class);
  
    /** 
     * 连接sftp服务器 
     *  
     * @param host 
     *            主机 
     * @param port 
     *            端口 
     * @param username 
     *            用户名 
     * @param password 
     *            密码 
     * @return ChannelSftp 
     */  
    public static ChannelSftp connect(String host, int port, String username,  
            String password) throws Exception {  
        ChannelSftp sftp = null;  
        JSch jsch = new JSch();  
        jsch.getSession(username, host, port);  
        Session sshSession = jsch.getSession(username, host, port);  
        LOG.info("Session created.");  
        sshSession.setPassword(password);  
        Properties sshConfig = new Properties();  
        sshConfig.put("StrictHostKeyChecking", "no");  
        sshSession.setConfig(sshConfig);  
        sshSession.connect();  
        LOG.info("Session connected.");  
        LOG.info("Opening Channel.");  
        Channel channel = sshSession.openChannel("sftp");  
        channel.connect();  
        sftp = (ChannelSftp) channel;  
        LOG.info("Connected to " + host + " success.");  
        return sftp;  
    }  
  
    /**
     * 创建目录
    * @Title: mkdir 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param path
    * @param @param sftp  参数说明 
    * @return void    返回类型 
    * @throws
     */
    public static void mkdir(String path, ChannelSftp sftp){
    	try {
			sftp.mkdir(path);
		} catch (SftpException e) {
			LOG.info(e.getMessage());
		}
    }
    /** 
     * 上传文件 
     *  
     * @param directory 
     *            上传的目录 
     * @param uploadFile 
     *            要上传的文件 
     * @param sftp 
     */  
    public static void upload(String directory, String uploadFile, ChannelSftp sftp)  
            throws Exception {  
        sftp.cd(directory);  
        File file = new File(uploadFile);  
        sftp.put(new FileInputStream(file), file.getName());  
        LOG.info("上传成功.");  
    }  
  
    /** 
     * 下载文件 
     *  
     * @param directory 
     *            下载目录 
     * @param downloadFile 
     *            下载的文件 
     * @param saveFile 
     *            存在本地的路径 
     * @param sftp 
     */  
    public static void download(String directory, String downloadFile,  
            String saveFile, ChannelSftp sftp) throws Exception {  
        sftp.cd(directory);  
        File file = new File(saveFile);  
        sftp.get(downloadFile, new FileOutputStream(file));  
    }  
  
    /**
     * @throws SftpException 
     * 
    * @Title: cd 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param dir  参数说明 
    * @return void    返回类型 
    * @throws
     */
    public static void cd(String dir,ChannelSftp sftp) throws SftpException{
    	sftp.cd(dir);  
    }
    
    public static InputStream get(String src,ChannelSftp sftp) throws SftpException{
    	return sftp.get(src);
    }
    /** 
     * 删除文件 
     *  
     * @param directory 
     *            要删除文件所在目录 
     * @param deleteFile 
     *            要删除的文件 
     * @param sftp 
     */  
    public static void delete(String directory, String deleteFile, ChannelSftp sftp)  
            throws Exception {  
        sftp.cd(directory);  
        sftp.rm(deleteFile);  
    }  
  
    /** 
     * 列出目录下的文件 
     *  
     * @param directory 
     *            要列出的目录 
     * @param sftp 
     * @return 
     * @throws SftpException 
     */  
    public static Vector listFiles(String directory, ChannelSftp sftp)  
            throws Exception {  
        return sftp.ls(directory);  
    }  
}  