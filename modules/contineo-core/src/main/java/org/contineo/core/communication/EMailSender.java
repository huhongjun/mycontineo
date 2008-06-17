package org.contineo.core.communication;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.contineo.util.Context;
import org.contineo.util.config.SettingsConfig;

/**
 * SMTP E-Mail sender service 
 * 
 * @author Michael Scholz, Marco Meschieri
 */
public class EMailSender {

	private String host = "localhost";

	private String defaultAddress = "contineo@acme.com";

	private String username = "";

	private String password = "";

	private int port = 25;

	public EMailSender() {
	}

	public String getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(String fromAddress) {
		this.defaultAddress = fromAddress;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * This method sends an email using the smtp-protocol. The email can be a
	 * simple mail or a multipart mail containing up to 5 attachments.
	 * 
	 * @param account E-Mail account of the sender.
	 * @param email E-Mail which should be sent.
	 * @throws Exception
	 */
	public void send(EMail email) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");

		Session sess = Session.getDefaultInstance(props);
		javax.mail.Message message = new MimeMessage(sess);
		String frm=email.getAuthorAddress();
		if(StringUtils.isEmpty(frm))
			frm=defaultAddress;
		InternetAddress from = new InternetAddress(frm);
		InternetAddress[] to = email.getAddresses();
		message.setFrom(from);
		message.setRecipients(javax.mail.Message.RecipientType.TO, to);
		message.setSubject(email.getSubject());

		if (email.getAttachmentCount() > 0) {
			SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			String userdir = conf.getValue("userdir");
			String filename = userdir + "/mails/" + email.getMessageId() + "/";
			MimeBodyPart body = new MimeBodyPart();
			body.setContent(email.getMessageText(), "text/plain");

			Multipart mpMessage = new MimeMultipart();
			mpMessage.addBodyPart(body);

			Collection attachments = email.getAttachments().values();
			Iterator iter = attachments.iterator();

			while (iter.hasNext()) {
				Attachment att = (Attachment) iter.next();
				DataSource fdSource = new FileDataSource(filename + att.getFilename());
				DataHandler fdHandler = new DataHandler(fdSource);
				MimeBodyPart part = new MimeBodyPart();
				part.setDataHandler(fdHandler);
				part.setFileName(att.getFilename());
				mpMessage.addBodyPart(part);
			}

			message.setContent(mpMessage);
		} else {
			message.setContent(email.getMessageText(), "text/plain");
		}

		Transport trans = sess.getTransport("smtp");
		trans.connect(host, port, username, password);

		Address[] adr = message.getAllRecipients();
		trans.sendMessage(message, adr);
		trans.close();
	}
}