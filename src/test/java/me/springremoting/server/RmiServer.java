package me.springremoting.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RmiServer {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("classpath:remoting-servlet.xml");
    	System.out.println("服务器启动成功");  
	}
}
