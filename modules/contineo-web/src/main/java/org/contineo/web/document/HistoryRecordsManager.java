package org.contineo.web.document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.contineo.core.document.Document;
import org.contineo.core.document.History;
import org.contineo.core.document.dao.DocumentDAO;
import org.contineo.core.document.dao.HistoryDAO;

import org.contineo.util.Context;

import org.contineo.web.i18n.Messages;
import org.contineo.web.navigation.PageContentBean;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;


/**
 * Control that allows the user to list history events
 *
 * @author Marco Meschieri
 * @version $Id: HistoryRecordsManager.java,v 1.1 2006/08/23 08:09:52 marco Exp $
 * @since 3.0
 */
public class HistoryRecordsManager {
    protected static Log log = LogFactory.getLog(HistoryRecordsManager.class);
    private Collection<History> histories = new ArrayList<History>();
    private Document selectedDocument;

    /**
     * Changes the currently selected document and updates the articles list.
     *
     * @param doc
     */
    public void selectDocument(Document doc) {
        selectedDocument = doc;

        // initiate the list
        if (histories != null) {
            histories.clear();
        } else {
            histories = new ArrayList<History>(10);
        }

        try {
            int menuId = selectedDocument.getMenuId();
            int docId = selectedDocument.getDocId();

            DocumentDAO docDao = (DocumentDAO) Context.getInstance()
                                                      .getBean(DocumentDAO.class);
            Document document = docDao.findByMenuId(menuId);
            docId = document.getDocId();

            HistoryDAO historyDAO = (HistoryDAO) Context.getInstance()
                                                        .getBean(HistoryDAO.class);
            histories = historyDAO.findByDocId(doc.getDocId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),
                e.getMessage());
        }
    }

    /**
     * Cleans up the resources used by this class. This method could be called
     * when a session destroyed event is called.
     */
    public void dispose() {
        histories.clear();
    }

    /**
     * Gets the list of histories which will be used by the ice:dataTable
     * component.
     *
     * @return array list of histories
     */
    public Collection<History> getHistories() {
        return histories;
    }

    public String back() {
        Application application = FacesContext.getCurrentInstance()
                                              .getApplication();
        DocumentNavigation documentNavigation = ((DocumentNavigation) application.createValueBinding(
                "#{documentNavigation}")
                                                                                 .getValue(FacesContext.getCurrentInstance()));
        documentNavigation.setSelectedPanel(new PageContentBean("documents"));
        selectedDocument = null;

        return null;
    }

    public Document getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(Document selectedDocument) {
        this.selectedDocument = selectedDocument;
    }
}
