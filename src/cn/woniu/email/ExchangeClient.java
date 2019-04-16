/**
 * 
 */
package cn.woniu.email;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.WebProxy;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.credential.WebProxyCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

/**
 * @author woniu1983
 *
 */
public class ExchangeClient {

	public static class ExchangeMailBuilder {
		private String hostname;
		private String url;
		private ExchangeVersion exchangeVersion;
		private String domain;
		private String username;
		private String password;
		private String subject;
		private String from;
		private String recipientTo;
		private List<String> recipientCc;
		private List<String> recipientBcc;
		private List<String> attachments;
		private String message;
		private String proxyHost;
		private int proxyPort;
		private String proxyUser;
		private String proxyPwd;
		private String proxyDomain;
		

		public ExchangeMailBuilder() {
			this.exchangeVersion = ExchangeVersion.Exchange2010_SP1;
			this.hostname = "";
			this.url = "";
			this.username = "";
			this.password = "";
			this.subject = "";
			this.recipientTo = "";
			this.recipientCc = new ArrayList<String>(0);
			this.recipientBcc = new ArrayList<String>(0);
			this.attachments = new ArrayList<String>(0);
			this.message = "";
			this.proxyHost = "";
			this.proxyPort = 80;
			this.proxyUser = "";
			this.proxyPwd = "";
			this.proxyDomain = "";
		}

		public ExchangeMailBuilder hostname(String hostname) {
			this.hostname = hostname;
			return this;
		}
		
		public ExchangeMailBuilder url(String url) {
			this.url = url;
			return this;
		}

		public ExchangeMailBuilder exchangeVersion(ExchangeVersion exchangeVersion) {
			this.exchangeVersion = exchangeVersion;
			return this;
		}

		public ExchangeMailBuilder domain(String domain) {
			this.domain = domain;
			return this;
		}

		public ExchangeMailBuilder username(String username) {
			this.username = username;
			return this;
		}

		public ExchangeMailBuilder password(String password) {
			this.password = password;
			return this;
		}

		public ExchangeMailBuilder from(String senderEmail) {
			this.from = senderEmail;
			return this;
		}

		public ExchangeMailBuilder subject(String subject) {
			this.subject = subject;
			return this;
		}

		public ExchangeMailBuilder recipientTo(String recipientTo) {
			this.recipientTo = recipientTo;
			return this;
		}

		public ExchangeMailBuilder proxy(String proxyHost, int proxyPort, String puser, String ppwd, String pdomain) {
			this.proxyHost = proxyHost;
			this.proxyPort = proxyPort;
			this.proxyDomain = pdomain;
			this.proxyUser = puser;
			this.proxyPwd = ppwd;
			return this;
		}

		public ExchangeMailBuilder recipientCc(String recipientCc, String... recipientsCc) {
			// Prepare the list.
			List<String> recipients = new ArrayList<String>(1 + recipientsCc.length);
			recipients.add(recipientCc);
			recipients.addAll(Arrays.asList(recipientsCc));
			// Set the list.
			this.recipientCc = recipients;
			return this;
		}

		public ExchangeMailBuilder recipientCc(List<String> recipientCc) {
			this.recipientCc = recipientCc;
			return this;
		}

		public ExchangeMailBuilder recipientBcc(String recipientBcc, String... recipientsBcc) {
			// Prepare the list.
			List<String> recipients = new ArrayList<String>(1 + recipientsBcc.length);
			recipients.add(recipientBcc);
			recipients.addAll(Arrays.asList(recipientsBcc));
			// Set the list.
			this.recipientBcc = recipients;
			return this;
		}

		public ExchangeMailBuilder recipientBcc(List<String> recipientBcc) {
			this.recipientBcc = recipientBcc;
			return this;
		}

		public ExchangeMailBuilder attachments(String attachment, String... attachments) {
			// Prepare the list.
			List<String> attachmentsToUse = new ArrayList<String>(1 + attachments.length);
			attachmentsToUse.add(attachment);
			attachmentsToUse.addAll(Arrays.asList(attachments));
			// Set the list.
			this.attachments = attachmentsToUse;
			return this;
		}

		public ExchangeMailBuilder attachments(List<String> attachments) {
			this.attachments = attachments;
			return this;
		}

		public ExchangeMailBuilder message(String message) {
			this.message = message;
			return this;
		}

		public ExchangeClient build() {
			return new ExchangeClient(this);
		}
	}

	private final String hostname;
	private final String url;
	private final ExchangeVersion exchangeVersion;
	private final String domain;
	private final String username;
	private final String password;
	private final String subject;
	private final String from;
	private final String recipientTo;
	private final List<String> recipientCc;
	private final List<String> recipientBcc;
	private final List<String> attachments;
	private final String message;
	private final String proxyHost;
	private final int proxyPort;
	private final String proxyUser;
	private final String proxyPwd;
	private final String proxyDomain;
	private boolean trace = false;

	private ExchangeClient(ExchangeMailBuilder builder) {
		this.hostname = builder.hostname;
		this.url = builder.url;
		this.exchangeVersion = builder.exchangeVersion;
		this.domain = builder.domain;
		this.username = builder.username;
		this.password = builder.password;
		this.from = builder.from;
		this.subject = builder.subject;
		this.recipientTo = builder.recipientTo;
		this.recipientCc = builder.recipientCc;
		this.recipientBcc = builder.recipientBcc;
		this.attachments = builder.attachments;
		this.message = builder.message;
		this.proxyHost = builder.proxyHost;
		this.proxyPort = builder.proxyPort;
		this.proxyUser = builder.proxyUser;
		this.proxyPwd = builder.proxyPwd;
		this.proxyDomain = builder.proxyDomain;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean sendExchange() {
		// The Exchange Server Version.
		ExchangeService exchangeService = new ExchangeService(exchangeVersion);
		
		if(this.proxyHost != null && !this.proxyHost.trim().isEmpty()) {
			WebProxyCredentials cred = null;
			if(this.proxyUser != null && !this.proxyUser.trim().isEmpty()) {
				cred = new WebProxyCredentials(this.proxyUser, this.proxyPwd, this.proxyDomain);
			}
			
			WebProxy proxy = new WebProxy(this.proxyHost.trim(), this.proxyPort, cred);
			exchangeService.setWebProxy(proxy);
		}
		
//		exchangeService.setTraceEnabled(this.trace);

		// Credentials to sign in the MS Exchange Server.
		System.out.println("username=" + username + "  password=" + password + "  domain=" + domain);
		ExchangeCredentials exchangeCredentials = new WebCredentials(username, password, domain);
		exchangeService.setCredentials(exchangeCredentials);
//		exchangeService.setPreAuthenticate(true); //TODO

		try {
			exchangeService.setUrl(new URI(url));
//			exchangeService.autodiscoverUrl(from);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// Email Message
		EmailMessage emailMessage;
		try {
			emailMessage = new EmailMessage(exchangeService);
            emailMessage.setSubject(subject);
            emailMessage.setBody(MessageBody.getMessageBodyFromText(message));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		try {
			emailMessage.getToRecipients().add(recipientTo);
		} catch (ServiceLocalException e) {
			e.printStackTrace();
			return false;
		}

		for (String recipient : recipientCc) {
            try {
                emailMessage.getCcRecipients().add(recipient);
            } catch (ServiceLocalException e) {
            	e.printStackTrace();
                return false;
            }
        }
		
		for (String recipient : recipientBcc) {
            try {
                emailMessage.getBccRecipients().add(recipient);
            } catch (ServiceLocalException e) {
            	e.printStackTrace();
                return false;
            }
        }
		
		for (String attachmentPath : attachments) {
            try {
                emailMessage.getAttachments().addFileAttachment(attachmentPath);
            } catch (ServiceLocalException e) {
            	e.printStackTrace();
                return false;
            }
        }

        try {
            emailMessage.send();
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
        
        return true;
	}

}
