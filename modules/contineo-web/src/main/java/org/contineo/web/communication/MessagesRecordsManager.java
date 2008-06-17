package org.contineo.web.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.contineo.core.communication.SystemMessage;
import org.contineo.core.communication.dao.SystemMessageDAO;
import org.contineo.util.Context;
import org.contineo.web.SessionManagement;
import org.contineo.web.StyleBean;
import org.contineo.web.i18n.Messages;
import org.contineo.web.navigation.NavigationBean;
import org.contineo.web.navigation.PageContentBean;


/**
 * <p>
 * The <code>MessagesRecordsManager</code> class is responsible for
 * constructing the list of <code>SystemMessage</code> beans which will be
 * bound to a ice:dataTable JSF component.
 * </p>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 *
 * @author Marco Meschieri
 * @version $Id: DocumentsRecordsManager.java,v 1.1 2007/06/29 06:28:29 marco
 *          Exp $
 * @since 3.0
 */
public class MessagesRecordsManager {
    protected static Log log = LogFactory.getLog(MessagesRecordsManager.class);
    private List<SystemMessage> messages = new ArrayList<SystemMessage>();
    private PageContentBean selectedPanel = new PageContentBean("list");
    private NavigationBean navigation;
    private MessageForm form;

    public MessagesRecordsManager() {
        messages.clear();
    }

    public PageContentBean getSelectedPanel() {
        return selectedPanel;
    }

    public void setSelectedPanel(PageContentBean panel) {
        this.selectedPanel = panel;
    }

    /**
     * Reloads the messages list
     */
    public String listMessages() {
        SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
        messages = smdao.findByRecipient(SessionManagement.getUsername());
        smdao.deleteExpiredMessages(SessionManagement.getUsername());

        PageContentBean content = new PageContentBean("messages",
                "communication/messages");
        content.setContentTitle(Messages.getMessage("db.readmessages"));
        content.setIcon(StyleBean.getImagePath("message.png"));
        navigation.setSelectedPanel(content);

        setSelectedPanel(new PageContentBean("list"));

        return null;
    }

    /**
     * Shows the message insert form
     */
    public String addMessage() {
        Application application = FacesContext.getCurrentInstance()
                                              .getApplication();
        NavigationBean navigation = ((NavigationBean) application.createValueBinding(
                "#{navigation}").getValue(FacesContext.getCurrentInstance()));

        PageContentBean content = new PageContentBean("message",
                "communication/messages");
        content.setContentTitle(Messages.getMessage("db.createmessage"));
        content.setIcon(StyleBean.getImagePath("message.png"));
        navigation.setSelectedPanel(content);

        // Initialize the form
        form.setReadOnly(false);
        form.setMessage(new SystemMessage());

        setSelectedPanel(new PageContentBean("insert"));

        return null;
    }

    /**
     * Cleans up the resources used by this class. This method could be called
     * when a session destroyed event is called.
     */
    public void dispose() {
        messages.clear();
    }

    /**
     * Gets the list of SystemMessages which will be used by the ice:dataTable
     * component.
     */
    public Collection<SystemMessage> getMessages() {
        if (messages.size() == 0) {
            listMessages();
        }

        return messages;
    }

    public String selectMessage() {
        Map map = FacesContext.getCurrentInstance().getExternalContext()
                              .getRequestMap();
        SystemMessage record = (SystemMessage) map.get("messageRecord");
        SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance()
                                                           .getBean(SystemMessageDAO.class);

        if (record.getRead() == 0) {
            record.setRead(1);
            smdao.store(record);

            if (record.getConfirmation() == 1) {
                Date date = new Date();
                SystemMessage sysmess = new SystemMessage();
                sysmess.setAuthor("SYSTEM");
                sysmess.setRecipient(record.getAuthor());
                sysmess.setSubject("Confirmation");
                sysmess.setMessageText("To: " + record.getRecipient() +
                    "\nMessage: " + record.getMessageText());
                sysmess.setSentDate(String.valueOf(date.getTime()));
                sysmess.setRead(0);
                sysmess.setConfirmation(0);
                sysmess.setPrio(record.getPrio());
                sysmess.setDateScope(record.getDateScope());
                smdao.store(sysmess);
            }
        }

        // Initialize the form
        Application application = FacesContext.getCurrentInstance()
                                              .getApplication();
        MessageForm form = ((MessageForm) application.createValueBinding(
                "#{messageForm}").getValue(FacesContext.getCurrentInstance()));
        form.setReadOnly(true);
        form.setMessage(record);

        selectedPanel = new PageContentBean("view");

        return null;
    }

    public String deleteMessage() {
        Map map = FacesContext.getCurrentInstance().getExternalContext()
                              .getRequestMap();
        SystemMessage record = (SystemMessage) map.get("messageRecord");

        if (SessionManagement.isValid()) {
            try {
                SystemMessageDAO smDao = (SystemMessageDAO) Context.getInstance()
                                                                   .getBean(SystemMessageDAO.class);
                boolean deleted = smDao.delete(record.getMessageId());

                if (!deleted) {
                    Messages.addLocalizedError("errors.action.deletesysmess");
                } else {
                    Messages.addLocalizedInfo("msg.action.deletesysmess");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Messages.addLocalizedError("errors.action.deletesysmess");
            }
        } else {
            return "login";
        }

        listMessages();

        return null;
    }

    public int getToBeReadCount() {
        SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance()
                                                           .getBean(SystemMessageDAO.class);
        int smcount = smdao.getCount(SessionManagement.getUsername());

        return smcount;
    }

    public int getCount() {
        if (messages.size() == 0) {
            listMessages();
        }

        return messages.size();
    }

    public void setNavigation(NavigationBean navigation) {
        this.navigation = navigation;
    }

    public void setForm(MessageForm form) {
        this.form = form;
    }
}
