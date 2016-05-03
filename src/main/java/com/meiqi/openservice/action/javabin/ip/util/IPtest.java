package com.meiqi.openservice.action.javabin.ip.util;

public class IPtest{  
    
   public static void main(String[] args) 
{  
                //指定纯真数据库的文件名，所在文件夹  
        IPSeeker ip=new IPSeeker("qqwry.Dat","C:\\Users\\Administrator\\Desktop\\新建文件夹 (3)");  
         //测试IP 58.20.43.13  
         System.out.println(ip.getIPLocation("118.122.120.144").getCountry());  
         System.out.println(ip.getIPLocation("125.70.179.89").getArea());
}  
}  
