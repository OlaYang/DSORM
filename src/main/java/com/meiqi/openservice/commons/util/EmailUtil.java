/**   
* @Title: EmailUtil.java 
* @Package com.meiqi.openservice.commons.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月21日 下午2:12:42 
* @version V1.0   
*/
package com.meiqi.openservice.commons.util;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import com.meiqi.dsmanager.util.SysConfig;
/** 
 * @ClassName: EmailUtil 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月21日 下午2:12:42 
 *  
 */
public class EmailUtil {
    private static final Logger Log     = Logger.getLogger(EmailUtil.class);
    
    // 字符编码
    private static final String CHARSET = "UTF-8";
    
    private static String hostName;
    private static String userName;
    private static String pwd;
    private static int port;
    
    static {
        hostName = SysConfig.getValue("mail.customer_service_hostname");
        userName = SysConfig.getValue("mail.customer_service_userName_1");
        pwd = SysConfig.getValue("mail.customer_service_password");
        port=Integer.parseInt(SysConfig.getValue("mail.customer_service_port"));
    }
    
    public static boolean sendEmail(String hostName,String userName,String pwd,String receiver,String sender,String title,String content) throws EmailException{
        
     // 使用SimpleEmail对于中文内容，可能会产生乱码
        SimpleEmail email = new SimpleEmail ();
        email.setCharset(CHARSET);
        // SMTP服务器名
        email.setHostName ( hostName);
        // 登陆邮件服务器的用户名和密码
        email.setAuthentication ( userName, pwd );
        // 接收人
        email.addTo ( receiver);
        // 发送人
        email.setFrom (sender);
        // 标题
        email.setSubject (title);
        // 邮件内容
        email.setMsg (content);
        // 发送
        email.send();
        return true;
    }
    
    ////// 以下代码解决发送到外部邮箱失败的问题

    private static Session      session      = null;
    private static Transport    transport    = null;
    private static boolean      debugEnabled = false;
    private static boolean      sslEnabled   = true;

    private static final String SSL_FACTORY  = "com.meiqi.openservice.commons.util.SimpleSSLSocketFactory";


    public static void sendTextEmail(String receiver, String title, String content) throws MessagingException {
        sendMessage(null, receiver, null, userName, title, content, null);
    }
    
    public static void sendHtmlEmail(String receiver, String title, String content)throws MessagingException {
        sendMessage(null, receiver, null, userName, title, null, content);
    }
    

    /**
     * Sends a message, specifying all of its fields.
     * <p>
     *
     * @param toName
     *            the name of the recipient of this email.
     * @param toEmail
     *            the email address of the recipient of this email.
     * @param fromName
     *            the name of the sender of this email.
     * @param fromEmail
     *            the email address of the sender of this email.
     * @param subject
     *            the subject of the email.
     * @param textBody
     *            plain text body of the email, which can be <tt>null</tt> if
     *            the html body is not null.
     * @param htmlBody
     *            html body of the email, which can be <tt>null</tt> if the text
     *            body is not null.
     * 
     * @throws MessagingException 
     * @throws UnsupportedEncodingException 
     */
    public static void sendMessage(String toName, String toEmail, String fromName, String fromEmail, String subject,
            String textBody, String htmlBody) throws MessagingException {
        // Check for errors in the given fields:
        if (toEmail == null || fromEmail == null || subject == null || (textBody == null && htmlBody == null)) {
            throw new MessagingException("Error sending email: Invalid fields: " + ((toEmail == null) ? "toEmail " : "")
                    + ((fromEmail == null) ? "fromEmail " : "") + ((subject == null) ? "subject " : "")
                    + ((textBody == null && htmlBody == null) ? "textBody or htmlBody " : ""));
        } else {
            String encoding = MimeUtility.mimeCharset("UTF-8");
            MimeMessage message = createMimeMessage();
            Address to;
            Address from;

            try {
                if (toName != null) {
                    to = new InternetAddress(toEmail, toName, encoding);
                } else {
                    to = new InternetAddress(toEmail, "", encoding);
                }
    
                if (fromName != null) {
                    from = new InternetAddress(fromEmail, fromName, encoding);
                } else {
                    from = new InternetAddress(fromEmail, "", encoding);
                }
            } catch (UnsupportedEncodingException e) {
                throw new MessagingException(e.getMessage());
            }

            // Set the date of the message to be the current date
            //SimpleDateFormat format = DateUtils.sdf;
            //format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            //message.setHeader("Date", format.format(new Date()));
            message.setHeader("Content-Transfer-Encoding", "8bit");
            message.setRecipient(Message.RecipientType.TO, to);
            message.setFrom(from);
            message.setSubject(subject, encoding);
            // Create HTML, plain-text, or combination message
            if (textBody != null && htmlBody != null) {
                MimeMultipart content = new MimeMultipart("alternative");
                // Plain-text
                MimeBodyPart text = new MimeBodyPart();
                text.setText(textBody, encoding);
                text.setDisposition(Part.INLINE);
                content.addBodyPart(text);
                // HTML
                MimeBodyPart html = new MimeBodyPart();
                html.setContent(htmlBody, "text/html; charset=UTF-8");
                html.setDisposition(Part.INLINE);
                html.setHeader("Content-Transfer-Encoding", "8bit");
                content.addBodyPart(html);
                // Add multipart to message.
                message.setContent(content);
                message.setDisposition(Part.INLINE);
                sendMessage(message);
            } else if (textBody != null) {
                MimeBodyPart bPart = new MimeBodyPart();
                bPart.setText(textBody, encoding);
                bPart.setDisposition(Part.INLINE);
                bPart.setHeader("Content-Transfer-Encoding", "8bit");
                MimeMultipart mPart = new MimeMultipart();
                mPart.addBodyPart(bPart);
                message.setContent(mPart);
                message.setDisposition(Part.INLINE);
                // Add the message to the send list
                sendMessage(message);
            } else if (htmlBody != null) {
                MimeBodyPart bPart = new MimeBodyPart();
                bPart.setContent(htmlBody, "text/html; charset=UTF-8");
                bPart.setDisposition(Part.INLINE);
                bPart.setHeader("Content-Transfer-Encoding", "8bit");
                MimeMultipart mPart = new MimeMultipart();
                mPart.addBodyPart(bPart);
                message.setContent(mPart);
                message.setDisposition(Part.INLINE);
                // Add the message to the send list
                sendMessage(message);
            }
        }
    }



    public static void sendMessage(MimeMessage message) throws MessagingException {
        if (message != null) {
            send(Collections.singletonList(message), false);
        } else {
            Log.error("Cannot add null email message to queue.");
        }
    }


    public static void send(Collection<MimeMessage> messages, boolean continueOnFailed) throws MessagingException {
        URLName url = new URLName("smtp", hostName, port, "", userName, pwd);
        if (session == null) {
            createSession();
        }
        if (transport == null) {
            transport = new com.sun.mail.smtp.SMTPTransport(session, url);
            
        }
        if (!transport.isConnected()) {
            transport.connect(hostName, port, userName, pwd);
        }
        
        for (MimeMessage message : messages) {
            try {
                // Attempt to send message, but catch exceptions caused by invalid
                // addresses so that other messages can continue to be sent if continueOnFailed set to true.
                transport.sendMessage(message, message.getRecipients(MimeMessage.RecipientType.TO));
            } catch (MessagingException e) {
                e.printStackTrace();
                if (!continueOnFailed) {
                    throw e;
                }
            }
        }
    }



    private static MimeMessage createMimeMessage() {
        if (session == null) {
            createSession();
        }
        return new MimeMessage(session);
    }



    private static void createSession() {
        Properties mailProps = new Properties();
        mailProps.setProperty("mail.smtp.host", hostName);
        mailProps.setProperty("mail.smtp.port", String.valueOf(port));
        // Allow messages with a mix of valid and invalid recipients to still be sent.
        mailProps.setProperty("mail.smtp.sendpartial", "true");
        mailProps.setProperty("mail.debug", String.valueOf(debugEnabled));

        if (sslEnabled) {
            // Register with security provider.
            Security.setProperty("ssl.SocketFactory.provider", SSL_FACTORY);
            mailProps.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            mailProps.setProperty("mail.smtp.socketFactory.fallback", "true");
        }

        // If a username is defined, use SMTP authentication.
        if (userName != null) {
            mailProps.put("mail.smtp.auth", "true");
        }
        session = Session.getInstance(mailProps, null);
    }
}
