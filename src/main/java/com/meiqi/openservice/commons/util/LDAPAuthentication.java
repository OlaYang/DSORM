/**   
* @Title: UserAuthenticateUtil.java 
* @Package com.meiqi.openservice.commons.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年12月30日 下午4:27:48 
* @version V1.0   
*/
package com.meiqi.openservice.commons.util;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.meiqi.dsmanager.util.LogUtil;
 
/**
 * ladp用户认证
* @ClassName: LDAPAuthentication 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhouyongxiong
* @date 2015年12月31日 下午3:57:45 
*
 */
public class LDAPAuthentication {
    private final static String URL = "ldap://58.220.17.162:389/";
    private final static String BASEDN = "dc=lejj,dc=com"; 
    private final static String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static LdapContext ctx = null;
    private final static Control[] connCtls = null;
    private final static String root = "cn=Manager,dc=lejj,dc=com";
    private final static String pwd = "123456";
  
    private static void LDAP_connect() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
        env.put(Context.PROVIDER_URL, URL + BASEDN);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, root);   // 管理员
        env.put(Context.SECURITY_CREDENTIALS,pwd);  // 管理员密码
         
        try {
            ctx = new InitialLdapContext(env, connCtls);
             
        } catch (javax.naming.AuthenticationException e) {
            LogUtil.error("认证失败："+",error:"+e);
        } catch (Exception e) {
            LogUtil.error("认证出错："+",error:"+e);
        }
        
    }
  
    private static Map<String,String> getUserDN(String uid) {
        Map<String,String> result=new HashMap<String,String>();
        String userDN = "";
        LDAP_connect();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> en = ctx.search("", "uid=" + uid, constraints);
            if (en == null || !en.hasMoreElements()) {
                LogUtil.error("未找到该用户");
            }
            // maybe more than one element
            while (en != null && en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult si = (SearchResult) obj;
                    userDN += si.getName();
                    userDN += "," + BASEDN;
                    result.put("userDN", userDN);
                    Attributes as=si.getAttributes();
                    String mail=as.get("mail").toString();
                    if(StringUtils.isNotEmpty(mail)){
                        String mailTmp=mail.split(":")[1];
                        if(StringUtils.isNotEmpty(mailTmp)){
                            result.put("email", mailTmp.trim());
                        }
                    }
                    String cn=as.get("cn").toString();
                    if(StringUtils.isNotEmpty(cn)){
                        String cnTmp=cn.split(":")[1];
                        if(StringUtils.isNotEmpty(cnTmp)){
                            result.put("cn", cnTmp.trim());
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            LogUtil.error("查找用户时产生异常。"+",error:"+e);
        }
        return result;
    }
  
    public static Map<String,String> authenricate(String UID, String password) {
        Map<String,String> result= getUserDN(UID);
        String userDN = result.get("userDN");
  
        try {
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(connCtls);
        } catch (Exception e) {
            LogUtil.error(userDN + " 验证失败"+",error:"+e);
            return null;
        }finally{
            if(ctx!=null){
                try {
                    ctx.close();
                } catch (NamingException e) {
                    LogUtil.error("关闭ladp连接时产生异常。"+",error:"+e);
                }
            }
        }
        return result;
    }
     
    public static void main(String[] args) {
        System.out.println(LDAPAuthentication.authenricate("", ""));
    }
}