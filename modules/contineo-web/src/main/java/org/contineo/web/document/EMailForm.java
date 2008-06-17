package org.contineo.web.document;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.contineo.core.FileBean;
import org.contineo.core.communication.Attachment;
import org.contineo.core.communication.EMail;
import org.contineo.core.communication.EMailSender;
import org.contineo.core.communication.Recipient;
import org.contineo.core.document.Document;
import org.contineo.core.document.DownloadTicket;
import org.contineo.core.security.Menu;
import org.contineo.core.security.User;
import org.contineo.core.security.dao.MenuDAO;
import org.contineo.util.Context;
import org.contineo.util.config.MimeTypeConfig;
import org.contineo.util.config.SettingsConfig;
import org.contineo.web.SessionManagement;
import org.contineo.web.i18n.Messages;
import org.contineo.web.navigation.PageContentBean;

/**
 * This form is used to send emails
 * 
 * @author Michael Scholz, Marco Meschieri
 * @version $Id: EMailForm.java,v 1.2 2006/09/03 16:24:37 marco Exp $
 * @since 1.0
 */
public class EMailForm {
	protected static Log log = LogFactory.getLog(EMailForm.class);

	private String author;

	private String recipient;

	private String subject;

	private String text;

	private Document selectedDocument;

	private Collection<Menu> attachments = new ArrayList<Menu>();

	private DownloadTicket downloadTicket;

	private DocumentNavigation documentNavigation;

	public EMailForm() {
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Collection<Menu> getAttachments() {
		return attachments;
	}

	public void setAttachments(Collection<Menu> attachments) {
		this.attachments = attachments;
	}

	public void reset() {
		author = "";
		recipient = "";
		subject = "";
		text = "";
		attachments.clear();
		downloadTicket = null;
		selectedDocument = null;
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public DownloadTicket getDownloadTicket() {
		return downloadTicket;
	}

	public void setDownloadTicket(DownloadTicket downloadTicket) {
		this.downloadTicket = downloadTicket;
	}

	public String toString() {
		return (new ReflectionToStringBuilder(this) {
			protected boolean accept(java.lang.reflect.Field f) {
				return super.accept(f);
			}
		}).toString();
	}

	public String send() {
		EMail email;
		SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
		String udir = conf.getValue("userdir");
		String maildir = udir + "/mails/";
		if (SessionManagement.isValid()) {
			try {
				User user = SessionManagement.getUser();
				email = new EMail();

				email.setAccountId(-1);
				email.setAuthor(user.getUserName());
				//email.setAuthorAddress(user.getEmail());
				email.setAuthorAddress(getAuthor());
				
				Recipient recipient = new Recipient();
				recipient.setAddress(getRecipient());
				email.addRecipient(recipient);
				email.setFolder("outbox");
				email.setMessageText(getText());
				email.setRead(1);
				email.setSentDate(String.valueOf(new Date().getTime()));
				email.setSubject(getSubject());
				email.setUserName(user.getUserName());
				
				maildir = maildir + String.valueOf(email.getMessageId()) + "/";
				
				FileBean.createDir(maildir);
				FileBean.writeFile(email.getMessageText(), maildir + "email.mail");
				int i = 2;

				for (Menu menu : attachments) {
					createAttachment(email, menu.getMenuId(), i++);
				}
				
				try {
					EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
					sender.send(email);
					Messages.addLocalizedInfo("msg.action.saveemail");
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					Messages.addLocalizedError("errors.action.saveemail");
				}finally{
					FileUtils.forceDelete(new File(maildir));
				}

				setAuthor(user.getEmail());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.saveemail");
			}
		} else {
			return "login";
		}

		documentNavigation.setSelectedPanel(new PageContentBean("documents"));
		reset();

		return null;
	}

	private void createAttachment(EMail email, int menuId, int partid) {
		Attachment att = new Attachment();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu menu = menuDao.findByPrimaryKey(menuId);

		if (menuDao.isReadEnable(menu.getMenuId(), email.getUserName())) {
			att.setIcon(menu.getMenuIcon());
			att.setFilename(menu.getMenuRef());

			String extension = menu.getMenuRef().substring(menu.getMenuRef().lastIndexOf(".") + 1);
			MimeTypeConfig mtc = (MimeTypeConfig) Context.getInstance().getBean(MimeTypeConfig.class);
			String mimetype = mtc.getMimeApp(extension);

			if ((mimetype == null) || mimetype.equals("")) {
				mimetype = "application/octet-stream";
			}

			att.setMimeType(mimetype);

			SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			String docdir = conf.getValue("docdir");
			String doc = docdir + "/" + menu.getMenuPath() + "/" + menu.getMenuId() + "/" + menu.getMenuRef();
			String userdir = conf.getValue("userdir");
			String mail = userdir + "/mails/" + String.valueOf(email.getMessageId()) + "/";
			FileBean.createDir(mail);
			FileBean.copyFile(doc, mail + menu.getMenuRef());
			
			if (att != null) {
				email.addAttachment(partid, att);
			}
		}
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}