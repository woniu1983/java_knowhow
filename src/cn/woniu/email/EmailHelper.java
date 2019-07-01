/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.email;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.sun.mail.util.MailSSLSocketFactory;


/** 
 * @ClassName: EmailHelper <br/> 
 * @Description: Support SMTP Sending  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月2日 上午8:51:43 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class EmailHelper {
	private final String SMTP_TIMEOUT = "15000";		// in million seconds

    /**
     * TODO 有些公司可能不支持将mail.from设置为和sender的邮箱不同的值 ， 即两者必须相同
     */
    String mailFrom = "sender@PrintServer";

	String server;
	String port;

	public enum SSL_TYPE {
		NONE,
		TLS,
		SSL,
		;
	}
	SSL_TYPE ssl;

	private final String CHARSET = "utf-8";

	Authenticator authenticator = null;

	public EmailHelper(String smtpServer, String smtpPort, SSL_TYPE sslType)
	{
		server = smtpServer;
		port = smtpPort;
		ssl = sslType;
	}

	public void setAuthentication(final String username, final String password)
	{
		if(username == null || username.isEmpty())
		{
			return;
		}
		authenticator = new Authenticator(){

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}

		};
	}
	public boolean send(String sender, String[] to, String subject, String text, String[] attachment)
	{

		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-Java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);

		Session session = getSession(sender);
		if(session == null)
		{
			return false;
		}

		Message mail = createMail(session, sender, to, subject, text, attachment);
		if(mail == null)
		{
			return false;
		}
		boolean isSuccess = false;
		try {
			Transport.send(mail);
			isSuccess = true;
		} catch (MessagingException e) {
			e.getMessage();
		}
		return isSuccess;
	}

	public boolean sendTestMail(String sender, String[] to, String subject, String content) {
		Session session = getSession(sender);
		if(session == null)
		{
			return false;
		}

		Message mail = createTestMail(session, sender, to, subject, content);
		if(mail == null)
		{
			return false;
		}
		boolean isSuccess = false;
		try {
			Transport.send(mail);
			isSuccess = true;
		} catch (MessagingException e) {
			System.out.println("#Send Test Mail" + e.getMessage());
		}
		return isSuccess;
	}

	public boolean checkConnection(String sender)
	{
		Session session = getSession(sender);
		if(checkConnection(session))
		{
			return true;
		}
		return false;
	}

	private boolean checkConnection(Session session)
	{
		if(session == null)
		{
			return false;
		}
		boolean isSuccess = false;
		try {
			Transport transport = session.getTransport("smtp");
			transport.connect();
			transport.close();
			isSuccess = true;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	private Session getSession(String sender)
	{
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", server);
		properties.setProperty("mail.smtp.port", port);
		properties.setProperty("mail.smtp.connectiontimeout", SMTP_TIMEOUT);
		properties.setProperty("mail.smtp.timeout", SMTP_TIMEOUT);
        // TODO 有些公司可能不支持将mail.from设置为和sender的邮箱不同的值 ， 即两者必须相同
        if (sender == null || sender.trim().isEmpty()) {
            properties.setProperty("mail.from", mailFrom);
        } else {
            properties.setProperty("mail.from", sender);
        }

		switch(ssl)
		{
		case SSL:
			properties.setProperty("mail.smtp.ssl.enable", "true");
			properties.setProperty("mail.smtp.ssl.trust", server);
//			properties.setProperty("mail.smtp.socketFactory.port", port);
//			properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//			properties.setProperty("mail.smtp.socketFactory.fallback", "false");
            MailSSLSocketFactory sf;
            try {
                sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                properties.put("mail.smtp.ssl.socketFactory", sf);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
			break;
		case TLS:
			properties.setProperty("mail.smtp.starttls.enable", "true");
			properties.setProperty("mail.smtp.ssl.trust", server);
			break;
		default:
			break;
		}

		Session session;
		if(authenticator != null)
		{
			properties.setProperty("mail.smtp.auth", "true");
			session = Session.getInstance(properties, authenticator);
		}
		else
		{
			session = Session.getInstance(properties);
		}
		//		session.setDebug(true);

		return session;
	}

	private Message createMail(Session session, String sender, String[] to, String subject, String text, String[] attachment)
	{
		if(session == null)
		{
			return null;
		}
		MimeMessage message = null;
		try {
			message = new MimeMessage(session);
			// set subject
			message.setSubject(subject, CHARSET);

			MimeMultipart multipart = new MimeMultipart();
			// set text
			MimeBodyPart text_part = new MimeBodyPart();
			text_part.setText(text + "\n", CHARSET);	// append \n so that attachment will be add to next line, if any
			multipart.addBodyPart(text_part);

			if(attachment != null && attachment.length != 0) {	
				File file =null;
				for(int i=0;i<attachment.length;i++) {
					MimeBodyPart attachment_part = new MimeBodyPart();
					file = new File(attachment[i]);
					attachment_part.attachFile(file);
					attachment_part.setFileName(MimeUtility.encodeText(file.getName()));
					multipart.addBodyPart(attachment_part);
				}
				System.out.println(attachment.length);
			}
			message.setContent(multipart);

			// set from
			try {
				message.setFrom(new InternetAddress(sender));
			} catch (MessagingException e){
				System.out.println("Create Mail: " + e.getMessage() + " Try Default Sender.");
			}


			// set to
			InternetAddress[] to_list = new InternetAddress[to.length];
			for(int i = 0; i < to_list.length; ++i)
			{
				to_list[i] = new InternetAddress(to[i]);
				to_list[i].validate();
			}
			message.setRecipients(RecipientType.TO, to_list);

		} catch (MessagingException e) {
			message = null;
			System.out.println("Create Mail Fail: " + e.getMessage());
		}
		catch (IOException e)
		{
			message = null;
			System.out.println("Create Mail Fail: " + e.getMessage());
		}
		return message;
	}

	private Message createTestMail(Session session, String sender, String[] to, String subject, String text) {
		if(session == null) {
			return null;
		}
		MimeMessage message = null;
		try {
			message = new MimeMessage(session);
			// set subject
			message.setSubject(subject, CHARSET);

			MimeMultipart multipart = new MimeMultipart();
			// set text
			MimeBodyPart text_part = new MimeBodyPart();
			text_part.setText(text + "\n", CHARSET);	// append \n so that attachment will be add to next line, if any
			multipart.addBodyPart(text_part);

			message.setContent(multipart);

			// set from
			message.setFrom(new InternetAddress(sender));

			// set to
			InternetAddress[] to_list = new InternetAddress[to.length];
			for(int i = 0; i < to_list.length; ++i)
			{
				to_list[i] = new InternetAddress(to[i]);
				to_list[i].validate();
			}
			message.setRecipients(RecipientType.TO, to_list);

		} catch (MessagingException e) {
			message = null;
			System.out.println("Create Test Mail Fail: " + e.getMessage());
		} catch (Exception e) {
			message = null;
			System.out.println("Create Test Mail Fail: " + e.getMessage());
		}
		return message;
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;
		try {
			InternetAddress address = new InternetAddress(email);
			address.validate();
			isValid = true;
		} catch (AddressException e) {
			// invalid email address
		}
		return isValid;
	}

	public static void main(String[] args) {
		
		testMail();
		
//		String server = "10.10.10.246";
//		String port = "465";
//		SSL_TYPE ssl = SSL_TYPE.TLS;
//
//		String username = "mail01@test.com.cn";
//		String password = "12345678";
//		EmailHelper helper = new EmailHelper(server, port, ssl);
//		helper.setAuthentication(username, password);
//
//		String sender = "a";
//		String[] to = new String[]{"mail01@test.com.cn"};
//		String subject = "test from admin tool";
//		String text = "test text";
//
//		// 发送邮件
//		helper.send(sender, to, subject, text, null);
//
//		// 检查邮箱服务器连接
//		boolean result = helper.checkConnection();
//		System.out.println(result);
//
//		// 检查邮件是否可用
//		boolean result2 = EmailHelper.isEmailValid("abc@test.com.cn");
	}
	
	private static void testMail() {
		String server = "192.168.1.111";
		String port = "25";
		SSL_TYPE ssl = SSL_TYPE.NONE;

		String username = "woniu";
		String password = "12345678";
		EmailHelper helper = new EmailHelper(server, port, ssl);
		helper.setAuthentication(username, password);

		String sender = "woniu@test.com";
		String[] to = new String[]{"woniu@test.com"};
		String subject = "test from Eclipse";
		String text = "test text";

		// 检查邮箱服务器连接
		boolean result = helper.checkConnection(sender);
		System.out.println(result);

		// 发送邮件
		helper.send(sender, to, subject, text, null);
		
	}



}
